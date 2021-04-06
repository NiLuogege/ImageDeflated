package com.niluogege.plugin

import com.niluogege.plugin.extension.ImageDeflatedExtension
import com.niluogege.plugin.extension.TinyExtension
import com.niluogege.plugin.extension.WebpExtension
import com.niluogege.plugin.task.ImageDeflatedTask
import org.gradle.api.Plugin
import org.gradle.api.Project


class ImageDeflatedPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("imageDeflated", ImageDeflatedExtension)
        project.extensions.add("tiny", TinyExtension)
        project.extensions.add("webp", WebpExtension)

        project.afterEvaluate {

            def android = project.extensions.android
            ImageDeflatedExtension imageDeflated = project.extensions.imageDeflated
            ImageDeflatedExtension imageDeflated2 = project.imageDeflated


            android.applicationVariants.all { variant ->
                createTask(project, variant)
            }

            android.buildTypes.all { buildType ->
                createTask(project, buildType)
            }

            println("imageDeflated=${imageDeflated.toString()} tiny=${imageDeflated.tiny} open=${imageDeflated.tiny?.open} ")
            println("imageDeflated2=${imageDeflated2.toString()} tiny=${imageDeflated2.tiny} open=${imageDeflated2.tiny?.open} ")
        }


    }


    private static void createTask(Project project, variant) {
        def variantName = variant.name.capitalize()
        def taskName = "imageDeflate$variantName"
        if (project.tasks.findByName(taskName) == null) {
            def imageDeflatedTask = project.tasks.create(taskName, ImageDeflatedTask)
            def mergeResourcesTask = variant.mergeResourcesProvider.get()
            mergeResourcesTask.dependsOn(imageDeflatedTask)

            print("mergeResourcesTask=${mergeResourcesTask.getName()}")
        }
    }
}