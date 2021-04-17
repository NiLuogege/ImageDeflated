package com.niluogege.plugin.bean;

import com.niluogege.plugin.utils.RegexUtils;

import java.util.HashSet;
import java.util.regex.Pattern;

public class BaseConfig {
    public boolean open;
    private HashSet<String> whiteList = new HashSet<>();
    private HashSet<Pattern> whiteListPattern = new HashSet<>();

    public void setWhiteList(HashSet<String> whiteList) {

        if (whiteList != null && whiteList.size() > 0) {
            this.whiteList = whiteList;

            for (String part : whiteList) {
                whiteListPattern.add(Pattern.compile(RegexUtils.convertToPatternString(part)));
            }
        }
    }

    public HashSet<Pattern> getWhiteList() {
        return whiteListPattern;
    }
}
