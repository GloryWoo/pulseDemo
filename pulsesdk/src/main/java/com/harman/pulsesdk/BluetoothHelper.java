package com.harman.pulsesdk;

import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;

public class BluetoothHelper {
	public static String SPP_UUID ="00001101-0000-1000-8000-00805F9B34FB";
	public static final int REQUEST_ENABLE=1000;
	public static boolean isBluetoothSupported()
	{
		BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
		return adapter!=null;
	}
	
	public static boolean isBluetoothEnabled()
	{
		BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
		return adapter.isEnabled();
	}
	
	public static boolean isA2DPDeviceConnected(Context context)
	{
		BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
		int state= adapter.getProfileConnectionState(BluetoothProfile.A2DP);
		return state==BluetoothProfile.STATE_CONNECTED;
	}
	
	public static boolean getA2DPProfileProxy(Context context,ServiceListener listener)
	{
		BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
		boolean success= adapter.getProfileProxy(context, listener, BluetoothProfile.A2DP);
		return success;
	}
	
	public static BluetoothSocket createBluetoothSocket(BluetoothDevice device) {
		BluetoothSocket socket = null;
		UUID uuid = UUID.fromString(SPP_UUID);
		try {
//			Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
//			socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
			socket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception e) {
        	try
        	{
        		socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        	}
        	catch(Exception ex)
        	{
        		socket=null;
        	}
        }
		return socket;
	  }
	
	public static BluetoothSocket connect(BluetoothDevice device) {
		BluetoothSocket socket = null;
		try {
			socket=createBluetoothSocket(device);
			if(socket==null) return null;
			socket.connect();
		} catch (Exception e) {
			socket=null;
			e.printStackTrace();
		}
		return socket;
	}
}
