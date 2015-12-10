package com.harman.pulsesdk;

import android.app.Activity;

/**
 * Created by lee on 15/11/17.
 */
public interface PulseHandlerInterface {

    public void registerPulseNotifiedListener(PulseNotifiedListener listener);
    public Boolean ConnectMasterDevice(Activity activity);
    public Boolean isConnectMasterDevice();
    public Boolean RequestDeviceInfo();
    public Boolean GetLEDPattern();
    public Boolean SetDeviceName(String devName, int devIndex);
    public Boolean SetLEDPattern(PulseThemePattern pattern);
    public Boolean SetDeviceChannel(int devIndex, int channel);
    public Boolean SetBrightness(int brightness);
    public Boolean SetBackgroundColor(PulseColor color, boolean inlcudeSlave);
    public Boolean SetColorImage(PulseColor[] bitmap);
    public Boolean SetCharacterPattern(char character, PulseColor foreground, PulseColor background, boolean inlcudeSlave);
    public void PropagateLedPattern();
    public Boolean CaptureColorFromColorPicker();
    public void GetMicrophoneSoundLevel();
    public void SetLEDAndSoundFeedback(int devIndex);
}
