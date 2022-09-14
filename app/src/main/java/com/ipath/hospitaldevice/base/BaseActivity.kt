package com.ipath.hospitaldevice.base

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
//import com.contec.pm10.code.connect.ContecSdk
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.ipath.hospitaldevice.R
import com.ipath.hospitaldevice.databinding.MainActivityBinding

import com.ipath.hospitaldevice.utils.UserPreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    lateinit var imagBack: ImageView
    lateinit var title: TextView
    lateinit var navController: NavController
    lateinit var bottomMenu: ConstraintLayout
    lateinit var mBinding: MainActivityBinding

    @Inject
    lateinit var userPreferenceHelper: UserPreferenceHelper
    lateinit var bottomNavigation: BottomNavigationView
    var isFirstTime = false
    private val TAG = "DeviceSearchActivity"
    private val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val REQUEST_LOCATION = 0
    private val REQUEST_ENABLE_BT = 1
    private var permissionOk = false

    //    private var sdk: ContecSdk? = null
    val BLUETOOTH_PERMISSIONS_S =
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    var permissionlistener: PermissionListener? = null
    val APP_STORAGE_ACCESS_REQUEST_CODE = 501
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("mylog", "onCreate: ")
        mBinding = DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.main_activity)
//        setContentView(R.layout.main_activity)

        navController = findNavController(R.id.my_nav_host_fragment)
        val graph = navController
            .navInflater.inflate(R.navigation.navigation)
        navController.graph = graph
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.title = ""
//        sdk = ContecSdk(applicationContext)
//        sdk?.init(false)
        //set permissions，设置权限
        //set permissions，设置权限
        permissionOk = false
        setPermissions()
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.people,R.id.stage,R.id.liveChat -> {
//                    bottomMenu.visibility = View.VISIBLE
//                    bottomNavigation.visibility = View.VISIBLE
//                }
//                else->{
//                    bottomMenu.visibility = View.GONE
//                    bottomNavigation.visibility = View.GONE
//                }
//            }
//        }
//        bottomNavigation.setupWithNavController(navController)
//        bottomNavigation.setOnNavigationItemReselectedListener {
//            when (it.itemId) {
//                R.id.liveChat -> {
//                    navController.navigate(JoinEventDirections.actionJointToOnlyPeople())
//                }

//            }
//        }
    }

    fun changeStatusBarColor(isLight: Boolean = false) {
        if (isLight) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark
            window.statusBarColor =
                ContextCompat.getColor(this, R.color.white) // set status background white
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            val decorView = window.decorView //set status background black
            decorView.systemUiVisibility =
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //set status text  light
        }
    }

    private fun setPermissions() {
        permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        try {
                            val intent =
                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            intent.addCategory("android.intent.category.DEFAULT")
                            intent.data = Uri.parse(
                                java.lang.String.format(
                                    "package:%s",
                                    applicationContext.packageName
                                )
                            )
                            startActivityForResult(
                                intent,
                                APP_STORAGE_ACCESS_REQUEST_CODE
                            )
                        } catch (e: Exception) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                            startActivityForResult(
                                intent,
                                APP_STORAGE_ACCESS_REQUEST_CODE
                            )
                        }
                    }
                }
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
//                setPermissions()
                //              Toast.makeText(DashBoard.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        }


            //申请蓝牙权限
            if (mBluetoothAdapter == null) {
                Toast.makeText(
                    this, "" +
                            "this is not support bluetooth", Toast.LENGTH_LONG
                ).show()
            } else if (!mBluetoothAdapter.isEnabled()) {

                //启动蓝牙
                val enableBtIntent = Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE
                )
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                permissionOk = true
            }

    }

    private fun hasPermission(permissionType: String): Boolean {
        return let { ContextCompat.checkSelfPermission(it, permissionType) } ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

}