package com.niluogege.plugin.extension


class TinyExtension extends BaseExtension {
    String key//Tiny的KEy
    long threshold = 1024 * 10 //10kb, 文件超过10kb 才进行压缩，tiny的免费压缩次数有限，需要珍使用
    int compressionsCountPerMonth = 500 // 每月的压缩次数，tiny每月免费500 张


}