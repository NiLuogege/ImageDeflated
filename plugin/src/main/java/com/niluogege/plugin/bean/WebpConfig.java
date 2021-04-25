package com.niluogege.plugin.bean;

public class WebpConfig extends BaseConfig {
    public int quality = 75; //压缩质量 0-100
    public long threshold = 1024; //1kb, 文件超过1kb 才进行压缩，对比发现 当 png 文件大小 大于 500 byte时才会是正压缩
    public String path; //cwebp 依赖文件路径

    @Override
    public String toString() {
        return "WebpConfig{" +
                "path='" + path + '\'' +
                ", threshold=" + threshold +
                ", quality=" + quality +
                ", open=" + open +
                ", whiteList=" + getWhiteList() +
                '}';
    }
}
