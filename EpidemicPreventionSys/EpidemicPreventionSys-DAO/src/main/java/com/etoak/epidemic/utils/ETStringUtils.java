package com.etoak.epidemic.utils;

public class ETStringUtils {
    public static boolean isEmpty(String name){
        return (name==null || name.length()==0);
    }
    public static boolean isNotEmpty(String name) {
        return !isEmpty(name);
    }
}
