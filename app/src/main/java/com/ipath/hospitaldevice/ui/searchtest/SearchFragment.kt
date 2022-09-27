package com.ipath.hospitaldevice.ui.searchtest


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
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
import com.contec.pm10.code.bean.DeviceParameter
import com.contec.pm10.code.bean.DeviceType
import com.contec.pm10.code.bean.EcgData
import com.contec.pm10.code.bean.SdkConstants
import com.contec.pm10.code.callback.*
import com.contec.pm10.code.connect.ContecSdk
import com.contec.pm10.code.tools.Utils
import com.ficat.easyble.BleDevice
import com.ficat.easyble.BleManager
import com.ficat.easyble.gatt.callback.BleCallback
import com.ficat.easyble.gatt.callback.BleConnectCallback
import com.ficat.easyble.gatt.callback.BleNotifyCallback
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.ipath.hospitaldevice.R
import com.ipath.hospitaldevice.base.BaseFragment
import com.ipath.hospitaldevice.ble.data.DataParser
import com.ipath.hospitaldevice.ble.medicaldevice.bean.DeviceParamsBean
import com.ipath.hospitaldevice.ble.medicaldevice.bean.TakeDrugBean
import com.ipath.hospitaldevice.ble.medicaldevice.example.m11x
import com.ipath.hospitaldevice.databinding.SearchFragmentBinding
import com.ipath.hospitaldevice.ui.adapter.AlarmBeanAdapter
import com.ipath.hospitaldevice.ui.adapter.DeviceSearchAdapter
import com.ipath.hospitaldevice.utils.Utils.getManufacturerSpecificData
import com.zayata.zayatabluetoothsdk.bean.*
import com.zayata.zayatabluetoothsdk.bluetooth.CmdExplain
import com.zayata.zayatabluetoothsdk.bluetooth.CmdExplainEN
import com.zayata.zayatabluetoothsdk.bluetooth.CmdSize
import com.zayata.zayatabluetoothsdk.callback.*
import com.zayata.zayatabluetoothsdk.utils.ByteUtil
import com.zayata.zayatabluetoothsdk.utils.CRC16Util
import com.zayata.zayatabluetoothsdk.utils.NoticeUtils
import kotlinx.coroutines.*
import okhttp3.internal.and
import okhttp3.internal.notify
import java.util.*
import kotlin.coroutines.CoroutineContext


