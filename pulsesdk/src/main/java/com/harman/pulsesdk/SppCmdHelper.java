package com.harman.pulsesdk;

import android.util.Log;

import java.io.OutputStream;
import java.util.ArrayList;

public class SppCmdHelper {
	public static enum SppCmdType
	{
		cmd_none,
		cmd_get_pid,
		cmd_get_mid,
		cmd_get_battery_status,
		cmd_set_left_channel,
		cmd_set_right_channel,
		cmd_set_stereo_channel,
		cmd_set_device_name,
		cmd_get_linked_device_count,
		cmd_get_active_channel,
		cmd_get_audio_source,
		cmd_req_link_dev,
		cmd_req_drop_link_dev,
		cmd_req_start_linking,
		cmd_req_led_and_sound_feedback,
		cmd_req_device_software_version,
		cmd_req_dfu_start,
		cmd_req_dfu_start_with_sec_id,
		cmd_req_color_from_color_picker,
		cmd_notify_dfu_tart,
		cmd_set_dfu_data,
		cmd_notify_Sec_Start,
		cmd_reqLedPatternInfo,
		cmd_setLedPattern,
		cmd_setBrightness,
		cmd_setBackgroundColor,
		cmd_setColorImage,
		cmd_setCharacterPattern,
		cmd_propagateLedPattern,
		cmd_getMicrophoneSoundLevel,
	}
	
	public static enum ByeByeReason
	{
		unknown,
		device_power_off,
		kick_by_other_app
	}
	
	public static enum UpgradeSectionId
	{
		MCU,
		BT,
		TRADITIONAL,     //only for flip3 and extreme
		None
	}
	
	public static SppCmdType bleCmdType=SppCmdType.cmd_none;
	
	static OutputStream os;
	public static void init(OutputStream stream)
	{
		os=stream;
	}
	public static byte[] getDeviceCmd(String deviceName,int devIndex) {
		if(deviceName.length()>16)
		{
			deviceName=deviceName.substring(0,16);
		}
		
		String hexDeviceName=HexHelper.encodeHexStr(deviceName.getBytes());
		byte[]data=HexHelper.decodeHex(hexDeviceName.toCharArray());
		
		byte deviceNameLen=(byte) data.length;
		byte payloadLen=(byte) (deviceNameLen+3);
		byte[] cmd_set_device_name = new byte[] 
			{ 
				(byte) 0xaa, 
				(byte) 0x15,
				(byte) payloadLen,
				(byte) devIndex,
				(byte) 0xc1,
				(byte) deviceNameLen 
			};
		
        ArrayList<Byte> cmd=new ArrayList<Byte>();
		
		for(int i=0;i<cmd_set_device_name.length;i++)
		{
			cmd.add(cmd_set_device_name[i]);
		}
		
		for(int i=0;i<data.length;i++)
		{
			cmd.add(data[i]);
		}
		
		byte[] cmdFull=new byte[cmd.size()];
		for(int i=0;i<cmd.size();i++)
		{
			cmdFull[i]=cmd.get(i);
		}
		
		return cmdFull;
	}
	
	public static byte[] getReqLinkDevCmd(String mac)
	{
		String hexMac=mac;//HexHelper.encodeHexStr(mac.getBytes());
		byte[]data=HexHelper.decodeHex(hexMac.toCharArray());
		
		byte[]CMD_REQ_LINK_DEV=new byte[]
				            {
				               (byte)0xaa,
				               (byte)0x21,
				               (byte)0x06,
				               (byte)0x00,
				               (byte)0x00,
				               (byte)0x00,
				               (byte)0x00,
				               (byte)0x00,
				               (byte)0x00
				            };
		for(int i=0;i<6;i++)
		{
			CMD_REQ_LINK_DEV[i+3]=data[i];
		}
		return CMD_REQ_LINK_DEV;
	}
	
