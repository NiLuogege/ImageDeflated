package com.niluogege.plugin

import com.niluogege.plugin.extension.ImageDeflatedExtension
import com.niluogege.plugin.extension.TinyExtension
import com.niluogege.plugin.extension.WebpExtension
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
            def imageDeflated = project.extensions.imageDeflated
            def imageDeflated2 = project.imageDeflated


            print("android=$android imageDeflated=$imageDeflated imageDeflated2=$imageDeflated2")
        }
    }

}