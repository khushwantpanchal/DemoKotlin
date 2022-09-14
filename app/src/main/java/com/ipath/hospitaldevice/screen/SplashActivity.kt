package com.ipath.hospitaldevice.screen

//import com.contec.pm10.code.connect.ContecSdk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ipath.hospitaldevice.R
import com.ipath.hospitaldevice.base.BaseActivity
import com.ipath.hospitaldevice.databinding.SplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    lateinit var mBinding: SplashScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("mylog", "onCreate: ")
        mBinding = DataBindingUtil.setContentView<SplashScreenBinding>(this, R.layout.splash_screen)
        Handler().postDelayed(Runnable { // This method will be executed once the timer is over
            val i = Intent(this@SplashActivity, BaseActivity::class.java)
            startActivity(i)
            finish()
        }, 3000)
    }




}