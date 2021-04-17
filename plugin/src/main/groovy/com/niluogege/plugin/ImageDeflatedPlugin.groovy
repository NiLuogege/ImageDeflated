package com.niluogege.plugin

import com.android.build.gradle.tasks.MergeResources
import com.android.ide.common.resources.ResourcePreprocessor
import com.android.ide.common.resources.ResourceSet
import com.niluogege.plugin.bean.TinyConfig
import com.niluogege.plugin.bean.WebpConfig
import com.niluogege.plugin.extension.ImageDeflatedExtension
import com.niluogege.plugin.extension.TinyExtension
import com.niluogege.plugin.extension.WebpExtension
import com.niluogege.plugin.task.ImageDeflatedTask
import com.niluogege.plugin.utils.MD5Utils
import com.niluogege.plugin.utils.ReflectUtil
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.lang.reflect.Method


class ImageDeflatedPlugin implements Plugin<Project> {
    static final cacheDirName = "imageDeflated"
    static Project project
    static TinyConfig tinyConfig = new TinyConfig()
    static WebpConfig webpConfig = new WebpConfig()

    @Override
    void apply(Project project) {
        ImageDeflatedPlugin.project = project

        project.extensions.create("imageDeflated", ImageDeflatedExtension)
        project.imageDeflated.extensions.create("tiny", TinyExtension)
        project.imageDeflated.extensions.create("webp", WebpExtension)

        project.afterEvaluate {

            def android = project.extensions.android
            ImageDeflatedExtension imageDeflated = project.extensions.imageDeflated


            android.applicationVariants.all { variant ->
                createTask(project, variant)
            }

            android.buildTypes.all { buildType ->
                createTask(project, buildType)
            }

            extension2Config(imageDeflated)

        }


    }

    private static void extension2Config(ImageDeflatedExtension imageDeflated) {
        TinyExtension tiny = imageDeflated.tiny
        WebpExtension webp = imageDeflated.webp


        if (tiny != null) {
            tinyConfig.key = tiny.key
            tinyConfig.compressionsCountPerMonth = tiny.compressionsCountPerMonth
            tinyConfig.threshold = tiny.threshold
            tinyConfig.open = tiny.open
            tinyConfig.setWhiteList(tiny.whiteList)
        }

        if (webp != null) {
            webpConfig.open = webp.open
            webpConfig.setWhiteList(webp.whiteList)
        }
    }


    private static void createTask(Project project, variant) {
        def variantName = variant.name.capitalize()
        def taskName = "imageDeflate$variantName"
        if (project.tasks.findByName(taskName) == null) {
            def imageDeflatedTask = project.tasks.create(taskName, ImageDeflatedTask)
            MergeResources mergeResourcesTask = variant.mergeResourcesProvider.get()
            mergeResourcesTask.dependsOn(imageDeflatedTask)

            hookMergeResourcesTask(mergeResourcesTask)
        }


    }

    private static void hookMergeResourcesTask(MergeResources mergeResourcesTask) {
        mergeResourcesTask.doFirst {
            println("ImageDeflatedPlugin mergeResourcesTask doFirst")

            clearCacheDirRoot()

            List<ResourceSet> resourceSets = getResourceSets(mergeResourcesTask)

            List<File> waitDeflateDirs = changeSourceFile(resourceSets)

            Deflateder.deflate(waitDeflateDirs, tinyConfig, webpConfig)
        }

        mergeResourcesTask.doLast {
            println("ImageDeflatedPlugin mergeResourcesTask doLast")
        }
    }


    private static List<ResourceSet> getResourceSets(MergeResources mergeResourcesTask) {
        Class clazz = mergeResourcesTask.getClass()
        Method getPreprocessor = ReflectUtil.getDeclaredMethodRecursive(clazz, "getPreprocessor")
        Method getConfiguredResourceSets = ReflectUtil.getDeclaredMethodRecursive(clazz, "getConfiguredResourceSets", ResourcePreprocessor.class)
        ResourcePreprocessor preprocessor = (ResourcePreprocessor) getPreprocessor.invoke(mergeResourcesTask);
        List<ResourceSet> resourceSets = (List<ResourceSet>) getConfiguredResourceSets.invoke(mergeResourcesTask, preprocessor);
        return resourceSets
    }

    private static List<File> changeSourceFile(List<ResourceSet> resourceSets) {
        List<File> waitDeflateDirs = new ArrayList<>();
        System.out.println("替换前 resourceSet = " + resourceSets.toString())

        //将 resourceSets 中的res 文件路径 替换为 我们自己的
        for (ResourceSet resourceSet : resourceSets) {
            List<File> sourceFiles = resourceSet.getSourceFiles()
            List<File> newSourceFiles = new ArrayList<>();
            for (File file : sourceFiles) {
                if (file.exists()) {
                    File resCacheDir = getCacheDir(resourceSet, file)
                    newSourceFiles.add(resCacheDir)
                    FileUtils.copyDirectory(file, resCacheDir)
                }
            }
            sourceFiles.clear()
            sourceFiles.addAll(newSourceFiles)
            waitDeflateDirs.addAll(newSourceFiles)
        }
        System.out.println("替换后 resourceSet = " + resourceSets.toString())
        return waitDeflateDirs
    }

    private static File getCacheDir(ResourceSet resourceSet, File file) {
        String newFileName

        if (file.parentFile != null) {
            newFileName = resourceSet.configName.replaceAll(":", "_") + "-" + file.parentFile.name + "-" + MD5Utils.getMD5(file.absolutePath)
        } else {
            newFileName = resourceSet.configName.replaceAll(":", "_") + "-" + MD5Utils.getMD5(file.absolutePath)
        }

        File newFile = new File(getCacheDirRoot(), newFileName)
        if (newFile.exists()) {
            FileUtils.deleteDirectory(newFile)

        }

//        System.out.println("res 文件替换 srcFile= " + file.getAbsolutePath() + " destFile= " + newFile.getAbsolutePath())

        return newFile
    }

    private static File getCacheDirRoot() {
        File cacheDirRoot = new File(project.buildDir, cacheDirName)
        if (!cacheDirRoot.exists()) {
            cacheDirRoot.mkdirs()
        }
        return cacheDirRoot
    }

    private static void clearCacheDirRoot() {
        File cacheDirRoot = new File(project.buildDir, cacheDirName)
        if (cacheDirRoot.exists()) {
            FileUtils.deleteDirectory(cacheDirRoot)
        }
    }
}