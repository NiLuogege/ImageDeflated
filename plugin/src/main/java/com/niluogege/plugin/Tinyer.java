package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.tinify.Source;
import com.tinify.Tinify;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class Tinyer {

    private List<File> waitDeflateDirs;
    private TinyConfig tinyConfig;
    private final TargetFileFilter targetFileFilter;
    private final TargetDirFilter targetDirFilter;


    public Tinyer(List<File> waitDeflateDirs, TinyConfig tinyConfig) {
        this.waitDeflateDirs = waitDeflateDirs;
        this.tinyConfig = tinyConfig;
        targetFileFilter = new TargetFileFilter(tinyConfig);
        targetDirFilter = new TargetDirFilter();

        Tinify.setKey(tinyConfig.key);

        System.out.println(tinyConfig.toString());
    }

    public void tiny() throws Exception {
        if (tinyConfig.open) {
            if (waitDeflateDirs != null && waitDeflateDirs.size() > 0) {
                for (File waitDeflateDir : waitDeflateDirs) {

                    for (File file : FileUtils.listFilesAndDirs(waitDeflateDir, targetFileFilter, targetDirFilter)) {


                        if (!file.isDirectory()) {
                            String filePath = file.getAbsolutePath();

                            int compressionCount = Tinify.compressionCount();
                            System.out.println("compressionCount=" + compressionCount + " file=" + filePath);

                            if (compressionCount < tinyConfig.compressionsCountPerMonth) {
                                Source source = Tinify.fromFile(filePath);
                                source.toFile(filePath);
//                                source.toFile(filePath.replace(".png", "_origin.png"));
                            }
                        }
                    }
                }
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
