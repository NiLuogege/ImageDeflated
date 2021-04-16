package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.tinify.Source;
import com.tinify.Tinify;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    }

    public void tiny() throws Exception {
        if (tinyConfig.open) {
            if (waitDeflateDirs != null && waitDeflateDirs.size() > 0) {
                for (File waitDeflateDir : waitDeflateDirs) {

                    for (File file : FileUtils.listFilesAndDirs(waitDeflateDir, new TargetFileFilter(), new TargetDirFilter())) {


                        if (!file.isDirectory()) {

                            System.out.println(file.getName());

                            String filePath = file.getAbsolutePath();

                            Source source = Tinify.fromFile(filePath);
                            source.toFile(filePath.replace(".png", "-origin.png"));
                        }
                    }
                }
            }
        }
    }


    private static class TargetFileFilter extends AbstractFileFilter {
        @Override
        public boolean accept(File file) {

            String fileName = file.getName().toLowerCase();
            if (!file.isDirectory() && (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))) {
                return true;
            }
            return false;
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
