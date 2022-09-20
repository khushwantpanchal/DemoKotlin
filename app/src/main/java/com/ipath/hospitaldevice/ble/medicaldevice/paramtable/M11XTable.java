package com.ipath.hospitaldevice.ble.medicaldevice.paramtable;

import com.zayata.zayatabluetoothsdk.bean.ParamTlvBean;

import java.util.ArrayList;
import java.util.List;


/**
 * M11X系列 tag工具类 Tags Tool
 *  Created by linyb on 21/4/13.
 */

public class M11XTable {

    //状态参数
    public static final byte TAG_DEV_TYPE = (byte) 0x01;//设备类型 Device Type ; size : 1
    public static final byte TAG_DEV_VERSION = (byte) 0x03;//固件版本号 Dievce Firmware revision ; size : 1
    public static final byte TAG_REG_INFO = (byte) 0x04;//注册信息 Registration Information ; size : 1
    public static final byte TAG_DEV_MODEL = (byte) 0x10;//设备型号 Device model ; size : 1
    public static final byte TAG_AUDIO_TYPE = (byte) 0x11;//音频类型 Audio mode ; size : 1
    public static final byte TAG_BAT_STAT = (byte) 0x12;//电池状态 Battery status ;  size : 1
    public static final byte TAG_ROTATE_FAULT = (byte) 0x13;//旋转故障 Rotate failure ;  size : 1
    public static final byte TAG_ALARM_STAT_1 = (byte) 0x21;//闹钟1-状态 Alarm-1 status ;  size : 1
    public static final byte TAG_ALARM_STAT_2 = (byte) 0x22;//闹钟2-状态 Alarm-2 status ;  size : 1
    public static final byte TAG_ALARM_STAT_3 = (byte) 0x23;//闹钟3-状态 Alarm-3 status ;  size : 1
    public static final byte TAG_ALARM_STAT_4 = (byte) 0x24;//闹钟4-状态 Alarm-4 status ;  size : 1
    public static final byte TAG_ALARM_STAT_5 = (byte) 0x25;//闹钟5-状态 Alarm-5 status ;  size : 1
    public static final byte TAG_ALARM_STAT_6 = (byte) 0x26;//闹钟6-状态 Alarm-6 status ;  size : 1
    public static final byte TAG_ALARM_STAT_7 = (byte) 0x27;//闹钟7-状态 Alarm-7 status ;  size : 1
    public static final byte TAG_ALARM_STAT_8 = (byte) 0x28;//闹钟8-状态 Alarm-8 status ;  size : 1
    public static final byte TAG_ALARM_STAT_9 = (byte) 0x29;//闹钟9-状态 Alarm-9 status ;  size : 1

