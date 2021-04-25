package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.niluogege.plugin.bean.WebpConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

public class Deflateder {

    private final static TargetDirFilter targetDirFilter = new TargetDirFilter();
    private static final String recordFileName = "record.md";
    private static RecordWriter rw = null;

    public static void deflate(List<File> waitDeflateDirs, String recordDir, TinyConfig tinyConfig, WebpConfig webpConfig) {
        try {
            long startTime = System.currentTimeMillis();
            File recordFile = new File(recordDir, recordFileName);
            rw = new RecordWriter(recordFile, tinyConfig, webpConfig);
            Tinyer tinyer = new Tinyer(tinyConfig);
            Webper webper = new Webper(waitDeflateDirs, webpConfig);

            if (waitDeflateDirs != null && waitDeflateDirs.size() > 0) {
                for (File waitDeflateDir : waitDeflateDirs) {

                    for (File file : FileUtils.listFilesAndDirs(waitDeflateDir, FileFileFilter.FILE, targetDirFilter)) {
                        if (!file.isDirectory()) {
                            long startFileLength = file.length();

                            File tinyedFile = tinyer.tiny(file);
                            long tinyedFileLength = tinyedFile != null ? tinyedFile.length() : -1;


                            File webpedFile = webper.webp(tinyedFile == null ? file : tinyedFile);
                            long webpedFileLength = webpedFile != null ? webpedFile.length() : -1;


                            writeRecord(file, startFileLength, tinyedFile, tinyedFileLength, webpedFile, webpedFileLength);
                        }
                    }
                }
            }

            long endTime = System.currentTimeMillis();
            System.out.println("");
            System.out.println("imageDeflated success. cost time " + (endTime - startTime) / 1000 + "s . record in = " + recordFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rw.close();
        }

    }

    private static void writeRecord(File file, long startFileLength, File tinyedFile, long tinyedFileLength, File webpedFile, long webpedFileLength) throws IOException {
        if (isMainGenerated(file) && (tinyedFile != null || webpedFile != null)) {
            String tinyedFileLengthStr = tinyedFileLength == -1 ? "/" : String.valueOf(tinyedFileLength);
            String webpedFileLengthStr = webpedFileLength == -1 ? "/" : String.valueOf(webpedFileLength);

            long denominator;
            if (webpedFileLength != -1) {
                denominator = webpedFileLength;
            } else if (tinyedFileLength != -1) {
                denominator = tinyedFileLength;
            } else {
                denominator = startFileLength;
            }
            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(2);
            float compressionRatio = (1 - ((float) denominator / (float) startFileLength)) * 100;

            BufferedWriter writer = rw.getWriter();
            writer.append("|")
                    .append(file.getParentFile().getName()).append("/").append(file.getName())
                    .append("|")
                    .append(String.valueOf(startFileLength))
                    .append("|")
                    .append(tinyedFileLengthStr)
                    .append("|")
                    .append(webpedFileLengthStr)
                    .append("|")
                    .append(String.valueOf(format.format(compressionRatio)))
                    .append("|")
                    .append("\n")
                    .flush();

            System.out.println("imageDeflated working " + file.getName());
            if (compressionRatio < 0) {
                System.out.println("imageDeflated  is not work in file" + file.getName() + " add it to whiteList");
            }
        }
    }

    private static boolean isMainGenerated(File file) {
        while (file.getParentFile() != null) {
            if (file.getName().startsWith("main$Generated")) {
                return true;
            } else {
                file = file.getParentFile();
            }
        }
        return false;

    }
}
