package com.ipath.hospitaldevice.ble.medicaldevice.example;

import android.util.Log;

import com.ipath.hospitaldevice.ble.medicaldevice.CmdConst;
import com.ipath.hospitaldevice.ble.medicaldevice.paramtable.M11XTable;
;
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
public class m11x {
    static int opId = 0;
    public m11x(int opId){
       this.opId=opId;
    }
    /*
     * Get device configuration information
     * 获取设备配置信息
     * mac:设备mac地址 device mac adress
     * */
    public static void getDeviceInfo(String mac){
//        List<ParamTlvBean> configList = new ArrayList<>();
//        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_TIME_FORMAT, (byte)0x01, new byte[]{(byte)0x00});
//        ParamTlvBean deviceTagsBean2 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_BEEP_KIND, (byte)0x01, new byte[]{(byte)0x00});
//        ParamTlvBean deviceTagsBean3 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_VOLUME, (byte)0x01, new byte[]{(byte)0x00});
//        ParamTlvBean deviceTagsBean4 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_REMIND_TIME, (byte)0x01, new byte[]{(byte)0x00});
//        configList.add(deviceTagsBean1);
//        configList.add(deviceTagsBean2);
//        configList.add(deviceTagsBean3);
//        configList.add(deviceTagsBean4);

        //Use the M11xTable util
        List<ParamTlvBean> confList = M11XTable.get(M11XTable.TAG_TIME_FORMAT, M11XTable.TAG_RING_TYPE, M11XTable.TAG_VOLUME, M11XTable.TAG_ALARM_KEEP);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }


        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setParams(confList);
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
//                    Log.d("linyb","   //M115"+ boxInfoBean.);

