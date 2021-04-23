package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.niluogege.plugin.bean.WebpConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RecordWriter {

    private final BufferedWriter recordBw;

    public RecordWriter(File file, TinyConfig tinyConfig, WebpConfig webpConfig) throws Exception {
        recordBw = new BufferedWriter(new FileWriter(file));
        writeConfig(tinyConfig, webpConfig);
        writeTitle();
    }


    public void writeConfig(TinyConfig tinyConfig, WebpConfig webpConfig) throws IOException {
        recordBw.append("### 配置")
                .append("\n")
                .append("- tinyConfig").append(" => ").append(tinyConfig.toString())
                .append("\n")
                .append("- webpConfig").append(" => ").append(webpConfig.toString())
                .append("\n")
                .flush();
    }


    public void writeTitle() throws IOException {
        recordBw.append("### 详细信息")
                .append("\n")
                .append("| 文件  | tiny前/byte  |tiny后/byte  |webp后/byte  |总压缩率%  |")
                .append("\n")
                .append("|---|---|---|---|---|")
                .append("\n")
                .flush();
    }


    public BufferedWriter getWriter() {
        return recordBw;
    }


    public void close() {
        try {
            recordBw.flush();
            recordBw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
