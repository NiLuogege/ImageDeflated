package com.niluogege.plugin;

import com.niluogege.plugin.bean.BaseConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RecordWriter {

    private final BufferedWriter recordBw;

    public RecordWriter(BaseConfig config, String fileName) throws Exception {
        recordBw = new BufferedWriter(new FileWriter(new File(config.recordFilePath, fileName)));
        writeTitle();
        recordBw.write(config.getClass().getSimpleName() + " == " + config.toString());
    }


    public void close() throws IOException {
        recordBw.flush();
        recordBw.close();
    }

    public void writeTitle() throws IOException {
        recordBw.write("### ");
    }

}
