package com.ocwvar.SDKTest

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

import com.zayata.zayatabluetoothsdk.bluetooth.BluetoothManager
import com.zayata.zayatabluetoothsdk.callback.OpenBleFailCallBack
import com.zayata.zayatabluetoothsdk.callback.OpenBleSuccCallBack

/**
 * [系统Application类，设置全局变量以及初始化组件]
 * [System Application class, setting global variables and initializing components]
 */
class MyApplication : Application() {
    var needRefresh = false
    private val tag = MyApplication::class.java.simpleName
    var isAppToForeground = false
    override fun onCreate() {
        super.onCreate()
        instance = this
        myContext = this
        init()
    }

    /**
     * 初始化 initialize
     */
    private fun init() {
        BluetoothManager.getInstance().openBle(this, object : OpenBleSuccCallBack() {
            override fun onCallBack(msg: String) {}
        }, object : OpenBleFailCallBack() {
            override fun onCallBack(code: Int, msg: String) {}
        })


        //LanguageUtils.changeAppLanguage(myContext);
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    /**
     * 退出应用 exitApp
     */

    companion object {
        const val ISDEBUG = false
        var myContext: Context? = null

        /*
     * 是否完成  整个项目 Whether the entire project has been completed
     */
        var isCompleteProject = false
        var instance: MyApplication? = null
            private set
    }
}