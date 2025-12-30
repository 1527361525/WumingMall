package com.wlyykf.mall.utils;

import org.springframework.util.StringUtils;

import java.util.Random;

public class StringUtil {

    /**
     * 路径是否合法
     * @param path 路径
     * @return 是否合法
     */
    public static boolean pathIsOk(String path){
        if(StringUtils.isEmpty(path)){
            return true;
        }
        if(path.contains("../") || path.contains("..\\")){
            return false;
        }
        return true;
    }

    /**
     * 获取文件后缀
     * @param fileName 文件名
     * @return 文件后缀
     */
    public static String getFileSuffix(String fileName){
        if(StringUtils.isEmpty(fileName) || !fileName.contains(".")){
            return null;
        }
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return suffix;
    }

    /**
     * 获取随机字符串
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }
}
