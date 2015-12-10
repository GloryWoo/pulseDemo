package com.harman.pulsesdk;

/**
 * Created by lee on 15/11/23.
 */
public interface PulseNotifiedListener {
    public void onConnectMasterDevice();
    public void onDisconnectMasterDevice();
    public void onLEDPatternChanged(PulseThemePattern pattern);
    public void onSoundEvent(int soundLevel);
    public void onRetCaptureColor(PulseColor capturedColor);
    public void onRetCaptureColor(byte red, byte green, byte blue);
    public void onRetSetDeviceInfo(boolean ret);
    public void onRetGetLEDPattern(PulseThemePattern pattern);
    public void onRetRequestDeviceInfo(DeviceModel[] deviceModel);
}
