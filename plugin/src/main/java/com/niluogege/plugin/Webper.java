package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.niluogege.plugin.bean.WebpConfig;
import com.tinify.Source;
import com.tinify.Tinify;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class Webper {


    private List<File> waitDeflateDirs;
    private WebpConfig webpConfig;
    private final TargetFileFilter targetFileFilter;
    private final TargetDirFilter targetDirFilter;


    public Webper(List<File> waitDeflateDirs, WebpConfig webpConfig) {
        this.waitDeflateDirs = waitDeflateDirs;
        this.webpConfig = webpConfig;
        targetFileFilter = new TargetFileFilter(webpConfig);
        targetDirFilter = new TargetDirFilter();


        System.out.println(webpConfig.toString());
    }

    public void webp() throws Exception {
        if (webpConfig.open) {
            if (waitDeflateDirs != null && waitDeflateDirs.size() > 0) {
                for (File waitDeflateDir : waitDeflateDirs) {

                    for (File file : FileUtils.listFilesAndDirs(waitDeflateDir, targetFileFilter, targetDirFilter)) {


                        if (!file.isDirectory()) {
                            String filePath = file.getAbsolutePath();


                        }
                    }
                }
            }
        }
    }


    private static class TargetFileFilter extends AbstractFileFilter {
        private WebpConfig tinyConfig;
        private HashSet<Pattern> whiteList;

        public TargetFileFilter(WebpConfig webpConfig) {
            this.tinyConfig = webpConfig;
            this.whiteList = webpConfig.getWhiteList();
        }

        @Override
        public boolean accept(File file) {

            String fileName = file.getName().toLowerCase();
            if (!file.isDirectory()
                    && suffixFilter(fileName)
                    && whiteListFilter(fileName)
            ) {
                return true;
            }
            return false;
        }

        private boolean suffixFilter(String fileName) {
            return !fileName.endsWith(".9.png")
                    && (fileName.endsWith(".png")
                    || fileName.endsWith(".jpg")
                    || fileName.endsWith(".jpeg"));
        }


        private boolean whiteListFilter(String fileName) {
            for (Pattern pattern : whiteList) {
                boolean matche = pattern.matcher(fileName).matches();
//                System.out.println("fileName= " + fileName + " pattern=" + pattern.toString() + " matche= " + matche);
                if (matche) {
                    return false;
                }
            }
            return true;
        }
    }
}
