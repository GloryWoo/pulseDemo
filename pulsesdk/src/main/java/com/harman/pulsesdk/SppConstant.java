package com.harman.pulsesdk;
//spp常量
public class SppConstant {
	public static final String BASE_ADDRESS="00000000-0000-1000-8000-00805F9B34FB";
	public static final String GAP="00001800-0000-1000-8000-00805F9B34FB";
	public static final String GATT="00001801-0000-1000-8000-00805F9B34FB";
	public static final String BLE_RX_TX="65786365-6c70-6f69-6e74-2e636f6d0000";
	
	//GAP service
	public static final String DEVICE_NAME_CHAR="00002a00-0000-1000-8000-00805F9B34FB";
	public static final String MANUFACTURE_SPECIFIC_DATA_CHAR="00002a01-0000-1000-8000-00805F9B34FB";
	
	//BLE_RX_TX service
	public static final String RX_CHAR="65786365-6c70-6f69-6e74-2e636f6d0001";//RX_CHAR characteristic,read/notify
	public static final String TX_CHAR="65786365-6c70-6f69-6e74-2e636f6d0002";//TX_CHAR characteristic,write
	
	
	public static final String RET_CMD_SUCCESS="aa00021500";
	public static final String RET_CMD_DEV_ACK="aa00";
	public static final String RET_CMD_APP_ACK="aa01";
	public static final String RET_GET_DEV_INFO="aa12";


	public static final byte[] RET_CMD_ACK={(byte)0xAA, 0x00};
	public static final byte[] RET_CMD_DEV_INFO={(byte)0xAA, 0x12};
	public static final byte[] RET_LED_PATTERN = {(byte)0xAA, 0x52};
	public static final byte[] RET_LED_PATTERN_CHANGE = {(byte)0xAA, 0x55};
	public static final byte[] RET_SOUND_EVENT = {(byte)0xAA, 0x61};
	public static final byte[] RET_COLOR_PICKER = {(byte)0xAA, 0x57};

	public static final byte RET_DEVICE_NAME= (byte)0xc1;
	public static final byte RET_PID= 0x42;
	public static final byte RET_MID= 0x43;
	public static final byte RET_BATTERY_STATUS= 0x44;
	public static final byte RET_LINKED_DEVICE_COUNT= 0x45;
	public static final byte RET_ACTIVE_CHANNEL= 0x46;
	public static final byte RET_AUDIO_SOURCE= 0x47;
	public static final byte RET_DEVICE_MAC= 0x48;

	public static final byte RET_SET_DEV_ACK=0x15;
	
	//public static final String RET_GET_DEVICE_MAC="aa12080048";
	
	public static final String CMD_DEV_BYE_BYE="aa02";
	public static final String CMD_APP_BYE_BYE="aa03";

	public static final String CMD_GET_DEVICE_NAME="c1";
	public static final String CMD_GET_PID="42";
	public static final String CMD_GET_MID="43";
	public static final String CMD_GET_BATTERY_STATUS="44";
	public static final String CMD_GET_LINKED_DEVICE_COUNT="45";
	public static final String CMD_GET_ACTIVE_CHANNEL="46";
	public static final String CMD_GET_AUDIO_SOURCE="47";
	public static final String CMD_GET_DEVICE_MAC="48";
	
	public static final String RET_REQ_LINK_DEV="aa00022100";
	
	public static final String CMD_NOTIFY_LINK_DEV_DROP="aa22";
	
	public static final String RET_REQ_DROP_LINK_DEV="aa00022300";
	
	//new version begin
	public static final String RET_REQ_VER="aa42";
	public static final String NOTIFY_DFU_STATUS_CHANGE="aa4501";
	public static final String RET_CMD_ACK_REQ_DFU_START_WITH_SECTION_ID="aa000246";
	
	//LED Pattern Control
	public static final String RET_RetLedPatternInfo="aa52";
	public static final String RET_NotifyInsLelChange="aa5404";
	public static final String RET_NotifyLedPattem="aa55";
	public static final String RET_RetColorSniffer="aa5703";
}
