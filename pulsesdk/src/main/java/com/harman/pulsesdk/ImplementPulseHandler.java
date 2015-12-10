package com.harman.pulsesdk;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.os.Looper;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lee on 15/11/17.
 */
public class ImplementPulseHandler implements PulseHandlerInterface {

    private Activity mActivity;

    private PulseNotifiedListener pulseNotifiedListener=null;
    private Boolean bConnectMasterDevice = false;
    private Boolean bGetDeviceInfo = false;
    private BluetoothSocket mSocket=null;
    private InputStream is=null;
    private OutputStream os =null;
    private BluetoothDevice bluetoothDevice=null;
    private Lock lock = new ReentrantLock();
    private Condition conditionA = lock.newCondition();
    private Lock lockSetDev = new ReentrantLock();
    private Condition condSetDev = lockSetDev.newCondition();
    private DeviceModel[] device = new DeviceModel[2];

    private PulseThemePattern LEDPattern = PulseThemePattern.PulseTheme_Firework;
    private int SetDevInfoACK = 0;
    private PulseColor captureColor=new PulseColor();

    public ImplementPulseHandler(){
        device[0] = new DeviceModel();
        device[1] = new DeviceModel();
    }

    public void registerPulseNotifiedListener(PulseNotifiedListener listener){pulseNotifiedListener = listener;}

    public Boolean isConnectMasterDevice(){
        return bConnectMasterDevice;
    }

    public Boolean ConnectMasterDevice(Activity activity) {
        mActivity = activity;
        boolean bBluetoothEnabled= BluetoothHelper.isBluetoothEnabled();
        if(!bBluetoothEnabled) {
            Log.i("hello", "!bBluetoothEnabled");
            return false;
        }

        if (bConnectMasterDevice){
            return true;
        }

        BluetoothHelper.getA2DPProfileProxy(activity, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                List<BluetoothDevice> deviceList = proxy.getConnectedDevices();
                if (deviceList.size() == 0) {
                    Log.i("hello", "deviceList.size()==0");
                } else if (deviceList.size() == 1) {
                    bluetoothDevice = deviceList.get(0);

//                    Log.i("hello", "device mac:" + bluetoothDevice.getAddress());
//                    device.DeviceName = bluetoothDevice.getName();
//                    device.Mac = bluetoothDevice.getAddress().toLowerCase().replace(":", "");

                    mSocket = BluetoothHelper.connect(bluetoothDevice);
                    if (mSocket != null && mSocket.isConnected()) {
                        try {
                            is = mSocket.getInputStream();
                            os = mSocket.getOutputStream();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new MyMessageThread().start();

                        SppCmdHelper.init(os);
                        SppCmdHelper.reqDeviceSoftwareVersion();

                        bConnectMasterDevice = true;
                        pulseNotifiedListener.onConnectMasterDevice();
                    }

                } else if (deviceList.size() > 1) {
                    Log.i("hello", "device:" + deviceList.size());
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                Log.i("hello", "a2dp onServiceDisconnected");
                bConnectMasterDevice = false;
                pulseNotifiedListener.onDisconnectMasterDevice();
            }
        });
        return true;
    }

    public Boolean RequestDeviceInfo() {
        if (!bConnectMasterDevice){
            return false;
        }
        return true;
    }

    public Boolean GetLEDPattern(){
        if (!bConnectMasterDevice){
            return false;
        }
        SppCmdHelper.reqLedPatternInfo();
        return true;
    }

    public Boolean SetDeviceName(String devName, int devIndex) {
        if (!bConnectMasterDevice){
            return false;
        }
        SppCmdHelper.setDeviceName(devName, devIndex);
        device[devIndex].DeviceName = devName;
        return true;
    }
    public Boolean SetDeviceChannel(int devIndex, int channel) {
        if (!bConnectMasterDevice){
            return false;
        }
        SppCmdHelper.setDeviceChannel(devIndex, channel);
        device[devIndex].ActiveChannel = channel;
        return true;
    }
    public Boolean SetLEDPattern(PulseThemePattern pattern) {
        if (!bConnectMasterDevice){
            return false;
        }
        SppCmdHelper.setLedPattern(pattern.ordinal());
        LEDPattern = pattern;
        return true;
    }
    public Boolean SetBrightness(int brightness) {
        if (!bConnectMasterDevice){
            return false;
        }
        SppCmdHelper.SetBrightness(brightness);
        return true;
    }
    public Boolean SetBackgroundColor(PulseColor color, boolean inlcudeSlave) {
        if (!bConnectMasterDevice){
            return false;
        }
        int idx = WebColorHelper.RGBToWeb216Index(color);
        SppCmdHelper.SetBackgroundColor(idx, inlcudeSlave);
        return true;
    }
    public Boolean SetColorImage(PulseColor[] bitmap){
        if (!bConnectMasterDevice){
            return false;
        }
        int[] idxPixel = new int[99];
        for(int i=0; i<99;i++){
            idxPixel[i] = WebColorHelper.RGBToWeb216Index(bitmap[i]);
        }
        SppCmdHelper.setColorImage(idxPixel);
        return true;
    }
    public Boolean SetCharacterPattern(char character, PulseColor foreground, PulseColor background, boolean inlcudeSlave){
        if (!bConnectMasterDevice){
            return false;
        }
        int foregroundColor = WebColorHelper.RGBToWeb216Index(foreground);
        int backgroundColor = WebColorHelper.RGBToWeb216Index(background);
        SppCmdHelper.SetCharacterPattern(character, foregroundColor, backgroundColor, inlcudeSlave);
        return true;
    }

