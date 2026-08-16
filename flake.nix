{
  # Keep this line accurate and one line long: `nix flake metadata` prints it,
  # and it is the first thing a cold agent learns about the repo.
  description = "guireader -- JavaFX/Maven desktop app that samples screen pixels to read on-screen GUIs. Run `nix flake show` for the command map.";

  # nixpkgs is the only input, on purpose. The one thing flake-utils would buy
  # here is eachDefaultSystem, and the machinery below spells that out in a
  # single genAttrs line -- which keeps the system list in this file, where a
  # reader can see it, instead of in a second input.
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs =
    # `self` is mandatory: the machinery below anchors on it. `...` rather than
    # a closed { self, nixpkgs }: adding a second input later would otherwise
    # fail with "called with unexpected argument".
    { self, nixpkgs, ... }:
    let
      lib = nixpkgs.lib;

      # ======================================================================
      # PER-REPO BLOCK 5 -- the repo's own name
      # ======================================================================
      # Cosmetic: it appears in the interactive dev-shell banner and nowhere
      # else. It is how a human tells two open shells apart, so it still has to
      # be the clone's name.
      repoName = "guireader";

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
      # jdk17, pinned by major, because that is what the project targets:
      # pom.xml sets maven-compiler-plugin source and target to 17, and
      # GUIReaderGUI.iml carries LANGUAGE_LEVEL="JDK_17". The pom also pins
      # JavaFX 18 (javafx-controls/fxml/web), 18.0.1 (javafx-swing) and 19-ea+5
      # (javafx-media), so a JDK bump is a JavaFX bump too -- check both.
      #
      # The FULL jdk, not jdk17_headless, and that is load-bearing rather than
      # sloppy. This program wants the X11 AWT backend:
      # read/CaptureScreen.java constructs a java.awt.Robot and
      # show/GUIReaderController.java goes through
      # javafx.embed.swing.SwingFXUtils. jdk17 ships
      # lib/openjdk/lib/libawt_xawt.so; jdk17_headless is built with
      # --enable-headless-only, which is in its derivation's configureFlags and
      # in jdk17's is not. What the choice costs, measured at this lock on
      # x86_64-linux with `nix path-info -S --store https://cache.nixos.org`:
      # jdk17 is 813,939,304 B (776 MiB) of closure against jdk17_headless's
      # 542,320,136 B (517 MiB).
      toolchain = pkgs: [
        # ---- this repo's ecosystem ----
        pkgs.jdk17
        pkgs.maven

        # ---- dev-shell conveniences ----
        # No verb below invokes any of these three -- the command texts run
        # mvn, java and cat, and nothing else -- and the machinery's anchor
        # deliberately does not need git either, since it compares flake.nix
        # with bash builtins. They are here for the human or agent at the
        # prompt; the price is that `nix flake check` realises them too.
        pkgs.git
        pkgs.jq
        pkgs.gnumake
      ];

      # ======================================================================
      # PER-REPO BLOCK 2 -- libraries that get dlopened, not linked
      # ======================================================================
      # This list is longer than a plain JVM repo's for one specific reason: the
      # JavaFX runtime is not the JDK's here, it comes out of the Maven
      # artifacts pinned in pom.xml. Those jars carry prebuilt .so files that
      # JavaFX unpacks into ~/.openjfx/cache/<version>/ and System.load()s at
      # startup. Nothing patchelfs them and NixOS has no /usr/lib to fall back
      # on, so LD_LIBRARY_PATH is the only way they find anything.
      #
      # Load-bearing, and measured rather than assumed: the same launch with
      # LD_LIBRARY_PATH unset gets as far as
      #   Loading library prism_es2 from resource failed:
      #   ~/.openjfx/cache/18+12/libprism_es2.so: libGL.so.1: cannot open
      #   shared object file
      #   ~/.openjfx/cache/18+12/libglassgtk3.so: libgthread-2.0.so.0: cannot
      #   open shared object file
      # and then dies on `UnsatisfiedLinkError: no glassgtk3 in
      # java.library.path`, before the first window.
      #
      # The list is exactly what those .so files ask for, not a guess. Measured
      # over the natives in javafx-graphics-18-linux.jar and
      # javafx-media-19-ea+5-linux.jar (libglass*.so, libprism_*.so,
      # libjavafx_font*.so, libgstreamer-lite.so, libjfxmedia.so, ...):
      #
      #   readelf -d *.so | grep NEEDED    -> libgtk-3.so.0 libgdk-3.so.0
      #     libglib-2.0.so.0 libgio-2.0.so.0 libgobject-2.0.so.0
      #     libgmodule-2.0.so.0 libgthread-2.0.so.0 libpango-1.0.so.0
      #     libpangoft2-1.0.so.0 libcairo.so.2 libgdk_pixbuf-2.0.so.0
      #     libfreetype.so.6 libGL.so.1 libasound.so.2 libX11.so.6 libXtst.so.6
      #   strings -a *.so                  -> libfontconfig.so.1, dlopened by
      #     the font natives rather than linked, so it is NEEDED by nothing and
      #     still has to be here.
      #
      # The same scan also turns up gtk2 (libgtk-x11-2.0, from libglassgtk2.so)
      # and ffmpeg (libav*, libswscale, from the avplugin shims). Neither is
      # provided: the measured launch loads glassgtk3, not glassgtk2, and no
      # avplugin was reached. Add them only against an actual failure.
      #
      # Sufficient, and measured that way round too: with exactly this list the
      # GUI starts and stays up.
      #
      # This fixes shared libraries only, and it does not conjure a display.
      # Measured, with DISPLAY and WAYLAND_DISPLAY removed from the
      # environment: `dev-build` still exits 0, while `dev-run` reaches
      # "java.lang.UnsupportedOperationException: Unable to open DISPLAY" and
      # exits 1. A display is a host property no flake can supply.
      nativeLibs = pkgs: [
        pkgs.gtk3
        pkgs.glib
        pkgs.pango
        pkgs.cairo
        pkgs.gdk-pixbuf
        pkgs.freetype
        pkgs.fontconfig
        pkgs.libGL
        pkgs.alsa-lib
        # Top-level libx11/libxtst, and NOT the xorg.* set: `xorg.libX11`
        # resolves to the same store path on this pin but prints "evaluation
        # warning: The xorg package set has been deprecated, 'xorg.libX11' has
        # been renamed to 'libx11'" on every eval, which is exactly the kind of
        # per-call noise that fills an agent's context.
        pkgs.libx11
        pkgs.libxtst
      ];

      # ======================================================================
      # PER-REPO BLOCK 3 -- constant environment variables
      # ======================================================================
      # Constants only. Anything that must READ an existing value
      # (LD_LIBRARY_PATH) or UNSET something (SOURCE_DATE_EPOCH) is the
      # machinery's business, not this attrset's.
      #
      # Applied to BOTH surfaces -- the dev shell and every `nix run` wrapper --
      # so a command cannot behave differently depending on how it was invoked.
      envVars = pkgs: {
        # Mandatory, not decorative. The openjdk setup hook that mkShell runs
        # is literally
        #   if [ -z "${JAVA_HOME-}" ]; then export JAVA_HOME=.../lib/openjdk; fi
        # so on its own an ambient JAVA_HOME inherited from the host wins, and
        # maven compiles against some other JDK. Setting it here beats the
        # host, measured: with JAVA_HOME=/nonexistent-host-jdk in the caller's
        # environment, `nix develop -c` still reports the store path below.
        #
        # Use the passthru, never hardcode a suffix: `.home` is
        # "${jdk}/lib/openjdk" on Linux but "${jdk}" itself on aarch64-darwin,
        # where jdk17 is a zulu-ca-jdk.
        JAVA_HOME = pkgs.jdk17.home;

        # maven 3.9.16 (the version this lock resolves) reads MAVEN_ARGS, so
        # even a bare `mvn` an agent types by hand gets batch mode. Measured by
        # putting a bogus flag in it: maven answers "Unrecognized option".
        # Interactive maven has no tty under `nix run` or `nix develop -c` and
        # a prompt would hang until the agent's timeout.
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
      # Three verbs, and the omissions are information rather than laziness --
      # do not add stubs for them:
      #   no `test`  there is no src/test tree at all (src/ contains only
      #              main/). pom.xml declares junit-jupiter-api and -engine at
      #              test scope, but no test class exists, so `mvn test`
      #              prints "No tests to run." and exits 0 (measured) -- a
      #              green signal an agent would read as "the suite passes".
      #   no `lint`  the project configures no static analysis and no formatter:
      #              pom.xml has no spotless/checkstyle/pmd/spotbugs plugin and
      #              there is no .editorconfig. If it ever adopts one, add the
      #              tool to the toolchain and wire lint/fmt to it here.
      #
      # `build` and `run` call need_writable_checkout first: both put maven's
      # output in $REPO_ROOT/target, which is not a thing to attempt in the
      # read-only store snapshot. `setup` deliberately does not, and that is
      # measured, not an oversight -- pointed at the store snapshot's pom.xml it
      # exits 0 and creates nothing there, because everything it fetches lands
      # in ~/.m2. So `nix run <this-repo>#setup` from anywhere at all is a
      # legitimate way to warm the artifact cache, and a guard would only break
      # it.
      commands = pkgs: {
        setup = {
          # Optional -- `build` resolves what it needs on its own. This just
          # front-loads the artifacts so the first real build is quiet.
          #
          # NOT hermetic, and it cannot be made so: maven resolves from Maven
          # Central into ~/.m2. Nix owns the JDK and maven itself; maven owns
          # its artifacts. Do not try to nixify these dependencies, and do not
          # run this offline -- it will fail, by design.
          description = "(network) resolve all Maven dependencies into ~/.m2";
          text = ''mvn -B -q -f "$REPO_ROOT/pom.xml" dependency:go-offline "$@"'';
        };
        build = {
          # -B (batch) is mandatory, see MAVEN_ARGS above. -q keeps maven's INFO
          # stream out of the agent's context -- a successful `nix run .#build`
          # prints nothing at all. `-- -X` still wins over it when something
          # needs debugging (measured: `-q -X` emits the debug log).
          #
          # KNOWN UPSTREAM BREAKAGE, not a flake problem, and reproduced before
          # writing this: `mvn package` fails in maven-jar-plugin:3.5.0:jar
          # (default-jar) with
          #   Error assembling JAR: Manifest file:
          #   .../src/main/resources/META-INF/MANIFEST.MF does not exist.
          # because pom.xml points <manifestFile> there while the file actually
          # lives in src/main/java/META-INF/. That is why this verb stops at
          # `compile` -- the largest step that is honestly green today. Fix the
          # pom path, then make this `package`.
          description = "compile the sources (network on first run, fills ~/.m2)";
          text = ''
            need_writable_checkout
            mvn -B -q -f "$REPO_ROOT/pom.xml" compile "$@"
          '';
        };
        run = {
          # Deliberately NOT `mvn javafx:run`, and this was measured rather than
          # guessed. The pom pins that plugin's mainClass to
          # `com.example.guireadergui/com.guireadergui.Main` inside its
          # `default-cli` execution, and no class named Main exists in this
          # tree, so `mvn javafx:run` dies with
          #   Error: Could not find or load main class com.guireadergui.Main
          #   in module com.example.guireadergui
          # Overriding it does not help -- an explicit execution
          # <configuration> beats the user property, and
          # `-Djavafx.mainClass=com.guireadergui.show.GUIReader javafx:run`
          # produces that same line, verbatim. Fix the pom (the real entrypoint
          # is com.guireadergui.show.GUIReader) if you want the plugin path
          # back.
          #
          # So launch it the way show/Launcher.java exists to allow: JavaFX on
          # the CLASSPATH, entered through Launcher, which is a plain class,
          # while GUIReader is the one that extends Application. Expect one
          # line on startup that is not a fault -- "WARNING: Unsupported
          # JavaFX configuration: classes were loaded from 'unnamed module
          # @...'". That is the classpath launch: the tree does carry a
          # src/main/java/module-info.java, and this verb does not use it.
          #
          # classpath.txt is regenerated every run on purpose: nothing else
          # updates it when pom.xml changes, and a stale one then fails in ways
          # that look like code bugs.
          description = "launch the JavaFX GUI (needs an X11/XWayland DISPLAY)";
          text = ''
            need_writable_checkout
            mvn -B -q -f "$REPO_ROOT/pom.xml" compile
            mvn -B -q -f "$REPO_ROOT/pom.xml" dependency:build-classpath \
              -Dmdep.outputFile="$REPO_ROOT/target/classpath.txt"
            java -cp "$REPO_ROOT/target/classes:$(cat "$REPO_ROOT/target/classpath.txt")" \
              com.guireadergui.show.Launcher "$@"
          '';
        };
      };

      # ======================================================================
      # PER-REPO BLOCK 6 -- checks beyond the canonical two
      # ======================================================================
      # The machinery's `anchoring` check proves rootPreamble and guardPreamble
      # behave. It cannot prove that THIS repo's verbs call them -- only this
      # file knows which verb writes. `build` and `run` do, so both must refuse
      # in a foreign tree, and that is what this check pins.
      #
      # It can fail, and that was checked rather than assumed: with the guard
      # deleted from `build` in a scratch copy of this repo, the check goes red
      # on "dev-build failed, but not on the guard" -- the verb got all the way
      # to maven, which then died on plugin resolution instead. Nothing here
      # needs a network, because the refusal happens before the first maven
      # invocation, which is also why `setup` -- the verb with no guard -- is
      # absent from the list below: it would need Maven Central, and the
      # sandbox has none.
      extraChecks = pkgs: {
        verbGuards =
          pkgs.runCommand "verb-guard-check" { nativeBuildInputs = lib.attrValues (wrappers pkgs); }
            ''
              set -euo pipefail

              # A decoy that looks like this repo to any anchor weaker than the
              # canonical one: same ecosystem marker files, different flake.
              mkdir decoy
              printf '{\n  description = "a different repo";\n  outputs = _: { };\n}\n' > decoy/flake.nix
              printf '<project><artifactId>decoy</artifactId></project>\n' > decoy/pom.xml
              mkdir -p decoy/src/main/java
              printf 'class Victim { }\n' > decoy/src/main/java/Victim.java
              cp -r decoy decoy.orig

              # Logs live outside the decoy so the final diff can be exact
              # rather than filtered.
              while IFS= read -r verb; do
                [ -n "$verb" ] || continue
                if ( cd decoy && "dev-$verb" ) > "$verb.log" 2>&1; then
                  echo "dev-$verb succeeded in a foreign tree; it must refuse" >&2
                  cat "$verb.log" >&2
                  exit 1
                fi
                grep -q "needs a writable" "$verb.log" || {
                  echo "dev-$verb failed, but not on the guard" >&2
                  cat "$verb.log" >&2
                  exit 1
                }
              done <<'GUARDED_VERBS_EOF'
              build
              run
              GUARDED_VERBS_EOF

              diff -r decoy decoy.orig
              touch "$out"
            '';
      };

      # >>>>> BEGIN CANONICAL MACHINERY v1 <<<<<
      # ======================================================================
      # Everything from the BEGIN sentinel above to the END sentinel on the last
      # line of this file is fleet-canonical text: the same bytes in every repo
      # that carries this flake style. That is a checkable claim, not a boast --
      #
      #   sed -n '/BEGIN CANONICAL MACHINERY v1/,$p' flake.nix | sha256sum
      #
      # prints the same digest in every repo, or one of them has been edited.
      # (`,$p`, not a range ending on the END sentinel: a range whose closing
      # pattern were spelled out here would terminate on this very comment.)
      # Nothing here names a repository, a language, a tool or a project file.
      # If you find such a name below, it is contamination: the fix is to move
      # it into the per-repo section above, never to special-case it here.
      #
      # This region READS exactly these names from the per-repo section:
      #   nixpkgs  self  lib  repoName  toolchain  nativeLibs  envVars
      #   commands  extraChecks
      # and DEFINES exactly these:
      #   systems  forAllSystems  ldPreamble  rootPreamble  guardPreamble
      #   wrappers  helpFor  anchorCheck
      # plus the four flake outputs apps / devShells / checks / formatter.
      # Anything else in scope is invisible to it. The types of those eight
      # inputs, and the shell variables this region exports into command texts,
      # are specified in INTERFACE.md, which travels with this block.
      #
      # To change behaviour here you change it in every repo at once and bump
      # the version in both sentinels. A local edit is a bug by construction:
      # the digest above stops matching, and -- because rootPreamble anchors on
      # flake.nix byte-identity -- an edited working tree also stops being
      # recognised by wrappers built from the previous revision.
      # ======================================================================

      # ---- systems policy: decided once for the whole fleet ----
      #
      # Read this list as "evaluated on three, built on one". That is what was
      # measured, and it is all it means:
      #   * `nix flake check --all-systems` passes, so every output attribute
      #     below EVALUATES on all three systems.
      #   * only x86_64-linux has ever been BUILT. The machine this was verified
      #     on has no aarch64 emulation -- no binfmt handler, and `extra-
      #     platforms` is x86-only -- so aarch64 cannot be built there at all.
      # It is not a statement that anything works on aarch64. Do not upgrade it
      # into one in a README.
      #
      # Evaluating all three is still worth its seconds, because the failure it
      # catches is an eval-time failure: a `pkgs.<attr>` that exists on Linux
      # and not on darwin (`stdenv.cc.cc.lib` is the usual one) throws during
      # evaluation, and `nix flake check` without --all-systems checks only the
      # current system and sails straight past it.
      #
      # x86_64-darwin is deliberately absent. nixpkgs 26.11 replaced that whole
      # attribute set with a `throw`. genAttrs is lazy, so plain `nix develop`
      # on Linux would not notice -- it detonates later, on the --all-systems
      # run this policy requires. Add it back only against a separate
      # nixpkgs-26.05-darwin input.
      systems = [
        "x86_64-linux"
        "aarch64-linux"
        "aarch64-darwin"
      ];

      # Stand-in for flake-utils.lib.eachDefaultSystem. Passes `pkgs` rather
      # than a system string, because that is what every call site wants, and
      # keeps the system list in this file rather than in a second input's
      # hardcoded copy of it.
      forAllSystems = f: lib.genAttrs systems (system: f nixpkgs.legacyPackages.${system});

      # Prepend, never assign: a host LD_LIBRARY_PATH may be carrying something
      # the user needs, and clobbering it breaks binaries they launch from here.
      # Linux only -- on darwin the loader variable is DYLD_*, and exporting a
      # Linux-shaped value there is at best useless.
      #
      # `&&` short-circuits in Nix, so on darwin `nativeLibs pkgs` is never
      # forced. That is load-bearing for the systems policy above: it is what
      # lets a repo list Linux-only attrs in nativeLibs and still evaluate on
      # aarch64-darwin. Do not reorder the two operands.
      ldPreamble =
        pkgs:
        lib.optionalString (pkgs.stdenv.hostPlatform.isLinux && nativeLibs pkgs != [ ]) ''
          export LD_LIBRARY_PATH="${lib.makeLibraryPath (nativeLibs pkgs)}''${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}"
        '';

      # Every command gets $SRC_ROOT and $REPO_ROOT. `nix run` and `nix develop`
      # both start in whatever directory they were invoked from, and no verb may
      # act on that directory -- these two are what it acts on instead.
      #
      # $SRC_ROOT is this flake's own source, snapshotted into the store when
      # the flake was evaluated. It is the one anchor that is always available:
      # `nix run /path/to/repo#lint` tells the running program nothing whatever
      # about /path/to/repo (flake refs are location-independent by design, and
      # there is no $FLAKE_DIR to read), so without `self` a wrapper invoked
      # that way has literally no way to name the repo it belongs to. Two
      # limitations worth knowing: it is read-only, being a store path, and in a
      # git checkout it contains only TRACKED files.
      #
      # $REPO_ROOT is the writable checkout when the caller is standing in one,
      # and $SRC_ROOT when they are not. Three things this deliberately is NOT:
      #
      #   * NOT `pwd`. A fallback to the caller's directory is how `fmt`
      #     rewrites a stranger's source tree and how `lint` prints "all checks
      #     passed" having read none of this repo.
      #   * NOT `git rev-parse --show-toplevel`. Run from inside some OTHER git
      #     repo it cheerfully answers with THAT repo's top level. It also needs
      #     git on PATH and a .git directory, so it fails on an export and in
      #     any wrapper whose toolchain omits git.
      #   * NOT an inherited $REPO_ROOT from the environment. The dev shell
      #     EXPORTS this variable, so honouring it would mean that running
      #     `nix run /path/to/B#fmt` from inside repo A's dev shell points B's
      #     formatter at A. An explicit path argument is how a caller overrides
      #     a verb's target; an ambient variable is how they do it by accident.
      #
      # Instead: walk up from $PWD and take the first ancestor that IS this
      # repo, proved by carrying a byte-identical flake.nix. A single tracked
      # filename, a marker directory, or a set of them is not proof -- sibling
      # repos in a fleet share those, and a decoy can be built to carry any list
      # of names you care to publish. The whole flake.nix is what distinguishes
      # repos, because description, toolchain and command map all differ, so the
      # whole flake.nix is what gets compared. Compared with bash's own
      # `$(<file)` rather than cmp or sha256sum, so the check depends on no
      # package at all -- pure builtins, correct even in a wrapper whose PATH
      # carries nothing but the repo's own toolchain.
      #
      # Consequence worth knowing: edit flake.nix and the dev-* wrappers in an
      # already-open `nix develop` stop recognising the tree, because they were
      # built from the previous flake.nix. That is a stale shell telling you so
      # -- re-enter it. `nix run` re-evaluates every time and never sees this.
      rootPreamble = ''
        SRC_ROOT=${lib.escapeShellArg "${self}"}
        export SRC_ROOT

        _dev_find_root() {
          local dir ref
          ref=$(<"$SRC_ROOT/flake.nix") || return 1
          dir=$(
            unset CDPATH
            cd -P -- "''${1:-.}" 2>/dev/null && pwd
          ) || return 1
          while [ -n "$dir" ]; do
            if [ -f "$dir/flake.nix" ] && [ "$(<"$dir/flake.nix")" = "$ref" ]; then
              printf '%s\n' "$dir"
              return 0
            fi
            dir=''${dir%/*}
          done
          return 1
        }

        REPO_ROOT="$(_dev_find_root "$PWD" || printf '%s\n' "$SRC_ROOT")"
        export REPO_ROOT
      '';

      # Wrappers only, not the shellHook -- an interactive shell has no business
      # carrying this function around. Any command text that writes files calls
      # it first, and it is the reason a mutating verb can fail loudly instead
      # of falling back to "well, the cwd then".
      #
      # The test is $REPO_ROOT != $SRC_ROOT, i.e. "rootPreamble found a real
      # checkout", not a permission or a store-path-prefix test. Both of those
      # answer a narrower question: a checkout may be read-only for unrelated
      # reasons, and a store path is not the only tree we must refuse to write.
      guardPreamble = ''
        need_writable_checkout() {
          if [ "$REPO_ROOT" != "$SRC_ROOT" ]; then
            return 0
          fi
          echo "''${0##*/}: this command rewrites files, so it needs a writable" >&2
          echo "checkout of this repo -- and standing in $PWD there is none: no" >&2
          echo "parent directory carries this flake's flake.nix. The only tree in" >&2
          echo "reach is the read-only store snapshot $SRC_ROOT, and rewriting" >&2
          echo "$PWD instead is exactly the bug this guard exists to prevent." >&2
          echo "cd into the repo (or \`nix develop\` it), or pass an explicit path." >&2
          exit 1
        }
      '';

      # One derivation per command, reused by both `apps` and the dev shell, so
      # the two can never diverge. `dev-` prefixed because a bare `test` binary
      # earlier on PATH would shadow the POSIX shell builtin and quietly break
      # every script in the repo that uses it.
      #
      # writeShellApplication, not writeShellScriptBin: it runs shellcheck at
      # BUILD time and sets `set -euo pipefail`, so an unquoted $@ or a silently
      # ignored failure is a `nix flake check` failure rather than a surprise in
      # front of an agent.
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
              ${guardPreamble}
              ${ldPreamble pkgs}
              ${cmd.text}
            '';
          }
        ) (commands pkgs);

      # `dev-help` is generated from the same attrset as everything else, so it
      # cannot describe a verb that does not exist or miss one that does. No
      # runtimeInputs: printing the map must work with nothing installed.
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

      # The regression gate for rootPreamble and guardPreamble, which are the
      # two pieces of this flake that can silently damage a tree that is not
      # this repo. It tests the MECHANISM, not any verb, which is precisely what
      # makes it fleet-generic: it needs to know nothing about what this repo
      # does, only that the anchor resolves and the guard refuses.
      #
      # The decoy is a real directory carrying a real flake.nix that differs.
      # Marker-file anchors pass a decoy like this -- that is the whole point of
      # the probe -- and so does any anchor that trusts `pwd`. Probe 2 is the
      # other half, and without it a guard that refused everything would score a
      # perfect pass: a tree that IS byte-identical must still be adopted, or
      # every mutating verb in the repo is dead. Probe 3 pins the subdirectory
      # case, which is the normal one for an agent working inside a repo.
      #
      # A per-repo probe that drives the actual verbs is strictly better and
      # cannot live here -- it has to know which verb writes and which needs a
      # network. INTERFACE.md shows how to add one via `extraChecks`.
      anchorCheck =
        pkgs:
        pkgs.runCommand "anchor-check" { } ''
          set -euo pipefail

          # The two preambles under test, verbatim, in a file the probes source.
          # A quoted heredoc, so every $ below is the bash the wrappers see.
          cat > preamble.sh <<'CANONICAL_PREAMBLE_EOF'
          ${rootPreamble}
          ${guardPreamble}
          CANONICAL_PREAMBLE_EOF

          mkdir decoy
          printf '{\n  description = "a different repo";\n  outputs = _: { };\n}\n' > decoy/flake.nix
          printf 'do not touch me\n' > decoy/victim.txt
          cp -r decoy decoy.orig

          # ---- probe 1: a foreign tree must not be adopted ----
          if ! ( cd decoy && . ../preamble.sh && [ "$REPO_ROOT" = "$SRC_ROOT" ] ); then
            echo "anchor adopted a directory that is not this repo" >&2
            exit 1
          fi
          # In a subshell: need_writable_checkout ends in `exit`, which would
          # otherwise take this whole build down instead of failing a condition.
          if ( cd decoy && . ../preamble.sh && need_writable_checkout ) > guard.log 2>&1; then
            echo "need_writable_checkout accepted a tree that is not this repo" >&2
            exit 1
          fi
          if ! diff -r decoy decoy.orig; then
            echo "the probes modified the foreign tree" >&2
            exit 1
          fi

          # ---- probe 2: a byte-identical checkout must be adopted ----
          cp -r ${lib.escapeShellArg "${self}"} checkout
          chmod -R u+w checkout
          if ! ( cd checkout && . ../preamble.sh &&
                 [ "$REPO_ROOT" = "$(pwd -P)" ] && need_writable_checkout ); then
            echo "anchor refused a byte-identical checkout of this repo" >&2
            exit 1
          fi

          # ---- probe 3: from a subdirectory, still the checkout root ----
          mkdir -p checkout/probe3/deeper
          if ! ( cd checkout/probe3/deeper && . ../../../preamble.sh &&
                 [ "$REPO_ROOT" = "$(cd -P ../.. && pwd)" ] ); then
            echo "anchor did not walk up to the checkout root from a subdirectory" >&2
            exit 1
          fi

          touch "$out"
        '';
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

          # Natively-compiled extension modules are routinely built at -O0,
          # where glibc's _FORTIFY_SOURCE stops being a warning and becomes a
          # hard error.
          hardeningDisable = [ "fortify" ];

          shellHook = ''
            # mkShell inherits SOURCE_DATE_EPOCH=315532800 (1980-01-01) from
            # stdenv, and any wheel or zip built in here then dies with "ZIP does
            # not support timestamps before 1980".
            unset SOURCE_DATE_EPOCH

            # $REPO_ROOT and $SRC_ROOT are exported here as a convenience for
            # the human at the prompt. Every wrapper re-resolves them from
            # scratch and none of them reads these, on purpose: a stale value
            # exported by one repo's shell must never steer another repo's verb.
            ${rootPreamble}
            ${ldPreamble pkgs}

            # Nothing networked, nothing stateful and nothing interactive above
            # this line, and nothing below it either. No environment
            # bootstrapping, no dependency installation, no `read`, no
            # `exec $SHELL`. Bootstrapping in the hook makes a cold
            # `nix develop -c <anything>` start downloading before it runs
            # anything, on EVERY invocation -- the exact failure an unattended
            # agent cannot diagnose. That is what a `setup` verb is for.

            # The banner is interactive-only, and this guard is load-bearing:
            # shellHook output lands on the STDOUT of `nix develop -c <cmd>`, so
            # an unguarded echo corrupts anything parsing it
            # (`nix develop -c cat x.json | jq` fails to parse). $- is the only
            # reliable discriminator here -- it lacks `i` for `nix develop -c`
            # and has it at an interactive prompt. Do not test $PS1 (unset in
            # both) or $IN_NIX_SHELL (set in both). >&2 is the second layer, for
            # the case where a caller runs us on a pty.
            case $- in
              *i*) echo "${repoName} dev shell -- 'dev-help' for the command map" >&2 ;;
            esac
          '';
        };
      });

      # `nix flake check` -- honest by construction, and the only gate this
      # style has. `toolchain` realises the whole toolchain closure (so a typo'd
      # or currently-broken attr fails here, not halfway through a task) and
      # builds every wrapper, which runs shellcheck over every command text.
      # `anchoring` is the regression test described above.
      #
      # Repo-specific checks go in `extraChecks`, never here. They may not
      # shadow either canonical name: silently replacing `anchoring` with
      # something weaker is the exact failure this whole file exists to make
      # impossible, so a collision is an eval error with both names in it.
      #
      # NEVER add a check that always passes. An agent reads "all checks
      # passed!" as a signal, and a fake check makes `nix flake check` a liar.
      checks = forAllSystems (
        pkgs:
        let
          canonical = {
            toolchain =
              pkgs.runCommand "toolchain-check"
                {
                  nativeBuildInputs = toolchain pkgs ++ lib.attrValues (wrappers pkgs) ++ [ (helpFor pkgs) ];
                }
                ''
                  set -euo pipefail
                  dev-help > help.txt

                  # A while-read over a heredoc rather than `for x in <list>`,
                  # which is a bash syntax error when the list is empty -- and a
                  # repo with no verbs yet is a legitimate state.
                  while IFS= read -r verb; do
                    [ -n "$verb" ] || continue
                    command -v "dev-$verb" > /dev/null || {
                      echo "dev-$verb is not on PATH" >&2
                      exit 1
                    }
                    grep -q -- "dev-$verb" help.txt || {
                      echo "dev-$verb is missing from the dev-help map" >&2
                      exit 1
                    }
                  done <<'CANONICAL_VERBS_EOF'
                  ${lib.concatStringsSep "\n" (lib.attrNames (commands pkgs))}
                  CANONICAL_VERBS_EOF

                  touch "$out"
                '';
            anchoring = anchorCheck pkgs;
          };
          extra = extraChecks pkgs;
          clash = lib.intersectLists (lib.attrNames canonical) (lib.attrNames extra);
        in
        if clash != [ ] then
          throw "extraChecks must not redefine canonical checks: ${lib.concatStringsSep ", " clash}"
        else
          canonical // extra
      );

      # `nix fmt` -- formats the *Nix* in this repo; project code gets a `fmt`
      # verb. nixfmt-tree (the treefmt wrapper) rather than bare nixfmt, because
      # bare nixfmt tries to parse every path handed to it and fails on non-Nix
      # files. This file ships already formatted, so `nix fmt` is a no-op rather
      # than a diff across the fleet.
      #
      # This is the one verb here NOT anchored to $REPO_ROOT, and it cannot be:
      # `nix fmt` is nix's own verb, and nix -- not this flake -- decides which
      # paths the formatter receives, passing the cwd when the user names none.
      # A wrapper that overrode them would break `nix fmt path/to/one/file.nix`,
      # and it cannot tell that "." apart from the default. So `nix fmt` formats
      # where you stand, by design; the `fmt` verb is the anchored one.
      formatter = forAllSystems (pkgs: pkgs.nixfmt-tree);
    };
}
# >>>>> END CANONICAL MACHINERY v1 <<<<<
