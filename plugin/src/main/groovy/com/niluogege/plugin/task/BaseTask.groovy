package com.niluogege.plugin.task

import org.gradle.api.DefaultTask

class BaseTask extends DefaultTask {

    BaseTask() {
        group "imageDeflated"
    }

    def log(String str){
        println("[imageDeflated] ${str}")
    }
}