class SearchFragment : BaseFragment<SearchFragmentBinding, SearchVM>(), PatientNavigator,
    CoroutineScope, NoticeUtils.INotice {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var deviceSearchAdapter: DeviceSearchAdapter? = null

    lateinit var bleManager: BleManager
    var bleManagerBluetoothManager=com.zayata.zayatabluetoothsdk.bluetooth.BluetoothManager.getInstance()
    var isConnected = false
    var isConnecting= false
    var isAlaramSet= false
    private val searchVM: SearchVM by viewModels()
    private var isListen = false
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager =
            context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val ecgDataArrayList = ArrayList<EcgData>()
    private var contecSdk: ContecSdk? = null
    var sp02 = "";
    var beat = "";
    var GluecosedL = "";
    var GluecosedLmmolLvalue = "";
    var Celcius = "";
    var Fahrenheit = "";
    var ECGDataREcord = "";
    var mmHgHigh = "";
    var mmHgLow = "";
    var beatBp = "";
    var color: Int = Color.WHITE;
    var color2: Int = Color.WHITE;
    var color3: Int = Color.WHITE;

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    var arg: String? = "";
    var mobile: String? = "";
    var email: String? = "";
    var device: String = "";

    private var mDataParser: DataParser? = null

    private val scanResults = mutableListOf<ScanResult>()

    lateinit var activityResultLauncher: ActivityResultLauncher<String>;
    lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>;
    private var isScanning = false
    lateinit var selectedAlarm : AlarmBean
    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }


    override fun getViewModel(): SearchVM {
        searchVM.setNavigator(this)
        return searchVM
    }

    override fun setupToolBar() {

    }

    override fun getBindingVariable(): Int {
        return BR.sfragment
    }

    override fun getLayoutId(): Int {
        return R.layout.search_fragment
    }

    override fun setupUI() {
        ecgConnectivity()
        setupTitle(getString(R.string.device))
        setupBackButtonEnable(true, object : View.OnClickListener {
            override fun onClick(v: View?) {
                disconnect()
            }
        })
        getView()?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                return if (keyCode == KeyEvent.KEYCODE_BACK) {
                    disconnect()
                    true
                } else false
            }
        })
        arg = arguments?.getString("pname")
        email = arguments?.getString("email")
        mobile = arguments?.getString("mobile")
        device = arguments?.getString("device")!!
        viewDataBinding?.pName?.text = arg
        viewDataBinding?.dName?.text = device


        val linearLayoutManager = LinearLayoutManager(context)
        deviceSearchAdapter = context?.let { DeviceSearchAdapter(it, scanResults) }

        viewDataBinding?.recyclerView?.setLayoutManager(linearLayoutManager)
        viewDataBinding?.recyclerView?.setAdapter(deviceSearchAdapter)
        setUpAdapter()
        viewDataBinding?.btnSend?.setOnClickListener {
            if (device.toString().equals(Const.Oximeter) ||
                device.toString().equals(Const.Glucometer) ||
                device.toString().equals(Const.Thermometer) ||
                device.toString().equals(Const.ECG) ||
                device.toString().equals(Const.MedicinePillBox) ||
                device.toString().equals(Const.BloodPressure)
            ) {
                checkPermissions()
            } else {
                "Please Select device".toast()
            }
        }
        viewDataBinding?.btnSend?.callOnClick()
        viewDataBinding?.btnRetry?.setOnClickListener {

            if (isConnected) {

                stopScan()
                disconnect()
            }
        }
        viewDataBinding?.newData?.setOnClickListener {
            //contecSdk!!.getData(communicateCallback)
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(selectedAlarm.hour)
                    .setMinute(selectedAlarm.minute)
                    .setTitleText("Select Medical tablet time")
                    .build()
            picker.show(this.childFragmentManager, "tag");

            picker.addOnPositiveButtonClickListener(View.OnClickListener {

                selectedAlarm.hour=picker.hour
                selectedAlarm.minute=picker.minute
//                m11x.setDeviceRemindTime(scanResults[0].device.address,3)
                var position=viewDataBinding?.spinner?.selectedItemPosition!!
                m11x.setAlarm(scanResults[0].device.address, selectedAlarm.seq, picker.hour, picker.minute,
                    object : DevParamCallBack() {
                        override fun respCb(paramOpRespCbBean: ParamOpRespCbBean) {
                            isAlaramSet=true

                            m11x.alarmList.get(position).hour=selectedAlarm.hour
                            m11x.alarmList.get(position).minute= selectedAlarm.minute
                            val spinAdapter =
                                AlarmBeanAdapter(
                                    activity!!.applicationContext,
                                    m11x.alarmList
                                )

                            activity?.runOnUiThread {
                                viewDataBinding?.spinner?.adapter = spinAdapter
                                Toast.makeText(context, "Alarm Set Successfully", Toast.LENGTH_SHORT).show()
                                viewDataBinding!!.spinner.setSelection(position)
                                viewDataBinding?.spinner?.onItemSelectedListener =
                                    object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            parent: AdapterView<*>,
                                            view: View,
                                            position: Int,
                                            id: Long
                                        ) {
                                            // Get the value selected by the user
                                            // e.g. to store it as a field or immediately call a method
                                            val user1 = parent.selectedItem as AlarmBean
                                            selectedAlarm = user1

                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                                    }


                                //DO SOMETHING
                            }
//                            m11x.getAlarmList(
//                                scanResults[0].device.address,
//                                object : DevParamCallBack() {
//                                    override fun respCb(paramOpRespCbBean: ParamOpRespCbBean) {
//                                        val data = paramOpRespCbBean.params
//                                        val alarmBean1 = AlarmBean(
//                                            1,
//                                            data[2].value[0].toInt(),
//                                            data[0].value[0].toInt(), data[1].value[0].toInt()
//                                        )
//                                        val alarmBean2 = AlarmBean(
//                                            2,
//                                            data[5].value[0].toInt(),
//                                            data[3].value[0].toInt(), data[4].value[0].toInt()
//                                        )
//                                        val alarmBean3 = AlarmBean(
//                                            3,
//                                            data[8].value[0].toInt(),
//                                            data[6].value[0].toInt(), data[7].value[0].toInt()
//                                        )
//                                        val alarmBean4 = AlarmBean(
//                                            4,
//                                            data[11].value[0].toInt(),
//                                            data[9].value[0].toInt(), data[10].value[0].toInt()
//                                        )
//                                        val alarmBean5 = AlarmBean(
//                                            5,
//                                            data[14].value[0].toInt(),
//                                            data[12].value[0].toInt(), data[13].value[0].toInt()
//                                        )
//                                        val alarmBean6 = AlarmBean(
//                                            6,
//                                            data[17].value[0].toInt(),
//                                            data[15].value[0].toInt(), data[16].value[0].toInt()
//                                        )
//                                        val alarmBean7 = AlarmBean(
//                                            7,
//                                            data[20].value[0].toInt(),
//                                            data[18].value[0].toInt(), data[19].value[0].toInt()
//                                        )
//                                        val alarmBean8 = AlarmBean(
//                                            8,
//                                            data[23].value[0].toInt(),
//                                            data[21].value[0].toInt(), data[22].value[0].toInt()
//                                        )
//                                        val alarmBean9 = AlarmBean(
//                                            9,
//                                            data[26].value[0].toInt(),
//                                            data[24].value[0].toInt(), data[25].value[0].toInt()
//                                        )
//                                        m11x.alarmList.add(alarmBean1)
//                                        m11x.alarmList.add(alarmBean2)
//                                        m11x.alarmList.add(alarmBean3)
//                                        m11x.alarmList.add(alarmBean4)
//                                        m11x.alarmList.add(alarmBean5)
//                                        m11x.alarmList.add(alarmBean6)
//                                        m11x.alarmList.add(alarmBean7)
//                                        m11x.alarmList.add(alarmBean8)
//                                        m11x.alarmList.add(alarmBean9)
//                                        for (i in m11x.alarmList.indices) {
//                                            Log.e(
//                                                "linyb",
//                                                "index:" + m11x.alarmList[i].seq + " hour:" + m11x.alarmList[i].time + ", hours:" + m11x.alarmList[i].hour + ", status:" + m11x.alarmList[i].status
//                                            )
//                                        }
//
//                                        val spinAdapter =
//                                            AlarmBeanAdapter(
//                                                activity!!.applicationContext,
//                                                m11x.alarmList
//                                            )
//
//                                        activity?.runOnUiThread {
//                                            viewDataBinding?.spinner?.adapter = spinAdapter
//                                            Toast.makeText(context, "Alarm Set Successfully", Toast.LENGTH_SHORT).show()
//                                            viewDataBinding!!.spinner.setSelection(position)
//                                            viewDataBinding?.spinner?.onItemSelectedListener =
//                                                object : AdapterView.OnItemSelectedListener {
//                                                    override fun onItemSelected(
//                                                        parent: AdapterView<*>,
//                                                        view: View,
//                                                        position: Int,
//                                                        id: Long
//                                                    ) {
//                                                        // Get the value selected by the user
//                                                        // e.g. to store it as a field or immediately call a method
//                                                        val user1 = parent.selectedItem as AlarmBean
//                                                        selectedAlarm = user1
//
//                                                    }
//
//                                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
//                                                }
//
//
//                                            //DO SOMETHING
//                                        }
//                                    }
//                                })

                        }
                    })
            })

        }
        viewDataBinding?.btnReport?.setOnClickListener {
            if (isConnected) {
                stopScan()
                disconnect()
                val bundle = Bundle()
                bundle.putString("pname", arg)
                bundle.putString("email", email)
                bundle.putString("mobile", mobile)
                bundle.putString("sp02", sp02)
                bundle.putString("beat", beat)
                bundle.putString("GluecosedL", GluecosedL)
                bundle.putString("GluecosedLmmolLvalue", GluecosedLmmolLvalue)
                bundle.putString("Celcius", Celcius)
                bundle.putString("Fahrenheit", Fahrenheit)
                bundle.putString("ECGDataREcord", ECGDataREcord)
                bundle.putString("mmHgHigh", mmHgHigh)
                bundle.putString("mmHgLow", mmHgLow)
                bundle.putString("beatBp", beatBp)
                bundle.putInt("color", color)
                bundle.putInt("color2", color2)
                bundle.putInt("color3.", color3)
                bundle.putBoolean("setalarm", isAlaramSet)
                if( device.toString().equals(Const.MedicinePillBox) ) {
                    bundle.putString(
                        "alarm",
                        selectedAlarm.seq.toString() + " - Time - " + selectedAlarm.hour.toString() + ":" + selectedAlarm.minute + " - Status - " + selectedAlarm.seq.toString()
                    )
                }else{
                    bundle.putString(
                        "alarm",
                        ""
                    )
                }
                    findNavController().navigate(R.id.action_patientFragment_to_reportFragment, bundle);
            }

        }

        deviceSearchAdapter!!.setOnItemClick(object : DeviceSearchAdapter.OnItemClickListener {
            @SuppressLint("MissingPermission")
            override fun onItemClick(position: Int) {

                connect(position);
            }
        })



        mDataParser = DataParser(object : DataParser.onPackageReceivedListener {


            override fun onOxiParamsChanged(params: DataParser.OxiParams?) {
                runBlocking(Dispatchers.Main) {

                    if (device.toString().equals(Const.Oximeter)) {
                        sp02 = params?.spo2.toString()
                        beat = params?.pulseRate.toString()
                        color = params?.color!!
                        color2 = params?.color2!!
                        color3 = params?.color3!!
                        viewDataBinding?.tvStatus?.setText(
                            "SpO2: " + params?.spo2.toString() + " \nPulse Rate: " + params?.pulseRate
                        )
                    } else if (device.toString().equals(Const.Glucometer)) {
                        var ml: Int = (params!!.mmolLvalue)
                        var result: Double = ml.toDouble() / 18
                        val number: Double = result
                        val number3digits: Double = String.format("%.3f", number).toDouble()
                        val number2digits: Double = String.format("%.2f", number3digits).toDouble()
                        val solution: Double = String.format("%.1f", number2digits).toDouble()
                        GluecosedL = params?.mmolLvalue.toString()
                        GluecosedLmmolLvalue = solution.toString()
                        color = params?.color!!
                        color2 = params?.color2!!
                        color3 = params?.color3!!
                        viewDataBinding?.tvStatus?.setText(
                            "mg/dL: " + (params?.mmolLvalue).toString() + " \nmmol/L: " + (solution).toString()
                        )
                    } else if (device.toString().equals(Const.Thermometer)) {

                        Celcius = params!!.Celcius.toString()
                        val solutionCelcius: Double =
                            String.format("%.1f", Celcius.toDouble()).toDouble()
                        Celcius = solutionCelcius.toString()
                        Fahrenheit = params!!.Fahrenheit.toString()
                        color = params?.color!!
                        color2 = params?.color2!!
                        color3 = params?.color3!!
                        val solutionFahrenheit: Double =
                            String.format("%.1f", Fahrenheit.toDouble()).toDouble()
                        Fahrenheit = solutionFahrenheit.toString()
                        viewDataBinding?.tvStatus?.setText(
                            (Celcius).toString() + " °C" + " \n" + (Fahrenheit).toString() + " °F"
                        )

                    } else if (device.toString().equals(Const.ECG)) {
                        ECGDataREcord = params?.ecgData.toString()
                        viewDataBinding?.tvStatus?.setText(ECGDataREcord)
                    } else if (device.toString().equals(Const.MedicinePillBox)) {

                        Celcius = params!!.Celcius.toString()
                        val solutionCelcius: Double =
                            String.format("%.1f", Celcius.toDouble()).toDouble()
                        Celcius = solutionCelcius.toString()
                        Fahrenheit = params!!.Fahrenheit.toString()
                        color = params?.color!!
                        color2 = params?.color2!!
                        color3 = params?.color3!!
                        val solutionFahrenheit: Double =
                            String.format("%.1f", Fahrenheit.toDouble()).toDouble()
                        Fahrenheit = solutionFahrenheit.toString()
                        viewDataBinding?.tvStatus?.setText(
                            (Celcius).toString() + " °C" + "  \n" + (Fahrenheit).toString() + " °F"
                        )
                    } else if (device.toString().equals(Const.BloodPressure)) {

                        mmHgHigh = params!!.mmHgHigh.toString()
                        mmHgLow = params!!.mmHgLow.toString()
                        beatBp = params!!.beat.toString()
                        color = params?.color!!
                        color2 = params?.color2!!
                        color3 = params?.color3!!
                        viewDataBinding?.tvStatus?.setText(
                            "Systolic " + (mmHgHigh).toString() + " mmHg" + "  \n" + "Diastolic " + (mmHgLow).toString() + " mmHg \n" + (beatBp).toString() + " BPM"
                        )
                    } else {

                    }
                }
            }

            override fun onPlethWaveReceived(amp: Int) {
                runBlocking(Dispatchers.Main) {
                    viewDataBinding?.wfvPleth?.addAmp(amp)
                }
            }

        })


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
                viewDataBinding?.btnRetry?.visibility = View.GONE
                viewDataBinding?.btnReport?.visibility = View.GONE
                isConnected = false
                bleManager.disconnect(device.address)
                //connection timeout
                Log.d("MybLe", "Intent: $failCode")
            } else {
                viewDataBinding?.btnRetry?.visibility = View.GONE
                viewDataBinding?.btnReport?.visibility = View.GONE
                isConnected = false
                bleManager.disconnect(device.address)
                //connection fail due to other reasons
                Log.d("MybLe", "Intent: $failCode")
            }
        }

        override fun onConnected(devicebt: BleDevice) {

            isConnected = true
            Log.d("MybLe", "Intent: ${devicebt.connected}")
            viewDataBinding?.btnRetry?.visibility = View.VISIBLE
            viewDataBinding?.btnReport?.visibility = View.VISIBLE
            viewDataBinding?.btnSend?.text = "Disconnect"
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()

            bleManager.notify(devicebt,
                if (device.toString()
                        .equals(Const.Glucometer)
                ) Const.UUID_SERVICE_DATA_GlucoMeter.toString()
                else if (device.toString()
                        .equals(Const.Oximeter)
                ) Const.UUID_SERVICE_DATA_Oximeter.toString()
                else if (device.toString()
                        .equals(Const.Thermometer)
                ) Const.UUID_SERVICE_DATA_Thermometer.toString()
                else if (device.toString()
                        .equals(Const.MedicinePillBox)
                ) Const.UUID_SERVICE_DATA_MedicinePillBox.toString() else Const.UUID_SERVICE_DATA_BloodPressure.toString(),
                if (device.toString().equals(Const.Glucometer))
                    Const.UUID_CHARACTER_RECEIVE_GlucoMeter.toString() else if (device.toString()
                        .equals(Const.Oximeter)
                )
                    Const.UUID_CHARACTER_RECEIVE_Oximeter.toString() else if (device.toString()
                        .equals(Const.Thermometer)
                ) Const.UUID_CHARACTER_RECEIVE_Thermometer.toString() else if (device.toString()
                        .equals(Const.MedicinePillBox)
                ) Const.UUID_CHARACTER_RECEIVE_MedicinePillBox.toString() else Const.UUID_CHARACTER_RECEIVE_BloodPressure.toString(),
                object : BleNotifyCallback {
                    override fun onCharacteristicChanged(data: ByteArray, devicebt: BleDevice) {
                        if (device.toString().equals(Const.Oximeter)) {
                            mDataParser!!.start()
                            if (data.size == 10) {
                                mDataParser!!.start()
                                mDataParser!!.add(data!!, Const.Oximeter)
                                Log.e("MybLe1", "add: " + Arrays.toString(data))
//                            Log.d("MybLe", "Intent1: ${data.toString()}")
//                            Log.d("MybLe", "hex: ${data.toHex()}")
//                            Log.d("MybLe", "Intent3: ${String(data)}")
                            }
                        } else if (device.toString().equals(Const.Glucometer)) {

                            if (!Arrays.toString(data)
                                    .equals("[-2, 106, 117, 90, 85, -86, -69, -52]") &&
                                !Arrays.toString(data)
                                    .equals("[-2, 106, 117, 90, 85, -69, -69, -52]")
                            ) {
                                val de = data[7]
                                if (de.toInt() > 5 || de.toInt() < 0) {
                                    mDataParser!!.start()
                                    mDataParser!!.add(data!!, Const.Glucometer)

                                    Log.e("MybLe1", "add: " + Arrays.toString(data))
                                }
//                                    Log.d("MybLe", "Intent1: ${data.toString()}")
//                                    Log.d("MybLe", "hex: ${data.toHex()}")
//                                    Log.d("MybLe", "Intent3: ${String(data)}")
                            }
                        } else if (device.toString().equals(Const.Thermometer)) {

                            Log.e("MybLe1", "add: " + Arrays.toString(data))
                            for (datalog in data) {
                                val hi = datalog
                                val hinumber = hi.toInt()
                                val hinumberunsignedHex = String.format("%02X", hinumber and 0xff)
                                val lownumberdecimal: Int = hinumberunsignedHex.toInt(16)
                                Log.e(
                                    "MybLe  current",
                                    hinumber.toString() + "\nDecimal " + lownumberdecimal.toString() + "\nHexa " + hinumberunsignedHex.toString()
                                )
                            }
                            mDataParser!!.start()
                            mDataParser!!.add(data, Const.Thermometer)
                        } else if (device.toString().equals(Const.MedicinePillBox)) {

                            Log.e("MybLe1", "add: " + Arrays.toString(data))
                            for (datalog in data) {
                                val hi = datalog
                                val hinumber = hi.toInt()
                                val hinumberunsignedHex = String.format("%02X", hinumber and 0xff)
                                val lownumberdecimal: Int = hinumberunsignedHex.toInt(16)
                                Log.e(
                                    "MybLe  current",
                                    hinumber.toString() + "\nDecimal " + lownumberdecimal.toString() + "\nHexa " + hinumberunsignedHex.toString()
                                )
                            }
                            mDataParser!!.start()
                            mDataParser!!.add(data, Const.MedicinePillBox)
                        } else if (device.toString().equals(Const.BloodPressure)) {
                            Log.e("MybLe1", "add: " + Arrays.toString(data))
                            if (data.size == 8) {
                                Log.e("MybLe1", "add: " + Arrays.toString(data))
                                for (datalog in data) {
                                    val hi = datalog
                                    val hinumber = hi.toInt()
                                    val hinumberunsignedHex =
                                        String.format("%02X", hinumber and 0xff)
                                    val lownumberdecimal: Int = hinumberunsignedHex.toInt(16)
                                    Log.e(
                                        "MybLe  current",
                                        hinumber.toString() + "\nDecimal " + lownumberdecimal.toString() + "\nHexa " + hinumberunsignedHex.toString()
                                    )
                                }
                                mDataParser!!.start()
                                mDataParser!!.add(data, Const.BloodPressure)
                            }
                        } else {
                            Log.e("MybLe1", "add: " + Arrays.toString(data))
                            for (datalog in data) {
                                val hi = datalog
                                val hinumber = hi.toInt()
                                val hinumberunsignedHex = String.format("%02X", hinumber and 0xff)
                                val lownumberdecimal: Int = hinumberunsignedHex.toInt(16)
                                Log.e(
                                    "MybLe  current",
                                    hinumber.toString() + "\nDecimal " + lownumberdecimal.toString() + "\nHexa " + hinumberunsignedHex.toString()
                                )
                            }
                            mDataParser!!.start()
                            mDataParser!!.add(data, Const.ECG)

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
                                Log.d("MybLe", "Intent: ${device.connected}")

                            }
                        }
                    }
                })
        }

        override fun onDisconnected(info: String, status: Int, device: BleDevice) {
            isConnected = false
            viewDataBinding?.btnRetry?.visibility = View.GONE
            viewDataBinding?.btnReport?.visibility = View.GONE
            viewDataBinding?.tvStatus?.text = ""
            viewDataBinding?.tvParams?.text = ""
            Toast.makeText(context!!, "Disconnected", Toast.LENGTH_SHORT).show()
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
                    if ((device.toString()
                            .equals(Const.Thermometer) && result.device.name != null && result.device.name.contains(
                            Const.Thermometer
                        )) || (device.toString()
                            .equals(Const.Glucometer) && result.device.name != null && result.device.name.contains(
                            Const.Glu
                        )) || (device.toString()
                            .equals(Const.BloodPressure) && result.device.name != null && result.device.name.contains(
                            Const.BP
                        )) || (device.toString()
                            .equals(Const.MedicinePillBox) && result.device.name != null && result.device.name.contains(
                            Const.PillBox
                        )) || (!device.toString().equals(Const.Thermometer) && !device.toString()
                            .equals(Const.Glucometer) && !device.toString()
                            .equals(Const.BloodPressure) && !device.toString()
                            .equals(Const.MedicinePillBox))
                    ) {

                        scanResults.add(result)
                        scanResults.sortByDescending { it.rssi }
                        val predicate = Predicate { x: ScanResult -> x.device.name == null }
                        removeItems(scanResults, predicate)
                    }
                }
                if (!isConnected && scanResults.size == 1) {
                    deviceSearchAdapter?.notifyItemChanged(indexQuery)
                    connect(0)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("ScanCallback", "onScanFailed: code $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(position: Int) {
        stopScan()

        if (!isConnected) {


            viewDataBinding?.tvParams?.setText("Name:")
            viewDataBinding?.tvName?.setText(scanResults.get(position).device.name)
            viewDataBinding?.tvMac?.setText("Mac Address:")
            viewDataBinding?.tvMacAddress?.setText(scanResults.get(position).device.address)
            if (viewDataBinding?.wfvPleth?.mSurfaceHolder?.lockCanvas() != null) {
                viewDataBinding?.wfvPleth?.reset()
            }
            if (device.toString().equals(Const.ECG)) {
                isListen = false
                contecSdk!!.defineBTPrefix(DeviceType.PM10, "EMAY")
                contecSdk!!.connect(scanResults.get(position).device, mConnectCallback)
            } else if (device.toString().equals(Const.MedicinePillBox)) {
                if(!isConnecting) {

                    val mac: String = scanResults.get(position).device.address.replace(":", "")
                    Log.d("linyb", "mac = $mac")
                    val macByte = mac.toByteArray()
                    val CRC16MAC = CRC16Util.getCRC(macByte).toUpperCase()
                    Log.d("linyb", "MACcrc16 = $CRC16MAC")
//                com.zayata.zayatabluetoothsdk.bluetooth.BluetoothManager.getInstance().closeAll()
                    //BluetoothManager.getInstance().connect(CRC16MAC,datas.get(pos).getMac());
                    //String[] macList = new String[]{datas.get(pos).getMac(),"A6:C0:80:D3:0C:91"};

                    //BluetoothManager.getInstance().connect(CRC16MAC,datas.get(pos).getMac());
                    //String[] macList = new String[]{datas.get(pos).getMac(),"A6:C0:80:D3:0C:91"};
                    val openDevConnBean = OpenDevConnBean()
                    openDevConnBean.dn = scanResults.get(position).device.address
                    openDevConnBean.bleId = scanResults.get(position).device.address
                    openDevConnBean.isShowLog = true //debug switch
                    isConnecting = true //debug switch


                    openDevConnBean.succCb = object : ConnSuccCallBack() {
                        override fun onCallBack(data: ConnSuccCbBean) {

                            val paramValueList = data.confParams
                            Log.d("linyb", "succCb mac = " + data.mac)
                            for (i in paramValueList.indices) {
                                Log.d("linyb", "succCb tag " + i + "=" + paramValueList[i].tag)
                                Log.d(
                                    "linyb",
                                    "succCb value " + i + "=" + ByteUtil.bytesToHexString(
                                        paramValueList[i].value
                                    )
                                )
                            }
                            //                    Toast.makeText(context, "connection succeeded", Toast.LENGTH_SHORT)
//                        .show()
                            Log.e("\"MYBLE\"", "connection succeeded")
                            Log.e("MYBLE", "status = Connected")
                            isConnecting = false
                            isConnected = true
                            m11x.getAlarmList(
                                scanResults[0].device.address,
                                object : DevParamCallBack() {
                                    override fun respCb(paramOpRespCbBean: ParamOpRespCbBean) {
                                        val data = paramOpRespCbBean.params
                                        val alarmBean1 = AlarmBean(
                                            1,
                                            data[2].value[0].toInt(),
                                            data[0].value[0].toInt(), data[1].value[0].toInt()
                                        )
                                        val alarmBean2 = AlarmBean(
                                            2,
                                            data[5].value[0].toInt(),
                                            data[3].value[0].toInt(), data[4].value[0].toInt()
                                        )
                                        val alarmBean3 = AlarmBean(
                                            3,
                                            data[8].value[0].toInt(),
                                            data[6].value[0].toInt(), data[7].value[0].toInt()
                                        )
                                        val alarmBean4 = AlarmBean(
                                            4,
                                            data[11].value[0].toInt(),
                                            data[9].value[0].toInt(), data[10].value[0].toInt()
                                        )
                                        val alarmBean5 = AlarmBean(
                                            5,
                                            data[14].value[0].toInt(),
                                            data[12].value[0].toInt(), data[13].value[0].toInt()
                                        )
                                        val alarmBean6 = AlarmBean(
                                            6,
                                            data[17].value[0].toInt(),
                                            data[15].value[0].toInt(), data[16].value[0].toInt()
                                        )
                                        val alarmBean7 = AlarmBean(
                                            7,
                                            data[20].value[0].toInt(),
                                            data[18].value[0].toInt(), data[19].value[0].toInt()
                                        )
                                        val alarmBean8 = AlarmBean(
                                            8,
                                            data[23].value[0].toInt(),
                                            data[21].value[0].toInt(), data[22].value[0].toInt()
                                        )
                                        val alarmBean9 = AlarmBean(
                                            9,
                                            data[26].value[0].toInt(),
                                            data[24].value[0].toInt(), data[25].value[0].toInt()
                                        )
                                        m11x.alarmList.add(alarmBean1)
                                        m11x.alarmList.add(alarmBean2)
                                        m11x.alarmList.add(alarmBean3)
                                        m11x.alarmList.add(alarmBean4)
                                        m11x.alarmList.add(alarmBean5)
                                        m11x.alarmList.add(alarmBean6)
                                        m11x.alarmList.add(alarmBean7)
                                        m11x.alarmList.add(alarmBean8)
                                        m11x.alarmList.add(alarmBean9)
                                        for (i in m11x.alarmList.indices) {
                                            Log.e(
                                                "linyb",
                                                "index:" + m11x.alarmList[i].seq + " hour:" + m11x.alarmList[i].time + ", hours:" + m11x.alarmList[i].hour + ", status:" + m11x.alarmList[i].status
                                            )
                                        }

                                        val spinAdapter =
                                            AlarmBeanAdapter(
                                                activity!!.applicationContext,
                                                m11x.alarmList
                                            )

                                        activity?.runOnUiThread {
                                            viewDataBinding?.spinner?.adapter = spinAdapter
                                            viewDataBinding?.spinner?.onItemSelectedListener =
                                                object : AdapterView.OnItemSelectedListener {
                                                    override fun onItemSelected(
                                                        parent: AdapterView<*>,
                                                        view: View,
                                                        position: Int,
                                                        id: Long
                                                    ) {
                                                        // Get the value selected by the user
                                                        // e.g. to store it as a field or immediately call a method
                                                        val user1 = parent.selectedItem as AlarmBean
                                                        selectedAlarm = user1

                                                    }

                                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                                }
                                            //DO SOMETHING
                                        }
                                    }
                                })
                            activity!!.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                                viewDataBinding?.btnRetry?.visibility = View.GONE
                                viewDataBinding?.btnReport?.visibility = View.VISIBLE
                                viewDataBinding?.btnSend?.text = "Disconnect"
                                viewDataBinding?.newData?.text = "Set Alarm"
                                viewDataBinding?.newData?.visibility = View.VISIBLE
                                viewDataBinding?.alarmView?.visibility = View.VISIBLE
                                viewDataBinding?.tvStatus?.visibility = View.GONE
                                viewDataBinding?.tvTitle?.visibility = View.GONE
                            })
                        }

                    }
                    openDevConnBean.failCb = object : ConnFailCallBack() {
                        override fun onCallBack(data: ConnFailCbBean) {
                            Log.d("linyb", "failCb")
                            activity!!.runOnUiThread(java.lang.Runnable {

                                Toast.makeText(
                                    context,
                                    "Device is disconnected",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                isConnecting = false
                                viewDataBinding?.btnRetry?.visibility = View.GONE
                                viewDataBinding?.btnReport?.visibility = View.GONE
                                viewDataBinding?.tvStatus?.text = ""
                                viewDataBinding?.tvParams?.text = ""
                                viewDataBinding?.alarmView?.visibility = View.GONE
                                viewDataBinding?.btnSend?.setText("Search")
                                viewDataBinding?.newData?.visibility = View.GONE
                                viewDataBinding?.tvStatus?.visibility = View.VISIBLE
                                viewDataBinding?.tvTitle?.visibility = View.VISIBLE
                            })
                            Log.e("\"MYBLE\"", "Connection failed")
                            isConnected = false


                        }
                    }
                    openDevConnBean.notifyCb = object : NotifyCallBack() {
                        override fun onCallBack(data: NotifyCbBean) {
                            Log.d("linyb", "notifyCb")
                        }
                    }

                    openDevConnBean.eventCb = object : EventCallBack() {
                        override fun onCallBack(data: EventCbBean) {
                            Log.d("linyb", "eventCb")
                            val eventValueList = data.params
                            for (i in eventValueList.indices) {
                                when (eventValueList[i].tag) {
                                    0xb0.toByte() -> {
                                        Log.d(
                                            "linyb",
                                            "0xb0 size = " + eventValueList[i].len + ", value = " + ByteUtil.bytesToHexString(
                                                eventValueList[i].value
                                            )
                                        )
                                        val TakeDrugData = eventValueList[i].value

                                        //服药数据 Medication data
                                        val clock = TakeDrugBean()
                                        clock.year =
                                            ByteUtil.bytes2Int(
                                                byteArrayOf(
                                                    TakeDrugData[1],
                                                    TakeDrugData[0]
                                                )
                                            ).toString() + ""

                                        clock.month = TakeDrugData[2].toString() + ""
                                        clock.date = TakeDrugData[3].toString() + ""
                                        if (TakeDrugData[8] == 0x00.toByte()) {
                                            clock.status = 2
                                        } else {
                                            clock.status = 1
                                        }
                                        clock.ahead = TakeDrugData[9] + 1
                                        clock.alarm_time =
                                            String.format(
                                                Locale.getDefault(), "%02d",
                                                TakeDrugData[6]
                                            ) + ":" + String.format(
                                                Locale.getDefault(), "%02d",
                                                TakeDrugData[7]
                                            )

                                        clock.drug_time =
                                            (String.format(
                                                Locale.getDefault(), "%02d",
                                                TakeDrugData[4]
                                            ) + ":" + String.format(
                                                Locale.getDefault(), "%02d",
                                                TakeDrugData[5]
                                            ))

                                        Log.d(
                                            "linyb",
                                            ("KEY_EVENT_PARAMS_CHANGE month = " + clock.month.toString() + "KEY_EVENT_PARAMS_CHANGE date = " + clock.date.toString() + "KEY_EVENT_PARAMS_CHANGE getAlarm_time = " + clock.alarm_time.toString() + "KEY_EVENT_PARAMS_CHANGE getDrug_time = " + clock.drug_time)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    openDevConnBean.disconnCb = object : DisconnCallBack() {
                        override fun onCallBack(data: DisconnCbBean) {
                            Log.d("linyb", "disconnCb")
                            activity!!.runOnUiThread(java.lang.Runnable {
                                isConnecting = false
                                Toast.makeText(
                                    context,
                                    "Device is disconnected",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                viewDataBinding?.btnRetry?.visibility = View.GONE
                                viewDataBinding?.btnReport?.visibility = View.GONE
                                viewDataBinding?.tvStatus?.text = ""
                                viewDataBinding?.tvParams?.text = ""
                                viewDataBinding?.btnSend?.setText("Search")
                                viewDataBinding?.alarmView?.visibility = View.GONE
                                viewDataBinding?.newData?.visibility = View.GONE
                                viewDataBinding?.tvStatus?.visibility = View.VISIBLE
                                viewDataBinding?.tvTitle?.visibility = View.VISIBLE
                            })
                            Log.e("\"MYBLE\"", "Connection failed")
                            isConnected = false

                        }
                    }

                    bleManagerBluetoothManager.openDevConn(openDevConnBean)

                    // bleManager = BleManager.getInstance().init(context)
                    // bleManager.connect(
                    //    scanResults.get(position).device.address,
                    //    bleConnectCallback
                    // );
                }

            } else {
                bleManager = BleManager.getInstance().init(context)
                bleManager.connect(
                    scanResults.get(position).device.address,
                    bleConnectCallback
                );
            }


        } else {

            disconnect()
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (isConnected) {

            if (device.toString().equals(Const.ECG)) {

                if (isConnected) {
                    contecSdk?.disconnect()
                }
            } else if (device.toString().equals(Const.MedicinePillBox)) {

                if (isConnected) {
                    bleManagerBluetoothManager
                        .closeBle(callbackCloseable)
                    bleManagerBluetoothManager.closeAll()
                }else{
                    bleManagerBluetoothManager
                        .closeBle(callbackCloseable)
                    bleManagerBluetoothManager.closeAll()
                }
            } else {

                if (bleManager.connectedDevices.size > 0) {
                    bleManager.disconnectAll()
                }
            }
            viewDataBinding?.tvParams?.setText("")
            viewDataBinding?.tvName?.setText("")
            viewDataBinding?.tvMac?.setText("")
            viewDataBinding?.tvMacAddress?.setText("")
        }
    }

    override fun setupObserver() {

    }


    override fun onEventClicked() {

    }

    fun ByteArray.toHex(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }


    //endregion

    //region Permissions
    private fun checkPermissions() {

        if (BleManager.supportBle(context)) {
            if (BleManager.isBluetoothOn()) {
                if ((hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && hasPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )) || (hasPermission(Manifest.permission.BLUETOOTH_SCAN) && hasPermission(
                        Manifest.permission.BLUETOOTH_CONNECT
                    ))
                ) {
                    startScan()
                } else {
                    askPermission()
                }

            } else {
                askPermission()

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
            disconnect()
        } else {
            //start scan with specified scanOptions
            disconnect()
            var filters: List<ScanFilter>? = null
            filters = ArrayList()
            if (device.toString().equals(Const.Oximeter)) {
                filters.add(
                    ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid(Const.UUID_SERVICE_DATA_Oximeter)).build()
                )
            }

            if (device.toString().equals(Const.Glucometer)) {
                filters.add(
                    ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid(Const.UUID_SERVICE_DATA_GlucoMeter)).build()
                )
            }

            if (device.toString().equals(Const.Thermometer)) {
                filters.add(
                    ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid(Const.UUID_SERVICE_DATA_Thermometer)).build()
                )
            }
            if (device.toString().equals(Const.MedicinePillBox)) {
                filters.add(
                    ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid(Const.UUID_SERVICE_DATA_MedicinePillBox)).build()
                )
            }
            if (device.toString().equals(Const.BloodPressure)) {
                filters.add(
                    ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid(Const.UUID_SERVICE_DATA_BloodPressure)).build()
                )
            }
            scanResults.clear()
            deviceSearchAdapter?.setData(scanResults)
            if (device.toString().equals(Const.ECG)) {

                contecSdk?.startBluetoothSearch(searchCallback, 20000)
            } else if (device.toString().equals(Const.BloodPressure)) {

                bleScanner.startScan(filters, scanSettings, scanCallback)
            } else if (device.toString().equals(Const.Thermometer)) {

                bleScanner.startScan(null, scanSettings, scanCallback)
            } else if (device.toString().equals(Const.MedicinePillBox)) {

                bleScanner.startScan(null, scanSettings, scanCallback)
            } else {
                bleScanner.startScan(filters, scanSettings, scanCallback)
//
            }
            isScanning = true
            Log.d("TAG", "scanResults: $scanResults")
//            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        if (device.toString().equals(Const.ECG)) {

            contecSdk?.stopBluetoothSearch()

            isScanning = false
        } else {
            Log.d("TAG", "scanResults: $scanResults")
            bleScanner.stopScan(scanCallback)
            isScanning = false
        }
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


    override fun onDestroy() {
        super.onDestroy()
        mDataParser!!.stop()
        NoticeUtils.getInstance().unRegister(this)
        if (isScanning) {
            if (device.equals(Const.MedicinePillBox)) {
                try {
                    try {
                        bleManagerBluetoothManager.stopBleScan()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    fun askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            var permissionlistener = object : PermissionListener {
                override fun onPermissionGranted() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        BleManager.enableBluetooth(activity, 12530);
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
            } else {
                BleManager.enableBluetooth(activity, 12530);
            }
        } else {
            var permissionlistener = object : PermissionListener {
                override fun onPermissionGranted() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        BleManager.enableBluetooth(activity, 12530);
                    }
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
//                setPermissions()
                    //              Toast.makeText(DashBoard.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || !hasPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

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
            } else {
                BleManager.enableBluetooth(activity, 12530);
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


    fun ecgConnectivity() {
        contecSdk = ContecSdk(context)
        contecSdk!!.init(false)
    }

    private val mConnectCallback = object : ConnectCallback {
        override fun onConnectStatus(status: Int) {
            activity!!.runOnUiThread(java.lang.Runnable {


                if (status == SdkConstants.CONNECT_CONNECTED) {
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(context, "connection succeeded", Toast.LENGTH_SHORT)
//                        .show()
                    Log.e("\"MYBLE\"", "connection succeeded")
                    if (contecSdk != null) {
                        Log.e("MYBLE", "status = $status")
                        isConnected = true
//                Log.d("MybLe", "Intent: ${status}")
                        viewDataBinding?.btnRetry?.visibility = View.VISIBLE
                        viewDataBinding?.btnReport?.visibility = View.VISIBLE
                        viewDataBinding?.btnSend?.text = "Disconnect"

                        contecSdk!!.setTransSpeed(DeviceParameter.TransSpeed.FAST) //Set transfer rate
//
                        contecSdk!!.setDataType(DeviceParameter.DataType.ALL) //Set acquisition data type
//
                        contecSdk!!.getData(communicateCallback)
                    }
                } else if (status == SdkConstants.CONNECT_DISCONNECTED || status == SdkConstants.CONNECT_DISCONNECT_SERVICE_UNFOUND || status == SdkConstants.CONNECT_DISCONNECT_NOTIFY_FAIL || status == SdkConstants.CONNECT_DISCONNECT_EXCEPTION) {
                    Toast.makeText(context, "Device is disconnected", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("\"MYBLE\"", "Connection failed")
                    isConnected = false
                    viewDataBinding?.btnRetry?.visibility = View.GONE
                    viewDataBinding?.btnReport?.visibility = View.GONE
                    viewDataBinding?.tvStatus?.text = ""
                    viewDataBinding?.tvParams?.text = ""
                    viewDataBinding?.btnSend?.text = "Search"
                }
            })
        }

        override fun onOpenStatus(p0: Int) {
            activity!!.runOnUiThread(java.lang.Runnable {
//                tvStatus.setText("status = $status")
                if (p0 == SdkConstants.OPEN_SUCCESS) {
                    Toast.makeText(
                        context,
                        "Device turned on successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("MYBLE", "Device turned on successfully")
                } else {
                    Toast.makeText(context, "Device failed to open", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("MYBLE", "Device failed to open")
                }
            })
        }
    }

    /**
     * 获取数据回调
     */
    var communicateCallback: CommunicateCallback = object : CommunicateCallback {
        /**
         * 获取数据失败
         * @param errorCode
         */
        override fun onFail(errorCode: Int) {
            activity!!.runOnUiThread(java.lang.Runnable {
                if (contecSdk != null) {
                    Log.e("MYBLE", "Get data timed out" + errorCode)
                    disconnect()

                    isConnected = false
                    viewDataBinding?.btnRetry?.visibility = View.GONE
                    viewDataBinding?.btnReport?.visibility = View.GONE
                    viewDataBinding?.btnSend?.text = "Search Device"
//                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                }
            })
        }

        /**
         * 设备中没有未上传数据
         */
        override fun onDataEmpty() {

            activity!!.runOnUiThread(java.lang.Runnable {
//                tvGetData.setText("There is no unuploaded data in the device")
                "Data not available please take reading".toast()
                if (isListen) {
                    if (null != contecSdk) {
                        contecSdk!!.listenRemoteDevice(
                            scanResults[0].device,
                            listenRemoteDeviceCallback
                        )
                    }
                }
            })
        }

        /**
         * 按段返回心电数据
         * @param ecgData
         */
        override fun onData(ecgData: EcgData) {
            activity!!.runOnUiThread(java.lang.Runnable {
                ecgDataArrayList.add(ecgData)
                if (ecgDataArrayList.size == 1) {
                    val stringBuffer = StringBuffer()
                    Log.e(TAG, "currentCount = " + ecgData.currentCount)
                    Log.e(TAG, "size = " + ecgData.size)
                    Log.e(TAG, "pr = " + ecgData.pr)
                    for (i in ecgData.chineseResult.indices) {
                        Log.e(TAG, "re = " + ecgData.chineseResult[i])
                        Log.e(TAG, "in = " + ecgData.indexResult[i])
                    }
//                    stringBuffer.append(
//                        """
//                    uploadCount = ${ecgData.uploadCount}
//
//                    """.trimIndent()
//                    )
//                    stringBuffer.append(
//                        """
//                    currentCount = ${ecgData.currentCount}
//
//                    """.trimIndent()
//                    )
                    stringBuffer.append(
                        """
                    size = ${ecgData.size}
                    
                    """.trimIndent()
                    )
                    stringBuffer.append(
                        """
                    pr = ${ecgData.pr}
                    
                    """.trimIndent()
                    )
                    val date: String =
                        convertFormat(ecgData.year) + "-" + convertFormat(
                            ecgData.month
                        ) +
                                "-" + convertFormat(
                            ecgData.day
                        ) + " " + convertFormat(
                            ecgData.hour
                        ) +
                                ":" + convertFormat(
                            ecgData.min
                        ) + ":" + convertFormat(
                            ecgData.sec
                        )
                    stringBuffer.append("date = $date\n")
//                    for (i in ecgData.chineseResult.indices) {
//                        stringBuffer.append("Chresult = " + ecgData.englishResult[i] + "   ")
//                    }
//                    stringBuffer.append("\n")
                    for (i in ecgData.englishResult.indices) {
                        stringBuffer.append("Enresult = " + ecgData.englishResult[i] + "   ")
                    }
                    stringBuffer.append("\n")
                    Log.e("Result", "onData: " + stringBuffer)
                    mDataParser!!.start()
                    mDataParser!!.addECG(stringBuffer.toString(), Const.ECG)

                }
            })
        }

        /**
         * Progress callback for each item
         * @param uploadCount
         * @param currentCount
         * @param progress
         */
        override fun onProgress(uploadCount: Int, currentCount: Int, progress: Int) {
            activity!!.runOnUiThread(java.lang.Runnable {
                Log.e(
                    TAG, "uploadCount = " + uploadCount + "   currentCount = " +
                            currentCount + "   progress = " + progress
                );
//                tvGetData.setText("current progress = $progress")
            })
        }

        /**
         * data received
         */
        override fun onDataComplete() {
            activity!!.runOnUiThread(java.lang.Runnable {
                Log.e("MYLBL", "data received")
                contecSdk!!.deleteData(DeviceParameter.DataType.ALL, 101, deleteDataCallback)
                ecgDataArrayList.clear()
                if (isListen) {
                    if (null != contecSdk) {
                        contecSdk!!.listenRemoteDevice(
                            scanResults[0].device,
                            listenRemoteDeviceCallback
                        )
                    }
                }
            })
        }

        /**
         * After the data is received, the data is deleted successfully
         */
        override fun onDeleteSuccess() {
            requireActivity().runOnUiThread(java.lang.Runnable {
                Log.e(
                    "MYLBL",
                    "After the data is received, the data is deleted successfully"
                )
            })
        }

        /**
         * After receiving the data，Failed to delete data
         */
        override fun onDeleteFail() {
            requireActivity().runOnUiThread(java.lang.Runnable {
                Log.e(
                    "MYLBL",
                    "After receiving the data，Failed to delete data"
                )
            })
        }
    }

    var deleteDataCallback: DeleteDataCallback = object : DeleteDataCallback {
        override fun onFail(p0: Int) {


        }

        override fun onSuccess() {


        }

    }

    /**
     * 监听状态回调
     */
    var listenRemoteDeviceCallback =
        ListenRemoteDeviceCallback { listenStatus ->
            requireActivity().runOnUiThread(java.lang.Runnable {
                Log.e("Mylog", "listenStatus = $listenStatus")
                if (listenStatus == SdkConstants.LISTEN_SUCCESS) {
                    if (contecSdk != null) {
                        contecSdk!!.getData(communicateCallback)
                    }
                }
            })
        }

    fun convertFormat(num: Int): String? {
        return if (num < 10) {
            "0$num"
        } else {
            "" + num
        }
    }

    var searchCallback: BluetoothSearchCallback = object : BluetoothSearchCallback {
        @SuppressLint("MissingPermission")
        override fun onScanResult(result: ScanResult) {
            activity!!.runOnUiThread(java.lang.Runnable {
                val device = result.device
                if (device.name.contains("PM10")) {
                    val record = result.scanRecord!!.bytes

                    Log.e(TAG, "BYTE = " + Utils.bytesToHexString(record))
                    var manufactorSpecificString: String? = ""
                    if (record != null) {
                        val manufactorSpecificBytes: ByteArray? =
                            getManufacturerSpecificData(record)
                        if (manufactorSpecificBytes != null) {
                            manufactorSpecificString = String(manufactorSpecificBytes)
                        }
                    }
                    Log.e(TAG, device.name)
                    Log.e(TAG, String(record!!))
                    Log.e(TAG, manufactorSpecificString!!)
                    val stringBuffer = StringBuffer()
                    if (device.type == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                        stringBuffer.append(device.name + "(classic)" + "bluetooth" + "\n")
                    } else if (device.type == BluetoothDevice.DEVICE_TYPE_DUAL) {
                        stringBuffer.append(device.name + "(dual)" + "bluetooth" + "\n")
                    } else if (device.type == BluetoothDevice.DEVICE_TYPE_LE) {
                        stringBuffer.append(device.name + "(ble)" + "bluetooth" + "\n")
                    }
                    if (manufactorSpecificString != null) {
                        if (manufactorSpecificString.contains("DT") && manufactorSpecificString.contains(
                                "DATA"
                            )
                        ) {
                            val index = manufactorSpecificString.indexOf("DT")
                            val date = manufactorSpecificString.substring(index + 2, index + 8)
                            stringBuffer.append(
                                manufactorSpecificString + "\n"
                                        + "有数据, " + "当前时间为" + date
                            )
                        } else if (manufactorSpecificString.contains("DT")) {
                            val index = manufactorSpecificString.indexOf("DT")
                            val date = manufactorSpecificString.substring(index + 2, index + 8)
                            stringBuffer.append(
                                manufactorSpecificString + "\n"
                                        + "没有数据, " + "当前时间为" + date
                            )
                        } else if (manufactorSpecificString.contains("DATA")) {
                            stringBuffer.append(
                                (manufactorSpecificString + "\n"
                                        + "有数据，没有时间")
                            )
                        }
                    }
                    if (result != null) {

                        val indexQuery =
                            scanResults.indexOfFirst { it.device.address == result.device.address }
                        if (indexQuery != -1) {
                            scanResults[indexQuery] = result
                            deviceSearchAdapter?.notifyItemChanged(indexQuery)
                        } else {
                            if ((device.toString() == Const.Thermometer && result.device.name != null && result.device.name.contains(
                                    Const.Thermometer
                                )) || !device.toString().equals(Const.Thermometer)
                            ) {

                                scanResults.add(result)
                                scanResults.sortByDescending { it.rssi }
                                val predicate = Predicate { x: ScanResult -> x.device.name == null }
                                removeItems(scanResults, predicate)
                            }
                        }
                        if (!isConnected && scanResults.size == 1) {
                            deviceSearchAdapter?.notifyItemChanged(indexQuery)
                            connect(0)
                        }
                    }
                }

            }
            )

        }

        override fun onScanError(errorCode: Int) {
            activity!!.runOnUiThread(java.lang.Runnable {
                if (errorCode == SdkConstants.SCAN_FAIL_BT_UNSUPPORT) {
                    Log.e(TAG, "this no bluetooth")
                } else if (errorCode == SdkConstants.SCAN_FAIL_BT_DISABLE) {
                    Log.e(TAG, "bluetooth not enable")
                }
            })
        }

        override fun onScanComplete() {
            activity!!.runOnUiThread(java.lang.Runnable { Log.e(TAG, "search complete") })
        }
    }

    override fun doNotice(key: Int, data: Any) {
        val datas: ArrayList<DeviceParamsBean> = ArrayList<DeviceParamsBean>()
        var mac = data.toString()
        Log.d("linyb", "mac do notice"+mac)
        when (key) {
            NoticeUtils.KEY_M105_BEGIN_CONNECT -> {
                Log.d("linyb", "KEY_M105_BEGIN_CONNECT")
                requireActivity().runOnUiThread(java.lang.Runnable {
//                    adapter.changeData(
//                        data as String,
//                        getString(R.string.connecting)
//                    )
                })
            }


            NoticeUtils.KEY_M105_CONNECT_SUCCESS -> {
                Log.d("linyb", "KEY_M105_CONNECT_SUCCESS")
                var supportConfig =
                    bleManagerBluetoothManager
                        .getSupportConfigMultidevice(mac)
                var supportState =
                    bleManagerBluetoothManager
                        .getSupportStateMultidevice(mac)
                var supportControl =
                    bleManagerBluetoothManager
                        .getSupportControlMultidevice(mac)
                var supportEvent =
                    bleManagerBluetoothManager
                        .getSupportEventMultidevice(mac)

                val locale = resources.configuration.locale
                val language = locale.language

                //CmdExplain中文  CmdExplainEN英文 CmdExplain Chinese CmdExplainEN English

                //CmdExplain中文  CmdExplainEN英文 CmdExplain Chinese CmdExplainEN English
                for (i in supportConfig.indices) {
                    val deviceParamsBean = DeviceParamsBean()
                    deviceParamsBean.type = getString(R.string.configuration_parameter)
                    deviceParamsBean.tag = supportConfig.get(i)
                    deviceParamsBean.size = CmdSize.getCmdSize(supportConfig.get(i))
                    if (language.contains("en")) {
                        deviceParamsBean.explain = CmdExplainEN.getCmdExplain(supportConfig.get(i))
                    } else {
                        deviceParamsBean.explain = CmdExplain.getCmdExplain(supportConfig.get(i))
                    }
                    datas.add(deviceParamsBean)
                }
                for (i in supportState.indices) {
                    val deviceParamsBean = DeviceParamsBean()
                    deviceParamsBean.type = getString(R.string.state_parameter)
                    deviceParamsBean.tag = supportState.get(i)
                    deviceParamsBean.size = CmdSize.getCmdSize(supportState.get(i))
                    if (language.contains("en")) {
                        deviceParamsBean.explain = CmdExplainEN.getCmdExplain(supportState.get(i))
                    } else {
                        deviceParamsBean.explain = CmdExplain.getCmdExplain(supportState.get(i))
                    }
                    datas.add(deviceParamsBean)
                }
                for (i in supportControl.indices) {
                    val deviceParamsBean = DeviceParamsBean()
                    deviceParamsBean.type = getString(R.string.controls_parameter)
                    deviceParamsBean.tag = supportControl.get(i)
                    deviceParamsBean.size = CmdSize.getCmdSize(supportControl.get(i))
                    if (language.contains("en")) {
                        deviceParamsBean.explain = CmdExplainEN.getCmdExplain(supportControl.get(i))
                    } else {
                        deviceParamsBean.explain = CmdExplain.getCmdExplain(supportControl.get(i))
                    }
                    datas.add(deviceParamsBean)
                }
                for (i in supportEvent.indices) {
                    val deviceParamsBean = DeviceParamsBean()
                    deviceParamsBean.type = getString(R.string.event_parameter)
                    deviceParamsBean.tag = supportEvent.get(i)
                    deviceParamsBean.size = CmdSize.getCmdSize(supportEvent.get(i))
                    if (language.contains("en")) {
                        deviceParamsBean.explain = CmdExplainEN.getCmdExplain(supportEvent.get(i))
                    } else {
                        deviceParamsBean.explain = CmdExplain.getCmdExplain(supportEvent.get(i))
                    }
                    datas.add(deviceParamsBean)
                }
                requireActivity().runOnUiThread(java.lang.Runnable {
//                    adapter.changeData(
//                        data as String,
//                        getString(R.string.connected)
//                    )
                })
                val bundle = Bundle()
                bundle.putString("mac", data.toString())
                Log.d("linyb", "KEY_M105_CONNECT_SUCCESS mac=$data")
//                PageJumpPresenter.junmp(
//                    this@SelectDeviceActivity,
//                    DeviceConnectedActivity::class.java, bundle, false
//                )
            }
            NoticeUtils.KEY_M105_CONNECT_FAILURE -> {
                Log.d("linyb", "KEY_M105_CONNECT_FAILURE")
                requireActivity().runOnUiThread(java.lang.Runnable {
//                    adapter.changeData(
//                        data as String,
//                        getString(R.string.connection_error)
//                    )
                })
            }
            NoticeUtils.KEY_M105_DISCONNECT -> {
                Log.d("linyb", "KEY_M105_DISCONNECT")
                requireActivity().runOnUiThread(java.lang.Runnable {
//                    adapter.changeData(
//                        data as String,
//                        getString(R.string.not_connected)
//                    )
                })
            }
        }
//        requireActivity().runOnUiThread(java.lang.Runnable {
//            adapter.notifyDataSetChanged() })
    }

    val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> viewDataBinding?.tvParams?.text = msg.obj as String
                3 -> viewDataBinding?.tvName?.text = msg.obj as String
                4 -> viewDataBinding?.tvMac?.text = msg.obj as String
                5 -> viewDataBinding?.tvMacAddress?.text = (msg.obj as StringBuffer)
            }
        }
    }

    private fun getDeviceVersion() {
        val stateList: MutableList<ParamTlvBean> = ArrayList()
        val paramTlvBean1 = ParamTlvBean(0x01.toByte(), 0x01.toByte(), byteArrayOf(0x00.toByte()))
        val paramTlvBean3 = ParamTlvBean(0x03.toByte(), 0x01.toByte(), byteArrayOf(0x00.toByte()))
        val paramTlvBean4 = ParamTlvBean(0x04.toByte(), 0x01.toByte(), byteArrayOf(0x00.toByte()))
        stateList.add(paramTlvBean1)
        stateList.add(paramTlvBean3)
        stateList.add(paramTlvBean4)
        val paramOpBean = ParamOpBean()
        paramOpBean.dn = scanResults.get(0).device.address
        paramOpBean.opId = 1
        paramOpBean.params = stateList
        paramOpBean.devParamCallBack = object : DevParamCallBack() {
            override fun respCb(paramOpRespCbBean: ParamOpRespCbBean) {
                Log.d("linyb", "status = " + paramOpRespCbBean.stat)
                val value = paramOpRespCbBean.params
                for (i in value.indices) {
                    when (value[i].tag) {
                        0x01.toByte() -> {
                            val message = Message()
                            message.what = 1
                            message.obj =
                                """
                                ${getString(R.string.device_type)}${if (value[i].value[0] == 0x01.toByte()) "M10X" else "M11X"}
                                MAC = ${scanResults.get(0).device.address}
                                """.trimIndent()
                            mHandler.sendMessage(message)
                        }
                        0x03.toByte() -> {
                            val message3 = Message()
                            message3.what = 3
                            message3.obj =
                                getString(R.string.firmware_version) + (value[i].value[0] and 0xF0 shr 4) + "." + (value[i].value[0] and 0x0F) + ""
                            mHandler.sendMessage(message3)
                        }
                        0x04.toByte() -> {
                            val message4 = Message()
                            message4.what = 4
                            message4.obj =
                                getString(R.string.registration_type) + if (value[i].value[0] == 0x00.toByte()) getString(
                                    R.string.unforced_synchronization
                                ) else getString(R.string.forced_synchronization)
                            mHandler.sendMessage(message4)
                        }
                    }
                }
                for (i in value.indices) {
                    Log.d("linyb","tag = " + value[i].tag + ", size = " + value[i].len + ", value = " + ByteUtil.bytesToHexString(value[i].value))
                }
            }
        }
        bleManagerBluetoothManager
            .getDevStat(paramOpBean)
    }

    var callbackCloseable: CloseBleCallBack = object : CloseBleCallBack() {
        override fun onCallBack(p0: String?) {
            activity!!.runOnUiThread(java.lang.Runnable {

                Toast.makeText(context, "Device is disconnected", Toast.LENGTH_SHORT)
                    .show()
                viewDataBinding?.btnRetry?.visibility = View.GONE
                viewDataBinding?.btnReport?.visibility = View.GONE
                viewDataBinding?.tvStatus?.text = ""
                viewDataBinding?.tvParams?.text = ""
                viewDataBinding?.btnSend?.setText("Search")
                viewDataBinding?.alarmView?.visibility = View.GONE
                viewDataBinding?.newData?.visibility = View.GONE
                viewDataBinding?.tvStatus?.visibility = View.VISIBLE
                viewDataBinding?.tvTitle?.visibility = View.VISIBLE
                isConnected = false
            })
            Log.d("linyb","tag = " + p0 )

        }

    }


}