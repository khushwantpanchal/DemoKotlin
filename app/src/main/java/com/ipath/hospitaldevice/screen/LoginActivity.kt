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
import com.ipath.hospitaldevice.databinding.LoginScreenBinding
import com.ipath.hospitaldevice.databinding.SplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var mBinding: LoginScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("mylog", "onCreate: ")
        mBinding = DataBindingUtil.setContentView<LoginScreenBinding>(this, R.layout.login_screen)

        mBinding.btnLogin.setOnClickListener {
            val i = Intent(this@LoginActivity, BaseActivity::class.java)
            startActivity(i)
            finish()
        }
    }




}