package com.niluogege.plugin;

import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TargetDirFilter extends AbstractFileFilter {
    private static final List<String> targetDirName = new ArrayList<String>() {{
        add("drawable");
        add("mipmap");
    }};

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