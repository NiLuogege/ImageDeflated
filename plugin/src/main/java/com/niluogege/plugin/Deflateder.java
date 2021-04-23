package com.niluogege.plugin;

import com.niluogege.plugin.bean.TinyConfig;
import com.niluogege.plugin.bean.WebpConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;

import java.io.BufferedWriter;
import java.io.File;
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
                        if (!file.isDirectory() && suffixFilter(file.getName())) {
                            long startFileLength = file.length();
                            BufferedWriter writer = rw.getWriter();
                            writer.append("|")
                                    .append(file.getParentFile().getName())
                                    .append("/")
                                    .append(file.getName())
                                    .append("|")
                                    .append(getKb(file))
                                    .append("|");

                            boolean tinyed = tinyer.tiny(file);
                            if (tinyed) {
                                writer.append(getKb(file)).append("|");
                            } else {
                                writer.append("/").append("|");
                            }

                            boolean webped = webper.webp(file);
                            if (webped) {
                                writer.append(getKb(file)).append("|");
                            } else {
                                writer.append("/").append("|");
                            }

                            long endFileLength = file.length();
                            writer.append((int) ((endFileLength / startFileLength) * 100) + "").append("|");
                            writer.append("\n");
                            writer.flush();

                        }
                        System.out.print(".");
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

    private static boolean suffixFilter(String fileName) {
        return !fileName.endsWith(".9.png")
                && (fileName.endsWith(".png")
                || fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg"));
    }

    private static String getKb(File file) {
        return file.length() + "";
    }

}
