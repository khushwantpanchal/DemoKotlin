package com.ipath.hospitaldevice.ui.patientreport


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
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.Predicate
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.berry_med.spo2_ble.data.Const
import com.ipath.hospitaldevice.R
import com.ipath.hospitaldevice.base.BaseFragment
import com.ipath.hospitaldevice.ble.BleController
import com.ipath.hospitaldevice.ble.adapter.DeviceListAdapter
import com.ipath.hospitaldevice.ble.adapter.SearchDevicesDialog
import com.ipath.hospitaldevice.ble.data.DataParser
import com.ipath.hospitaldevice.databinding.ReportFragmentBinding
import com.ipath.hospitaldevice.databinding.SearchFragmentBinding
import com.ipath.hospitaldevice.ui.adapter.DeviceSearchAdapter
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess


class ReportFragment : BaseFragment<ReportFragmentBinding, ReportVM>(), ReportNavigator,
    CoroutineScope {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var deviceSearchAdapter: DeviceSearchAdapter? = null

    private val searchVM: ReportVM by viewModels()
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager =
            context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                Log.d("TAG", "Intent: $intent")
            }
        }


    private val scanResults = mutableListOf<ScanResult>()

    override fun getViewModel(): ReportVM {
        searchVM.setNavigator(this)
        return searchVM
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()


    override fun setupToolBar() {

    }

    override fun getBindingVariable(): Int {
        return BR.reportfragment
    }

    override fun getLayoutId(): Int {
        return R.layout.report_fragment
    }

    override fun setupUI() {
        setupTitle(getString(R.string.searchdetails))
        setupBackButtonEnable(true,false)
        val arg = arguments?.getString("pname")
        val email = arguments?.getString("email")
        val mobile = arguments?.getString("mobile")
        val sp02 = arguments?.getString("sp02")
        val beat = arguments?.getString("beat")
        val GluecosedL = arguments?.getString("GluecosedL")
        val GluecosedLmmolLvalue = arguments?.getString("GluecosedLmmolLvalue")
        val Celcius = arguments?.getString("Celcius")
        val Fahrenheit = arguments?.getString("Fahrenheit")
        val ECGDataREcord = arguments?.getString("ECGDataREcord")


        viewDataBinding?.pName?.text = arg
        viewDataBinding?.pMobile?.text = mobile
        viewDataBinding?.pEmail?.text = email
        if(!sp02.isNullOrEmpty()) {
            viewDataBinding?.pResult?.setText(
                "SpO2: " + sp02
                    .toString() + "   Pulse Rate:" + beat
            )
        }else if(!GluecosedL.isNullOrEmpty()) {
            viewDataBinding?.pResult?.setText(
                "mg/dL: " + (GluecosedL)
                    .toString() + "   mmol/L: " + (GluecosedLmmolLvalue).toString()
            )

        }else if(!Celcius.isNullOrEmpty()) {
            viewDataBinding?.pResult?.setText(
                "mg/dL: " + (GluecosedL)
                    .toString() + "   mmol/L: " + (GluecosedLmmolLvalue).toString()
            )

        }else{
            viewDataBinding?.pResult?.setText(
                ECGDataREcord
            )
        }
        viewDataBinding?.btnNewTest?.setOnClickListener {
            findNavController().navigate(ReportFragmentDirections.actionReportFragmentToMainFragment())
        }
        viewDataBinding?.btnReg?.setOnClickListener {
            findNavController().popBackStack()
        }

        viewDataBinding?.btnExit?.setOnClickListener {
            activity?.finish();
            exitProcess(0);
        }

        viewDataBinding?.btnSendReport?.setOnClickListener {
            val builder = context?.let { it1 -> AlertDialog.Builder(it1) }
            builder?.setTitle("Androidly Alert")
            builder?.setMessage("We have a message")
            val alertDialog = builder?.create()
            builder?.setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    alertDialog?.dismiss()
                })

            alertDialog?.show()

        }


    }


    override fun setupObserver() {

    }


    override fun onEventClicked() {
//        findNavController().navigate(WelcomeFragmentDirections.Fragment_to_patientFragment)
    }


}