package com.niluogege.plugin;

import com.android.build.gradle.internal.LoggingUtil;
import com.android.build.gradle.tasks.MergeResources;
import com.android.ide.common.resources.ResourcePreprocessor;
import com.android.ide.common.resources.ResourceSet;
import com.tinify.Source;
import com.tinify.Tinify;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class Deflateder {

    private Deflateder() {
    }

    private static final class Inner {
        private static final Deflateder instance = new Deflateder();
    }

    public static Deflateder getInstance() {

        return Inner.instance;
    }

    private boolean isInit = false;

    public Deflateder init() {
        Tinify.setKey("Jd3rDN5x3R9QClQ1qS8zPNTqCw0dc48L");
        isInit = true;
        return this;
    }

    public void deflate(String outputDirPath, String generatedPngsOutputDir, Object mergeResources) {

        try {


            Class clazz = Class.forName("com.android.build.gradle.tasks.MergeResources_Decorated");
//            Class clazz = mergeResources.getClass();



            Method getPreprocessor = clazz.getDeclaredMethod("getPreprocessor");
            getPreprocessor.setAccessible(true);

            Method getConfiguredResourceSets = clazz.getDeclaredMethod("getConfiguredResourceSets", ResourcePreprocessor.class);
            getConfiguredResourceSets.setAccessible(true);

            ResourcePreprocessor preprocessor = (ResourcePreprocessor) getPreprocessor.invoke(mergeResources);
            List<ResourceSet> resourceSets = (List<ResourceSet>) getConfiguredResourceSets.invoke(mergeResources, preprocessor);

            for (ResourceSet resourceSet : resourceSets) {
                System.out.println("rs= " + resourceSet.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void tiny() {
//        File outputDir = new File(outputDirPath);
//        if (outputDir.exists()) {
//            for (File file : FileUtils.listFilesAndDirs(outputDir, TrueFileFilter.TRUE, TrueFileFilter.TRUE)) {
//
//                System.out.println(file.getName());
//
//                if (file.getName().endsWith(".png.flat")) {
//                    String filePath = file.getAbsolutePath();
//
//
//                    Source source = Tinify.fromFile(filePath);
//                    source.toFile(filePath.replace(".flat",""));
//                }
//            }
//        }
    }
}
