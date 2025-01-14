package com.chocolate.nigerialoanapp.ui.login

import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.LoginResponse
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class ForgetRegisterFragment : BaseRegisterFragment() {

    companion object {
        const val TAG = "ForgetRegisterFragment"
    }

    override fun initView(view: View) {
        super.initView(view)
        val tvTitle = view.findViewById<AppCompatTextView>(R.id.tv_register_title)
        val tvSignUp = view.findViewById<AppCompatTextView>(R.id.tv_sign_up)
        tvTitle?.text = resources.getString(R.string.reset_password)
        tvSignUp?.text = resources.getString(R.string.reset)
    }

    override fun verifyCodeLogin(verfiyCode: String, password: String) {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            var phoneNum: String = ""
            if (activity is LoginActivity) {
                (activity as LoginActivity).mPhoneNum?.let {
                    phoneNum = it.replace(" ", "")
                }
            }
            jsonObject.put("mobile", "234$phoneNum")
            jsonObject.put("verify_code", verfiyCode)
            jsonObject.put("password", password)
            jsonObject.put("confirm_password", password)
            jsonObject.put("fcm_token", "123456") //FCM Token

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkhttpClient", " verify code login = ${jsonObject.toString()}")
        }
        OkGo.post<String>(Api.PASSWORD_UPDATE).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val loginResponse = checkResponseSuccess(response, LoginResponse::class.java)
                    if (loginResponse == null) {
                        return
                    }
                    if (TextUtils.equals(loginResponse.login_status, "success")) {
                        toMainPage(loginResponse, password)
                    } else {

                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                }
            })
    }
}