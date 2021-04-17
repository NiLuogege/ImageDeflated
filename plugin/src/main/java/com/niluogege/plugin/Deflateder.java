package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.niluogege.plugin.bean.WebpConfig;

import java.io.File;
import java.util.List;

public class Deflateder {


    public static void deflate(List<File> waitDeflateDirs, TinyConfig tinyConfig, WebpConfig webpConfig) {

        try {
            Tinyer tinyer = new Tinyer(waitDeflateDirs, tinyConfig);
            tinyer.tiny();

            Webper webper = new Webper(waitDeflateDirs, webpConfig);
            webper.webp();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
