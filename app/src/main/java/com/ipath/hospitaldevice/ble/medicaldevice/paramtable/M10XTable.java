package com.ipath.hospitaldevice.ble.medicaldevice.paramtable;

import com.zayata.zayatabluetoothsdk.bean.ParamTlvBean;

import java.util.ArrayList;
import java.util.List;


/**
 * M10X系列 tag工具类 Tags Tool
 *  Created by linyb on 21/4/13.
 */

public class M10XTable {

    //状态参数
    public static final byte TAG_DEV_TYPE = (byte) 0x01;//设备类型 Device Type ; size : 1
    public static final byte TAG_DEV_VERSION = (byte) 0x03;//固件版本号 Dievce Firmware revision ; size : 1
    public static final byte TAG_REG_INFO = (byte) 0x04;//注册信息 Registration Information ; size : 1
    public static final byte TAG_BAT_LEVEL = (byte) 0x10;//电池电量 Battery level ;  size : 1
    public static final byte TAG_BAT_STAT = (byte) 0x11;//电池状态 Battery status ;  size : 1

    //配置参数
    public static final byte TAG_TIME_FORMAT = (byte) 0x50;//时间格式 Time format ;  size : 1
    public static final byte TAG_RING_TYPE = (byte) 0x51;//铃声种类 The bell type ;  size : 1
    public static final byte TAG_VOLUME = (byte) 0x52;//设备音量 Device volume ;  size : 1
    public static final byte TAG_ALARM_KEEP = (byte) 0x53;//提醒时长 Remind time ;  size : 1
    public static final byte TAG_ALARM_1 = 0x54;//闹钟1 Alarm-1 ; size : 4
    public static final byte TAG_ALARM_2 = 0x55;//闹钟2 Alarm-2 ; size : 4
    public static final byte TAG_ALARM_3 = 0x56;//闹钟3 Alarm-3 ; size : 4
    public static final byte TAG_ALARM_4 = 0x57;//闹钟4 Alarm-4 ; size : 4
    public static final byte TAG_ALARM_5 = 0x58;//闹钟5 Alarm-5 ; size : 4
    public static final byte TAG_ALARM_6 = 0x59;//闹钟6 Alarm-6 ; size : 4
    public static final byte TAG_KEY_TONE = 0x5A;//按键音开关 key tone； size : 1

    //事件参数
    public static final byte TAG_TAKE_RECORD = (byte) 0xB0;//取药事件Take medicine event ;  size : 10

    //控制参数
    public static final byte TAG_PUB_KEY = (byte) 0xD0;//公钥 public key ; size : 2
    public static final byte TAG_PRI_KEY = (byte) 0xD1;//私钥 private  key ; size : 2
    public static final byte TAG_REBOOT = (byte) 0xD2;//设备重启 reboot ;  size : 1
    public static final byte TAG_FACTORY = (byte) 0xD3;//恢复出厂设置 reset ;  size : 1
    public static final byte TAG_TIME_CAL = (byte) 0xE0;//时间校准 Device Synchronize Time ;  size ：8
    public static final byte TAG_THIS_MUTE = (byte) 0xE2;//当前闹钟静音 The current alarm is muted ;  size : 1

    static ParamTlvBean[] paramTlvBeanList = new ParamTlvBean[]{
            new ParamTlvBean(TAG_DEV_TYPE,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_DEV_VERSION,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_REG_INFO,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_BAT_LEVEL,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_BAT_STAT,(byte)0x01,new byte[]{0x00}),

            new ParamTlvBean(TAG_TIME_FORMAT,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_RING_TYPE,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_VOLUME,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_KEEP,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_ALARM_1,(byte)0x04,new byte[]{0x00,0x00,0x00,0x00}),
            new ParamTlvBean(TAG_ALARM_2,(byte)0x04,new byte[]{0x00,0x00,0x00,0x00}),
            new ParamTlvBean(TAG_ALARM_3,(byte)0x04,new byte[]{0x00,0x00,0x00,0x00}),
            new ParamTlvBean(TAG_ALARM_4,(byte)0x04,new byte[]{0x00,0x00,0x00,0x00}),
            new ParamTlvBean(TAG_ALARM_5,(byte)0x04,new byte[]{0x00,0x00,0x00,0x00}),
            new ParamTlvBean(TAG_ALARM_6,(byte)0x04,new byte[]{0x00,0x00,0x00,0x00}),
            new ParamTlvBean(TAG_KEY_TONE,(byte)0x01,new byte[]{0x00}),

            new ParamTlvBean(TAG_TAKE_RECORD,(byte)0x0a,new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00}),

            new ParamTlvBean(TAG_PUB_KEY,(byte)0x02,new byte[]{0x00,0x00}),
            new ParamTlvBean(TAG_PRI_KEY,(byte)0x02,new byte[]{0x00,0x00}),
            new ParamTlvBean(TAG_REBOOT,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_FACTORY,(byte)0x01,new byte[]{0x00}),
            new ParamTlvBean(TAG_TIME_CAL,(byte)0x08,new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00}),
            new ParamTlvBean(TAG_THIS_MUTE,(byte)0x01,new byte[]{0x00}),
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

    public static byte[] alarmTagList = new byte[]{
            TAG_ALARM_1,
            TAG_ALARM_2,
            TAG_ALARM_3,
            TAG_ALARM_4,
            TAG_ALARM_5,
            TAG_ALARM_6
    };

//    public static ParamTlvBean deviceTimeFormat(byte value){
//        return new ParamTlvBean(TAG_DEVICE_TIME_FORMAT,(byte)0x01,new byte[]{value});
//    }
//
//    public static ParamTlvBean synchronizeDeviceTime(byte value1,byte value2,byte value3,byte value4,byte value5,byte value6,byte value7,byte value8){
//        return new ParamTlvBean(TAG_SYNCHRONIZE,(byte)0x08,new byte[]{value1,value2,value3,value4,value5,value6,value7,value8});
//    }

}
