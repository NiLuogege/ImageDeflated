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
                        if (!file.isDirectory()) {
                            long startFileLength = file.length();

                            File tinyedFile = tinyer.tiny(file);
                            long tinyedFileLength = tinyedFile != null ? tinyedFile.length() : -1;


                            File webpedFile = webper.webp(tinyedFile == null ? file : tinyedFile);
                            long webpedFileLength = webpedFile != null ? webpedFile.length() : -1;


                            if (tinyedFile != null || webpedFile != null) {
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
                                String compressionRatioStr = String.valueOf((1 - (denominator / startFileLength)) * 100);
                                System.out.println("denominator= "+denominator+" startFileLength="+startFileLength+" compressionRatioStr= "+compressionRatioStr);

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
                                        .append(compressionRatioStr)
                                        .append("|")
                                        .append("\n")
                                        .flush();
                            }


                            System.out.print(".");
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


}
