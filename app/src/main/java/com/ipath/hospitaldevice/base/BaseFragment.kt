package com.ipath.hospitaldevice.base

import android.os.Bundle
import android.service.autofill.OnClickAction
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import butterknife.OnClick
import com.ipath.hospitaldevice.R

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
        Log.e("mylog", "onCreate: 2")
        (activity as BaseActivity).mBinding?.txtTitleHome?.text = title
    }

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            val viewLifecycleOwnerLiveDataObserver =
                Observer<LifecycleOwner?> {
                    val viewLifecycleOwner = it ?: return@Observer

                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            viewDataBinding = null
                        }
                    })
                }

            override fun onCreate(owner: LifecycleOwner) {
                viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerLiveDataObserver)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerLiveDataObserver)
            }
        })
    }

    fun setupBackButtonEnable(isEnable: Boolean,clickableSpan: View.OnClickListener) {

        if (isEnable) {
            (activity as BaseActivity).mBinding?.imgBack?.visibility = View.VISIBLE
            (activity as BaseActivity).mBinding?.imgBack?.setOnClickListener {
                Log.e("mylog", "click back")
                clickableSpan.onClick((activity as BaseActivity).mBinding?.imgBack)
                this.findNavController().popBackStack()

            }
        } else {
            (activity as BaseActivity).findViewById<ImageView>(R.id.imgBack).visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewDataBinding?.unbind()
    }
}

interface FragmentListener {
    fun setupToolBar()
}