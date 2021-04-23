package com.niluogege.plugin.bean;

public class WebpConfig extends BaseConfig {
    public int quality = 75; //压缩质量 0-100
    public String path; //cwebp 依赖文件路径

    @Override
    public String toString() {
        return "WebpConfig{" +
                "path='" + path + '\'' +
                ", quality=" + quality +
                ", open=" + open +
                ", whiteList=" + getWhiteList() +
                '}';
    }
}
