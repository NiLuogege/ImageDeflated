package com.niluogege.plugin;


import com.niluogege.plugin.utils.CmdUtils;

public class Main {
    public static void main(String[] args) {
        try {
            String png ="C:\\Users\\niluogege\\Desktop\\2\\icon_qq.png";
            String webp ="C:\\Users\\niluogege\\Desktop\\2\\icon_qq.webp";
//            String cmd = "cwebp";
            String cmd = "D:\\softCacheData\\.gradle\\caches\\modules-2\\files-2.1\\com.niluogege.tools\\cwebp\\1.2.0\\ac1dbc746fce821c8c588fe9490fbcc97c90dda2\\cwebp-1.2.0";
            CmdUtils.runCmd(cmd, "-q", "80", png, "-o", webp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
