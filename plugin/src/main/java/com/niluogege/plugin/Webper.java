package com.niluogege.plugin;

import com.niluogege.plugin.bean.WebpConfig;
import com.niluogege.plugin.utils.CmdUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
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
                            String webpFilePath = new File(file.getParentFile(), getFileNameWithoutSuffix(file) + ".webp").getAbsolutePath();
                            CmdUtils.runCmd(webpConfig.path, "-q", webpConfig.quality + "", filePath, "-o", webpFilePath);
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    //获取不带后缀名的文件名
    private String getFileNameWithoutSuffix(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private static class TargetFileFilter extends BaseTargetFileFilter {
        private WebpConfig tinyConfig;
        private HashSet<Pattern> whiteList;

        public TargetFileFilter(WebpConfig webpConfig) {
            this.tinyConfig = webpConfig;
            this.whiteList = webpConfig.getWhiteList();
        }


        @Override
        HashSet<Pattern> getWhiteList() {
            return whiteList;
        }
    }
}