    //配置参数
    public static final byte TAG_TIME_FORMAT = (byte) 0x50;//时间格式 Time format ;  size : 1
    public static final byte TAG_RING_TYPE = (byte) 0x51;//铃声种类 The bell type ;  size : 1
    public static final byte TAG_VOLUME = (byte) 0x52;//设备音量 Device volume ;  size : 1
    public static final byte TAG_ALARM_KEEP = (byte) 0x53;//提醒时长 Remind time ;  size : 1
    public static final byte TAG_KEY_TONE = (byte) 0x5A;//按键音开关 key tone； size : 1
    public static final byte TAG_ALARM_HOUR_1 = (byte) 0x61;//闹钟1-小时 Alarm-1 hour ;  size : 1
    public static final byte TAG_ALARM_HOUR_2 = (byte) 0x62;//闹钟2-小时 Alarm-2 hour ;  size : 1
    public static final byte TAG_ALARM_HOUR_3 = (byte) 0x63;//闹钟3-小时 Alarm-3 hour ;  size : 1
    public static final byte TAG_ALARM_HOUR_4 = (byte) 0x64;//闹钟4-小时 Alarm-4 hour ;  size : 1
    public static final byte TAG_ALARM_HOUR_5 = (byte) 0x65;//闹钟5-小时 Alarm-5 hour ;  size : 1
    public static final byte TAG_ALARM_HOUR_6 = (byte) 0x66;//闹钟6-小时 Alarm-6 hour ;  size : 1
    public static final byte TAG_ALARM_HOUR_7 = (byte) 0x67;//闹钟7-小时 Alarm-7 hour ;  size : 1
    public static final byte TAG_ALARM_HOUR_8 = (byte) 0x68;//闹钟8-小时 Alarm-8 hour ;  size : 1
    public static final byte TAG_ALARM_HOUR_9 = (byte) 0x69;//闹钟9-小时 Alarm-9 hour ;  size : 1
    public static final byte TAG_ALARM_MINUTE_1 = (byte) 0x71;//闹钟1-分钟 Alarm-1 minute ;  size : 1
    public static final byte TAG_ALARM_MINUTE_2 = (byte) 0x72;//闹钟2-分钟 Alarm-2 minute ;  size : 1
    public static final byte TAG_ALARM_MINUTE_3 = (byte) 0x73;//闹钟3-分钟 Alarm-3 minute ;  size : 1
    public static final byte TAG_ALARM_MINUTE_4 = (byte) 0x74;//闹钟4-分钟 Alarm-4 minute ;  size : 1
    public static final byte TAG_ALARM_MINUTE_5 = (byte) 0x75;//闹钟5-分钟 Alarm-5 minute ;  size : 1
    public static final byte TAG_ALARM_MINUTE_6 = (byte) 0x76;//闹钟6-分钟 Alarm-6 minute ;  size : 1
    public static final byte TAG_ALARM_MINUTE_7 = (byte) 0x77;//闹钟7-分钟 Alarm-7 minute ;  size : 1
    public static final byte TAG_ALARM_MINUTE_8 = (byte) 0x78;//闹钟8-分钟 Alarm-8 minute ;  size : 1
    public static final byte TAG_ALARM_MINUTE_9 = (byte) 0x79;//闹钟9-分钟 Alarm-9 minute ;  size : 1
    public static final byte TAG_ALARM_SWITCH_1 = (byte) 0x81;//闹钟1-开关 Alarm-1 switch ;  size : 1
    public static final byte TAG_ALARM_SWITCH_2 = (byte) 0x82;//闹钟2-开关 Alarm-2 switch ;  size : 1
    public static final byte TAG_ALARM_SWITCH_3 = (byte) 0x83;//闹钟3-开关 Alarm-3 switch ;  size : 1
    public static final byte TAG_ALARM_SWITCH_4 = (byte) 0x84;//闹钟4-开关 Alarm-4 switch ;  size : 1
    public static final byte TAG_ALARM_SWITCH_5 = (byte) 0x85;//闹钟5-开关 Alarm-5 switch ;  size : 1
    public static final byte TAG_ALARM_SWITCH_6 = (byte) 0x86;//闹钟6-开关 Alarm-6 switch ;  size : 1
    public static final byte TAG_ALARM_SWITCH_7 = (byte) 0x87;//闹钟7-开关 Alarm-7 switch ;  size : 1
    public static final byte TAG_ALARM_SWITCH_8 = (byte) 0x88;//闹钟8-开关 Alarm-8 switch ;  size : 1
    public static final byte TAG_ALARM_SWITCH_9 = (byte) 0x89;//闹钟9-开关 Alarm-9 switch ;  size : 1

    //事件参数
    public static final byte TAG_TAKE_RECORD = (byte) 0xB0;//取药事件Take medicine event ;  size : 10

