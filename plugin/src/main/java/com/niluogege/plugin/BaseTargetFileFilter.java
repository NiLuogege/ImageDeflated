package com.niluogege.plugin;

import com.niluogege.plugin.bean.WebpConfig;

import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.util.HashSet;
import java.util.regex.Pattern;

public abstract class BaseTargetFileFilter extends AbstractFileFilter {


    @Override
    public boolean accept(File file) {

        String fileName = file.getName().toLowerCase();
        if (!file.isDirectory()
                && suffixFilter(fileName)
                && whiteListFilter(fileName)
                && customAccept(fileName, file)
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
        for (Pattern pattern : getWhiteList()) {
            boolean matche = pattern.matcher(fileName).matches();
            if (matche) {
                return false;
            }
        }
        return true;
    }


    //其他 过滤 条件
    protected boolean customAccept(String fileName, File file) {
        return true;
    }

    abstract HashSet<Pattern> getWhiteList();
}
