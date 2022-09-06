package com.ipath.hospitaldevice.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.berry_med.spo2_ble.data.Const

/**
 * Created by ZXX on 2017/4/28.
 */
class BleController private constructor(stateListener: StateListener) {
    //TAG
    private val TAG = this.javaClass.name
    private var mBtAdapter: BluetoothAdapter? = null
    lateinit var mStateListener: StateListener
    private var mBluetoothLeService: BluetoothLeService? = null
    private var chReceiveData: BluetoothGattCharacteristic? = null
    private var chModifyName: BluetoothGattCharacteristic? = null
    var isConnected = false
        private set

    /**
     * enable bluetooth adapter
     */
    @SuppressLint("MissingPermission")
    fun enableBtAdapter() {
        if (!mBtAdapter!!.isEnabled) {
            mBtAdapter!!.enable()
        }
    }

    /**
     * connect the bluetooth device
     * @param device
     */
    fun connect(device: BluetoothDevice) {
        mBluetoothLeService?.connect(device.address)
    }

    /**
     * Disconnect the bluetooth
     */
    fun disconnect() {
        mBluetoothLeService?.disconnect()
    }

    // Device scan callback.
    private val mLeScanCallback =
        LeScanCallback { device, rssi, scanRecord -> mStateListener.onFoundDevice(device) }

    /**
     * Scan bluetooth devices
     * @param enable
     */
    private val mHandler: Handler
    @SuppressLint("MissingPermission")
    fun scanLeDevice(enable: Boolean) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed({
                mBtAdapter!!.stopLeScan(mLeScanCallback)
                mStateListener.onScanStop()
            }, 5000)
            mBtAdapter!!.startLeScan(mLeScanCallback)
        } else {
            mBtAdapter!!.stopLeScan(mLeScanCallback)
            mStateListener.onScanStop()
        }
    }

    // Code to manage Service lifecycle.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (mBluetoothLeService?.initialize()==false) {
                Log.e(TAG, "Unable to initialize Bluetooth")
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }

    fun bindService(context: Context) {
        val gattServiceIntent = Intent(context, BluetoothLeService::class.java)
        context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        context.unbindService(mServiceConnection)
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private val mGattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mStateListener.onConnected()
                isConnected = true
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mStateListener.onDisconnected()
                chModifyName = null
                chReceiveData = null
                isConnected = false
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                initCharacteristic()
                mStateListener.onServicesDiscovered()
                mBluetoothLeService?.setCharacteristicNotification(chReceiveData!!, true)
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e(TAG, "onReceive: " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
            } else if (BluetoothLeService.ACTION_SPO2_DATA_AVAILABLE.equals(action)) {
                mStateListener.onReceiveData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA))
            }
        }
    }

    fun registerBtReceiver(context: Context) {
        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
    }

    fun unregisterBtReceiver(context: Context) {
        context.unregisterReceiver(mGattUpdateReceiver)
    }

    fun initCharacteristic() {
        val services: List<BluetoothGattService> = mBluetoothLeService?.supportedGattServices!!
        val mInfoService: BluetoothGattService? = null
        var mDataService: BluetoothGattService? = null
        if (services == null) return
        for (service in services) {
            if (service.uuid == Const.UUID_SERVICE_DATA_Oximeter) {
                mDataService = service
            }
        }
        if (mDataService != null) {
            val characteristics = mDataService.characteristics
            for (ch in characteristics) {
                if (ch.uuid == Const.UUID_CHARACTER_RECEIVE_Oximeter) {
                    chReceiveData = ch
                } else if (ch.uuid == Const.UUID_MODIFY_BT_NAME) {
                    chModifyName = ch
                }
            }
        }
    }

    val isChangeNameAvailable: Boolean
        get() = chModifyName != null

    fun changeBtName(name: String?) {
        if (mBluetoothLeService == null || chModifyName == null) return
        if (name == null || name == "") return
        val b = name.toByteArray()
        val bytes = ByteArray(b.size + 2)
        bytes[0] = 0x00
        bytes[1] = b.size.toByte()
        System.arraycopy(b, 0, bytes, 2, b.size)
        mBluetoothLeService!!.write(chModifyName!!, bytes)
    }

    /**
     * BTController interfaces
     */
    interface StateListener {
        fun onFoundDevice(device: BluetoothDevice?)
        fun onConnected()
        fun onDisconnected()
        fun onReceiveData(dat: ByteArray?)
        fun onServicesDiscovered()
        fun onScanStop()
    }

    companion object {
        private var mBleController: BleController? = null

        /**
         * Get a Controller
         * @return
         */
        fun getDefaultBleController(stateListener: StateListener): BleController? {
            if (mBleController == null) {
                mBleController = BleController(stateListener)
            }
            return mBleController
        }

        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            intentFilter.addAction(BluetoothLeService.ACTION_SPO2_DATA_AVAILABLE)
            return intentFilter
        }
    }

    init {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        mStateListener = stateListener
        mHandler = Handler()
    }
}