package com.ipath.hospitaldevice.ble.medicaldevice

/**
 * Created by Jimmy on 2019/1/10.
 */
object CmdConst {
    const val CMD_STATE_OK = 0x00.toByte() //请求成功 OK
    const val CMD_FUNC_OK = 0x00.toByte()
    const val CMD_HEAD = 0xbb.toByte() //请求头 request header
    const val CMD_HEAD_DEVICE = 0x11.toByte()
    const val CMD_APP_LOGIN = 0x81.toByte()
    const val CMD_APP_KEY = 0x02.toByte()
    const val CMD_APP_READ_CONFIG = 0x0a.toByte() //配置参数  configuration parameter
    const val CMD_APP_READ_STATE = 0x0b.toByte() //状态参数 state parameter
    const val CMD_APP_READ_CONTROL = 0x0c.toByte() //控制参数  controls parameter
    const val CMD_APP_READ_EVENT = 0x0d.toByte() //事件参数 event parameter
    const val CMD_APP_SYNCHRONIZE = 0x08.toByte()
    const val CMD_TLV_SYNCHRONIZE = 0xe0.toByte()
    const val CMD_APP_GET_CONFIG = 0x05.toByte()
    const val CMD_APP_SET_CONFIG = 0x06.toByte()
    const val CMD_APP_GET_STATE = 0x07.toByte()
    const val CMD_APP_NOTIFY_A = 0x83.toByte()
    const val CMD_APP_NOTIFY_B = 0x84.toByte()
    const val CMD_APP_UNBIND = 0xD3.toByte() //(byte)0xe1;
    const val CMD_APP_MUTE = 0xe2.toByte()
    const val CMD_DEVICE_LOGIN = 0x01.toByte()
    const val CMD_DEVICE_KEY = 0x82.toByte()
    const val CMD_DEVICE_GET_CONFIG = 0x85.toByte()
    const val CMD_DEVICE_SET_CONFIG = 0x86.toByte()
    const val CMD_DEVICE_SET_STATE = 0x87.toByte()
    const val CMD_DEVICE_SYNCHRONIZE = 0x88.toByte()
    const val CMD_DEVICE_CONFIG = 0x8a.toByte()
    const val CMD_DEVICE_STATE = 0x8b.toByte()
    const val CMD_DEVICE_CONTROL = 0x8c.toByte()
    const val CMD_DEVICE_EVENT = 0x8d.toByte()
    const val CMD_DEVICE_NOTIFY_A = 0x03.toByte()
    const val CMD_DEVICE_NOTIFY_B = 0x04.toByte()
    const val CMD_TLV_DEVICE_TIME_FORMAT = 0x50.toByte() //时间格式 time format
    const val CMD_TLV_DEVICE_BEEP_KIND = 0x51.toByte() //铃声种类 The bell type
    const val CMD_TLV_DEVICE_VOLUME = 0x52.toByte() //设备音量 Audio equipment
    const val CMD_TLV_DEVICE_REMIND_TIME = 0x53.toByte() //提醒时长 Remind the length
    const val CMD_TLV_DEVICE_ALARM = 0x53.toByte() //闹钟54-59 用时+1 Clocks 54-59 use +1
    const val CMD_TLV_DEVICE_BATTERY_CAP = 0x10.toByte() //电量 electric quantity
    const val CMD_TLV_DEVICE_BATTERY_STATE = 0x11.toByte() //电池状态 battery status
    const val CMD_TLV_DEVICE_NOTIFY_MEDICINE = 0xb0.toByte()
    const val CMD_APP_CONTROL = 0x08.toByte()
    const val CMD_TLV_DEVICE_BATTERY_STATE_M11X = 0x12.toByte()
    const val CMD_TLV_DEVICE_KEYPAD_STONE_M11X = 0x5a.toByte()
    const val CMD_TLV_DEVICE_ALARM_HOUR_M11X = 0x60.toByte()
    const val CMD_TLV_DEVICE_ALARM_MINUTE_M11X = 0x70.toByte()
    const val CMD_TLV_DEVICE_ALARM_SWITCH_M11X = 0x80.toByte()
    const val CMD_ALARM_STATE = 0x20.toByte()
    const val CMD_APP_TAKE_EDICINE_ADVANCE = 0xe4.toByte()
    const val CMD_DEVICE_REBOOT = 0xd2.toByte()
    const val CMD_DEVICE_RESET = 0xd3.toByte()
}