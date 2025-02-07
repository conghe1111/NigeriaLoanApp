package com.chocolate.nigerialoanapp.ui.login

import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.BarUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.BaseResponseBean
import com.chocolate.nigerialoanapp.collect.utils.AESUtil
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.SplashActivity
import com.chocolate.nigerialoanapp.ui.SplashActivity.Companion
import com.chocolate.nigerialoanapp.ui.dialog.ErrorStateDialog
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    var mPhoneNum: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this, true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_login)

        val loginRegisterFragment = LoginRegisterFragment()
        toFragment(loginRegisterFragment)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_container
    }

    fun toLoginFragment() {
        val registerFragment = LoginFragment()
        toFragment(registerFragment)
    }

    fun toRegisterFragment(isFirstRegister: Boolean = false) {
        val registerFragment =
            if (isFirstRegister) FirstRegisterFragment() else ForgetRegisterFragment()
        toFragment(registerFragment)
    }

    private var dialog: ErrorStateDialog? = null

    fun checkNetWork(callBack: CallBack) {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, " check NetWork ... = $jsonObject")
        }
        OkGo.post<String>(Api.LIVE).tag(TAG)
            .params("data", AESUtil.encrypt(jsonObject.toString()))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    var responseBean: BaseResponseBean? = null
                    try {
                        responseBean = com.alibaba.fastjson.JSONObject.parseObject(
                            response.body().toString(),
                            BaseResponseBean::class.java
                        )
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e
                        }
                    }
                    if (responseBean?.isRequestSuccess() == true) {
                        callBack.onSuccess()
                    } else {
                        showDialog()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    showDialog()
                }
            })

    }

    private fun showDialog() {
        val descStr = this.resources.getString(R.string.network_abnormal_please_try_again)
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        dialog = ErrorStateDialog(this, descStr, this)
        dialog?.show()
    }

    interface CallBack {
        fun onSuccess()

    }
}