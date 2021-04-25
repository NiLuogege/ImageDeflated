package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.niluogege.plugin.bean.WebpConfig;

import java.io.File;
import java.text.NumberFormat;

public class Demo {
    public static void main(String[] args) {

//        File file = new File("E:\\111work\\code\\code_me\\myGitHub\\ImageDeflated\\app\\build\\imageDeflated\\main$Generated-main-a529e8aacb8190e75e555dd4d4a1ebd2\\mipmap-xxhdpi\\place_gesture.png");
        File file = new File("E:\\111work\\code\\code_me\\myGitHub\\ImageDeflated\\app\\build\\imageDeflated\\main-main-a529e8aacb8190e75e555dd4d4a1ebd2\\mipmap-xxhdpi\\place_gesture.png");
        boolean mainGenerated = isMainGenerated(file);
        System.out.println("mainGenerated= " + mainGenerated);


    }

    private static boolean isMainGenerated(File file) {
        while (file.getParentFile() != null) {
            if (file.getName().startsWith("main$Generated")) {
                return true;
            } else {
                file = file.getParentFile();
            }
        }
        return false;

    }
}
