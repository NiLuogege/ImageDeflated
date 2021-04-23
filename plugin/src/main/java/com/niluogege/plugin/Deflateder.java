package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.niluogege.plugin.bean.WebpConfig;
import com.niluogege.plugin.utils.CmdUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;

import java.io.File;
import java.util.List;

public class Deflateder {

    private static final String recordFileName = "tiny.md";

    private final static TargetDirFilter targetDirFilter = new TargetDirFilter();

    public static void deflate(List<File> waitDeflateDirs, TinyConfig tinyConfig, WebpConfig webpConfig) {

        try {
            Tinyer tinyer = new Tinyer(tinyConfig);

            Webper webper = new Webper(waitDeflateDirs, webpConfig);

            if (waitDeflateDirs != null && waitDeflateDirs.size() > 0) {
                for (File waitDeflateDir : waitDeflateDirs) {

                    for (File file : FileUtils.listFilesAndDirs(waitDeflateDir, FileFileFilter.FILE, targetDirFilter)) {
                        if (!file.isDirectory()) {
                            tinyer.tiny(file);
                            webper.webp(file);
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
