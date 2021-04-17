package com.niluogege.plugin;



import com.niluogege.plugin.utils.RegexUtils;


import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile(RegexUtils.convertToPatternString("white*"));
        boolean matches = pattern.matcher("white_bg_center.png").matches();
        System.out.println("matches="+matches);

    }
}
