package com.ipath.hospitaldevice.ble.medicaldevice.example;

import com.ipath.hospitaldevice.ble.medicaldevice.CmdConst;

import com.ipath.hospitaldevice.ble.medicaldevice.bean.BoxInfoBean;
import com.zayata.zayatabluetoothsdk.bean.AlarmBean;
import com.zayata.zayatabluetoothsdk.bean.ParamOpBean;
import com.zayata.zayatabluetoothsdk.bean.ParamOpRespCbBean;
import com.zayata.zayatabluetoothsdk.bean.ParamTlvBean;
import com.zayata.zayatabluetoothsdk.bluetooth.BluetoothManager;
import com.zayata.zayatabluetoothsdk.callback.DevParamCallBack;
import com.zayata.zayatabluetoothsdk.utils.ByteUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by linyb
 * Time on 2021/2/7
 * SDK工具类
 */
public class m11x_lebox {

    static int opId = 0;
    
    /*
     * Get device configuration information
     * 获取设备配置信息
     * mac : 设备mac地址 Device MAC address
     * */
    public static void getBoxInfo(String mac, final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_TIME_FORMAT, (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean2 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_BEEP_KIND, (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean3 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_VOLUME, (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean4 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_REMIND_TIME, (byte)0x01, new byte[]{(byte)0x00});
        configList.add(deviceTagsBean1);
        configList.add(deviceTagsBean2);
        configList.add(deviceTagsBean3);
        configList.add(deviceTagsBean4);


        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (paramOpRespCbBean.getStat() == 0) {
                    List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                    /*
                     * Time_format：时间格式
                     * Alarm_ring：铃声类型
                     * Alarm_voice：音量
                     * Alarm_clock_duration：闹钟提醒时长
                     *
                     * Time_format: Time format
                     * Alarm_ring: Ring type
                     * Alarm_voice: Volume
                     * Alarm_clock_duration: The duration of the alarm clock
                     * */
                    BoxInfoBean boxInfoBean = new BoxInfoBean();
                    boxInfoBean.setTime_format(data.get(0).getValue()[0]);
                    boxInfoBean.setAlarm_ring(data.get(1).getValue()[0]);
                    boxInfoBean.setAlarm_voice(data.get(2).getValue()[0]);
                    boxInfoBean.setAlarm_clock_duration(data.get(3).getValue()[0]);

                    if (devParamCallBack != null) {
                        devParamCallBack.respCb(paramOpRespCbBean);
                    }
                }else {

                }
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);
    }

    /*
     * Device mute
     * 设备静音
     * mac : 设备mac地址 Device MAC address
     * */
    public static void muteBox(final String mac, final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_VOLUME, (byte)0x01, new byte[]{(byte)0x00});
        configList.add(deviceTagsBean1);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean);
                }
            }

        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);

    }

    /*
     * Sets the device ring type
     * 设置设备响铃类型
     * mac : 设备mac地址 Device MAC address
     *
     * kind：响铃类型
     * kind：ring type
     * */
    public static void setBeepKind(String mac, int kind, final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_VOLUME, (byte)0x01, new byte[]{(byte)kind});
        configList.add(deviceTagsBean1);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean respCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(respCbBean);
                }
            }

        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);

    }

    /*
     * Gets the current ringing type of the device
     * 获取设备当前响铃类型
     * mac : 设备mac地址 Device MAC address
     * */
    public static void getBeepKind(final String mac, final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_BEEP_KIND, (byte)0x01, new byte[]{(byte)0x00});
        configList.add(deviceTagsBean1);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                switch (paramOpRespCbBean.getParams().get(0).getValue()[0]){
                    case (byte)0x00:
                        //铃声1 The bell 1
                        break;
                    case (byte)0x01:
                        //铃声2 The bell 2
                        break;
                }
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean);
                }
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    /*
     * Set device alarm
     * 设置设备闹钟
     * mac : 设备mac地址 Device MAC address
     *
     * seq：闹钟ID
     * hour：小时
     * min：分钟
     *
     * seq: Alarm ID
     * hour：hours
     * min: minutes
     * */
    public static void setAlarm(final String mac, final int seq, int hour, int min, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_HOUR_M11X + seq), (byte)0x01, new byte[]{(byte)hour});
        ParamTlvBean deviceTagsBean2 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_MINUTE_M11X + seq), (byte)0x01, new byte[]{(byte)min});
        ParamTlvBean deviceTagsBean3 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + seq), (byte)0x01, new byte[]{(byte)0x01});
        configList.add(deviceTagsBean1);
        configList.add(deviceTagsBean2);
        configList.add(deviceTagsBean3);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean);
                }
            }

        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);
    }

    /*
     * Set multiple alarms
     * 设置多个闹钟
     * mac : 设备mac地址 Device MAC address
     *
     * index:Default 0
     * AlarmData：
     * List<AlarmBean> AlarmData = new ArrayList<>();
     * AlarmBean alarmBean = new AlarmBean(1,1,8,2);//set alarm 08:02
     * AlarmData.add(alarmBean);
     * */

    public static void setAlarmOneByOne(final String mac, final int index, final List<AlarmBean> AlarmData, final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_HOUR_M11X + AlarmData.get(index).getSeq()), (byte)0x01, new byte[]{(byte)AlarmData.get(index).getHour()});
        ParamTlvBean deviceTagsBean2 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_MINUTE_M11X + AlarmData.get(index).getSeq()), (byte)0x01, new byte[]{(byte)AlarmData.get(index).getMinute()});
        ParamTlvBean deviceTagsBean3 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + AlarmData.get(index).getSeq()), (byte)0x01, new byte[]{(byte)AlarmData.get(index).getStatus()});
        configList.add(deviceTagsBean1);
        configList.add(deviceTagsBean2);
        configList.add(deviceTagsBean3);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if(index < AlarmData.size()-1){
                    setAlarmOneByOne(mac,index+1,AlarmData, devParamCallBack);
                }else {
                    if (devParamCallBack != null) {
                        devParamCallBack.respCb(paramOpRespCbBean);
                    }
                }
            }

        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);

    }

    /*
     * Gets device alarm details
     * 获取设备闹钟详情
     * mac : 设备mac地址 Device MAC address
     *
     * seq：闹钟ID
     *
     * seq: Alarm ID
     * */
    public static void getAlarm(final String mac, final int seq, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_HOUR_M11X + seq), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean2 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_MINUTE_M11X + seq), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean3 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + seq), (byte)0x01, new byte[]{(byte)0x01});
        configList.add(deviceTagsBean1);
        configList.add(deviceTagsBean2);
        configList.add(deviceTagsBean3);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                int state = data.get(2).getValue()[0];
                int hour = data.get(0).getValue()[0];
                int minute = data.get(1).getValue()[0];
                AlarmBean alarmBean = new AlarmBean(seq,state,hour,minute);
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean);
                }
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    static List<AlarmBean> alarmList = new ArrayList<>();
    /*
     * Gets a list of device alarms
     * 获取设备闹钟列表
     * mac : 设备mac地址 Device MAC address
     * */
    public static void getAlarmList(final String mac, final DevParamCallBack devParamCallBack) {
        if(alarmList != null && alarmList.size() > 0){
            alarmList.removeAll(alarmList);
        }
//        for (int i = 1;i < 10;i++) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_HOUR_M11X + 1), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean2 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_MINUTE_M11X + 1), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean3 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + 1), (byte)0x01, new byte[]{(byte)0x01});
        configList.add(deviceTagsBean1);
        configList.add(deviceTagsBean2);
        configList.add(deviceTagsBean3);


        final int finalI = 1;

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                int state = data.get(2).getValue()[0];
                int hour = data.get(0).getValue()[0];
                int minute = data.get(1).getValue()[0];
                AlarmBean alarmBean = new AlarmBean(finalI, state, hour, minute);
                alarmList.add(alarmBean);

                getAlarmList(mac,2, devParamCallBack);
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);


    }

    private static void getAlarmList(final String mac, final int index, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_HOUR_M11X + index), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean2 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_MINUTE_M11X + index), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean3 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + index), (byte)0x01, new byte[]{(byte)0x01});
        configList.add(deviceTagsBean1);
        configList.add(deviceTagsBean2);
        configList.add(deviceTagsBean3);

        final int finalI = index;

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                int state = data.get(2).getValue()[0];
                int hour = data.get(0).getValue()[0];
                int minute = data.get(1).getValue()[0];
                AlarmBean alarmBean = new AlarmBean(finalI, state, hour, minute);
                alarmList.add(alarmBean);

                if(index < 9) {
                    getAlarmList(mac,index+1, devParamCallBack);
                }else {
                    if (devParamCallBack != null) {
                        paramOpRespCbBean.setAlarmList(alarmList);
                        devParamCallBack.respCb(paramOpRespCbBean);
                    }
                }
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    /*
     * Switch off device alarm
     * 关闭设备闹钟
     * mac : 设备mac地址 Device MAC address
     *
     * seq：闹钟ID
     *
     * seq: Alarm ID
     * */
    public static void deleteAlarm(String mac, final int seq, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();
//        ParamTlvBean parameters0 = new HashMap<>();
//        parameters0.put("tag", (byte)(CmdConst.CMD_TLV_DEVICE_ALARM_HOUR_M11X + seq));
//        parameters0.put("size", (byte)0x01);
//        parameters0.put("parameters", new byte[]{(byte)0x00});
//        configList.add(parameters0);
//        ParamTlvBean parameters1 = new HashMap<>();
//        parameters1.put("tag", (byte)(CmdConst.CMD_TLV_DEVICE_ALARM_MINUTE_M11X + seq));
//        parameters1.put("size", (byte)0x01);
//        parameters1.put("parameters", new byte[]{(byte)0x00});
//        configList.add(parameters1);

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + seq), (byte)0x01, new byte[]{(byte)0x00});
        configList.add(deviceTagsBean1);

        ParamOpBean setConfigParam = new ParamOpBean();
        setConfigParam.setDn(mac);
        setConfigParam.setOpId(opId++);
        setConfigParam.setBusy(false);
        setConfigParam.setParams(configList);
        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().setDevConf(setConfigParam);

    }

    /*
     * close all device alarm
     * 关闭全部设备闹钟
     * mac : 设备mac地址 Device MAC address
     * */
    public static void deleteAllAlarm(final String mac, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + 1), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean2 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + 2), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean3 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + 3), (byte)0x01, new byte[]{(byte)0x00});
        configList.add(deviceTagsBean1);
        configList.add(deviceTagsBean2);
        configList.add(deviceTagsBean3);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                deleteAllAlarm(mac,4,5,6, devParamCallBack);
            }

        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);

    }

    private static void deleteAllAlarm(final String mac, final int index1, int index2, final int index3, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + index1), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean2 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + index2), (byte)0x01, new byte[]{(byte)0x00});
        ParamTlvBean deviceTagsBean3 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + index3), (byte)0x01, new byte[]{(byte)0x00});
        configList.add(deviceTagsBean1);
        configList.add(deviceTagsBean2);
        configList.add(deviceTagsBean3);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if(index3 < 9) {
                    deleteAllAlarm(mac,7,8,9, devParamCallBack);
                }else {
                    if (devParamCallBack != null) {
                        devParamCallBack.respCb(paramOpRespCbBean);
                    }
                }
            }

        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);

    }

    /*
     * Set device volume
     * 设置设备音量
     * mac : 设备mac地址 Device MAC address
     *
     * level：音量
     * 0：静音
     * 1：小
     * 2：大
     *
     * level: volume
     * 0：mute
     * 1：little
     * 2：big
     * */
    public static void setVolume(String mac, int level, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_VOLUME, (byte)0x01, new byte[]{(byte)level});
        configList.add(deviceTagsBean1);

        ParamOpBean setConfigParam = new ParamOpBean();
        setConfigParam.setDn(mac);
        setConfigParam.setOpId(opId++);
        setConfigParam.setBusy(false);
        setConfigParam.setParams(configList);
        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().setDevConf(setConfigParam);

    }

    /*
     * Get device volume
     * 获取设备音量
     * mac : 设备mac地址 Device MAC address
     * */
    public static void getVolume(final String mac, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_VOLUME, (byte)0x01, new byte[]{(byte)0x00});
        configList.add(deviceTagsBean1);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                switch (data.get(0).getValue()[0]){
                    case (byte)0x00:
                        //静音 mute
                        break;
                    case (byte)0x01:
                        //小 little
                        break;
                    case (byte)0x02:
                        //大 high
                        break;
                }
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    /*
     * Set the device time format
     * 设置设备时间格式
     * mac : 设备mac地址 Device MAC address
     *
     * format：时间格式
     * 0：24小时制
     * 1：12小时制
     *
     * format: time format
     * 0：24-hour
     * 1：12-hour
     * */
    public static void setBoxTimeFormat(final String mac, int format, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_TIME_FORMAT, (byte)0x01, new byte[]{(byte)format});
        configList.add(deviceTagsBean1);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);

    }

    /*
     * Get the device time format
     * 获取设备时间格式
     * mac : 设备mac地址 Device MAC address
     * */
    public static void getBoxTimeFormat(String mac, final DevParamCallBack devParamCallBack) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_TIME_FORMAT, (byte)0x01, new byte[]{(byte)0x00});
        configList.add(deviceTagsBean1);

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(configList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                switch (data.get(0).getValue()[0]){
                    case (byte)0x00:
                        //24小时制 24-hour
                        break;
                    case (byte)0x01:
                        //12小时制 12-hour
                        break;
                }
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    /*
     * Set Device Time
     * 设置设备时间
     * mac : 设备mac地址 Device MAC address
     * */
    public static void setSysTime(String mac,final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> ctrlList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        int year = cal.get(Calendar.YEAR);
        byte[] byteYear = ByteUtil.intToByteArray(year);
        byte[] data = new byte[8];
        data[0] = byteYear[0];//年 year
        data[1] = byteYear[1];
        data[2] = (byte)(cal.get(Calendar.MONTH) + 1);//月 month
        data[3] = (byte)cal.get(Calendar.DATE);//日 day
        data[4] = (byte)cal.get(Calendar.HOUR_OF_DAY);//小时 hour
        data[5] = (byte)cal.get(Calendar.MINUTE);//分钟 minute
        data[6] = (byte)cal.get(Calendar.SECOND);//秒 second
        data[7] = (byte)(cal.get(Calendar.DAY_OF_WEEK) - 1);//星期 week

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_SYNCHRONIZE, (byte)0x08, data);
        ctrlList.add(deviceTagsBean1);


        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(ctrlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }

    /*
     * Gets the device battery status
     * 获取设备电池状态
     * mac : 设备mac地址 Device MAC address
     * */
    public static void getBatteryInfo(String mac,final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> stateList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_BATTERY_STATE_M11X, (byte)0x01, new byte[]{(byte)0x00});
        stateList.add(deviceTagsBean1);

        ParamOpBean stateParam = new ParamOpBean();
        stateParam.setDn(mac);
        stateParam.setOpId(opId++);
        stateParam.setBusy(false);
        stateParam.setParams(stateList);
        stateParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                switch (data.get(0).getValue()[0]){
                    case (byte)0x00:
                        //电量正常 Normal
                        break;
                    case (byte)0x01:
                        //电量低 Low
                        break;
                }
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().getDevStat(stateParam);

    }

    /*
     * Gets the device version number
     * 获取设备版本号
     * mac : 设备mac地址 Device MAC address
     * */
    public static void getBoxVersionCode(String mac,final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> stateList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)0x03, (byte)0x01, new byte[]{(byte)0x00});
        stateList.add(deviceTagsBean1);

        ParamOpBean stateParam = new ParamOpBean();
        stateParam.setDn(mac);
        stateParam.setOpId(opId++);
        stateParam.setBusy(false);
        stateParam.setParams(stateList);
        stateParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                String version = ((data.get(0).getValue()[0] & 0xF0) >> 4) + "." + (data.get(0).getValue()[0] & 0x0F);
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().getDevStat(stateParam);

    }

    /* ------------------------------------------------------  M110 ---------------------------------------------------------------*/

    /*
     * Early Dose
     * 提前取药
     * mac : 设备mac地址 Device MAC address
     * */
    public static void earlyDose(String mac,final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> controlList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_APP_TAKE_EDICINE_ADVANCE, (byte)0x01, new byte[]{(byte)0x00});
        controlList.add(deviceTagsBean1);

        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(controlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }


    /*
     * Get the device model
     * 获取设备型号
     * mac : 设备mac地址 Device MAC address
     * */
    public static void getDeviceModel(String mac,final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> stateList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)0x10, (byte)0x01, new byte[]{(byte)0x00});
        stateList.add(deviceTagsBean1);

        ParamOpBean stateParam = new ParamOpBean();
        stateParam.setDn(mac);
        stateParam.setOpId(opId++);
        stateParam.setBusy(false);
        stateParam.setParams(stateList);
        stateParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                switch (data.get(0).getValue()[0]){
                    case (byte)0x00:
                        //M110
                        break;
                    case (byte)0x02:
                        //M112
                        break;
                    case (byte)0x05:
                        //M115
                        break;
                }
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().getDevStat(stateParam);

    }

    /*
     * device reboot
     * 设备重启
     * mac : 设备mac地址 Device MAC address
     * */
    public static void setDeviceReboot(String mac,final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> ctrlList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_DEVICE_REBOOT, (byte)0x01, new byte[]{(byte)0x00});
        ctrlList.add(deviceTagsBean1);

        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(ctrlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }

    /*
     * Device Restore Factory Settings (Clears Device Data)
     * 设备恢复出厂设置（清空设备数据）
     * mac : 设备mac地址 Device MAC address
     * */
    public static void setDeviceReset(String mac,final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> ctrlList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_DEVICE_RESET, (byte)0x01, new byte[]{(byte)0x00});
        ctrlList.add(deviceTagsBean1);

        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(ctrlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }

    /*
     * Gets the device audio type
     * 获取设备音频类型
     * mac : 设备mac地址 Device MAC address
     * */
    public static void getDeviceAudioType(String mac,final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> stateList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)0x11, (byte)0x01, new byte[]{(byte)0x00});
        stateList.add(deviceTagsBean1);

        ParamOpBean stateParam = new ParamOpBean();
        stateParam.setDn(mac);
        stateParam.setOpId(opId++);
        stateParam.setBusy(false);
        stateParam.setParams(stateList);
        stateParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                switch (data.get(0).getValue()[0]){
                    case (byte)0x00:
                        //无 null
                        break;
                    case (byte)0x01:
                        //蜂鸣器 buzzer
                        break;
                    case (byte)0x02:
                        //语音 voice
                        break;
                }
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().getDevStat(stateParam);

    }

    /*
     * The current alarm is muted (taking the medicine time, the device has a sound reminder, let the current alarm clock mute, does not affect the next alarm)
     * 当前闹钟静音(服药时间到，设备发出声音提醒，让当前闹钟静音，不影响下次闹钟)
     * mac : 设备mac地址 Device MAC address
     * */
    public static void currentAlarmMuted(String mac,final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> ctrlList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_APP_MUTE, (byte)0x01, new byte[]{(byte)0x01});
        ctrlList.add(deviceTagsBean1);

        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(ctrlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }

    /*
     * Device alarm clock to remind the time
     * 设备闹钟提醒时长
     * mac : 设备mac地址 Device MAC address
     * minute:1-240
     * */
    public static void setDeviceRemindTime(final String mac, int minute, final DevParamCallBack devParamCallBack){
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_REMIND_TIME, (byte)0x01, new byte[]{(byte)minute});
        configList.add(deviceTagsBean1);

        ParamOpBean setConfigParam = new ParamOpBean();
        setConfigParam.setDn(mac);
        setConfigParam.setOpId(opId++);
        setConfigParam.setBusy(false);
        setConfigParam.setParams(configList);
        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                if (devParamCallBack != null) {
                    devParamCallBack.respCb(paramOpRespCbBean) ;
                }
            }

        });

        BluetoothManager.getInstance().setDevConf(setConfigParam);

    }

}
