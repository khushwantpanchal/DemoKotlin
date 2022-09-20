//package com.ipath.hospitaldevice.ble.medicaldevice.example;
//
//import android.util.Log;
//
//import com.ipath.hospitaldevice.ble.medicaldevice.paramtable.M10XTable;
//import com.ipath.hospitaldevice.ble.medicaldevice.bean.BoxInfoBean;
////import com.ocwvar.SDKTest.paramtable.M10XTable;
////import com.ocwvar.SDKTest.utils.ToastUtils;
//import com.zayata.zayatabluetoothsdk.bean.AlarmBean;
//import com.zayata.zayatabluetoothsdk.bean.ParamOpBean;
//import com.zayata.zayatabluetoothsdk.bean.ParamOpRespCbBean;
//import com.zayata.zayatabluetoothsdk.bean.ParamTlvBean;
//import com.zayata.zayatabluetoothsdk.bluetooth.BluetoothManager;
//import com.zayata.zayatabluetoothsdk.callback.DevParamCallBack;
//import com.zayata.zayatabluetoothsdk.utils.ByteUtil;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
///**
// * Created by linyb
// * Time on 2021/2/7
// * SDK工具类
// */
//public class m10x {
//    static int opId = 0;
//    /*
//     * Get device configuration information
//     * 获取设备配置信息
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void getDeviceInfo(String mac){
//
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.TAG_TIME_FORMAT, M10XTable.TAG_RING_TYPE, M10XTable.TAG_VOLUME, M10XTable.TAG_ALARM_KEEP);
//        if(confList.size() < 1) {
////            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                if (paramOpRespCbBean.getStat() == 0) {
//                    List<ParamTlvBean> data = paramOpRespCbBean.getParams();
//                    /*
//                     * Time_format：时间格式
//                     * Alarm_ring：铃声类型
//                     * Alarm_voice：音量
//                     * Alarm_clock_duration：闹钟提醒时长
//                     *
//                     * Time_format: Time format
//                     * Alarm_ring: Ring type
//                     * Alarm_voice: Volume
//                     * Alarm_clock_duration: The duration of the alarm clock
//                     * */
//                    BoxInfoBean boxInfoBean = new BoxInfoBean();
//                    boxInfoBean.setTime_format(data.get(0).getValue()[0]);
//                    boxInfoBean.setAlarm_ring(data.get(1).getValue()[0]);
//                    boxInfoBean.setAlarm_voice(data.get(2).getValue()[0]);
//                    boxInfoBean.setAlarm_clock_duration(data.get(3).getValue()[0]);
//
//                    //DO SOMETHING
//                }else {
//
//                }
//            }
//
//        });
//
//        BluetoothManager.getInstance().getDevConf(getConfigParam);
//    }
//
//    /*
//     * Sets the device ring type
//     * 设置设备响铃类型
//     * mac:设备mac地址 device mac adress
//     * kind：响铃类型
//     * kind：ring type
//     * */
//    public static void setRingType(String mac,  int kind){
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.TAG_RING_TYPE);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//        confList.get(0).setValue(new byte[]{(byte)kind});
//
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setBusy(false);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean respCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevConf(getConfigParam);
//
//    }
//
//    /*
//     * Gets the current ringing type of the device
//     * 获取设备当前响铃类型
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void getRingType(final String mac){
//
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.TAG_RING_TYPE);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setBusy(false);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                switch (paramOpRespCbBean.getParams().get(0).getValue()[0]){
//                    case (byte)0x00:
//                        //铃声1 The bell 1
//                        break;
//                    case (byte)0x01:
//                        //铃声2 The bell 2
//                        break;
//                }
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().getDevConf(getConfigParam);
//
//    }
//
//    /*
//     * Set device alarm
//     * 设置设备闹钟
//     * mac:设备mac地址 device mac adress
//     * seq：闹钟ID
//     * hour：小时
//     * min：分钟
//     * repeat:重复时间
//     * Repeat时bit0-bit6有如下意义：
//                        Bit0等于1表示周日一直有效
//                        Bit1等于1表示周一一直有效
//                        Bit2等于1表示周二一直有效
//                        Bit3等于1表示周三一直有效
//                        Bit4等于1表示周四一直有效
//                        Bit5等于1表示周五一直有效
//                        Bit6等于1表示周六一直有效
//     *
//     * seq: Alarm ID
//     * hour：hours
//     * min: minutes
//     * repeat:重复时间 repeat
//     * Repeat时bit0-bit6有如下意义：
//                        Bit0等于1表示周日一直有效
//                        Bit1等于1表示周一一直有效
//                        Bit2等于1表示周二一直有效
//                        Bit3等于1表示周三一直有效
//                        Bit4等于1表示周四一直有效
//                        Bit5等于1表示周五一直有效
//                        Bit6等于1表示周六一直有效
//     * */
//    public static void setAlarm(final String mac,  final int seq, int hour, int min, byte repeat) {
//
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.alarmTagList[seq-1]);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//        confList.get(0).setValue(new byte[]{(byte)hour,(byte)min,(byte)0x01,repeat});
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setBusy(false);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevConf(getConfigParam);
//    }
//
//    /*
//     * Gets device alarm details
//     * 获取设备闹钟详情
//     * mac:设备mac地址 device mac adress
//     * seq：闹钟ID
//     *
//     * seq: Alarm ID
//     * */
//    public static void getAlarm(final String mac, final int seq) {
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.alarmTagList[seq-1]);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setBusy(false);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
//
//                AlarmBean alarmBean = new AlarmBean(seq,data.get(0).getValue()[2],data.get(0).getValue()[0],data.get(0).getValue()[1],data.get(0).getValue()[3]);
//
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().getDevConf(getConfigParam);
//
//    }
//
//    static List<AlarmBean> alarmList = new ArrayList<>();
//    /*
//     * Gets a list of device alarms
//     * 获取设备闹钟列表
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void getAlarmList(final String mac) {
//        if(alarmList != null && alarmList.size() > 0){
//            alarmList.removeAll(alarmList);
//        }
////        for (int i = 1;i < 10;i++) {
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.alarmTagList[0], M10XTable.alarmTagList[1],
//                M10XTable.alarmTagList[2], M10XTable.alarmTagList[3],
//                M10XTable.alarmTagList[4], M10XTable.alarmTagList[5]);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        final int finalI = 1;
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setBusy(false);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
//                for(int i = 0;i<6;i++) {
//                    AlarmBean alarmBean = new AlarmBean(i, data.get(i).getValue()[2], data.get(i).getValue()[0], data.get(i).getValue()[1], data.get(i).getValue()[3]);
//                    alarmList.add(alarmBean);
//                }
//                for(int i = 0;i<6;i++) {
//                    Log.e("linyb", "index:" + alarmList.get(i).getSeq() + "hour:" + alarmList.get(i).getTime() + ", status:" + alarmList.get(i).getStatus());
//                }
//
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().getDevConf(getConfigParam);
//
//
//    }
//
//    /*
//     * Switch off device alarm
//     * 关闭设备闹钟
//     * mac:设备mac地址 device mac adress
//     * seq：闹钟ID
//     *
//     * seq: Alarm ID
//     * */
//    public static void closeAlarm(String mac,  final int seq) {
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.alarmTagList[seq-1]);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//        confList.get(0).setValue(new byte[]{(byte)0xff,(byte)0xff,(byte)0x00,(byte)0x7f});
//
//        ParamOpBean setConfigParam = new ParamOpBean();
//        setConfigParam.setDn(mac);
//        setConfigParam.setOpId(opId++);
//        setConfigParam.setBusy(false);
//        setConfigParam.setParams(confList);
//        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevConf(setConfigParam);
//
//    }
//
//    /*
//     * close all device alarm
//     * 关闭全部设备闹钟
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void closeAllAlarm(final String mac) {
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.alarmTagList[0], M10XTable.alarmTagList[1],
//                M10XTable.alarmTagList[2], M10XTable.alarmTagList[3],
//                M10XTable.alarmTagList[4], M10XTable.alarmTagList[5]);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//        confList.get(0).setValue(new byte[]{(byte)0xff,(byte)0xff,(byte)0x00,(byte)0x7f});
//        confList.get(1).setValue(new byte[]{(byte)0xff,(byte)0xff,(byte)0x00,(byte)0x7f});
//        confList.get(2).setValue(new byte[]{(byte)0xff,(byte)0xff,(byte)0x00,(byte)0x7f});
//        confList.get(3).setValue(new byte[]{(byte)0xff,(byte)0xff,(byte)0x00,(byte)0x7f});
//        confList.get(4).setValue(new byte[]{(byte)0xff,(byte)0xff,(byte)0x00,(byte)0x7f});
//        confList.get(5).setValue(new byte[]{(byte)0xff,(byte)0xff,(byte)0x00,(byte)0x7f});
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setBusy(false);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevConf(getConfigParam);
//
//    }
//
//    /*
//     * Set device volume
//     * 设置设备音量
//     * mac:设备mac地址 device mac adress
//     * level：音量
//     * 0：静音
//     * 1：小
//     * 2：大
//     *
//     * level: volume
//     * 0：mute
//     * 1：little
//     * 2：big
//     * */
//    public static void setDeviceVolume(String mac,  int level) {
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.TAG_VOLUME);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//        confList.get(0).setValue(new byte[]{(byte)level});
//
//        ParamOpBean setConfigParam = new ParamOpBean();
//        setConfigParam.setDn(mac);
//        setConfigParam.setOpId(opId++);
//        setConfigParam.setBusy(false);
//        setConfigParam.setParams(confList);
//        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevConf(setConfigParam);
//
//    }
//
//    /*
//     * Get device volume
//     * 获取设备音量
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void getDeviceVolume(final String mac) {
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.TAG_VOLUME);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setBusy(false);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
//                switch (data.get(0).getValue()[0]){
//                    case (byte)0x00:
//                        //静音 mute
//                        break;
//                    case (byte)0x01:
//                        //小 little
//                        break;
//                    case (byte)0x02:
//                        //大 high
//                        break;
//                }
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().getDevConf(getConfigParam);
//
//    }
//
//    /*
//     * Set the device time format
//     * 设置设备时间格式
//     * mac:设备mac地址 device mac adress
//     * format：时间格式
//     * 0：24小时制
//     * 1：12小时制
//     *
//     * format: time format
//     * 0：24-hour
//     * 1：12-hour
//     * */
//    public static void setTimeFormat(final String mac,  int format) {
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.TAG_TIME_FORMAT);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//        confList.get(0).setValue(new byte[]{(byte)format});
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setBusy(false);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevConf(getConfigParam);
//
//    }
//
//    /*
//     * Get the device time format
//     * 获取设备时间格式
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void getTimeFormat(String mac) {
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.TAG_TIME_FORMAT);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean getConfigParam = new ParamOpBean();
//        getConfigParam.setDn(mac);
//        getConfigParam.setOpId(opId++);
//        getConfigParam.setBusy(false);
//        getConfigParam.setParams(confList);
//        getConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
//                switch (data.get(0).getValue()[0]){
//                    case (byte)0x00:
//                        //24小时制 24-hour
//                        break;
//                    case (byte)0x01:
//                        //12小时制 12-hour
//                        break;
//                }
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().getDevConf(getConfigParam);
//
//    }
//
//    /*
//     * Device alarm clock to remind the time
//     * 设备闹钟提醒时长
//     * mac:设备mac地址 device mac adress
//     * minute:1-240
//     * */
//    public static void setDeviceRemindTime(final String mac,  int minute){
//        List<ParamTlvBean> confList = M10XTable.get(M10XTable.TAG_ALARM_KEEP);
//        if(confList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//        confList.get(0).setValue(new byte[]{(byte)minute});
//
//        ParamOpBean setConfigParam = new ParamOpBean();
//        setConfigParam.setDn(mac);
//        setConfigParam.setOpId(opId++);
//        setConfigParam.setBusy(false);
//        setConfigParam.setParams(confList);
//        setConfigParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevConf(setConfigParam);
//
//    }
//
//    /* -------------------------------------- ctrl  --------------------------------------     */
//
//    /*
//     * Set Device Time
//     * 设置设备时间
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void setDeviceSysTime(String mac){
//        Calendar cal = Calendar.getInstance();
//        cal.setFirstDayOfWeek(Calendar.SUNDAY);
//        int year = cal.get(Calendar.YEAR);
//        byte[] byteYear = ByteUtil.intToByteArray(year);
//        byte[] data = new byte[8];
//        data[0] = byteYear[0];//年 year
//        data[1] = byteYear[1];
//        data[2] = (byte)(cal.get(Calendar.MONTH) + 1);//月 month
//        data[3] = (byte)cal.get(Calendar.DATE);//日 day
//        data[4] = (byte)cal.get(Calendar.HOUR_OF_DAY);//小时 hour
//        data[5] = (byte)cal.get(Calendar.MINUTE);//分钟 minute
//        data[6] = (byte)cal.get(Calendar.SECOND);//秒 second
//        data[7] = (byte)(cal.get(Calendar.DAY_OF_WEEK) - 1);//星期 week
//
//        List<ParamTlvBean> ctrlList = M10XTable.get(M10XTable.TAG_TIME_CAL);
//        if(ctrlList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//        ctrlList.get(0).setValue(data);
//
//
//        ParamOpBean setCtrlParam = new ParamOpBean();
//        setCtrlParam.setDn(mac);
//        setCtrlParam.setOpId(opId++);
//        setCtrlParam.setBusy(false);
//        setCtrlParam.setParams(ctrlList);
//        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);
//
//    }
//
//    /*
//     * device reboot
//     * 设备重启
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void setDeviceReboot(String mac){
//        List<ParamTlvBean> ctrlList = M10XTable.get(M10XTable.TAG_REBOOT);
//        if(ctrlList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean setCtrlParam = new ParamOpBean();
//        setCtrlParam.setDn(mac);
//        setCtrlParam.setOpId(opId++);
//        setCtrlParam.setBusy(false);
//        setCtrlParam.setParams(ctrlList);
//        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);
//
//    }
//
//    /*
//     * Device Restore Factory Settings (Clears Device Data)
//     * 设备恢复出厂设置（清空设备数据）
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void setDeviceReset(String mac){
//        List<ParamTlvBean> ctrlList = M10XTable.get(M10XTable.TAG_FACTORY);
//        if(ctrlList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean setCtrlParam = new ParamOpBean();
//        setCtrlParam.setDn(mac);
//        setCtrlParam.setOpId(opId++);
//        setCtrlParam.setBusy(false);
//        setCtrlParam.setParams(ctrlList);
//        setCtrlParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().setDevCtrl(setCtrlParam);
//
//    }
//
//    /* -------------------------------------- stat  --------------------------------------     */
//
//    /*
//     * Gets the device battery status
//     * 获取设备电池状态
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void getBatteryInfo(String mac){
//        List<ParamTlvBean> stateList = M10XTable.get(M10XTable.TAG_BAT_STAT);
//        if(stateList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean stateParam = new ParamOpBean();
//        stateParam.setDn(mac);
//        stateParam.setOpId(opId++);
//        stateParam.setBusy(false);
//        stateParam.setParams(stateList);
//        stateParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
//                switch (data.get(0).getValue()[0]){
//                    case (byte)0x00:
//                        //电量正常 Normal
//                        break;
//                    case (byte)0x01:
//                        //电量低 Low
//                        break;
//                }
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().getDevStat(stateParam);
//
//    }
//
//    /*
//     * Gets the device version number
//     * 获取设备版本号
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void getDeviceVersionCode(String mac){
//        List<ParamTlvBean> stateList = M10XTable.get(M10XTable.TAG_DEV_VERSION);
//        if(stateList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean stateParam = new ParamOpBean();
//        stateParam.setDn(mac);
//        stateParam.setOpId(opId++);
//        stateParam.setBusy(false);
//        stateParam.setParams(stateList);
//        stateParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                List<ParamTlvBean> data = paramOpRespCbBean.getParams();
//                String version = ((data.get(0).getValue()[0] & 0xF0) >> 4) + "." + (data.get(0).getValue()[0] & 0x0F);
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().getDevStat(stateParam);
//
//    }
//
//    /*
//     * Get the Battery level
//     * 获取电池电量
//     * mac:设备mac地址 device mac adress
//     * */
//    public static void getBatteryLevel(String mac){
//        List<ParamTlvBean> stateList = M10XTable.get(M10XTable.TAG_BAT_LEVEL);
//        if(stateList.size() < 1) {
//            ToastUtils.showShort("参数不支持或错误！Parameters are not supported or wrong!");
//            return;
//        }
//
//        ParamOpBean stateParam = new ParamOpBean();
//        stateParam.setDn(mac);
//        stateParam.setOpId(opId++);
//        stateParam.setBusy(false);
//        stateParam.setParams(stateList);
//        stateParam.setDevParamCallBack(new DevParamCallBack() {
//            @Override
//            public void respCb(final ParamOpRespCbBean paramOpRespCbBean) {
//                //DO SOMETHING
//            }
//
//        });
//
//        BluetoothManager.getInstance().getDevStat(stateParam);
//
//    }
//
//}
