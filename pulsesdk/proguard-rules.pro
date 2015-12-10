# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/lee/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-optimizations !code/simplification/arithmetic
-allowaccessmodification
-repackageclasses ''
-dontpreverify
-dontwarn android.support.**
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep public class com.harman.pulsesdk.DeviceModel{
    *;
}

-keep public class com.harman.pulsesdk.PulseColor{
    *;
}

-keep class com.harman.pulsesdk.PulseThemePattern{
    *;
}

-keep public class com.harman.pulsesdk.PulseHandlerInterface{
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
    public Boolean SetCharacterPattern(int patternId, boolean includeSlave);
    public void PropagateLedPattern(int patternId);
    public Boolean CaptureColorFromColorPicker();
    public void SetLEDAndSoundFeedback(int devIndex);
}

-keep public class com.harman.pulsesdk.PulseNotifiedListener{
    public void onLEDPatternChanged(PulseThemePattern pattern);
    public void onSoundEvent(int soundLevel);
    public void onRetCaptureColor(PulseColor capturedColor);
    public void onRetCaptureColor(byte red, byte green, byte blue);
    public void onRetSetDeviceInfo(boolean ret);
    public void onRetGetLEDPattern(PulseThemePattern pattern);
    public void onRetRequestDeviceInfo(DeviceModel[] deviceModel);
}