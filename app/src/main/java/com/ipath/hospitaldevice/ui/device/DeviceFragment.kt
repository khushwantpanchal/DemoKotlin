package com.ipath.hospitaldevice.ui.device


import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.berry_med.spo2_ble.data.Const
import com.contec.pm10.code.bean.DeviceType
import com.contec.pm10.code.bean.EcgData
import com.contec.pm10.code.callback.*
import com.contec.pm10.code.connect.ContecSdk
import com.ficat.easyble.BleManager
import com.ipath.hospitaldevice.R
import com.ipath.hospitaldevice.base.BaseFragment
import com.ipath.hospitaldevice.ble.adapter.SearchDevicesDialog
import com.ipath.hospitaldevice.ble.data.DataParser
import com.ipath.hospitaldevice.databinding.DeviceFragmentBinding
import com.ipath.hospitaldevice.ui.adapter.DeviceListMedicalAdapter
import com.ipath.hospitaldevice.ui.adapter.DeviceSearchAdapter
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


class DeviceFragment : BaseFragment<DeviceFragmentBinding, DeviceVM>(), DeviceNavigator,
    CoroutineScope{
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var deviceSearchAdapter: DeviceListMedicalAdapter? = null

    private val searchVM: DeviceVM by viewModels()
    lateinit var device_array :Array<String>

    var  arg :String?= "";
    var mobile:String? = "";
    var email:String? = "";
    override fun getViewModel(): DeviceVM {
        searchVM.setNavigator(this)
        return searchVM
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun setupToolBar() {
    }

    override fun getBindingVariable(): Int {
        return BR.dfragment
    }

    override fun getLayoutId(): Int {
        return R.layout.device_fragment
    }

    override fun setupUI() {
        setupTitle(getString(R.string.report))
        setupBackButtonEnable(true,object : View.OnClickListener{
            override fun onClick(v: View?) {

            }

        })
        arg = arguments?.getString("pname")
        email = arguments?.getString("email")
        mobile = arguments?.getString("mobile")
        viewDataBinding?.pName?.text = arg;
        val linearLayoutManager = LinearLayoutManager(context)
         device_array = context?.resources?.getStringArray(R.array.devicelist) as Array<String>
        deviceSearchAdapter = context?.let { DeviceListMedicalAdapter(it, device_array) }

        viewDataBinding?.recyclerView?.setLayoutManager(linearLayoutManager)
        viewDataBinding?.recyclerView?.setAdapter(deviceSearchAdapter)
        setUpAdapter()



        deviceSearchAdapter!!.setOnItemClick(object : DeviceListMedicalAdapter.OnItemClickListener {
            @SuppressLint("MissingPermission")
            override fun onItemClick(position: Int) {

                val bundle = Bundle()
                bundle.putString("pname",arg)
                bundle.putString("email", email)
                bundle.putString("mobile", mobile)
                bundle.putString("device", device_array.get(position))
                findNavController().navigate(R.id.action_device_to_patientFragment,bundle);
            }
        })


    }


    override fun setupObserver() {

    }


    override fun onEventClicked() {
//        findNavController().navigate(WelcomeFragmentDirections.Fragment_to_patientFragment)
    }

    fun ByteArray.toHex(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun setUpAdapter() {
        deviceSearchAdapter = context?.let { DeviceListMedicalAdapter(it, device_array) }
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



    private fun String.toast() {
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }








}