	public static void sendCmd(byte[]value)
	{
		String result = HexHelper.encodeHexStr(value);
		Log.i("sendCmd", result);

		if(os==null) return;
		try {
			os.write(value);
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//修改设备名字
	public static void setDeviceName(String deviceName,int devIndex)
	{
		bleCmdType=SppCmdType.cmd_set_device_name;
		byte[] cmd=SppCmdHelper.getDeviceCmd(deviceName,devIndex);
		sendCmd(cmd);
	}
	//获取设备PID
	public static void getPID(int devIndex)
	{
		bleCmdType=SppCmdType.cmd_get_pid;
		byte[]CMD_GET_PID=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x13,
	               (byte)0x02,
	               (byte)devIndex,
	               (byte)0x42
	            };
		sendCmd(CMD_GET_PID);
	}
	//获取设备MID
	public static void getMID(int devIndex)
	{
		bleCmdType=SppCmdType.cmd_get_mid;
		byte[]CMD_GET_MID=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x13,
	               (byte)0x02,
	               (byte)devIndex,
	               (byte)0x43
	            };
		sendCmd(CMD_GET_MID);
	}
	
	public static void setMID(int devIndex,int mid)
	{
		bleCmdType=SppCmdType.cmd_get_mid;
		byte[]CMD_GET_MID=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x15,
	               (byte)0x02,
	               (byte)devIndex,
	               (byte)0x43,
	               (byte)mid
	            };
		sendCmd(CMD_GET_MID);
	}
	//获取设备电量
	public static void getBatteryStatus(int devIndex)
	{
		bleCmdType=SppCmdType.cmd_get_battery_status;
		byte[]CMD_GET_BATTERY_STATUS=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x13,
	               (byte)0x02,
	               (byte)devIndex,
	               (byte)0x44
	            };
		sendCmd(CMD_GET_BATTERY_STATUS);
	}
	//获取link在一起的设备数量
	public static void getLinkedDeviceCount(int devIndex)
	{
		bleCmdType=SppCmdType.cmd_get_linked_device_count;
		final byte[]CMD_GET_LINKED_DEVICE_COUNT=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x13,
	               (byte)0x02,
	               (byte)devIndex,
	               (byte)0x45
	            };
		sendCmd(CMD_GET_LINKED_DEVICE_COUNT);
	}
	//设置设备的声道
	public static void setDeviceChannel(int devIndex,int channel)
	{
		byte[]CMD_SET_ACTIVE_CHANNEL=new byte[]
			            {
			               (byte)0xaa,
			               (byte)0x15,
			               (byte)0x03,
			               (byte)devIndex,
			               (byte)0x46,
			               (byte)channel
			            };
		sendCmd(CMD_SET_ACTIVE_CHANNEL);
	}
	//获取设备的声道
	public static void getActiveChannel(int devIndex)
	{
		bleCmdType=SppCmdType.cmd_get_active_channel;
		byte[]CMD_GET_ACTIVE_CHANNEL=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x13,
	               (byte)0x02,
	               (byte)devIndex,
	               (byte)0x46
	            };
		sendCmd(CMD_GET_ACTIVE_CHANNEL);
	}
