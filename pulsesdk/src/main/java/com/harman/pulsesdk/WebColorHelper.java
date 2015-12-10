package com.harman.pulsesdk;

/**
 * Created by lee on 15/11/30.
 */
public class WebColorHelper {
    public static byte rgbToWeb216(byte r, byte g, byte b) {
        int result = r / 0x33 * 0x24 + (g / 0x33 * 0x06) + (b / 0x33);
        byte ret = (byte)result;
        return ret;
    }

    public static byte rgbToWeb216(PulseColor color) {
        int result = color.red / 0x33 * 0x24 + (color.green / 0x33 * 0x06) + (color.blue / 0x33);
        byte ret = (byte)result;
        return ret;
    }

    public static PulseColor web216ToRGB(byte webIndex)
    {
        int index = webIndex;
        if (index > 0xd7) {
            index = 0xd7;
        }
        PulseColor color = new PulseColor();
        color.red = (byte)(index / 0x24 * 0x33);
        index = index % 0x24;
        color.green = (byte)(index / 0x06 * 0x33);
        index = index % 0x06;
        color.blue = (byte)(index * 0x33);
        return color;
    }

    public static int RGBToWeb216Index(PulseColor color){
        int safeColorValue = 0;
        int[] BGRValue = {color.blue&0xff, color.green&0xff, color.red&0xff};
        //0 51 102 153 204 255
        for (int i = 0; i < 3; i++){
            if (Math.abs(BGRValue[i]-51) < 25){
                safeColorValue += Math.pow(6, i);
            }else if (Math.abs(BGRValue[i]-102) < 25){
                safeColorValue += Math.pow(6, i)*2;
            }else if (Math.abs(BGRValue[i]-153) < 25){
                safeColorValue += Math.pow(6, i)*3;
            }else if (Math.abs(BGRValue[i]-204) < 25){
                safeColorValue += Math.pow(6, i)*4;
            }else if (Math.abs(BGRValue[i]-255) < 25){
                safeColorValue += Math.pow(6, i)*5;
            }
        }
        return safeColorValue;
    }
}
