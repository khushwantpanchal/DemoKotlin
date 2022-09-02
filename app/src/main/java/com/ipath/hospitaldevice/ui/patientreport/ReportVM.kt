package com.ipath.hospitaldevice.ui.patientreport;

import androidx.lifecycle.MutableLiveData
import com.ipath.hospitaldevice.base.BaseViewModel
import javax.inject.Inject

class ReportVM @Inject constructor() : BaseViewModel<ReportNavigator>(){
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