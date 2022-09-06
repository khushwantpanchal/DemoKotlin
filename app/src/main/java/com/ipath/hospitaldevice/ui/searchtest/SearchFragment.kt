package com.ipath.hospitaldevice.ui.searchtest


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.util.Predicate
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.berry_med.spo2_ble.data.Const
import com.ficat.easyble.BleDevice
import com.ficat.easyble.BleManager
import com.ficat.easyble.gatt.callback.BleCallback
import com.ficat.easyble.gatt.callback.BleConnectCallback
import com.ficat.easyble.gatt.callback.BleNotifyCallback

import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.ipath.hospitaldevice.R
import com.ipath.hospitaldevice.base.BaseFragment
import com.ipath.hospitaldevice.ble.BleController
import com.ipath.hospitaldevice.ble.adapter.DeviceListAdapter
import com.ipath.hospitaldevice.ble.adapter.SearchDevicesDialog
import com.ipath.hospitaldevice.ble.data.DataParser
import com.ipath.hospitaldevice.databinding.SearchFragmentBinding
import com.ipath.hospitaldevice.ui.adapter.DeviceSearchAdapter
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext


class SearchFragment : BaseFragment<SearchFragmentBinding, SearchVM>(), PatientNavigator,
    CoroutineScope, BleController.StateListener {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var deviceSearchAdapter: DeviceSearchAdapter? = null
    lateinit var bleManager: BleManager
    var isConnected = false
    private val searchVM: SearchVM by viewModels()
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager =
            context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var mDataParser: DataParser? = null
    private var mBleControl: BleController? = null

    private var mSearchDialog: SearchDevicesDialog? = null
    private var mBtDevicesAdapter: DeviceListAdapter? = null
    private val mBtDevices = java.util.ArrayList<BluetoothDevice>()


    lateinit var activityResultLauncher: ActivityResultLauncher<String>;
    lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>;
    private var isScanning = false

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }
    var bleConnectCallback: BleConnectCallback = object : BleConnectCallback {
        override fun onStart(startConnectSuccess: Boolean, info: String, device: BleDevice) {
            if (startConnectSuccess) {
                Log.d("MybLe", "Intent: $startConnectSuccess")
            } else {
                //fail to start connection, see details from 'info'
                val failReason = info
                Log.d("MybLe", "Intent: $startConnectSuccess")
            }
        }

        override fun onFailure(failCode: Int, info: String, device: BleDevice) {
            if (failCode == BleConnectCallback.FAIL_CONNECT_TIMEOUT) {
                viewDataBinding?.btnRetry?.visibility= View.GONE
                viewDataBinding?.btnReport?.visibility= View.GONE
                isConnected=false
                bleManager.disconnect(device.address)
                //connection timeout
                Log.d("MybLe", "Intent: $failCode")
            } else {
                viewDataBinding?.btnRetry?.visibility= View.GONE
                viewDataBinding?.btnReport?.visibility= View.GONE
                isConnected=false
                bleManager.disconnect(device.address)
                //connection fail due to other reasons
                Log.d("MybLe", "Intent: $failCode")
            }
        }

        override fun onConnected(device: BleDevice) {

            isConnected=true
            Log.d("MybLe", "Intent: ${device.connected}")
            viewDataBinding?.btnRetry?.visibility= View.VISIBLE
            viewDataBinding?.btnReport?.visibility= View.VISIBLE
            viewDataBinding?.btnSend?.text = "Disconnect"
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
            bleManager.notify(device,
                if(viewDataBinding?.deviceList?.selectedItem.toString().equals("Glucometer")) Const.UUID_SERVICE_DATA_GlucoMeter.toString() else Const.UUID_SERVICE_DATA_Oximeter.toString(),
                if(viewDataBinding?.deviceList?.selectedItem.toString().equals("Glucometer")) Const.UUID_CHARACTER_RECEIVE_GlucoMeter.toString() else Const.UUID_CHARACTER_RECEIVE_Oximeter.toString(),

                object : BleNotifyCallback {
                    override fun onCharacteristicChanged(
                        data: ByteArray,
                        device: BleDevice
                    ) {
                        if (data.size == 10) {
                            mDataParser!!.add(data!!)
                            Log.e("MybLe", "add: " + Arrays.toString(data))
                            Log.d("MybLe", "Intent1: ${data.toString()}")
                            Log.d("MybLe", "hex: ${data.toHex()}")
                            Log.d("MybLe", "Intent3: ${String(data)}")
                        }
                    }

                    override fun onNotifySuccess(
                        notifySuccessUuid: String,
                        device: BleDevice
                    ) {
                        Log.d("MybLe", "Intent: ${device.connected}")
                    }

                    override fun onFailure(failCode: Int, info: String, device: BleDevice) {
                        when (failCode) {
                            BleCallback.FAIL_DISCONNECTED -> {}
                            BleCallback.FAIL_OTHER -> {}
                            else -> {


                            }
                        }
                    }
                })
        }

        override fun onDisconnected(info: String, status: Int, device: BleDevice) {
            isConnected=false
            viewDataBinding?.btnRetry?.visibility= View.GONE
            viewDataBinding?.btnReport?.visibility= View.GONE
            viewDataBinding?.tvStatus?.text=""
            viewDataBinding?.tvParams?.text=""
            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
            viewDataBinding?.btnSend?.setText("Search")

        }
    }

    private val scanSettings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
            .build()
    } else {
        ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    }

    //call while scan device
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                val indexQuery =
                    scanResults.indexOfFirst { it.device.address == result.device.address }
                if (indexQuery != -1) {
                    scanResults[indexQuery] = result
                    deviceSearchAdapter?.notifyItemChanged(indexQuery)
                } else {
                    scanResults.add(result)
                    scanResults.sortByDescending { it.rssi }
                    val predicate = Predicate { x: ScanResult -> x.device.name == null }
                    removeItems(scanResults, predicate)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("ScanCallback", "onScanFailed: code $errorCode")
        }
    }

    private val scanResults = mutableListOf<ScanResult>()

    override fun getViewModel(): SearchVM {
        searchVM.setNavigator(this)
        return searchVM
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()


    override fun setupToolBar() {

    }

    override fun getBindingVariable(): Int {
        return BR.sfragment
    }

    override fun getLayoutId(): Int {
        return R.layout.search_fragment
    }

    override fun setupUI() {
        setupTitle(getString(R.string.searchdetails))
        setupBackButtonEnable(true,true)
        val arg = arguments?.getString("pname")
        val email = arguments?.getString("email")
        val mobile = arguments?.getString("mobile")
        viewDataBinding?.pName?.text = arg;
        val adapter = context?.let {
            ArrayAdapter.createFromResource(
                it, R.array.devicelist, R.layout.spinner_item
            )
        }
        adapter?.setDropDownViewResource(R.layout.spinner_item)
        viewDataBinding?.deviceList?.setAdapter(adapter)

        val linearLayoutManager = LinearLayoutManager(context)
        deviceSearchAdapter = context?.let { DeviceSearchAdapter(it, scanResults) }

        viewDataBinding?.recyclerView?.setLayoutManager(linearLayoutManager)
        viewDataBinding?.recyclerView?.setAdapter(deviceSearchAdapter)
        setUpAdapter()
        viewDataBinding?.btnSend?.setOnClickListener {
            if(viewDataBinding?.deviceList?.selectedItem.toString().equals("Oximeter")||
                viewDataBinding?.deviceList?.selectedItem.toString().equals("Glucometer")) {
                checkPermissions()
            }else{
                "Please Select device".toast()
            }
        }

        viewDataBinding?.btnRetry?.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString("pname", arg)
//            bundle.putString("email", email)
//            bundle.putString("mobile", mobile)
//            findNavController().navigate(R.id.action_patientFragment, bundle);
            if(isConnected) {
                stopScan()
                bleManager.disconnectAll()
            }
        }
        viewDataBinding?.btnReport?.setOnClickListener {
            if(isConnected) {
                stopScan()
                bleManager.disconnectAll()
            }
            val bundle = Bundle()
            bundle.putString("pname", arg)
            bundle.putString("email", email)
            bundle.putString("mobile", mobile)
            bundle.putString("sp02", "85")
            bundle.putString("beat", "75")
            findNavController().navigate(R.id.action_patientFragment_to_reportFragment, bundle);
        }
//        recyclerClear.setOnClickListener {
//            if (isScanning) stopScan()
//            deviceSearchAdapter.setData(mutableListOf())
//        }
        deviceSearchAdapter!!.setOnItemClick(object : DeviceSearchAdapter.OnItemClickListener {
            @SuppressLint("MissingPermission")
            override fun onItemClick(position: Int) {
                stopScan()

                if (!isConnected) {
//                    mBleControl!!.scanLeDevice(true)
//                    mSearchDialog!!.show()
//                    mBtDevices.clear()
//                    mBtDevicesAdapter!!.notifyDataSetChanged()
                    viewDataBinding?.tvParams?.setText(
                        "Name:" + scanResults.get(position).device.name + "     " + "Mac:" + scanResults.get(
                            position
                        ).device.address
                    )
                    if (viewDataBinding?.wfvPleth?.mSurfaceHolder?.lockCanvas() != null) {
                        viewDataBinding?.wfvPleth?.reset()
                    }
//                    mBleControl!!.connect(scanResults.get(position).device)
                    bleManager = BleManager.getInstance().init(context)
                    bleManager.connect(
                        scanResults.get(position).device.address,
                        bleConnectCallback
                    );


                } else {
//                    viewDataBinding?.wfvPleth?.reset()
//                    mBleControl!!.disconnect()
                    bleManager.disconnect(scanResults.get(position).device.address)
                }

//                m_myUUID = UUID.fromString(scanResults.get(position).scanRecord?.serviceUuids?.get(0).toString())
//                m_address=scanResults.get(position).device.address
//                context?.let { ConnectToDevice(it).execute() }
            }
        })

        mDataParser = DataParser(object : DataParser.onPackageReceivedListener {


            override fun onOxiParamsChanged(params: DataParser.OxiParams?) {
                runBlocking(Dispatchers.Main) {
                    viewDataBinding?.tvStatus?.setText(
                        "SpO2: " + params?.spo2
                            .toString() + "   Pulse Rate:" + params?.pulseRate
                    )
                }
            }

            override fun onPlethWaveReceived(amp: Int) {
                runBlocking(Dispatchers.Main) {
                    viewDataBinding?.wfvPleth?.addAmp(amp)
                }
            }

        })
        mDataParser!!.start()

        mBleControl = BleController.getDefaultBleController(this)
        mBleControl!!.enableBtAdapter()
        context?.let { mBleControl!!.bindService(it) }

        mBtDevicesAdapter = DeviceListAdapter(context, mBtDevices)
        mSearchDialog = object : SearchDevicesDialog(context, mBtDevicesAdapter) {
            override fun onSearchButtonClicked() {
                mBtDevices.clear()
                mBtDevicesAdapter!!.notifyDataSetChanged()
                mBleControl!!.scanLeDevice(true)
            }

            @SuppressLint("MissingPermission")
            override fun onClickDeviceItem(pos: Int) {
                val device = mBtDevices[pos]

                mBleControl!!.connect(device)
                dismiss()
            }
        }

    }


    override fun setupObserver() {

    }


    override fun onEventClicked() {
//        findNavController().navigate(WelcomeFragmentDirections.Fragment_to_patientFragment)
    }

    fun ByteArray.toHex(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }


    //endregion

    //region Permissions
    private fun checkPermissions() {

        if (BleManager.supportBle(context)) {
            if (BleManager.isBluetoothOn()) {
                if((hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)&&hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))||(hasPermission(Manifest.permission.BLUETOOTH_SCAN) && hasPermission(Manifest.permission.BLUETOOTH_CONNECT))){
                    startScan()

                }else{
                    askPermission()
                }

            } else {
                BleManager.enableBluetooth(activity, 12530);
            }
        } else {

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun checkActivation(): Boolean {
        return checkLocationPermission() && checkBluetoothPermission()
    }
    //endregion

    //region Bluetooth
    private fun checkBluetoothPermission(): Boolean {
        return when (context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.BLUETOOTH
            )
        }) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }
    }


    private fun checkLocationPermission(): Boolean {
        return when (context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                // Handle Permission granted/rejected
                if (isGranted) {
                    "Permission granted!".toast()
                } else {
                    // Permission is denied
                    "Permission denied :(".toast()
                }
            }

        requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    Log.d("Permission requests", "${it.key} = ${it.value}")
                }
            }
    }

    private fun setUpAdapter() {
        deviceSearchAdapter = context?.let { DeviceSearchAdapter(it, scanResults) }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        viewDataBinding?.recyclerView?.apply {
            adapter = deviceSearchAdapter
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = viewDataBinding?.recyclerView?.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }
    //endregion

    //region BLE Actions
    @SuppressLint("MissingPermission")
    private fun startScan() {
        if (isScanning) {

            stopScan()
            if(isConnected){

                if( bleManager.connectedDevices.size>0){
                    bleManager.disconnectAll()
                }
            }
        } else {
             //start scan with specified scanOptions
            if(isConnected){
                if( bleManager.connectedDevices.size>0){
                    bleManager.disconnectAll()
                }
            }
            var filters: List<ScanFilter>? = null
            filters = ArrayList()
            if(viewDataBinding?.deviceList?.selectedItem.toString().equals("Oximeter")) {
                filters.add(
                    ScanFilter.Builder().setServiceUuid(ParcelUuid(Const.UUID_SERVICE_DATA_Oximeter)).build()
                )
            }

            if(viewDataBinding?.deviceList?.selectedItem.toString().equals("Glucometer")) {
                filters.add(
                    ScanFilter.Builder().setServiceUuid(ParcelUuid(Const.UUID_SERVICE_DATA_GlucoMeter)).build()
                )
            }
            scanResults.clear()
            deviceSearchAdapter?.setData(scanResults)
            bleScanner.startScan(filters, scanSettings, scanCallback)
            isScanning = true
            Log.d("TAG", "scanResults: $scanResults")
//            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {

        Log.d("TAG", "scanResults: $scanResults")
        bleScanner.stopScan(scanCallback)
        isScanning = false

//        }
    }
    //endregion

    private fun String.toast() {
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }

    private fun hasPermission(permissionType: String): Boolean {
        return context?.let { ContextCompat.checkSelfPermission(it, permissionType) } ==
                PackageManager.PERMISSION_GRANTED
    }


    private fun BluetoothAdapter.isBluetoothEnabled(): Boolean {
        return this.isEnabled
    }

    private fun <T> removeItems(list: MutableList<T>, predicate: Predicate<T>) {
        val newList: MutableList<T> = ArrayList()
        list.filter { predicate.test(it) }.forEach { newList.add(it) }
        list.removeAll(newList)
    }


    override fun onConnected() {
//        isConnected=true;
        viewDataBinding?.btnSend?.text = "Disconnect"
        Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
    }

    override fun onDisconnected() {
        isConnected=false;
        Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
        viewDataBinding?.btnSend?.setText("Search")
//        llChangeName.setVisibility(View.GONE)
    }

    override fun onReceiveData(dat: ByteArray?) {

        mDataParser!!.add(dat!!)
    }

    override fun onServicesDiscovered() {
//        llChangeName.setVisibility(if (mBleControl!!.isChangeNameAvailable()) View.VISIBLE else View.GONE)
    }

    override fun onScanStop() {
        mSearchDialog!!.stopSearch()
    }

    @SuppressLint("MissingPermission")
    override fun onFoundDevice(device: BluetoothDevice?) {
        if (!mBtDevices.contains(device) && device?.name != null) {
            mBtDevices.add(device!!)
            mBtDevicesAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataParser!!.stop()
        context?.let { mBleControl!!.unbindService(it) }
        System.exit(0)
    }

    override fun onResume() {
        super.onResume()
        context?.let { mBleControl!!.registerBtReceiver(it) }
    }

    override fun onPause() {
        super.onPause()
        context?.let { mBleControl!!.unregisterBtReceiver(it) }
    }

    fun askPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      var   permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                }
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
//                setPermissions()
                //              Toast.makeText(DashBoard.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN) || !hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {

                TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setRationaleTitle(R.string.rationale_title)
                    .setRationaleMessage(R.string.rationale_message)
                    .setDeniedTitle("Need Permission")
                    .setDeniedMessage("This app needs permission to use app features. \n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setGotoSettingButtonText("Settings")
                    .setPermissions(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                    )
                    .check()
            }
        }else{
            var   permissionlistener = object : PermissionListener {
                override fun onPermissionGranted() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    }
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
//                setPermissions()
                    //              Toast.makeText(DashBoard.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)||!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ) {

                TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setRationaleTitle(R.string.rationale_title)
                    .setRationaleMessage(R.string.rationale_message)
                    .setDeniedTitle("Need Permission")
                    .setDeniedMessage("This app needs permission to use app features. \n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setGotoSettingButtonText("Settings")
                    .setPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                    .check()
            }
        }
    }
    fun hexStr2Bytes(str: String?): ByteArray? {
        if (str == null) {
            return null
        }
        if (str.length == 0) {
            return ByteArray(0)
        }
        val byteArray = ByteArray(str.length / 2)
        for (i in byteArray.indices) {
            val subStr = str.substring(2 * i, 2 * i + 2)
            byteArray[i] = subStr.toInt(16).toByte()
        }
        return byteArray
    }


}