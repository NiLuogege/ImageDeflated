# ImageDeflated
android 图片 瘦身 插件，支持 图片压缩，并自动转为 webp


## Tiny
文件超过10kb 才进行压缩，tiny的免费压缩次数有限，需要珍惜使用


## 包含的功能点记录
- assert 目录下的 图片压缩，但不转webp (flutter项目的图片在这里，还有一些大图)
- 输出压缩 和 转webp 的mapping表，看能不能搞成.md 表格样式 （tiny瘦身效果，webp瘦身效果，整体瘦身效果）
- 衡量一下 要不要开多个线程提高 压缩速度
- 

## 问题
####  为了最小的影响原有的项目，所以我们不能直接 dependOn  mergeResourcesTask 来修改  原始的图片，
解决方案，hook 摸一个task，在打包的时候 将图片文件的路径进行修改。

- 看看 processDebugResources (ProcessAndroidResources.class) 是不是 切入点
  - hook processDebugResources 修改其输入文件的路径？？
  - doFirst 压缩图片 并修改其输入文件路径？？



## 注意：
- gradle 插件版本 3.6.3


## 学到的
##### 1. merge***Resources Task 中 会使用 aapt2 将资源（图片，布局（xml）,values） 解析为一个扩展名为 .flat 的中间二进制文件。具体为
  - 位于 res/values/ 目录下 的 XML 资源文件（如 String 和 Style），会被解析为  *.arsc.flat 作为扩展名的资源表
  - 除 res/values/ 目录下的文件以外的其他所有文件都将转换为扩展名为 *.flat 的二进制 XML 文件
  - 所有 PNG 文件都会被压缩，并采用 *.png.flat 扩展名。如果选择不压缩 PNG，您可以在编译期间使用 --no-crunch 选项。

然后还要注意 AAPT2 输出的文件不是可执行文件，后面在链接阶段会使用这些二进制文件作为输入来生成 APK，
不是可执行文件的意思就是说，图片已经不是真正的图片了，已经不能直接通过图片被打开了


##### 2. hook  MergeResources 的  getConfiguredResourceSets() 方法 获取 输入资源路径
```

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
                    System.out.println("rs= " + resourceSet.toString());
                }

//                Deflateder.deflate(outputDirPath, generatedPngsOutputDir, mergeResourcesTask)
            }

            mergeResourcesTask.doLast {
                println("doLast")
            }
        }
        
        //输出
        rs= GeneratedResourceSet{androidx.core:core:1.3.2$Generated, sources=[D:\softCacheData\.gradle\caches\transforms-2\files-2.1\150ab35489a9920dd53304c7947ce1b1\core-1.3.2\res]}
        rs= ResourceSet{androidx.core:core:1.3.2, sources=[D:\softCacheData\.gradle\caches\transforms-2\files-2.1\150ab35489a9920dd53304c7947ce1b1\core-1.3.2\res]}
        rs= GeneratedResourceSet{androidx.appcompat:appcompat-resources:1.2.0$Generated, sources=[D:\softCacheData\.gradle\caches\transforms-2\files-2.1\e2e828aeb528ae4b9bb5f5f252ab3c3b\jetified-appcompat-resources-1.2.0\res]}
        rs= ResourceSet{androidx.appcompat:appcompat-resources:1.2.0, sources=[D:\softCacheData\.gradle\caches\transforms-2\files-2.1\e2e828aeb528ae4b9bb5f5f252ab3c3b\jetified-appcompat-resources-1.2.0\res]}
        rs= GeneratedResourceSet{androidx.appcompat:appcompat:1.2.0$Generated, sources=[D:\softCacheData\.gradle\caches\transforms-2\files-2.1\df20a55592f948e0f00115dd862e269d\appcompat-1.2.0\res]}
        rs= ResourceSet{androidx.appcompat:appcompat:1.2.0, sources=[D:\softCacheData\.gradle\caches\transforms-2\files-2.1\df20a55592f948e0f00115dd862e269d\appcompat-1.2.0\res]}
        rs= GeneratedResourceSet{androidx.constraintlayout:constraintlayout:2.0.4$Generated, sources=[D:\softCacheData\.gradle\caches\transforms-2\files-2.1\10464ef207cad4d8a3ea58f3282aaf3d\constraintlayout-2.0.4\res]}
        rs= ResourceSet{androidx.constraintlayout:constraintlayout:2.0.4, sources=[D:\softCacheData\.gradle\caches\transforms-2\files-2.1\10464ef207cad4d8a3ea58f3282aaf3d\constraintlayout-2.0.4\res]}
        rs= GeneratedResourceSet{main$Generated, sources=[E:\111work\code\code_me\myGitHub\ImageDeflated\app\src\main\res, E:\111work\code\code_me\myGitHub\ImageDeflated\app\build\generated\res\rs\debug, E:\111work\code\code_me\myGitHub\ImageDeflated\app\build\generated\res\resValues\debug]}
        rs= ResourceSet{main, sources=[E:\111work\code\code_me\myGitHub\ImageDeflated\app\src\main\res, E:\111work\code\code_me\myGitHub\ImageDeflated\app\build\generated\res\rs\debug, E:\111work\code\code_me\myGitHub\ImageDeflated\app\build\generated\res\resValues\debug]}
        rs= GeneratedResourceSet{debug$Generated, sources=[E:\111work\code\code_me\myGitHub\ImageDeflated\app\src\debug\res]}
        rs= ResourceSet{debug, sources=[E:\111work\code\code_me\myGitHub\ImageDeflated\app\src\debug\res]}




```
我们可以 压缩完原有图片后 再修改  ResourceSet 中的路径 为压缩文件的路径 这样就 完成了hook


##### 3. 反射task 的时候 需要在 gradle 中，不能再 java中，因为拿不到具体的类名
