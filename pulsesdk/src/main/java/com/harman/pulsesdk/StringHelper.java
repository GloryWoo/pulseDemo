package com.harman.pulsesdk;

/**
 * Created by lee on 15/11/17.
 */
public class StringHelper {
    public static boolean IsNullOrEmpty(String str)
    {
        if(str==null || str.isEmpty()) return true;
        return false;
    }
}
