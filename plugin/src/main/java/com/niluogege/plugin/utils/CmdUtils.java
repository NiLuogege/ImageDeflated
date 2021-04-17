package com.niluogege.plugin.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CmdUtils {

    /**
     * 执行 命令行 ，java更加推荐
     *
     * @param cmd
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String runCmd(String... cmd) throws IOException, InterruptedException {
        String output;
        Process process = null;
        try {
            process = new ProcessBuilder(cmd).start();
            output = readInputStream(process.getInputStream());
            process.waitFor();
            if (process.exitValue() != 0) {
                System.err.println(String.format("%s Failed! Please check your signature file.\n", cmd[0]));
                throw new RuntimeException(readInputStream(process.getErrorStream()));
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return output;
    }

    public static String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}