    //控制参数
    public static final byte TAG_PUB_KEY = (byte) 0xD0;//公钥 public key ; size : 2
    public static final byte TAG_PRI_KEY = (byte) 0xD1;//私钥 private  key ; size : 2
    public static final byte TAG_REBOOT = (byte) 0xD2;//设备重启 reboot ;  size : 1
    public static final byte TAG_FACTORY = (byte) 0xD3;//恢复出厂设置 reset ;  size : 1
    public static final byte TAG_TIME_CAL = (byte) 0xE0;//时间校准 Device Synchronize Time ;  size ：8
    public static final byte TAG_THIS_MUTE = (byte) 0xE2;//当前闹钟静音 The current alarm is muted ;  size : 1
    public static final byte TAG_MAKE_RING = (byte) 0xE3;//响铃开关 Bell switch ;  size : 1
    public static final byte TAG_TAKE_EARLY = (byte) 0xE4;//提前取药 Early Dose ;  size : 1

    /*
    * ParamTlvBean(byte tag,byte size,byte[] value)
    * value默认值为new byte[]{0x00}，使用设置函数需要修改value的值
    * 例：
    * List<ParamTlvBean> confList = M11XTable.get(M11XTable.TAG_TIME_FORMAT);
    * confList.get(0).setValue(new byte[]{(byte)0x01});//设置时间格式为1，1为12小时制
    * */
    static ParamTlvBean[] paramTlvBeanList = new ParamTlvBean[]{
            new ParamTlvBean(TAG_DEV_TYPE,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_DEV_VERSION,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_REG_INFO,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_DEV_MODEL,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_AUDIO_TYPE,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_BAT_STAT,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ROTATE_FAULT,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_STAT_1,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_STAT_2,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_STAT_3,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_STAT_4,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_STAT_5,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_STAT_6,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_STAT_7,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_STAT_8,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_STAT_9,(byte)0x01,new byte[]{0x00}),

            new ParamTlvBean(TAG_TIME_FORMAT,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_RING_TYPE,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_VOLUME,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_KEEP,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_KEY_TONE,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_HOUR_1,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_HOUR_2,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_HOUR_3,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_HOUR_4,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_HOUR_5,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_HOUR_6,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_HOUR_7,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_HOUR_8,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_HOUR_9,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_MINUTE_1,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_MINUTE_2,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_MINUTE_3,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_MINUTE_4,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_MINUTE_5,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_MINUTE_6,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_MINUTE_7,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_MINUTE_8,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_MINUTE_9,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_SWITCH_1,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_SWITCH_2,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_SWITCH_3,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_SWITCH_4,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_SWITCH_5,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_SWITCH_6,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_SWITCH_7,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_SWITCH_8,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_SWITCH_9,(byte)0x01,new byte[]{0x00}),

            new ParamTlvBean(TAG_TAKE_RECORD,(byte)0x0a,new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00}),

            new ParamTlvBean(TAG_PUB_KEY,(byte)0x02,new byte[]{0x00,0x00}),
            new ParamTlvBean(TAG_PRI_KEY,(byte)0x02,new byte[]{0x00,0x00}),
            new ParamTlvBean(TAG_REBOOT,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_FACTORY,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_TIME_CAL,(byte)0x08,new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00}),
            new ParamTlvBean(TAG_THIS_MUTE,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_MAKE_RING,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_TAKE_EARLY,(byte)0x01,new byte[]{0x00}),
    };

    public static List<ParamTlvBean> get(byte ... tag){
        List<ParamTlvBean> list = new ArrayList<>();
        for (int i = 0;i< tag.length;i++) {
            for (int j = 0; j < paramTlvBeanList.length; j++) {
                if (paramTlvBeanList[j].getTag() == tag[i]) {
                    list.add(paramTlvBeanList[j]);
                }
            }
        }
        return list;
    }

    public static byte[] alarmHourTagList = new byte[]{
            TAG_ALARM_HOUR_1,
            TAG_ALARM_HOUR_2,
            TAG_ALARM_HOUR_3,
            TAG_ALARM_HOUR_4,
            TAG_ALARM_HOUR_5,
            TAG_ALARM_HOUR_6,
            TAG_ALARM_HOUR_7,
            TAG_ALARM_HOUR_8,
            TAG_ALARM_HOUR_9
    };
    public static byte[] alarmMinuteTagList = new byte[]{
            TAG_ALARM_MINUTE_1,
            TAG_ALARM_MINUTE_2,
            TAG_ALARM_MINUTE_3,
            TAG_ALARM_MINUTE_4,
            TAG_ALARM_MINUTE_5,
            TAG_ALARM_MINUTE_6,
            TAG_ALARM_MINUTE_7,
            TAG_ALARM_MINUTE_8,
            TAG_ALARM_MINUTE_9
    };
    public static byte[] alarmSwitchTagList = new byte[]{
            TAG_ALARM_SWITCH_1,
            TAG_ALARM_SWITCH_2,
            TAG_ALARM_SWITCH_3,
            TAG_ALARM_SWITCH_4,
            TAG_ALARM_SWITCH_5,
            TAG_ALARM_SWITCH_6,
            TAG_ALARM_SWITCH_7,
            TAG_ALARM_SWITCH_8,
            TAG_ALARM_SWITCH_9
    };
