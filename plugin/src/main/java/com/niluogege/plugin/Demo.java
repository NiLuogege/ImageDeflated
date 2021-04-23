package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.niluogege.plugin.bean.WebpConfig;

import java.io.File;

public class Demo {
    public static void main(String[] args) {
        try {
            String recordDir = "E:\\111work\\code\\code_me\\myGitHub\\ImageDeflated\\app\\build\\imageDeflated";
            RecordWriter  rw = new RecordWriter(new File(recordDir, "record.md"), new TinyConfig(), new WebpConfig());
            rw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
