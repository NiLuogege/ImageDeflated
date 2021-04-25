package com.niluogege.plugin;

import com.niluogege.plugin.bean.WebpConfig;
import com.niluogege.plugin.utils.CmdUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class Webper {


    private List<File> waitDeflateDirs;
    private WebpConfig webpConfig;
    private final TargetFileFilter targetFileFilter;

    public Webper(List<File> waitDeflateDirs, WebpConfig webpConfig) {
        this.waitDeflateDirs = waitDeflateDirs;
        this.webpConfig = webpConfig;
        targetFileFilter = new TargetFileFilter(webpConfig);

        System.out.println(webpConfig.toString());
    }

    public File webp(File file) throws Exception {
        if (webpConfig.open && !file.isDirectory() && targetFileFilter.accept(file)) {
            String filePath = file.getAbsolutePath();
            String webpFilePath = new File(file.getParentFile(), getFileNameWithoutSuffix(file) + ".webp").getAbsolutePath();
            CmdUtils.runCmd(webpConfig.path, "-q", webpConfig.quality + "", filePath, "-o", webpFilePath);
            file.delete();
            return new File(webpFilePath);
        }
        return null;
    }

    //获取不带后缀名的文件名
    private String getFileNameWithoutSuffix(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private static class TargetFileFilter extends BaseTargetFileFilter {
        private WebpConfig webpConfig;
        private HashSet<Pattern> whiteList;

        public TargetFileFilter(WebpConfig webpConfig) {
            this.webpConfig = webpConfig;
            this.whiteList = webpConfig.getWhiteList();
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
            return file.length() > webpConfig.threshold;
        }
    }
}