    public Boolean CaptureColorFromColorPicker() {
        if (!bConnectMasterDevice){
            return false;
        }
        SppCmdHelper.reqColorFromColorPicker();
        return true;
    }

    public void PropagateLedPattern() {
        if (!bConnectMasterDevice){
            return;
        }
        SppCmdHelper.PropagateLedPattern();
    }

    public void GetMicrophoneSoundLevel(){
        if (!bConnectMasterDevice){
            return;
        }
        SppCmdHelper.GetMicrophoneSoundLevel();
    }

    public void SetLEDAndSoundFeedback(int devIndex){
        SppCmdHelper.reqLEDAndSoundFeedback(devIndex);
    }

    private class MyMessageThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true && !Thread.interrupted()) {
                if(is==null) return;
                byte[] msg = new byte[1024];
                try {
                    int readed = is.read(msg);
                    byte[] buffer=new byte[readed];
                    for(int i=0;i<readed;i++) {
                        buffer[i]=msg[i];
                    }
                    processMessage(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processMessage(byte[] buffer) {
        String result = HexHelper.encodeHexStr(buffer);
        Log.i("my_msg", result);

        if (buffer[0] == SppConstant.RET_CMD_ACK[0] && buffer[1] == SppConstant.RET_CMD_ACK[1]) {
            if (buffer[3] == SppConstant.RET_SET_DEV_ACK){
                SetDevInfoACK = buffer[4];
            }
        }else if (buffer[0] == SppConstant.RET_CMD_DEV_INFO[0] && buffer[1] == SppConstant.RET_CMD_DEV_INFO[1]) {
            parseDevInfo(buffer);
        }else if (buffer[0] == SppConstant.RET_LED_PATTERN_CHANGE[0] && buffer[1] == SppConstant.RET_LED_PATTERN_CHANGE[1]){
            pulseNotifiedListener.onLEDPatternChanged(PulseThemePattern.values()[buffer[2]]);
        }else if (buffer[0] == SppConstant.RET_SOUND_EVENT[0] && buffer[1] == SppConstant.RET_SOUND_EVENT[1]){
            pulseNotifiedListener.onSoundEvent(buffer[2]);
        }else if (buffer[0] == SppConstant.RET_LED_PATTERN[0] && buffer[1] == SppConstant.RET_LED_PATTERN[1]){
            if(buffer[3] >= 0 && buffer[3] < PulseThemePattern.values().length)
                LEDPattern = PulseThemePattern.values()[buffer[3]];
            else
                LEDPattern = null;
            pulseNotifiedListener.onRetGetLEDPattern(LEDPattern);
        }else if (buffer[0] == SppConstant.RET_COLOR_PICKER[0] && buffer[1] == SppConstant.RET_COLOR_PICKER[1]){
            captureColor.blue = buffer[2];
            captureColor.green = buffer[3];
            captureColor.red = buffer[4];
            pulseNotifiedListener.onRetCaptureColor(captureColor);
        }
    }

    private void parseDevInfo(byte[] buffer){
        int pos = 2;
        int msgLen = buffer[pos];
        pos += 1;
        int devIndex = buffer[pos];
        device[devIndex].deviceIndex = devIndex;
        pos += 1;
//CMD_GET_DEVICE_NAME c1
        if (buffer[pos] == SppConstant.RET_DEVICE_NAME){
            pos += 1;
            int len = buffer[pos];
            pos += 1;
            char[] nameChar = new char[len];
            for(int i = 0; i< len; i++){
                nameChar[i] = (char)buffer[pos+i];
            }
            pos += len;
            device[devIndex].DeviceName = String.copyValueOf(nameChar);
        }
        if (pos>=msgLen){
            return;
        }
//CMD_GET_PID 42
        if (buffer[pos] == SppConstant.RET_PID) {
            pos += 1;
            byte[] pidbyte = new byte[2];
            for (int i = 0; i < 2; i++) {
                pidbyte[i] = buffer[pos + i];
            }
            device[devIndex].product = getPID(pidbyte);
            pos += 2;
        }
        if (pos>=msgLen){
            return;
        }
//CMD_GET_MID) != -1) {//43
        if (buffer[pos] == SppConstant.RET_MID) {
            pos += 1;
            device[devIndex].model = getMID(buffer[pos]);
            pos += 1;
        }
        if (pos>=msgLen){
            return;
        }
//CMD_GET_BATTERY_STATUS) != -1) {//44
        if (buffer[pos] == SppConstant.RET_BATTERY_STATUS) {
            pos += 1;
            device[devIndex].BatteryPower = buffer[pos];
            pos += 1;
            int state = device[devIndex].BatteryPower>>7;
            if (state == 1) {
                Log.i("hello", "charging: " + device[devIndex].BatteryPower);
            } else {
                Log.i("hello", "not charging: " + device[devIndex].BatteryPower);
            }
        }
        if (pos>=msgLen){
            return;
        }
//CMD_GET_LINKED_DEVICE_COUNT)) {//45
        if (buffer[pos] == SppConstant.RET_LINKED_DEVICE_COUNT) {
            pos += 1;
            device[devIndex].LinkedDeviceCount = buffer[pos];
            pos += 1;
        }
        if (pos>=msgLen){
            return;
        }
//CMD_GET_ACTIVE_CHANNEL)!=-1) {//46
        if (buffer[pos] == SppConstant.RET_ACTIVE_CHANNEL) {
            pos += 1;
            device[devIndex].ActiveChannel = buffer[pos];
            pos += 1;
        }
        if (pos>=msgLen){
            return;
        }
//CMD_GET_AUDIO_SOURCE)!=-1) {//47
        if (buffer[pos] == SppConstant.RET_AUDIO_SOURCE) {
            pos += 1;
            device[devIndex].AudioSource = buffer[pos];
            pos += 1;
        }
        if (pos>=msgLen){
            return;
        }
//CMD_GET_DEVICE_MAC)!=-1) {//48
        if (buffer[pos] == SppConstant.RET_DEVICE_MAC) {
            pos += 1;
            byte[] macbyte = new byte[6];
            for(int i = 0; i< 6; i++){
                macbyte[i] = buffer[pos+i];
            }
            device[devIndex].Mac = HexHelper.encodeHexStr(macbyte);
        }

        Log.i("hello", "name : " + device[devIndex].DeviceName);
        Log.i("hello", "PID : " + device[devIndex].product);
        Log.i("hello", "MID : " + device[devIndex].model);
        Log.i("hello", "battery: " + device[devIndex].BatteryPower);
        Log.i("hello", "get linked device count: " + device[devIndex].LinkedDeviceCount);
        Log.i("hello", "get active channel : " + device[devIndex].ActiveChannel);
        Log.i("hello", "get audio source : " + device[devIndex].AudioSource);
        Log.i("hello", "mac : " + device[devIndex].Mac);

        bGetDeviceInfo = true;
//            retGetDeviceInfo();
    }

    private void getDeviceInfo() {
        lock.lock();
        try {
            try {
                SppCmdHelper.reqLedPatternInfo();
                SppCmdHelper.reqDevInfo();
                conditionA.awaitNanos(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " getDeviceInfo");
        } finally {
            lock.unlock();
        }
    }

    private void retGetDeviceInfo() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " retGetDeviceInfo");
            conditionA.signal();
        } finally {
            lock.unlock();
        }
    }

    private void setDeviceInfo() {
        lockSetDev.lock();
        try {
            try {
                condSetDev.awaitNanos(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " setDeviceInfo");
        } finally {
            lockSetDev.unlock();
        }
    }

    private void retSetDeviceInfo() {
        lockSetDev.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " retSetDeviceInfo");
            condSetDev.signal();
        } finally {
            lockSetDev.unlock();
        }
    }

    private String getPID(byte[] pid){
        if(pid[0] == 0x00 && pid[1] == 0x26){
            return "JBL Pulse 2";
        }else{
            return "";
        }
    }

    private String getMID(byte mid){
        if(mid == 0x01){
            return "black";
        }else if(mid == 0x02){
            return "white";
        }else{
            return "";
        }
    }
