package com.niluogege.plugin

import com.android.build.gradle.tasks.MergeResources
import com.android.ide.common.resources.ResourcePreprocessor
import com.android.ide.common.resources.ResourceSet
import com.niluogege.plugin.extension.ImageDeflatedExtension
import com.niluogege.plugin.extension.TinyExtension
import com.niluogege.plugin.extension.WebpExtension
import com.niluogege.plugin.task.ImageDeflatedTask
import com.niluogege.plugin.utils.ReflectUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.lang.reflect.Method


class ImageDeflatedPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

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

                Class clazz = mergeResourcesTask.getClass()

                Method getPreprocessor = ReflectUtil.getDeclaredMethodRecursive(clazz, "getPreprocessor")
                Method getConfiguredResourceSets = ReflectUtil.getDeclaredMethodRecursive(clazz, "getConfiguredResourceSets", ResourcePreprocessor.class)

                ResourcePreprocessor preprocessor = (ResourcePreprocessor) getPreprocessor.invoke(mergeResourcesTask);
                List<ResourceSet> resourceSets = (List<ResourceSet>) getConfiguredResourceSets.invoke(mergeResourcesTask, preprocessor);

                for (ResourceSet resourceSet : resourceSets) {
                    List<File> sourceFiles = resourceSet.getSourceFiles()
                    System.out.println("rs= " + resourceSet.toString());
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


    }
}