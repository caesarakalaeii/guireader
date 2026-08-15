{
  # Keep this line accurate and one line long: `nix flake metadata` prints it,
  # and it is the first thing a cold agent learns about the repo.
  description = "guireader -- JavaFX/Maven desktop app that reads on-screen GUIs by sampling screen pixels. Run `nix flake show` for the command map.";

  # nixpkgs is the only input, on purpose.
  #
  # flake-utils would buy exactly one thing here -- eachDefaultSystem -- which is
  # the three-line genAttrs below. In exchange it costs a second lock node in
  # every repo (flake-utils transitively pulls `systems`, so really two), a
  # second upstream that can break one repo and not the others, and a hardcoded
  # system list this repo cannot edit. That list is currently broken: it still
  # contains x86_64-darwin, which now throws (see `systems` below).
  #
  # nixos-unstable is the same channel the author's own NixOS config tracks, so
  # `nix develop` here and `nixos-rebuild` there resolve the same store paths and
  # share one cache.
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs =
    # `...` rather than a closed { self, nixpkgs }: adding a second input later
    # would otherwise fail with "called with unexpected argument 'self'".
    { nixpkgs, ... }:
    let
      lib = nixpkgs.lib;

      # x86_64-darwin is deliberately absent. nixpkgs 26.11 replaced that whole
      # attribute set with `throw "Nixpkgs 26.11 has dropped support for
      # x86_64-darwin"`. genAttrs is lazy, so plain `nix develop` on Linux would
      # not notice -- it detonates later, on `nix flake check --all-systems`.
      # Add it back only against a separate nixpkgs-26.05-darwin input.
      systems = [
        "x86_64-linux"
        "aarch64-linux"
        "aarch64-darwin"
      ];

      # Stand-in for flake-utils.lib.eachDefaultSystem. Passes `pkgs` rather than
      # a system string, because that is what every call site below wants.
      forAllSystems = f: lib.genAttrs systems (system: f nixpkgs.legacyPackages.${system});

      # ======================================================================
      # PER-REPO BLOCK 1 -- the toolchain
      # ======================================================================
      # Everything the commands below need. `nix flake check` realises this
      # closure, so a typo'd attr name fails at the flake gate instead of
      # surfacing as "command not found" halfway through a task.
      #
      # Explicit `pkgs.foo`, never `with pkgs; [ ... ]`: when an attr disappears
      # in a nixpkgs bump, `with` reports a bare undefined identifier with no
      # hint of which set it came from, and the name is not greppable.
      #
      # jdk17, pinned by major, because that is what the project actually
      # targets: pom.xml sets maven-compiler-plugin source/target to 17 and
      # GUIReaderGUI.iml sets LANGUAGE_LEVEL="JDK_17". Do not "modernise" this to
      # jdk21 -- the JavaFX artifacts the pom pins are 18/19-ea, which is the
      # generation that ships against 17.
      #
      # The FULL jdk, not jdk17_headless (~916 MB vs ~643 MB), and that is
      # load-bearing rather than sloppy: read/CaptureScreen.java builds a
      # java.awt.Robot and show/GUIReaderController.java imports
      # javafx.embed.swing.SwingFXUtils, so the headless JDK -- which is built
      # without the AWT/X11 native libs -- cannot run this program at all.
      toolchain = pkgs: [
        # ---- this repo's ecosystem ----
        pkgs.jdk17
        pkgs.maven

        # ---- present in every repo in the fleet ----
        pkgs.git
        pkgs.jq
        pkgs.gnumake
      ];

      # ======================================================================
      # PER-REPO BLOCK 2 -- libraries that get dlopened, not linked
      # ======================================================================
      # This list is bigger than the fleet default for one specific reason: the
      # JavaFX runtime is NOT the JDK's here, it comes out of the Maven artifacts
      # pinned in pom.xml (org.openjfx:javafx-*:18). Those jars carry
      # libglass*.so / libprism_es2.so / libjavafx_font_pango.so /
      # libgstreamer-lite.so, which JavaFX unpacks to a temp dir and
      # System.load()s at startup. Nothing patchelfs them, and NixOS has no
      # /usr/lib for them to fall back on, so LD_LIBRARY_PATH is the only way
      # they ever find gtk3, X11 and GL. Verified load-bearing, not cargo cult:
      # with LD_LIBRARY_PATH unset the same launch dies before the first window
      # with `libprism_es2.so: libGL.so.1: cannot open shared object file` and
      # `libglassgtk3.so: libgthread-2.0.so.0: cannot open shared object file`.
      #
      # alsa-lib is for javafx.media (the app plays an mp3 alert, see
      # execute/SoundExecutioner.java); the rest is glass/prism/pango.
      #
      # This fixes shared libraries only, and it does not conjure a display: AWT
      # Robot and JavaFX both need a real X11/XWayland $DISPLAY, which is a host
      # property no flake can supply. `dev-build` is fine headless; `dev-run` is
      # not.
      nativeLibs = pkgs: [
        pkgs.stdenv.cc.cc.lib
        pkgs.zlib
        pkgs.gtk3
        pkgs.glib
        pkgs.pango
        pkgs.cairo
        pkgs.gdk-pixbuf
        # libatk-1.0.so lives in at-spi2-core now; the `atk` alias still
        # resolves on this pin but is on its way out.
        pkgs.at-spi2-core
        pkgs.freetype
        pkgs.fontconfig
        pkgs.libGL
        pkgs.alsa-lib
        # Top-level libx11/libxext/... and NOT the xorg.* set: on this pin
        # `xorg.libX11` still works but prints "evaluation warning: The xorg
        # package set has been deprecated" on every single eval, which is exactly
        # the kind of per-call noise that fills an agent's context.
        pkgs.libx11
        pkgs.libxext
        pkgs.libxrender
        pkgs.libxtst
        pkgs.libxxf86vm
      ];

      # ======================================================================
      # PER-REPO BLOCK 3 -- constant environment variables
      # ======================================================================
      # Only values that are constants belong here. Anything that must READ an
      # existing value (LD_LIBRARY_PATH), UNSET something (SOURCE_DATE_EPOCH) or
      # touch the work tree goes in the shellHook further down.
      #
      # This attrset is applied to BOTH surfaces -- the dev shell and every
      # `nix run` wrapper -- so a command cannot behave differently depending on
      # how it was invoked.
      envVars = pkgs: {
        # Mandatory, not decorative. openjdk's setup hook is
        # `if [ -z "$JAVA_HOME" ]; then export JAVA_HOME=...; fi`, so an ambient
        # JAVA_HOME from the host silently wins and maven then compiles against
        # some other JDK. Note `.home` is "${jdk}/lib/openjdk", not "${jdk}" --
        # use the passthru, never hardcode the suffix.
        JAVA_HOME = pkgs.jdk17.home;

        # Maven 3.9 reads MAVEN_ARGS, so even a bare `mvn` an agent types by hand
        # gets batch mode. Interactive Maven has no tty under `nix run` /
        # `nix develop -c` and a prompt would hang until the agent's timeout.
        MAVEN_ARGS = "-B";
      };

      # ======================================================================
      # PER-REPO BLOCK 4 -- the command map
      # ======================================================================
      # THE single source of truth. It generates `apps` (so `nix run .#build`
      # works), the `dev-*` wrappers on PATH inside the shell, and `dev-help`.
      # Nothing is written twice, so `nix flake show` can never disagree with
      # what `dev-build` actually runs.
      #
      # Three verbs, and the omissions are deliberate information rather than
      # laziness -- do not add stubs for them:
      #   no `test`  there is no src/test tree at all. pom.xml declares
      #              junit-jupiter, but zero test classes exist, so `mvn test`
      #              would print "No tests to run" and exit 0 -- a green signal
      #              an agent would read as "the suite passes".
      #   no `lint`  the project configures no static analysis and no formatter
      #              (no spotless/checkstyle/pmd plugin, no .editorconfig). If it
      #              ever adopts one, add pkgs.google-java-format to the
      #              toolchain and wire lint/fmt to it here.
      commands = pkgs: {
        setup = {
          # Optional -- `build` resolves what it needs on its own. This just
          # front-loads the ~90 artifacts (JavaFX 18 + ControlsFX + TilesFX +
          # httpclient5) so the first real build is quiet.
          #
          # NOT hermetic, and it cannot be made so: Maven resolves from Maven
          # Central into ~/.m2. Nix owns the JDK and Maven itself; Maven owns its
          # artifacts. Do not try to nixify these dependencies, and do not run
          # this offline -- it will fail, by design.
          description = "(network) resolve all Maven dependencies into ~/.m2";
          text = ''mvn -B -q -f "$REPO_ROOT/pom.xml" dependency:go-offline "$@"'';
        };
        build = {
          # -B (batch) is mandatory, see MAVEN_ARGS above. -q keeps the
          # per-artifact transfer-progress spam out of the agent's context; pass
          # `-- -X` when something needs debugging.
          #
          # KNOWN UPSTREAM BREAKAGE, not a flake problem, and reproduced before
          # writing this: `mvn package` fails in maven-jar-plugin with
          #   Error assembling JAR: Manifest file:
          #   .../src/main/resources/META-INF/MANIFEST.MF does not exist.
          # because pom.xml points manifestFile there while the file actually
          # lives in src/main/java/META-INF/. It cannot be overridden from the
          # command line. That is why this verb stops at `compile` -- the largest
          # step that is honestly green today. Fix the pom path (and the stale
          # Class-Path/Main-Class inside that manifest), then make this
          # `package`.
          description = "compile the sources (network on first run, fills ~/.m2)";
          text = ''mvn -B -q -f "$REPO_ROOT/pom.xml" compile "$@"'';
        };
        run = {
          # Deliberately NOT `mvn javafx:run`, and this was measured rather than
          # guessed. The pom pins that plugin's mainClass to
          # `com.example.guireadergui/com.guireadergui.Main` inside its
          # `default-cli` execution, and no such class exists in this tree, so
          # `mvn javafx:run` dies with
          #   Error: Could not find or load main class com.guireadergui.Main
          # A `-Djavafx.mainClass=...` override does NOT help: an explicit
          # execution <configuration> beats the user property. Fix the pom (the
          # real entrypoint is com.guireadergui.show.GUIReader) if you want the
          # plugin path back.
          #
          # So launch it the way show/Launcher.java exists to allow: JavaFX on
          # the CLASSPATH, entered through a class that does not itself extend
          # Application -- which is what dodges "Error: JavaFX runtime components
          # are missing". Expect one benign line on startup, `WARNING:
          # Unsupported JavaFX configuration: classes were loaded from 'unnamed
          # module'` -- that is the classpath launch, not a fault.
          #
          # classpath.txt is regenerated every run on purpose: a stale one
          # survives a pom.xml edit and then fails in ways that look like code
          # bugs.
          description = "launch the JavaFX GUI (needs an X11/XWayland DISPLAY)";
          text = ''
            mvn -B -q -f "$REPO_ROOT/pom.xml" compile
            mvn -B -q -f "$REPO_ROOT/pom.xml" dependency:build-classpath \
              -Dmdep.outputFile="$REPO_ROOT/target/classpath.txt"
            java -cp "$REPO_ROOT/target/classes:$(cat "$REPO_ROOT/target/classpath.txt")" \
              com.guireadergui.show.Launcher "$@"
          '';
        };
      };

      # ======================================================================
      # GENERIC MACHINERY -- byte-identical across the fleet, do not edit
      # ======================================================================

      # Prepend, never assign: a host LD_LIBRARY_PATH may be carrying something
      # the user needs, and clobbering it breaks binaries they launch from here.
      # Linux only -- on darwin the loader variable is DYLD_*, and exporting a
      # Linux-shaped value there is at best useless.
      ldPreamble =
        pkgs:
        lib.optionalString (pkgs.stdenv.hostPlatform.isLinux && nativeLibs pkgs != [ ]) ''
          export LD_LIBRARY_PATH="${lib.makeLibraryPath (nativeLibs pkgs)}''${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}"
        '';

      # Every command gets $REPO_ROOT. `nix run` and `nix develop` both start in
      # whatever directory they were invoked from, so a bare relative path
      # silently forks a second environment as soon as an agent works from a
      # subdirectory. Note we do NOT cd there: commands act on the caller's cwd
      # on purpose.
      rootPreamble = ''
        REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
        export REPO_ROOT
      '';

      # One derivation per command, reused by both `apps` and the dev shell, so
      # the two can never diverge. `dev-` prefixed because a bare `test` binary
      # earlier on PATH would shadow the POSIX shell builtin and quietly break
      # every script in the repo that uses it.
      wrappers =
        pkgs:
        lib.mapAttrs (
          name: cmd:
          pkgs.writeShellApplication {
            name = "dev-${name}";
            runtimeInputs = toolchain pkgs;
            runtimeEnv = envVars pkgs;
            meta.description = cmd.description;
            text = ''
              ${rootPreamble}
              ${ldPreamble pkgs}
              ${cmd.text}
            '';
          }
        ) (commands pkgs);

      helpFor =
        pkgs:
        let
          cmds = commands pkgs;
          names = lib.attrNames cmds;
          width = lib.foldl' (a: n: lib.max a (builtins.stringLength n)) 0 names;
          pad = n: n + lib.concatStrings (lib.genList (_: " ") (width - builtins.stringLength n));
          line = n: c: "  dev-${pad n}  ${c.description}";
        in
        pkgs.writeShellApplication {
          name = "dev-help";
          meta.description = "print this repo's command map (works offline)";
          text = ''
            cat <<'EOF'
            ${lib.concatStringsSep "\n" (lib.mapAttrsToList line cmds)}
            EOF
          '';
        };
    in
    {
      # `nix flake show` -- the discovery entrypoint, and deliberately the whole
      # machine-facing contract: every app carries a meta.description, which
      # `nix flake show` prints inline and `nix flake show --json` exposes at
      # .apps.<system>.<name>.description. Pure evaluation, so an agent gets the
      # entire command map in one cheap call without reading a README.
      #
      # Do NOT invent a top-level output for this (`agentManifest`, `probeThing`
      # ...). Nix answers with `warning: unknown flake output '<name>'` on every
      # single `nix flake check`, forever.
      apps = forAllSystems (
        pkgs:
        lib.mapAttrs (name: cmd: {
          type = "app";
          program = "${(wrappers pkgs).${name}}/bin/dev-${name}";
          meta.description = cmd.description;
        }) (commands pkgs)
      );

      # `nix develop` -- the toolchain, plus a dev-<verb> for every app.
      devShells = forAllSystems (pkgs: {
        default = pkgs.mkShell {
          packages = toolchain pkgs ++ lib.attrValues (wrappers pkgs) ++ [ (helpFor pkgs) ];

          env = envVars pkgs;

          # Some C extensions and node-gyp addons compile at -O0, where glibc's
          # _FORTIFY_SOURCE becomes a hard error instead of a warning.
          hardeningDisable = [ "fortify" ];

          shellHook = ''
            # mkShell inherits SOURCE_DATE_EPOCH=315532800 (1980-01-01) from
            # stdenv, and any jar or zip built in here then dies with "ZIP does
            # not support timestamps before 1980".
            unset SOURCE_DATE_EPOCH

            ${rootPreamble}
            ${ldPreamble pkgs}

            # Nothing networked, nothing stateful and nothing interactive above
            # this line, and nothing below it either. No `mvn dependency:resolve`,
            # no `mvn wrapper:wrapper`, no `read`, no `exec $SHELL`. Bootstrapping
            # in the hook makes a cold `nix develop -c mvn compile` start
            # downloading before it runs anything, on EVERY invocation -- the
            # exact failure an unattended agent cannot diagnose. That is what
            # `dev-setup` is for.

            # The banner is interactive-only, and this guard is load-bearing:
            # shellHook output lands on the STDOUT of `nix develop -c <cmd>`, so
            # an unguarded echo corrupts anything parsing it
            # (`nix develop -c cat x.json | jq` fails to parse). $- is the only
            # reliable discriminator here -- it lacks `i` for `nix develop -c`
            # and has it at an interactive prompt. Do not test $PS1 (unset in
            # both) or $IN_NIX_SHELL (set in both). >&2 is the second layer, for
            # the case where a caller runs us on a pty.
            case $- in
              *i*) echo "guireader dev shell -- 'dev-help' for the command map" >&2 ;;
            esac
          '';
        };
      });

      # `nix flake check` -- honest by construction. It realises the toolchain
      # closure (so a typo'd or currently-broken attr fails here) and builds
      # every wrapper, which runs shellcheck over every command text. Add real
      # test derivations beside it. NEVER add a check that always passes: an
      # agent reads "all checks passed!" as a signal, and a fake check makes
      # `nix flake check` a liar.
      #
      # There is deliberately no build-the-jar check: Maven resolves from
      # ~/.m2/Maven Central, which the nix sandbox has no network for. That
      # non-hermeticity is inherent to this repo, not something to paper over.
      checks = forAllSystems (pkgs: {
        toolchain =
          pkgs.runCommand "toolchain-check"
            {
              nativeBuildInputs = toolchain pkgs ++ lib.attrValues (wrappers pkgs);
            }
            ''
              for verb in ${lib.escapeShellArgs (lib.attrNames (commands pkgs))}; do
                command -v "dev-$verb" > /dev/null || {
                  echo "dev-$verb is not on PATH" >&2
                  exit 1
                }
              done
              touch "$out"
            '';
      });

      # `nix fmt` -- formats the *Nix* in this repo; project code has no
      # formatter (see the command map). nixfmt-tree (the treefmt wrapper) rather
      # than bare nixfmt, because bare nixfmt tries to parse every path handed to
      # it and fails on non-Nix files. This file ships already formatted, so
      # `nix fmt` is a no-op rather than a diff.
      formatter = forAllSystems (pkgs: pkgs.nixfmt-tree);
    };
}
