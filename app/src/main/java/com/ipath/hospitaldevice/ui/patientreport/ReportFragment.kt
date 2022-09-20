package com.ipath.hospitaldevice.ui.patientreport


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ipath.hospitaldevice.R
import com.ipath.hospitaldevice.base.BaseFragment
import com.ipath.hospitaldevice.databinding.ReportFragmentBinding
import com.ipath.hospitaldevice.ui.adapter.DeviceSearchAdapter
import com.ipath.hospitaldevice.utils.Utils.SetupHtmlView
import com.ipath.hospitaldevice.utils.Utils.SetupView
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
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
        setupTitle(getString(R.string.result))
        setupBackButtonEnable(true, object : View.OnClickListener {
            override fun onClick(v: View?) {

            }

        })
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
        val mmHgHigh = arguments?.getString("mmHgHigh")
        val mmHgLow = arguments?.getString("mmHgLow")
        val beatBp = arguments?.getString("beatBp")
        val color = arguments?.getInt("color")
        val color2 = arguments?.getInt("color2")
        val color3 = arguments?.getInt("color3")
        val isalarm = arguments?.getBoolean("setalarm",false)
        val alarm = arguments?.getString("alarm")


        viewDataBinding?.pName?.text = arg
        viewDataBinding?.pMobile?.text = mobile
        viewDataBinding?.pEmail?.text = email
        val c = Calendar.getInstance().time
//        println("Current time => $c")

        val df = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate: String = df.format(c)
        viewDataBinding?.pDate?.text=formattedDate;
        if (!sp02.isNullOrEmpty()) {
            viewDataBinding?.pResult?.setText(

                    SetupHtmlView(
                        SetupView(
                            "SpO2: " + sp02.toString(),
                            Integer.toHexString(color!!)
                        ) + "  <br>" + SetupView("Pulse Rate: " + beat, Integer.toHexString(color2!!))
                    )
            )
        } else if (!GluecosedL.isNullOrEmpty()) {
            viewDataBinding?.pResult?.setText(

                    SetupHtmlView(
                        SetupView(
                            "mg/dL: " + (GluecosedL).toString(),
                            Integer.toHexString(color!!)
                        ) + "  <br>" + SetupView(
                            "mmol/L: " + (GluecosedLmmolLvalue).toString(),
                            Integer.toHexString(color2!!)
                        )
                    )


            )

        } else if (!Celcius.isNullOrEmpty()) {
            viewDataBinding?.pResult?.setText(
                SetupHtmlView(
                    SetupView(
                        (Celcius).toString() + " °C ",
                        Integer.toHexString(color!!)
                    ) + "  <br>" + SetupView(
                        (Fahrenheit).toString() + " °F ",
                        Integer.toHexString(color2!!)
                    )
                )
            )

        } else if (!mmHgHigh.isNullOrEmpty()) {
            viewDataBinding?.pResult?.setText(
                SetupHtmlView(
                    SetupView(
                        (mmHgHigh).toString() + " mmHg ",
                        Integer.toHexString(color!!)
                    ) + "  <br>" + SetupView(
                        (mmHgLow).toString() + " mmHg  ",
                        Integer.toHexString(color2!!)
                    ) + "  <br>" + SetupView(
                        (beatBp).toString() + " BPM",
                        Integer.toHexString(color3!!)
                    )
                )
            )

        }else if (isalarm == true) {
            viewDataBinding?.pResult?.setText(
                SetupHtmlView(
                    SetupView(
                        "Alarm Time: "+alarm ,
                        Integer.toHexString(color!!)
                    )
                )
            )

        } else {
            viewDataBinding?.pResult?.setText(
                ECGDataREcord
            )
        }
//        if (color != null) {
//            viewDataBinding?.pResult?.setBackgroundColor(color)
//        }

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
            builder?.setTitle("Data Sync")
            builder?.setMessage("Uploaded Successfully!!")
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