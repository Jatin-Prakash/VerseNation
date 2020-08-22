package com.marketplace.versenation.util;

public class Utility {

    public static boolean isNotNullOrEmpty(String str){
        if(str != null && !str.isEmpty())
            return true;
        return false;
    }
}
