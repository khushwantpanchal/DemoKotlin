package com.ipath.hospitaldevice.ui.patient;

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.ipath.hospitaldevice.base.BaseViewModel
import javax.inject.Inject

class PatientVM @Inject constructor() : BaseViewModel<MainNavigator>(){

    public val getLoginFields= MutableLiveData<String>()
    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

}