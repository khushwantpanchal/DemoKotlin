package com.ipath.hospitaldevice.ui.device;

import androidx.lifecycle.MutableLiveData
import com.ipath.hospitaldevice.base.BaseViewModel
import javax.inject.Inject

class DeviceVM @Inject constructor() : BaseViewModel<DeviceNavigator>(){
    // TODO: Implement the ViewModel
    var lst = MutableLiveData<ArrayList<String>>()
    var newlist = arrayListOf<String>()

    fun add(blog: String){
        newlist.add(blog)
        lst.value=newlist
    }

    fun remove(blog: String){
        newlist.remove(blog)
        lst.value=newlist
    }
}