//获取设备的音频源
	public static void getAudioSource(int devIndex)
	{
		bleCmdType=SppCmdType.cmd_get_audio_source;
		byte[]CMD_GET_AUDIO_SOURCE=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x13,
	               (byte)0x02,
	               (byte)devIndex,
	               (byte)0x47
	            };
		sendCmd(CMD_GET_AUDIO_SOURCE);
	}
	//请求主设备返回当前link系统所有设备的设备信息
	public static void reqDevInfo()
	{
		byte[]CMD_REQ_DEV_INFO=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x11,
	               (byte)0x00
	            };
		sendCmd(CMD_REQ_DEV_INFO);
	}
	//请主设备和指定的设备进行link
	public static void reqLinkDev(String mac)
	{
		bleCmdType=SppCmdType.cmd_req_link_dev;
		mac=mac.replace(":", "");
		byte[]cmd=getReqLinkDevCmd(mac);
		sendCmd(cmd);
	}
	//请求主设备和指定的设备断开link
	public static void reqDropLinkDev(int devIndex)
	{
		bleCmdType=SppCmdType.cmd_req_drop_link_dev;
		
		final byte[]CMD_REQ_DROP_LINK_DEV=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x23,
	               (byte)0x01,
	               (byte)devIndex
	            };
		
		if(devIndex==0)
		{
			sendCmd(CMD_REQ_DROP_LINK_DEV);
		}
		else if(devIndex==1)
		{
			sendCmd(CMD_REQ_DROP_LINK_DEV);
		}
		else if(devIndex==2)
		{
			sendCmd(CMD_REQ_DROP_LINK_DEV);
		}
	}
	//app请求主设备进入Link模式，second=0，就是不会超时超时
	public static void ReqStartLinking(int second)
	{
		bleCmdType=SppCmdType.cmd_req_start_linking;
		
		final byte[]CMD_REQ_START_LINKING=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x25,
	               (byte)0x01,
	               (byte)second
	            };
		
		sendCmd(CMD_REQ_START_LINKING);
	}
	//请求指定的设备做出灯光和声音的反馈，在用户在APP里面点击设备的时候
	public static void reqLEDAndSoundFeedback(int devIndex)
	{
		bleCmdType=SppCmdType.cmd_req_led_and_sound_feedback;
		byte[]CMD_REQ_LED_AND_SOUND_FEEDBACK_DEVICE=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x31,
	               (byte)0x01,
	               (byte)devIndex
	            };
		sendCmd(CMD_REQ_LED_AND_SOUND_FEEDBACK_DEVICE);
	}
	//获取设备软件版本
	public static void reqDeviceSoftwareVersion()
	{
		bleCmdType=SppCmdType.cmd_req_device_software_version;
		byte[]CMD_REQ_DEVICE_SOFTWARE_VERSION=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x41,
	               (byte)0x00
	            };
		sendCmd(CMD_REQ_DEVICE_SOFTWARE_VERSION);
	}
	//发送req dfu start命令来启动设备升级
	public static byte[] reqDfuStart(int dfuCrc,int dfuSize)
	{
		bleCmdType=SppCmdType.cmd_req_dfu_start;
		byte[]CMD_REQ_DFU_START=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x43,
	               (byte)8,//8 bytes
	            };
		byte[] data=new byte[CMD_REQ_DFU_START.length+8];
		
		byte[] dataDfuCrc=BinaryHelper.Int2ByteArray(dfuCrc);
		byte[] dataDfuCrc2=new byte[4];
		dataDfuCrc2[0]=dataDfuCrc[2];
		dataDfuCrc2[1]=dataDfuCrc[3];
		dataDfuCrc2[2]=dataDfuCrc[0];
		dataDfuCrc2[3]=dataDfuCrc[1];
		dataDfuCrc=dataDfuCrc2;
		
		byte[] dataDfuSize=BinaryHelper.Int2ByteArray(dfuSize);
		
		for(int i=0;i<data.length;i++)
		{
			if(i<CMD_REQ_DFU_START.length)
			{
				data[i]=CMD_REQ_DFU_START[i];
			}
			else if(i>=CMD_REQ_DFU_START.length && i<CMD_REQ_DFU_START.length+4)
			{
				data[i]=dataDfuCrc[i-CMD_REQ_DFU_START.length];
			}
			else
			{
				data[i]=dataDfuSize[i-CMD_REQ_DFU_START.length-4];
			}
		}

		String cmd=HexHelper.encodeHexStr(data);
		sendCmd(data);
		return data;
	}
	
	public static byte[] reqDfuStart(int dfuCrc,UpgradeSectionId dfuSecIdx,int dfuSize,
			int dfuCrc2,UpgradeSectionId dfuSecIdx2,int dfuSize2)
	{
		if(dfuSecIdx==UpgradeSectionId.TRADITIONAL || dfuSecIdx2==UpgradeSectionId.TRADITIONAL) return null;
		if(dfuSecIdx==UpgradeSectionId.None) return null;
		
		int payloadLen=dfuSecIdx2==UpgradeSectionId.None?8:16;
		
		bleCmdType=SppCmdType.cmd_req_dfu_start_with_sec_id;
		byte[]CMD_REQ_DFU_START=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x43,
	               (byte)payloadLen,
	            };
		byte[] data=new byte[CMD_REQ_DFU_START.length+payloadLen];
		
		int tmp_dfuSecIdx=-1;
		if(dfuSecIdx==UpgradeSectionId.MCU)
		{
			tmp_dfuSecIdx=0;
		}
		else if(dfuSecIdx==UpgradeSectionId.BT)
		{
			tmp_dfuSecIdx=1;
		}
		
		byte[] dataDfuCrc=BinaryHelper.Int2ByteArray(dfuCrc);
		byte[] tmp_dataDfuCrc=new byte[4];
		tmp_dataDfuCrc[0]=dataDfuCrc[2];
		tmp_dataDfuCrc[1]=dataDfuCrc[3];
		tmp_dataDfuCrc[2]=dataDfuCrc[0];
		tmp_dataDfuCrc[3]=dataDfuCrc[1];
		dataDfuCrc=tmp_dataDfuCrc;

		
		byte[] dataDfuSize=BinaryHelper.Int2ByteArray(dfuSize);
		
		data[0]=CMD_REQ_DFU_START[0];
		data[1]=CMD_REQ_DFU_START[1];
		data[2]=CMD_REQ_DFU_START[2];
		
		data[3]=dataDfuCrc[0];
		data[4]=dataDfuCrc[1];
		data[5]=dataDfuCrc[2];
		data[6]=dataDfuCrc[3];
		
		data[7]=(byte)tmp_dfuSecIdx;
		
		data[8]=dataDfuSize[1];
		data[9]=dataDfuSize[2];
		data[10]=dataDfuSize[3];
		
		if(payloadLen==16)
		{
			int tmp_dfuSecIdx2=-1;
			if(dfuSecIdx2==UpgradeSectionId.MCU)
			{
				tmp_dfuSecIdx2=0;
			}
			else if(dfuSecIdx2==UpgradeSectionId.BT)
			{
				tmp_dfuSecIdx2=1;
			}
			
			byte[] dataDfuCrc2=BinaryHelper.Int2ByteArray(dfuCrc2);
			byte[] tmp_dataDfuCrc2=new byte[4];
			tmp_dataDfuCrc2[0]=dataDfuCrc[2];
			tmp_dataDfuCrc2[1]=dataDfuCrc[3];
			tmp_dataDfuCrc2[2]=dataDfuCrc[0];
			tmp_dataDfuCrc2[3]=dataDfuCrc[1];
			dataDfuCrc2=tmp_dataDfuCrc2;

			
			byte[] dataDfuSize2=BinaryHelper.Int2ByteArray(dfuSize2);
			
			data[11]=dataDfuCrc2[0];
			data[12]=dataDfuCrc2[1];
			data[13]=dataDfuCrc2[2];
			data[14]=dataDfuCrc2[3];
			
			data[15]=(byte)tmp_dfuSecIdx2;
			
			data[16]=dataDfuSize2[1];
			data[17]=dataDfuSize2[2];
			data[18]=dataDfuSize2[3];
		}


		String cmd=HexHelper.encodeHexStr(data);
		sendCmd(data);
		return data;
	}

	public static void reqColorFromColorPicker()
	{
		bleCmdType=SppCmdType.cmd_req_color_from_color_picker;
		byte[]cmd=new byte[]
				{
					(byte)0xaa,
					(byte)0x63,
				};
		sendCmd(cmd);
	}

	public static byte[] notifySecStart(int dfuCrc, UpgradeSectionId dfuSecIdx,int dfuSize) 
	{
		int dfuSecIdx2=dfuSecIdx==UpgradeSectionId.MCU?0:1;
		bleCmdType=SppCmdType.cmd_req_dfu_start_with_sec_id;
		byte[]CMD_REQ_DFU_START=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x46,
	               (byte)8,//8 bytes
	            };
		byte[] data=new byte[CMD_REQ_DFU_START.length+8];
		
		byte[] dataDfuCrc=BinaryHelper.Int2ByteArray(dfuCrc);//-2041963479
		byte[] dataDfuCrc2=new byte[4];
		dataDfuCrc2[0]=dataDfuCrc[2];
		dataDfuCrc2[1]=dataDfuCrc[3];
		dataDfuCrc2[2]=dataDfuCrc[0];
		dataDfuCrc2[3]=dataDfuCrc[1];
		dataDfuCrc=dataDfuCrc2;

		
		byte[] dataDfuSize=BinaryHelper.Int2ByteArray(dfuSize);//118784
		
		data[0]=CMD_REQ_DFU_START[0];
		data[1]=CMD_REQ_DFU_START[1];
		data[2]=CMD_REQ_DFU_START[2];
		
		data[3]=dataDfuCrc[0];
		data[4]=dataDfuCrc[1];
		data[5]=dataDfuCrc[2];
		data[6]=dataDfuCrc[3];
		
		data[7]=(byte)dfuSecIdx2;
		
		data[8]=dataDfuSize[1];
		data[9]=dataDfuSize[2];
		data[10]=dataDfuSize[3];
		
