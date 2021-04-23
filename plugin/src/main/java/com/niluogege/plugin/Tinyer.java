package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.tinify.Source;
import com.tinify.Tinify;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class Tinyer {

    private TinyConfig tinyConfig;
    private final TargetFileFilter targetFileFilter;


    public Tinyer(TinyConfig tinyConfig) throws Exception {
        this.tinyConfig = tinyConfig;
        targetFileFilter = new TargetFileFilter(tinyConfig);

        Tinify.setKey(tinyConfig.key);

        System.out.println(tinyConfig.toString());
    }

    public void tiny(File file) throws Exception {
        if (tinyConfig.open && !file.isDirectory() && targetFileFilter.accept(file)) {
            String filePath = file.getAbsolutePath();

            int compressionCount = Tinify.compressionCount();

            if (compressionCount < tinyConfig.compressionsCountPerMonth) {
                Source source = Tinify.fromFile(filePath);
                source.toFile(filePath);
            } else {
                System.out.println("tiny compressionCount not enough" + " file=" + filePath);
            }
        }
    }


    private static class TargetFileFilter extends BaseTargetFileFilter {
        private TinyConfig tinyConfig;
        private HashSet<Pattern> whiteList;

        public TargetFileFilter(TinyConfig tinyConfig) {
            this.tinyConfig = tinyConfig;
            this.whiteList = tinyConfig.getWhiteList();
        }

        @Override
        HashSet<Pattern> getWhiteList() {
            return whiteList;
        }

        @Override
        protected boolean customAccept(String fileName, File file) {
            return sizeFilter(file);
        }

        private boolean sizeFilter(File file) {
            return file.length() > tinyConfig.threshold;
        }
    }

}
