package com.niluogege.plugin.bean;

public class WebpConfig extends BaseConfig {
    public String path; //cwebp 依赖文件路径

    @Override
    public String toString() {
        return "WebpConfig{" +
                "path='" + path + '\'' +
                ", open=" + open +
                ", whiteList=" + getWhiteList() +
                '}';
    }
}
