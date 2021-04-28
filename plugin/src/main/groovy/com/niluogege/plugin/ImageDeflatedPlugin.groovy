package com.niluogege.plugin

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.MergeResources
import com.android.ide.common.resources.ResourcePreprocessor
import com.android.ide.common.resources.ResourceSet
import com.niluogege.plugin.bean.TinyConfig
import com.niluogege.plugin.bean.WebpConfig
import com.niluogege.plugin.extension.ImageDeflatedExtension
import com.niluogege.plugin.extension.TinyExtension
import com.niluogege.plugin.extension.WebpExtension
import com.niluogege.plugin.utils.MD5Utils
import com.niluogege.plugin.utils.ReflectUtil
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.lang.reflect.Field
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
                stickmergeResourcesTask(project, variant)
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
            webp.loadArtifact(project)
            webpConfig.open = webp.open
            webpConfig.setWhiteList(webp.whiteList)
            webpConfig.path = webp.path
            webpConfig.quality = webp.quality
            webpConfig.threshold = webp.threshold
        }
    }


    private static void stickmergeResourcesTask(Project project, BaseVariant variant) {
        MergeResources mergeResourcesTask = variant.mergeResourcesProvider.get()
        hookMergeResourcesTask(mergeResourcesTask)
    }

    private static void hookMergeResourcesTask(MergeResources mergeResourcesTask) {
        mergeResourcesTask.doFirst {
            println("ImageDeflatedPlugin mergeResourcesTask doFirst")

            clearCacheDirRoot()

            List<ResourceSet> resourceSets = getResourceSets(mergeResourcesTask)

            List<File> waitDeflateDirs = getWaitDeflateDirs(resourceSets)
            println("替换 resourceSets 后= ${resourceSets.toString()}")

            Deflateder.deflate(waitDeflateDirs, getCacheDirRoot().getAbsolutePath(), tinyConfig, webpConfig)
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

    private static void setResourceSets(MergeResources mergeResourcesTask, List<ResourceSet> resourceSets) {
        Class clazz = mergeResourcesTask.getClass()
        Field processedInputs = ReflectUtil.getDeclaredFieldRecursive(clazz, "processedInputs")
        processedInputs.set(mergeResourcesTask, resourceSets)
    }

    private static List<File> getWaitDeflateDirs(List<ResourceSet> resourceSets) {
        HashSet<File> waitDeflateDirs = new HashSet<>()

        //将 resourceSets 中的res 文件路径 替换为 我们自己的
        for (ResourceSet resourceSet : resourceSets) {

            List<File> sourceFiles = resourceSet.getSourceFiles()
            if (isModule(sourceFiles)) {//是module 直接 转
                waitDeflateDirs.addAll(sourceFiles)
            } else if (isAppModule(resourceSet.configName)) { //是主项目 copy 到缓存目录 再转
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
            } else { //aar 项目先不做处理

            }
        }
        return  new ArrayList<File>(waitDeflateDirs)
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

    //主项目的 configName 是 main 或者  main$Generated
    //是否是主项目
    private static boolean isAppModule(String configName) {
        return configName.contains("main")
    }


    ////module 的 ResourceSet 中只有一条路径 并且都在  ...build\intermediates\packaged_res... 下
    //是否是 module 依赖
    private static boolean isModule(List<File> sourceFiles) {
        String resourceSetSourcesPath = sourceFiles.first()
        boolean isModule = resourceSetSourcesPath.contains("intermediates\\packaged_res")
        println("isModule=$isModule resourceSetSourcesPath=$resourceSetSourcesPath")
        return isModule

    }
}