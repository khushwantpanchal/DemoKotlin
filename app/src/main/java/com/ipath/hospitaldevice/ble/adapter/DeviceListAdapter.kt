package com.ipath.hospitaldevice.ble.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.ipath.hospitaldevice.R

/**
 * Created by ZXX on 2015/12/30.
 */
class DeviceListAdapter(context: Context?, devices: ArrayList<BluetoothDevice>) :
    BaseAdapter() {
    private val mInflater: LayoutInflater
    private val mDevices: ArrayList<BluetoothDevice>
    override fun getCount(): Int {
        return mDevices.size
    }

    override fun getItem(position: Int): Any {
        return mDevices[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("MissingPermission")
    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val dev = mDevices[position]
        var llItem: LinearLayout? = null
        llItem = if (convertView != null) {
            convertView as LinearLayout
        } else {
            mInflater.inflate(R.layout.search_dialog_device_item, null) as LinearLayout?
        }
        val tvName = llItem!!.findViewById<View>(R.id.tvBtItemName) as TextView
        val tvAddr = llItem.findViewById<View>(R.id.tvBtItemAddr) as TextView
        tvName.text = dev.name
        tvAddr.text = "MAC: " + dev.address
        return llItem
    }

    init {
        mInflater = LayoutInflater.from(context)
        mDevices = devices
    }
}