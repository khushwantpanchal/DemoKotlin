package com.ipath.hospitaldevice.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ipath.hospitaldevice.R
import com.zayata.zayatabluetoothsdk.bean.AlarmBean
import android.content.Context
import androidx.core.view.setPadding

class AlarmBeanAdapter(val  mContextThis: Context,var alstAlarmBeanMain: List<AlarmBean>) :
    BaseAdapter() {
    private val alstAlarmBean: List<AlarmBean>
    private val mLayoutInflater: LayoutInflater = mContextThis.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var mContext: Context
    override fun getCount(): Int {
        return alstAlarmBean.size
    }

    override fun getItem(position: Int): AlarmBean {
        return alstAlarmBean[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        try {
            if (convertView == null) {
                //LayoutInflater inflater = mContext.getLayoutInflater();
                convertView = mLayoutInflater.inflate(android.R.layout.simple_spinner_item, parent, false)
            }
            val planAlarmBean: AlarmBean = getItem(position)
            val name = convertView?.findViewById<View>(android.R.id.text1) as TextView
            name.setPadding(20)
            name.setTextColor(mContext.resources.getColor(R.color.black))
            if(planAlarmBean!=null&&planAlarmBean.seq!=null) {
                name.setText(planAlarmBean.seq.toString()+" - Time - "+planAlarmBean.time+" - Status - "+planAlarmBean.status)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            val stack = ex.stackTrace
//            ErrorLogApiLog.Errorlog(
//                mContext,
//                ErrorLogConstants.CitySpinnerAdapter,
//                ErrorLogConstants.getView,
//                ex.message,
//                stack[0].lineNumber
//            )
        }
        return convertView!!
    }

    init {
        alstAlarmBean = alstAlarmBeanMain
        mContext = mContextThis
    }
}