package com.niluogege.plugin

import com.android.build.gradle.tasks.MergeResources
import com.android.ide.common.resources.ResourcePreprocessor
import com.android.ide.common.resources.ResourceSet
import com.niluogege.plugin.extension.ImageDeflatedExtension
import com.niluogege.plugin.extension.TinyExtension
import com.niluogege.plugin.extension.WebpExtension
import com.niluogege.plugin.task.ImageDeflatedTask
import com.niluogege.plugin.utils.MD5Utils
import com.niluogege.plugin.utils.ReflectUtil
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.impldep.com.amazonaws.util.Md5Utils

import java.lang.reflect.Method


class ImageDeflatedPlugin implements Plugin<Project> {
    static final cacheDirName = "imageDeflated"
    static Project project

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

            TinyExtension tiny = imageDeflated.tiny
            WebpExtension webp = imageDeflated.webp
            println("tiny  tinyKey=${tiny?.key} open=${tiny?.open} ")
            println("webp  whiteList=${webp?.whiteList?.toString()} open=${webp?.open} ")
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
        String outputDirPath = mergeResourcesTask.getOutputDir().getAsFile().get().getAbsolutePath()
        String generatedPngsOutputDir = mergeResourcesTask.getGeneratedPngsOutputDir().getAbsolutePath()

        println("mergeResourcesTask=${mergeResourcesTask.getName()} \n" +
                "outputDir=${outputDirPath} \n" +
                "generatedPngsOutputDir=${generatedPngsOutputDir} \n" +
                "mergedNotCompiledResourcesOutputDirectory=${mergeResourcesTask.getMergedNotCompiledResourcesOutputDirectory().toString()} \n" +
//                    "mergedNotCompiledResourcesOutputDirectory=${mergeResourcesTask.getMergedNotCompiledResourcesOutputDirectory().getAsFile().get().getAbsolutePath()} \n" +
                "")

        mergeResourcesTask.doFirst {
            println("doFirst")

            clearCacheDirRoot()

            Class clazz = mergeResourcesTask.getClass()

            Method getPreprocessor = ReflectUtil.getDeclaredMethodRecursive(clazz, "getPreprocessor")
            Method getConfiguredResourceSets = ReflectUtil.getDeclaredMethodRecursive(clazz, "getConfiguredResourceSets", ResourcePreprocessor.class)

            ResourcePreprocessor preprocessor = (ResourcePreprocessor) getPreprocessor.invoke(mergeResourcesTask);
            List<ResourceSet> resourceSets = (List<ResourceSet>) getConfiguredResourceSets.invoke(mergeResourcesTask, preprocessor);

            for (ResourceSet resourceSet : resourceSets) {
                List<File> sourceFiles = resourceSet.getSourceFiles()
                System.out.println("rs= " + resourceSet.toString());
                for (File file : sourceFiles) {
                    if (file.exists()) {
                        FileUtils.copyDirectory(file, getCacheDir(resourceSet, file))
                    }
                }
            }

            // 有时间的话 生成的png  也可以 进行压缩 generatedPngsOutputDir,还是算了！！
//                Deflateder.deflate(outputDirPath, generatedPngsOutputDir, mergeResourcesTask)
        }

        mergeResourcesTask.doLast {
            println("doLast")
        }


        File publicFile = mergeResourcesTask.getPublicFile().isPresent() ? getPublicFile().get().getAsFile() : null;
        println("publicFile=$publicFile")
    }

    private static File getCacheDir(ResourceSet resourceSet, File file) {
        String newFileName

        if (file.parentFile != null) {
            newFileName = resourceSet.configName.replaceAll(":","_") + "-" + file.parentFile.name + "-" + MD5Utils.getMD5(file.absolutePath)
        } else {
            newFileName = resourceSet.configName.replaceAll(":","_") + "-" + MD5Utils.getMD5(file.absolutePath)
        }

        File newFile = new File(getCacheDirRoot(), newFileName)
        if (newFile.exists()) {
            FileUtils.deleteDirectory(newFile)

        }

        println("newFile=${newFile.getAbsolutePath()}")

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