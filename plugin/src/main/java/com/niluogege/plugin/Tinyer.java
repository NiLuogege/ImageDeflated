package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.tinify.Source;
import com.tinify.Tinify;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class Tinyer {
    private static List<String> targetDirName = new ArrayList<String>() {{
        add("drawable");
        add("mipmap");
    }};

    private List<File> waitDeflateDirs;
    private TinyConfig tinyConfig;


    public Tinyer(List<File> waitDeflateDirs, TinyConfig tinyConfig) {
        this.waitDeflateDirs = waitDeflateDirs;
        this.tinyConfig = tinyConfig;

        Tinify.setKey(tinyConfig.key);

        System.out.println(tinyConfig.toString());
    }

    public void tiny() throws Exception {
        if (tinyConfig.open) {
            if (waitDeflateDirs != null && waitDeflateDirs.size() > 0) {
                for (File waitDeflateDir : waitDeflateDirs) {

                    for (File file : FileUtils.listFilesAndDirs(waitDeflateDir, new TargetFileFilter(tinyConfig), new TargetDirFilter())) {


                        if (!file.isDirectory()) {
                            String filePath = file.getAbsolutePath();

                            int compressionCount = Tinify.compressionCount();
                            System.out.println("compressionCount=" + compressionCount + " file=" + filePath);

                            if (compressionCount < tinyConfig.compressionsCountPerMonth) {
                                Source source = Tinify.fromFile(filePath);
//                                source.toFile(filePath);
                                source.toFile(filePath.replace(".png", "_origin.png"));
                            }
                        }
                    }
                }
            }
        }
    }


    private static class TargetFileFilter extends AbstractFileFilter {
        private TinyConfig tinyConfig;
        private HashSet<Pattern> whiteList;

        public TargetFileFilter(TinyConfig tinyConfig) {
            this.tinyConfig = tinyConfig;
            this.whiteList = tinyConfig.getWhiteList();
        }

        @Override
        public boolean accept(File file) {

            String fileName = file.getName().toLowerCase();
            if (!file.isDirectory()
                    && suffixFilter(fileName)
                    && sizeFilter(file)
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

        private boolean sizeFilter(File file) {
            return file.length() > tinyConfig.threshold;
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

    private static class TargetDirFilter extends AbstractFileFilter {
        @Override
        public boolean accept(File file) {

            if (file.isDirectory()) {
                String dirName = file.getName();
                String[] nameParts = dirName.split("-");
                if (targetDirName.contains(nameParts[0])) {
                    return true;
                }
            }
            return false;
        }
    }
}
