package com.guireadergui.execute;

import java.io.IOException;

public class CodeExecutioner {
    Process process;

    public CodeExecutioner() throws IOException {

        var processBuilder = new ProcessBuilder();

        processBuilder.command("C:\\Users\\Caesar\\Desktop\\Tools\\twitchtorelais_start.bat");

        process = processBuilder.start();

    }


}
