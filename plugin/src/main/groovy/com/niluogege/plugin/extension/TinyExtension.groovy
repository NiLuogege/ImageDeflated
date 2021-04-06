package com.niluogege.plugin.extension

class TinyExtension extends BaseExtension {
    String tinyKey//Tiny的KEy
    Long threshold = 1024 * 10 //10kb, 文件超过10kb 才进行压缩，tiny的免费压缩次数有限，需要珍惜使用
}