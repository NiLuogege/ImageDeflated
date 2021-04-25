package com.niluogege.plugin.extension

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.artifacts.configurations.ConfigurationInternal

class WebpExtension extends BaseExtension {
    int quality = 75 //压缩质量 0-100
    long threshold = 1024 //1kb, 文件超过1kb 才进行压缩，对比发现 当 png 文件大小 大于 500 byte时才会是正压缩
    private String artifact
    private String path


    /**
     * 指定依赖 格式为：  '<groupId>:<artifactId>:<version>'
     */
    def setArtifact(String spec) {
        this.artifact = spec
    }

    /**
     * 指定本地路径
     */
    def setPath(String path) {
        this.path = path
    }

    String getArtifact() {
        return artifact
    }

    String getPath() {
        return path
    }

    /**
     * 加载依赖
     */
    void loadArtifact(Project project) {
        if (path == null && artifact != null) {
            //获取 implementation 这个 Configuration
            ConfigurationInternal config = project.configurations.getByName("implementation")
            //设置 可以被解析
            config.setCanBeResolved(true)

            //创建依赖
            def groupId, artifactId, version
            (groupId, artifactId, version) = this.artifact.split(":")
            def notation = [group  : groupId,
                            name   : artifactId,
                            version: version,
                            ext    : 'exe']

            //添加依赖
            Dependency dep = project.dependencies.add(config.getName(), notation)
            //获取依赖文件
            File file = config.fileCollection(dep).singleFile

            if (!file.canExecute() && !file.setExecutable(true)) {
                throw new GradleException("Cannot set ${file} as executable")
            }

            this.path = file.path
        }
    }
}