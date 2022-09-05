package com.ipath.hospitaldevice.base

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ipath.hospitaldevice.R
import com.ipath.hospitaldevice.ui.patient.PatientFragmentDirections

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel<*>> : Fragment(),
    FragmentListener {

    var viewDataBinding: T? = null
    private var rootView: View? = null
    var mViewModel: V? = null
    var isMapFragment = false

    /**
     *  Override below method for set view model instance
     * */
    abstract fun getViewModel(): V

    /**
     *  Override below method for set binding variable
     * */
    abstract fun getBindingVariable(): Int

    /**
     * Override below method for set layout id
     * */
    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun setupUI()
    abstract fun setupObserver()
    fun setupTitle(title: String) {
        Log.e("mylog", "onCreate: 2", )
        (activity as BaseActivity).mBinding?.txtTitleHome?.text=title
    }

    fun setupBackButtonEnable(isEnable: Boolean,islastscreen: Boolean) {

        if (islastscreen) {
            (activity as BaseActivity).mBinding?.imgBack?.visibility=View.VISIBLE
            (activity as BaseActivity).mBinding?.imgBack?.setOnClickListener {
                Log.e("mylog", "click back", )
                this.findNavController().navigate(R.id.mainFragment)

            }
        }else if (isEnable) {
            (activity as BaseActivity).mBinding?.imgBack?.visibility=View.VISIBLE
            (activity as BaseActivity).mBinding?.imgBack?.setOnClickListener {
                Log.e("mylog", "click back", )
                this.findNavController().popBackStack()

            }
        }else{
            (activity as BaseActivity).findViewById<ImageView>(R.id.imgBack).visibility=View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        rootView = viewDataBinding?.root
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //(requireActivity() as BaseActivity).hideNavigationIcon()
        setupObserver()
        setupUI()
        mViewModel = getViewModel()
        viewDataBinding?.setVariable(getBindingVariable(), mViewModel)
        viewDataBinding?.lifecycleOwner = this;
        viewDataBinding?.executePendingBindings()

        setupToolBar()
    }

}

interface FragmentListener {
    fun setupToolBar()
}