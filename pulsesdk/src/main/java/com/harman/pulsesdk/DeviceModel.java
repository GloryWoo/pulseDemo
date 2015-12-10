package com.harman.pulsesdk;

/**
 * Created by lee on 15/11/17.
 */
public class DeviceModel {
    public int deviceIndex;//设备index
    public String DeviceName;//设备名字
    public String product;//产品id
    public String model;//model id
    public int BatteryPower;//电量
    public int LinkedDeviceCount;//link在一起的设备数量
    public int ActiveChannel;//当前声道
    public int AudioSource;//当前音频源
    public String Mac;//mac地址

    public String toString(){
        return "id:"+deviceIndex+",name:"+DeviceName+",linked count:"+LinkedDeviceCount
                +", channel:"+ActiveChannel+",source"+AudioSource;
    }
}