//		for(int i=0;i<data.length;i++)
//		{
//			if(i<CMD_REQ_DFU_START.length)
//			{
//				data[i]=CMD_REQ_DFU_START[i];//0,1,2
//			}
//			else if(i>=CMD_REQ_DFU_START.length && i<CMD_REQ_DFU_START.length+4)
//			{
//				data[i]=dataDfuCrc[i-CMD_REQ_DFU_START.length];//3,4,5,6
//			}
//			else if(i==CMD_REQ_DFU_START.length+4)
//			{
//				data[i]=(byte)dfuSecIdx2;//7
//			}else{
//				data[i]=dataDfuSize[i-CMD_REQ_DFU_START.length-4-1];
//			}
//		}

		String cmd=HexHelper.encodeHexStr(data);
		sendCmd(data);
		return data;
	}
	
	//在升级的过程中发送升级数据包
	public static byte[] setDfuData(int packIdx,byte[] dfuData)
	{
		bleCmdType=SppCmdType.cmd_set_dfu_data;
		byte[]CMD_SET_DFU_DATA=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x44,
	               (byte)dfuData.length,
	            };
		byte[] data=new byte[CMD_SET_DFU_DATA.length+dfuData.length];
		
		for(int i=0;i<CMD_SET_DFU_DATA.length;i++)
		{
			data[i]=CMD_SET_DFU_DATA[i];
		}
		
		for(int i=0;i<dfuData.length;i++)
		{
			data[i+3]=dfuData[i];
		}

		sendCmd(data);
		return data;
	}
	//only for ble,android not support
	public static void notifyDfuCancel(){
		final byte[]CMD_NOTIFY_DFU_CANCEL=new byte[]
	            {
	               (byte)0xaa,
	               (byte)0x47,
	               (byte)0x01,
	               (byte)0x01
	            };
		
		sendCmd(CMD_NOTIFY_DFU_CANCEL);
	}
	
	//*****LED Pattern Control****************//
	
		public static void reqLedPatternInfo()
		{
			bleCmdType=SppCmdType.cmd_reqLedPatternInfo;
			byte[]cmd=new byte[]
		            {
		               (byte)0xaa,
		               (byte)0x51,
		               (byte)0x00
		            };
			
			sendCmd(cmd);
		}
		
		public static void setLedPattern(int patternId) {
			bleCmdType = SppCmdType.cmd_setLedPattern;
			byte[] cmd = new byte[4];
			cmd[0] = (byte) 0xaa;
			cmd[1] = (byte) 0x53;
			cmd[2]=(byte)1;
			cmd[3] = (byte) patternId;
			sendCmd(cmd);
		}
		
		public static void setLedPattern(int patternId, byte[] patternStatus) {
			bleCmdType = SppCmdType.cmd_setLedPattern;
			byte[] cmd = new byte[4 + patternStatus.length];
			cmd[0] = (byte) 0xaa;
			cmd[1] = (byte) 0x53;
			cmd[2]=(byte)(patternStatus.length+1);
			cmd[3] = (byte) patternId;
			for(int i=4;i<cmd.length;i++)
			{
				cmd[i]=(byte) patternStatus[i-4];
			}
			sendCmd(cmd);
		}
		
		public static void SetBrightness(int brightness)
		{
			bleCmdType=SppCmdType.cmd_setBrightness;
			byte[]cmd=new byte[]
		            {
		               (byte)0xaa,
		               (byte)0x56,
		               (byte)0x01,
		               (byte)brightness
		            };
			
			sendCmd(cmd);
		}

		public static void SetBackgroundColor(int safeColorIdx, boolean inlcudeSlave)
		{
			bleCmdType=SppCmdType.cmd_setBackgroundColor;
			byte[]cmd=new byte[]
					{
							(byte)0xaa,
							(byte)0x58,
							(byte)0x02,
							(byte)safeColorIdx,
							(byte)(inlcudeSlave?1:0),
					};

			sendCmd(cmd);
		}

		public static void setColorImage(int[] idxPixel) {
			bleCmdType = SppCmdType.cmd_setColorImage;
			byte[] cmd = new byte[idxPixel.length+3];
			cmd[0] = (byte) 0xaa;
			cmd[1] = (byte) 0x59;
			cmd[2] = (byte) 0x63;
			for(int i=3;i<idxPixel.length+3;i++)
			{
				cmd[i]=(byte)idxPixel[i-3];
			}
			sendCmd(cmd);
		}

		public static void SetCharacterPattern(char character, int foreground, int background, boolean inlcudeSlave)
		{
			bleCmdType=SppCmdType.cmd_setCharacterPattern;
			byte[]cmd=new byte[]
					{
							(byte)0xaa,
							(byte)0x5C,
							(byte)0x04,
							(byte)character,
							(byte)foreground,
							(byte)background,
							(byte)(inlcudeSlave?1:0),
					};

			sendCmd(cmd);
		}

	public static void PropagateLedPattern()
	{
		bleCmdType=SppCmdType.cmd_propagateLedPattern;
		byte[]cmd=new byte[]
				{
						(byte)0xaa,
						(byte)0x5D,
				};

		sendCmd(cmd);
	}

	public static void GetMicrophoneSoundLevel()
	{
		bleCmdType=SppCmdType.cmd_getMicrophoneSoundLevel;
		byte[]cmd=new byte[]
				{
						(byte)0xaa,
						(byte)0x06,
						(byte)0x02,
				};

		sendCmd(cmd);
	}

		public static void testNotifyCmd()
		{
			byte[]cmd=new byte[]
		            {
		               (byte)0xaa,
		               (byte)0x32,
		               (byte)0x01,
		               (byte)0x00
		            };
			
			sendCmd(cmd);
		}
		
		public static void testNotifyCmd2()
		{
			byte[]cmd=new byte[]
		            {
		               (byte)0xaa,
		               (byte)0x48,
		               (byte)0x00
		            };
			
			sendCmd(cmd);
		}
		
		//*****LED Pattern Control****************//
}
