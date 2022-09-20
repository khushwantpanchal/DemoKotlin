package com.ipath.hospitaldevice.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.ocwvar.SDKTest.MyApplication
import com.zayata.zayatabluetoothsdk.bluetooth.BluetoothManager
import com.zayata.zayatabluetoothsdk.callback.OpenBleFailCallBack
import com.zayata.zayatabluetoothsdk.callback.OpenBleSuccCallBack
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    var needRefresh = false
    private val tag = App::class.java.simpleName
    var isAppToForeground = false
    val ISDEBUG = false
    var myContext: Context? = null

    /*
     * 是否完成  整个项目 Whether the entire project has been completed
     */
    var isCompleteProject = false
    private var instance: App? = null


    fun getInstance(): App? {
        return instance
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this
        myContext = this
        init()
    }
    companion object {
        private var context: Context? = null
        fun getContext(): Context? {
            return context
        }
        const val ISDEBUG = false
        var myContext: Context? = null

        /*
     * 是否完成  整个项目 Whether the entire project has been completed
     */
        var isCompleteProject = false
        var instance: MyApplication? = null
            private set
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


}