/*
    private byte RGBToSafeColor(PulseColor color){
        byte safeColor = 0x00;
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
        safeColor = (byte)safeColorValue;
        return safeColor;
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            pulseCommandService = null;
            Log.d("hello", "in onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pulseCommandService = ((PulseCommandService.MyBinder)(service)).getService();
            Log.d("hello", "in onServiceConnected");
        }
    };
    private void connection(){
        Log.d("hello", "connecting.....");
        Intent intent = new Intent("org.allin.android.bindService");
        mActivity.bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }
*/

//////////////////////////////////////////////////////////////////////
/*
    private void processMessage1(byte[] buffer) {
        String result = HexHelper.encodeHexStr(buffer);
        Log.i("my_msg", result);
        String[] cmds = result.split("aa");
        List<String> cmdList = getCmdList(cmds);
        for (String cmd : cmdList) {
            filterMessage(cmd);
        }

        SppCmdHelper.bleCmdType = SppCmdHelper.SppCmdType.cmd_none;
    }

    private List<String> getCmdList(String[]cmds) {
        List<String> cmdList=new ArrayList<String>();
        for(String item:cmds) {
            if(StringHelper.IsNullOrEmpty(item)) continue;
            cmdList.add("aa"+item);
        }
        if(cmdList.size()==1) return cmdList;
        List<String> cmdList2=new ArrayList<String>();
        for(int i=0;i<cmdList.size();i++) {
            String cmd=cmdList.get(i);
            String strCmdLen=cmd.substring(4, 6);
            int cmdLen=Integer.valueOf(strCmdLen,16);
            if(cmd.length()==6+cmdLen*2) {
                cmdList2.add(cmd);
                cmdList.remove(cmd);
                i--;
            }
            else {
                String cmd2=cmdList.get(i+1);
                String newCmd=cmd+cmd2;
                cmdList2.add(newCmd);
                cmdList.remove(cmd);
                cmdList.remove(cmd2);
                i--;
            }
        }

        return cmdList2;
    }

    private void filterMessage(String msg) {
        if (msg.indexOf(SppConstant.RET_GET_DEV_INFO) != -1) {
            String devInfo = msg.replace(SppConstant.RET_GET_DEV_INFO, "");
            int pos = 0;
            String msgLen = devInfo.substring(pos, 2);
            pos += 2;
            String token = devInfo.substring(pos, 4);
            pos += 2;
//CMD_GET_DEVICE_NAME c1
            pos += 2;
            String lenth = devInfo.substring(pos, pos + 2);
            pos += 2;
            int len = Integer.parseInt(lenth);
            char[] name = new char[len];
            for(int i = 0; i< len; i++){
                String str = devInfo.substring(pos+i*2, pos+i*2+2);
                int ch = Integer.valueOf(str,16);
                name[i] = (char)ch;
            }
            pos += len*2;
            Log.i("hello", "name : " + name);
//CMD_GET_PID 42
            pos += 2;
            String str = devInfo.substring(pos, pos+4);
            int PID = Integer.valueOf(str, 16);
            pos += 4;
            Log.i("hello", "PID : " + PID);
//CMD_GET_MID) != -1) {//43
            pos += 2;
            str = devInfo.substring(pos, pos+2);
            int MID = Integer.valueOf(str, 16);
            pos += 2;
            Log.i("hello", "MID : " + MID);
//CMD_GET_BATTERY_STATUS) != -1) {//44
            pos += 2;
            String battery = devInfo.substring(pos, pos + 2);
            pos += 2;
            int batteryPower = Integer.valueOf(battery, 16);
            int state = batteryPower>>7;
            if (state == 1) {
                Log.i("hello", "charging: " + batteryPower);
            } else {
                Log.i("hello", "not charging: " + batteryPower);
            }
//CMD_GET_LINKED_DEVICE_COUNT)) {//45
            pos += 2;
            String cnt = devInfo.substring(pos, pos+2);
            pos += 2;
            Log.i("hello", "get linked device count: " + cnt);
//CMD_GET_ACTIVE_CHANNEL)!=-1) {//46
            pos += 2;
            String channel = devInfo.substring(pos, pos+2);
            pos += 2;
            Log.i("hello", "get active channel : " + channel);

//CMD_GET_AUDIO_SOURCE)!=-1) {//47
            pos += 2;
            String audioSource = devInfo.substring(pos, pos+2);
            pos += 2;
            Log.i("hello", "get audio source : " + audioSource);
//CMD_GET_DEVICE_MAC)!=-1) {//48
            pos += 2;
            String MAC = devInfo.substring(pos, pos+12);
            Log.i("hello", "mac : " + MAC);

            retGetDeviceInfo();
        }
    }
    */
}
