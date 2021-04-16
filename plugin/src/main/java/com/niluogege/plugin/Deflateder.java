package com.niluogege.plugin;

import com.android.build.gradle.internal.LoggingUtil;
import com.android.build.gradle.tasks.MergeResources;
import com.android.ide.common.resources.ResourcePreprocessor;
import com.android.ide.common.resources.ResourceSet;
import com.niluogege.plugin.bean.TinyConfig;
import com.niluogege.plugin.bean.WebpConfig;
import com.tinify.Source;
import com.tinify.Tinify;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class Deflateder {


    public static void deflate(List<File> waitDeflateDirs, TinyConfig tinyConfig, WebpConfig webpConfig) {

        try {
            Tinyer tinyer = new Tinyer(waitDeflateDirs, tinyConfig);
            tinyer.tiny();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
