package com.niluogege.plugin.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

public class BaseConfig {
    public boolean open;
    private HashSet<String> whiteList = new HashSet<>();
    private HashSet<Pattern> whiteListPattern = new HashSet<>();

    public void setWhiteList(HashSet<String> whiteList) {

        System.out.println("setWhiteList= "+whiteList.toString());

        if (whiteList != null && whiteList.size() > 0) {
            this.whiteList = whiteList;

            for (String part : whiteList) {
                whiteListPattern.add(Pattern.compile(part));
            }
        }
    }

    public HashSet<Pattern> getWhiteList() {
        return whiteListPattern;
    }
}
