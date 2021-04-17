package com.niluogege.plugin.bean;

public class TinyConfig extends BaseConfig {
    public String key;
    public long threshold;
    public int compressionsCountPerMonth;

    @Override
    public String toString() {
        return "TinyConfig{" +
                "key='" + key + '\'' +
                ", threshold=" + threshold +
                ", compressionsCountPerMonth=" + compressionsCountPerMonth +
                ", open=" + open +
                ", whiteList=" + getWhiteList() +
                '}';
    }
}
