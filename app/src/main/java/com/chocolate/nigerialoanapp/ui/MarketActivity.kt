package com.chocolate.nigerialoanapp.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.ui.dialog.RequestPermissionDialog
import com.chocolate.nigerialoanapp.ui.dialog.RequestPermissionDialog2
import com.chocolate.nigerialoanapp.ui.loan.Loan0NewProductFragment
import com.chocolate.nigerialoanapp.ui.login.LoginActivity
import com.chocolate.nigerialoanapp.utils.FirebaseUtils

class MarketActivity : BaseActivity() {

    companion object {
        const val KEY_ALLOW_CONTACT = "key_allow_contact"
    }
    var allowFlag : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.acticity_market)
        val frament = Loan0NewProductFragment()
        toFragment(frament)
        allowFlag = SPUtils.getInstance().getBoolean(KEY_ALLOW_CONTACT, false)
        if (!allowFlag) {
            requestPermissionInternal()
        }
    }

    private fun requestPermissionInternal() {
        val dialog: RequestPermissionDialog = RequestPermissionDialog(this, this@MarketActivity)
        dialog.setOnItemClickListener(object : RequestPermissionDialog.OnItemClickListener() {
            override fun onClickAgree() {
                SPUtils.getInstance().put(KEY_ALLOW_CONTACT, true)
//                val utils = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    PermissionUtils.permission(
////                        Manifest.permission.READ_SMS,
////                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.POST_NOTIFICATIONSm
//                    )
//                } else {
//                    PermissionUtils.permission(
//                        Manifest.permission.READ_SMS,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    )
//                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val utils = PermissionUtils.permission(Manifest.permission.POST_NOTIFICATIONS)
                    utils.callback(object : PermissionUtils.SimpleCallback {
                        override fun onGranted() {
                            FirebaseUtils.logEvent("SYSTEM_PERMISSION_RESULT")
                        }

                        override fun onDenied() {
                            ToastUtils.showShort("please allow permission.")
                        }
                    }).request()
                }
            }

            override fun onClickCancel() {
                super.onClickCancel()
                finish()
            }
        })
        dialog.show()
        FirebaseUtils.logEvent("SYSTEM_PERMISSION_ENTER")
    }

    private fun requestPermission() {
        PermissionUtils.permission(
            Manifest.permission.POST_NOTIFICATIONS
        ).request()

//        val hasPermission = PermissionUtils.isGranted(
//            //            PermissionConstants.CAMERA,
//            PermissionConstants.SMS
//        )
//        val hasPermissionCoarseLocation =
//            PermissionUtils.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
//        //        val hasPermissionCallLog = PermissionUtils.isGranted(Manifest.permission.READ_CALL_LOG)
////        val hasPermissionReadPhoneState =
//        if (hasPermissionCoarseLocation && hasPermission) {
//            executeCache()
//        } else {
//            requestPermissionInternal()
//        }
    }

    fun toLogin() {
        val welcomeIntent = Intent(this@MarketActivity, LoginActivity::class.java)
        startActivity(welcomeIntent)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_marget_container
    }
}