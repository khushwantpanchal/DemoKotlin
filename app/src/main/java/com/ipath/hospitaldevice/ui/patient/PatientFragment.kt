package com.ipath.hospitaldevice.ui.patient

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns.EMAIL_ADDRESS
import android.util.Patterns.PHONE
import androidx.core.os.bundleOf
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
//import com.contec.pm10.code.connect.ContecSdk
import com.ipath.hospitaldevice.R
import com.ipath.hospitaldevice.base.BaseFragment
import com.ipath.hospitaldevice.databinding.PatientDetailsFragmentBinding

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class PatientFragment : BaseFragment<PatientDetailsFragmentBinding, PatientVM>(), MainNavigator, CoroutineScope {
//    private var sdk: ContecSdk? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()
    private val patientVM: PatientVM by viewModels()

    override fun getViewModel(): PatientVM {
        patientVM.setNavigator(this)
        return patientVM
    }

    override fun setupToolBar() {
    }

    override fun setupUI() {
//        sdk = ContecSdk(context)
//        sdk!!.init(false)
        setupTitle(getString(R.string.patient_details))
        setupBackButtonEnable(false)
    }

    override fun setupObserver() {
    }


    override fun getBindingVariable(): Int {
        return BR.patientVM
    }

    override fun getLayoutId(): Int {
        return R.layout.patient_details_fragment
    }

    override fun onClicked() {
        if (checkValidation()) {
            val bundle = Bundle()
            bundle.putString("pname", viewDataBinding?.txtName?.text.toString())
            bundle.putString("email", viewDataBinding?.txtEmail?.text.toString())
            bundle.putString("mobile", viewDataBinding?.txtMob?.text.toString())
            findNavController().navigate(R.id.action_mainFragment_to_patientFragment,bundle);
        }
    }

    fun checkValidation(): Boolean {
        var isvalid = true;

        if (TextUtils.isEmpty(viewDataBinding?.txtName?.text.toString())) {

            isvalid = false
            viewDataBinding?.txtName?.error = "Please Enter Name."
        }
        if (!EMAIL_ADDRESS.matcher(viewDataBinding?.txtEmail?.text.toString()).matches()) {
//            viewDataBinding?.txtName?.error = "";
            isvalid = false
            viewDataBinding?.txtEmail?.error = "Please Enter E-mail address."
        }
        if (!PHONE.matcher(viewDataBinding?.txtMob?.text.toString()).matches()&& viewDataBinding?.txtMob?.text.toString().length<10) {
//            viewDataBinding?.txtName?.error
//            viewDataBinding?.txtEmail?.error = "";
            isvalid = false
            viewDataBinding?.txtMob?.error = "Please Enter Phone Number."
        }

        return true;
    }


}