                    //do something
                }else {

                }
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);
    }

    /*
     * Sets the device ring type
     * 设置设备响铃类型
     * mac:设备mac地址 device mac adress
     * kind：响铃类型，参数值范围：0-1
     * kind：ring type, param value scope ：0-1
     * kind = 0：铃声1 bell1
     * kind = 1：铃声2 bell2
     * */
    public static void setRingType(String mac, int kind){
//        List<ParamTlvBean> configList = new ArrayList<>();
//        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_BEEP_KIND, (byte)0x01, new byte[]{(byte)kind});
//        configList.add(deviceTagsBean1);

        List<ParamTlvBean> confList = M11XTable.get(M11XTable.TAG_RING_TYPE);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }
        confList.get(0).setValue(new byte[]{(byte)kind});

        ParamOpBean setConfigParam = new ParamOpBean();
        setConfigParam.setDn(mac);
        setConfigParam.setOpId(opId++);
        setConfigParam.setBusy(false);
        setConfigParam.setParams(confList);
        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean respCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevConf(setConfigParam);

    }

    /*
     * Gets the current ringing type of the device
     * 获取设备当前响铃类型
     * mac:设备mac地址 device mac adress
     * */
    public static void getRingType(final String mac){
//        List<ParamTlvBean> configList = new ArrayList<>();
//        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_BEEP_KIND, (byte)0x01, new byte[]{(byte)0x00});
//        configList.add(deviceTagsBean1);

        List<ParamTlvBean> confList = M11XTable.get(M11XTable.TAG_RING_TYPE);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(confList);
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
                //do something
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    /*
     * Set device alarm
     * 设置设备闹钟
     * mac:设备mac地址 device mac adress
     * seq：闹钟ID ,参数值范围：1-9
     * hour：小时
     * min：分钟
     *
     * seq: Alarm ID ,param value scope：1-9
     * hour：hours,param value scope：0-23
     * min: minute,param value scope：0-59
     * */
    public static void setAlarm(final String mac, final int seq, int hour, int min,DevParamCallBack devParamCallBack) {
//        List<ParamTlvBean> configList = new ArrayList<>();
//        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_HOUR_M11X + seq), (byte)0x01, new byte[]{(byte)hour});
//        ParamTlvBean deviceTagsBean2 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_MINUTE_M11X + seq), (byte)0x01, new byte[]{(byte)min});
//        ParamTlvBean deviceTagsBean3 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + seq), (byte)0x01, new byte[]{(byte)0x01});
//        configList.add(deviceTagsBean1);
//        configList.add(deviceTagsBean2);
//        configList.add(deviceTagsBean3);

        List<ParamTlvBean> confList = M11XTable.get(M11XTable.alarmHourTagList[seq-1], M11XTable.alarmMinuteTagList[seq-1], M11XTable.alarmSwitchTagList[seq-1]);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }
        confList.get(0).setValue(new byte[]{(byte)hour});
        confList.get(1).setValue(new byte[]{(byte)min});
        confList.get(2).setValue(new byte[]{(byte)0x01});

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(confList);
        getConfigParam.setDevParamCallBack(devParamCallBack);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //do something
//            }
//
//        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);
    }

    /*
     * Gets device alarm details
     * 获取设备闹钟详情
     * mac:设备mac地址 device mac adress
     * seq：闹钟ID
     *
     * seq: Alarm ID
     * */
    public static void getAlarm(final String mac, final int seq) {
//        List<ParamTlvBean> configList = new ArrayList<>();
//        ParamTlvBean deviceTagsBean1 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_HOUR_M11X + seq), (byte)0x01, new byte[]{(byte)0x00});
//        ParamTlvBean deviceTagsBean2 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_MINUTE_M11X + seq), (byte)0x01, new byte[]{(byte)0x00});
//        ParamTlvBean deviceTagsBean3 = new ParamTlvBean((byte)(CmdConst.CMD_TLV_DEVICE_ALARM_SWITCH_M11X + seq), (byte)0x01, new byte[]{(byte)0x01});
//        configList.add(deviceTagsBean1);
//        configList.add(deviceTagsBean2);
//        configList.add(deviceTagsBean3);

        List<ParamTlvBean> confList = M11XTable.get(M11XTable.alarmHourTagList[seq-1], M11XTable.alarmMinuteTagList[seq-1], M11XTable.alarmSwitchTagList[seq-1]);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(confList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                int state = data.get(2).getValue()[0];
                int hour = data.get(0).getValue()[0];
                int minute = data.get(1).getValue()[0];
                AlarmBean alarmBean = new AlarmBean(seq,state,hour,minute);
                //do something
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    public static List<AlarmBean> alarmList = new ArrayList<>();
    /*
     * Gets a list of device alarms
     * 获取设备闹钟列表
     * mac:设备mac地址 device mac adress
     * */
    public static void getAlarmList(final String mac,DevParamCallBack devParamCallBack) {
        if(alarmList != null && alarmList.size() > 0){
            alarmList.removeAll(alarmList);
        }

        List<ParamTlvBean> confList = M11XTable.get(M11XTable.alarmHourTagList[0], M11XTable.alarmMinuteTagList[0], M11XTable.alarmSwitchTagList[0],
                M11XTable.alarmHourTagList[1], M11XTable.alarmMinuteTagList[1], M11XTable.alarmSwitchTagList[1],
                M11XTable.alarmHourTagList[2], M11XTable.alarmMinuteTagList[2], M11XTable.alarmSwitchTagList[2],
                M11XTable.alarmHourTagList[3], M11XTable.alarmMinuteTagList[3], M11XTable.alarmSwitchTagList[3],
                M11XTable.alarmHourTagList[4], M11XTable.alarmMinuteTagList[4], M11XTable.alarmSwitchTagList[4],
                M11XTable.alarmHourTagList[5], M11XTable.alarmMinuteTagList[5], M11XTable.alarmSwitchTagList[5],
                M11XTable.alarmHourTagList[6], M11XTable.alarmMinuteTagList[6], M11XTable.alarmSwitchTagList[6],
                M11XTable.alarmHourTagList[7], M11XTable.alarmMinuteTagList[7], M11XTable.alarmSwitchTagList[7],
                M11XTable.alarmHourTagList[8], M11XTable.alarmMinuteTagList[8], M11XTable.alarmSwitchTagList[8]);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return ;
        }

        final int finalI = 1;

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(confList);
        getConfigParam.setDevParamCallBack(devParamCallBack);

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    /*
     * Switch off device alarm
     * 关闭设备闹钟
     * mac:设备mac地址 device mac adress
     * seq：闹钟ID
     *
     * seq: Alarm ID
     * */
    public static void closeAlarm(String mac, final int seq) {

        List<ParamTlvBean> confList = M11XTable.get(M11XTable.alarmSwitchTagList[seq-1]);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

        ParamOpBean setConfigParam = new ParamOpBean();
        setConfigParam.setDn(mac);
        setConfigParam.setOpId(opId++);
        setConfigParam.setBusy(false);
        setConfigParam.setParams(confList);
        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevConf(setConfigParam);

    }

    public static void closeAllAlarm(final String mac) {
        List<ParamTlvBean> confList = M11XTable.get(
                M11XTable.alarmSwitchTagList[0],
                M11XTable.alarmSwitchTagList[1],
                M11XTable.alarmSwitchTagList[2],
                M11XTable.alarmSwitchTagList[3],
                M11XTable.alarmSwitchTagList[4],
                M11XTable.alarmSwitchTagList[5],
                M11XTable.alarmSwitchTagList[6],
                M11XTable.alarmSwitchTagList[7],
                M11XTable.alarmSwitchTagList[8]);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(confList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                closeAllAlarm(mac,opId,4,5,6, devParamCallBack);
                //do something
            }

        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);

    }

    /*
     * Set device volume
     * 设置设备音量
     * mac:设备mac地址 device mac adress
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
    public static void setDeviceVolume(String mac, int level) {
        List<ParamTlvBean> configList = new ArrayList<>();

        ParamTlvBean deviceTagsBean1 = new ParamTlvBean(CmdConst.CMD_TLV_DEVICE_VOLUME, (byte)0x01, new byte[]{(byte)level});
        configList.add(deviceTagsBean1);

        List<ParamTlvBean> confList = M11XTable.get(M11XTable.TAG_VOLUME);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }
        confList.get(0).setValue(new byte[]{(byte)level});

        ParamOpBean setConfigParam = new ParamOpBean();
        setConfigParam.setDn(mac);
        setConfigParam.setOpId(opId++);
        setConfigParam.setBusy(false);
        setConfigParam.setParams(configList);
        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevConf(setConfigParam);

    }

    /*
     * Get device volume
     * 获取设备音量
     * mac:设备mac地址 device mac adress
     * */
    public static void getDeviceVolume(final String mac) {
        List<ParamTlvBean> confList = M11XTable.get(M11XTable.TAG_VOLUME);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(confList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                switch (data.get(0).getValue()[0]){
                    case (byte)0x00:
                        Log.d("linyb","  //静音 mute");


                        break;
                    case (byte)0x01:
                        Log.d("linyb","   //小 little");

                        break;
                    case (byte)0x02:
                        Log.d("linyb","   //大 high");

                        break;
                }
                //do something
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    /*
     * Set the device time format
     * 设置设备时间格式
     * mac:设备mac地址 device mac adress
     * format：时间格式
     * 0：24小时制
     * 1：12小时制
     *
     * format: time format
     * 0：24-hour
     * 1：12-hour
     * */
    public static void setTimeFormat(final String mac, int format) {
        List<ParamTlvBean> confList = M11XTable.get(M11XTable.TAG_TIME_FORMAT);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }
        confList.get(0).setValue(new byte[]{(byte)format});

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(confList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevConf(getConfigParam);

    }

    /*
     * Get the device time format
     * 获取设备时间格式
     * mac:设备mac地址 device mac adress
     * */
    public static void getTimeFormat(String mac) {
        List<ParamTlvBean> confList = M11XTable.get(M11XTable.TAG_TIME_FORMAT);
        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

        ParamOpBean getConfigParam = new ParamOpBean();
        getConfigParam.setDn(mac);
        getConfigParam.setOpId(opId++);
        getConfigParam.setBusy(false);
        getConfigParam.setParams(confList);
        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
                switch (data.get(0).getValue()[0]){
                    case (byte)0x00:
                        Log.e("linyb", " 24小时制 24-hour");
                        break;
                    case (byte)0x01:
                        Log.e("linyb", "  12小时制 12-hour");
                        break;
                }
                //do something
            }

        });

        BluetoothManager.getInstance().getDevConf(getConfigParam);

    }

    /*
     * Device alarm clock to remind the time
     * 设备闹钟提醒时长
     * mac:设备mac地址 device mac adress
     * minute:1-240
     * */
    public static void setDeviceRemindTime(final String mac, int minute){
        List<ParamTlvBean> configList = M11XTable.get(M11XTable.TAG_ALARM_KEEP);
        if(configList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }
        configList.get(0).setValue(new byte[]{(byte)0x01});

        ParamOpBean setConfigParam = new ParamOpBean();
        setConfigParam.setDn(mac);
        setConfigParam.setOpId(opId++);
        setConfigParam.setBusy(false);
        setConfigParam.setParams(configList);
        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevConf(setConfigParam);

    }

    /* -------------------------------------- ctrl  --------------------------------------     */

    /*
     * Set Device Time
     * 设置设备时间
     * mac:设备mac地址 device mac adress
     * */
    public static void setDeviceSysTime(String mac){
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

        List<ParamTlvBean> ctrlList = M11XTable.get(M11XTable.TAG_TIME_CAL);
        if(ctrlList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }
        ctrlList.get(0).setValue(data);

        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(ctrlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }



    /*
     * Early Dose
     * 提前取药
     * mac:设备mac地址 device mac adress
     * */
    public static void earlyDose(String mac){
        List<ParamTlvBean> controlList = M11XTable.get(M11XTable.TAG_TAKE_EARLY);
        if(controlList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(controlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }

    /*
     * The current alarm is muted (taking the medicine time, the device has a sound reminder, let the current alarm clock mute, does not affect the next alarm)
     * 当前闹钟静音(服药时间到，设备发出声音提醒，让当前闹钟静音，不影响下次闹钟)
     * mac:设备mac地址 device mac adress
     * */
    public static void deviceCurrentAlarmMuted(String mac){
        List<ParamTlvBean> ctrlList = M11XTable.get(M11XTable.TAG_THIS_MUTE);
        if(ctrlList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }
        ctrlList.get(0).setValue(new byte[]{(byte)0x01});

        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(ctrlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }

    /*
     * device reboot
     * 设备重启
     * mac:设备mac地址 device mac adress
     * */
    public static void setDeviceReboot(String mac){
        List<ParamTlvBean> ctrlList = M11XTable.get(M11XTable.TAG_REBOOT);
        if(ctrlList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(ctrlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }

    /*
     * Device Restore Factory Settings (Clears Device Data)
     * 设备恢复出厂设置（清空设备数据）
     * mac:设备mac地址 device mac adress
     * */
    public static void setDeviceReset(String mac){
        List<ParamTlvBean> ctrlList = M11XTable.get(M11XTable.TAG_FACTORY);
        if(ctrlList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

        ParamOpBean setCtrlParam = new ParamOpBean();
        setCtrlParam.setDn(mac);
        setCtrlParam.setOpId(opId++);
        setCtrlParam.setBusy(false);
        setCtrlParam.setParams(ctrlList);
        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
            @Override
            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
                //do something
            }

        });

        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);

    }

    /* -------------------------------------- stat  --------------------------------------     */


    /*
     * Get the device model
     * 获取设备型号
     * mac:设备mac地址 device mac adress
     * */
    public static void getDeviceModel(String mac){
        List<ParamTlvBean> stateList = M11XTable.get(M11XTable.TAG_DEV_MODEL);
        if(stateList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

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
                        Log.d("linyb","  //M110");


                        break;
                        case (byte)0x01:
                        Log.d("linyb","  //M111");


                        break;
                    case (byte)0x02:
                        Log.d("linyb","  //M112");


                        break;
                    case (byte)0x05:
                        Log.d("linyb","   //M115");


                        break;
                }
                //do something
            }

        });

        BluetoothManager.getInstance().getDevStat(stateParam);

    }

    /*
     * Gets the device audio type
     * 获取设备音频类型
     * mac:设备mac地址 device mac adress
     * */
    public static void getDeviceAudioType(String mac){
        List<ParamTlvBean> stateList = M11XTable.get(M11XTable.TAG_AUDIO_TYPE);
        if(stateList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

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
                        Log.d("linyb","//无 null");


                        break;
                    case (byte)0x01:
                        Log.d("linyb"," //蜂鸣器 buzzer");


                        break;
                    case (byte)0x02:
                        Log.d("linyb","  //语音 voice");


                        break;
                }
                //do something
            }

        });

        BluetoothManager.getInstance().getDevStat(stateParam);

    }

    /*
     * Gets the device battery status
     * 获取设备电池状态
     * mac:设备mac地址 device mac adress
     * */
    public static void getBatteryInfo(String mac){
        List<ParamTlvBean> stateList = M11XTable.get(M11XTable.TAG_BAT_STAT);
        if(stateList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

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
                        Log.d("linyb","  //电量正常 Normal");

                        break;
                    case (byte)0x01:
                        Log.d("linyb","   //电量低 Low");


                        break;
                }
                //do something
            }

        });

        BluetoothManager.getInstance().getDevStat(stateParam);

    }

    /*
     * Gets the device version number
     * 获取设备版本号
     * mac:设备mac地址 device mac adress
     * */
    public static void getDeviceVersionCode(String mac){
        List<ParamTlvBean> stateList = M11XTable.get(M11XTable.TAG_DEV_VERSION);
        if(stateList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
            return;
        }

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
                Log.d("linyb"," version"+version);

                //do something
            }

        });

        BluetoothManager.getInstance().getDevStat(stateParam);

    }

}
