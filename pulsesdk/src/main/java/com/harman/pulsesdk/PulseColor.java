package com.harman.pulsesdk;

/**
 * Created by lee on 15/11/24.
 */
public class PulseColor {
    public byte red;
    public byte green;
    public byte blue;

    public PulseColor(){}

    public PulseColor(int r, int g, int b){
        red = (byte)(r&0xff);
        green = (byte)(g&0xff);
        blue = (byte)(b&0xff);
    }
}