//    public static ParamTlvBean deviceTimeFormat(byte value){
//        return new ParamTlvBean(TAG_DEVICE_TIME_FORMAT,(byte)0x01,new byte[]{value});
//    }
//
//    public static ParamTlvBean synchronizeDeviceTime(byte value1,byte value2,byte value3,byte value4,byte value5,byte value6,byte value7,byte value8){
//        return new ParamTlvBean(TAG_SYNCHRONIZE,(byte)0x08,new byte[]{value1,value2,value3,value4,value5,value6,value7,value8});
//    }

    /*//status param
    private static final byte M11X_TAG_DEVICE_TYPE = (byte)0x01;//设备类型 Device Type ;  size : 1
    private static final byte M11X_TAG_DEVICE_FIRMWARE_VERSION= (byte)0x03;//固件版本号 Dievce Firmware revision ;  size : 1
    private static final byte M11X_TAG_DEVICE_REGISTRATION_INFO= (byte)0x04;//注册信息 Registration Information ;  size : 1

    private static final byte M11X_TAG_DEVICE_MODEL = (byte)0x10;//设备型号 Device model ;  size : 1
    private static final byte M11X_TAG_DEVICE_AUDIO_MODE = (byte)0x11;//音频类型 Audio mode ;  size : 1
    private static final byte M11X_TAG_DEVICE_BATTERY_STATUS = (byte)0x12;//电池状态 Battery status ;  size : 1
    private static final byte M11X_TAG_DEVICE_ROTATE_FAILURE = (byte)0x13;//旋转故障 Rotate failure ;  size : 1

    private static final byte M11X_TAG_DEVICE_ALARM_1_STATUS = (byte)0x21;//闹钟1-状态 Alarm-1 status ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_2_STATUS = (byte)0x22;//闹钟2-状态 Alarm-2 status ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_3_STATUS = (byte)0x23;//闹钟3-状态 Alarm-3 status ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_4_STATUS = (byte)0x24;//闹钟4-状态 Alarm-4 status ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_5_STATUS = (byte)0x25;//闹钟5-状态 Alarm-5 status ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_6_STATUS = (byte)0x26;//闹钟6-状态 Alarm-6 status ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_7_STATUS = (byte)0x27;//闹钟7-状态 Alarm-7 status ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_8_STATUS = (byte)0x28;//闹钟8-状态 Alarm-8 status ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_9_STATUS = (byte)0x29;//闹钟9-状态 Alarm-9 status ;  size : 1


    //config param
    private static final byte M11X_TAG_DEVICE_TIME_FORMAT = (byte)0x50;//时间格式 Time format ;  size : 1
    private static final byte M11X_TAG_DEVICE_BELL_TYPE = (byte)0x51;//铃声种类 The bell type ;  size : 1
    private static final byte M11X_TAG_DEVICE_VOLUME = (byte)0x52;//设备音量 Device volume ;  size : 1
    private static final byte M11X_TAG_DEVICE_REMIND_TIME = (byte)0x53;//提醒时长 Remind time ;  size : 1
    private static final byte M11X_TAG_DEVICE_KEY_TONE = (byte)0x53;//按键音开关 key tone； size : 1

    private static final byte M11X_TAG_DEVICE_ALARM_1_HOUR = (byte)0x61;//闹钟1-小时 Alarm-1 hour ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_2_HOUR = (byte)0x62;//闹钟2-小时 Alarm-2 hour ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_3_HOUR = (byte)0x63;//闹钟3-小时 Alarm-3 hour ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_4_HOUR = (byte)0x64;//闹钟4-小时 Alarm-4 hour ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_5_HOUR = (byte)0x65;//闹钟5-小时 Alarm-5 hour ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_6_HOUR = (byte)0x66;//闹钟6-小时 Alarm-6 hour ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_7_HOUR = (byte)0x67;//闹钟7-小时 Alarm-7 hour ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_8_HOUR = (byte)0x68;//闹钟8-小时 Alarm-8 hour ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_9_HOUR = (byte)0x69;//闹钟9-小时 Alarm-9 hour ;  size : 1

    private static final byte M11X_TAG_DEVICE_ALARM_1_MINUTE = (byte)0x71;//闹钟1-分钟 Alarm-1 minute ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_2_MINUTE = (byte)0x72;//闹钟2-分钟 Alarm-2 minute ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_3_MINUTE = (byte)0x73;//闹钟3-分钟 Alarm-3 minute ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_4_MINUTE = (byte)0x74;//闹钟4-分钟 Alarm-4 minute ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_5_MINUTE = (byte)0x75;//闹钟5-小时 Alarm-5 minute ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_6_MINUTE = (byte)0x76;//闹钟6-分钟 Alarm-6 minute ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_7_MINUTE = (byte)0x77;//闹钟7-分钟 Alarm-7 minute ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_8_MINUTE = (byte)0x78;//闹钟8-分钟 Alarm-8 minute ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_9_MINUTE = (byte)0x79;//闹钟9-分钟 Alarm-9 minute ;  size : 1

    private static final byte M11X_TAG_DEVICE_ALARM_1_SWITCH = (byte)0x81;//闹钟1-开关 Alarm-1 switch ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_2_SWITCH = (byte)0x82;//闹钟2-开关 Alarm-2 switch ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_3_SWITCH = (byte)0x83;//闹钟3-开关 Alarm-3 switch ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_4_SWITCH = (byte)0x84;//闹钟4-开关 Alarm-4 switch ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_5_SWITCH = (byte)0x85;//闹钟5-开关 Alarm-5 switch ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_6_SWITCH = (byte)0x86;//闹钟6-开关 Alarm-6 switch ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_7_SWITCH = (byte)0x87;//闹钟7-开关 Alarm-7 switch ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_8_SWITCH = (byte)0x88;//闹钟8-开关 Alarm-8 switch ;  size : 1
    private static final byte M11X_TAG_DEVICE_ALARM_9_SWITCH = (byte)0x89;//闹钟9-开关 Alarm-9 switch ;  size : 1

    //event param
    private static final byte M11X_TAG_DEVICE_TAKE_MEDICINE_EVENT = (byte)0xb0;//取药事件Take medicine event ;  size ：8

    //ctrl param
    private static final byte M11X_TAG_DEVICE_REBOOT = (byte)0xd2;//设备重启 reboot
    private static final byte M11X_TAG__DEVICE_RESET = (byte)0xd3;//恢复出厂设置 reset

    private static final byte M11X_TAG_DEVICE_SYNCHRONIZE_TIME = (byte)0xe0;//时间校准 Device Synchronize Time
    private static final byte M11X_TAG_DEVICE_CURRENT_ALARM_MUTE  = (byte)0xe2;//当前闹钟静音 The current alarm is muted
    private static final byte M11X_TAG_DEVICE_BELL_SWITCH = (byte)0xe3;//响铃开关 Bell switch
    private static final byte M11X_TAG_DEVICE_EARLY_DOSE = (byte)0xe4;//提前取药 Early Dose*/
}
