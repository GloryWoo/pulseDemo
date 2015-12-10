package com.harman.pulsesdk;

import java.io.*;

public class BinaryHelper {
	public static byte[] Int2ByteArray(int num) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream oos;
		byte[] buffer = null;
		try {
			oos = new DataOutputStream(bos);
			oos.writeInt(num);
			oos.flush();
			oos.close();
			buffer = bos.toByteArray();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}
	
	public static int ByteArray2Int(byte[] buffer) {
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
		DataInputStream dis;
		int num=-1;
		try {
			dis = new DataInputStream(bis);
			num = dis.readInt();
			return num;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return num;
	}

	public static short ByteArray2Short(byte[] buffer) {
		return (short) (((buffer[1] << 8) | buffer[0] & 0xff));
	}
}
