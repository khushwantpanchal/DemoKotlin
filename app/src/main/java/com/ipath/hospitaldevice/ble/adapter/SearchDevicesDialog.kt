package com.ipath.hospitaldevice.ble.adapter

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import com.ipath.hospitaldevice.R


/**
 * Created by ZXX on 2017/4/28.
 */
abstract class SearchDevicesDialog(context: Context?, adapter: DeviceListAdapter?) :
    Dialog(context!!) {
    private val lvBluetoothDevices: ListView
    private val pbSearchDevices: ProgressBar
    private val btnSearchDevices: Button
    fun stopSearch() {
        pbSearchDevices.visibility = View.GONE
        btnSearchDevices.visibility = View.VISIBLE
    }

    private fun startSearch() {
        onSearchButtonClicked()
        pbSearchDevices.visibility = View.VISIBLE
        btnSearchDevices.visibility = View.GONE
    }

    override fun show() {
        super.show()
        startSearch()
    }

    abstract fun onSearchButtonClicked()
    abstract fun onClickDeviceItem(pos: Int)

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.devices_dialog)
        lvBluetoothDevices = findViewById<View>(R.id.lvBluetoothDevices) as ListView
        lvBluetoothDevices.adapter = adapter
        lvBluetoothDevices.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                onClickDeviceItem(
                    position
                )
            }
        pbSearchDevices = findViewById<View>(R.id.pbSearchDevices) as ProgressBar
        btnSearchDevices = findViewById<View>(R.id.btnSearchDevices) as Button
        btnSearchDevices.setOnClickListener { startSearch() }
    }
}