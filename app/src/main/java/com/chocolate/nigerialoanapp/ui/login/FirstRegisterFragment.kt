package com.chocolate.nigerialoanapp.ui.login

import android.os.Bundle
import android.text.TextUtils
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.LoginResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class FirstRegisterFragment : BaseRegisterFragment() {

    companion object {
        const val TAG = "FirstRegisterFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseUtils.logEvent("SYSTEM_REGISTER_ENTER_USSD")
    }

    override fun verifyCodeLogin(verfiyCode: String, password: String) {
        showOrHideLoading(true)
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
            jsonObject.put("appsflyer_id", "")
            jsonObject.put("google_advertising_id", "")
            jsonObject.put("campaign", "")
            jsonObject.put("source", "")    //广告来源（source）
            jsonObject.put("medium", "")    //广告媒介（medium）
            jsonObject.put("login_ip", "")  //登录IP
            jsonObject.put("fcm_token", "") //FCM Token

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.VERIFY_CODE_LOGIN).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    showOrHideLoading(false)
                    val loginResponse = checkResponseSuccess(response, LoginResponse::class.java)
                    if (loginResponse == null) {
                        return
                    }
                    if (TextUtils.equals(loginResponse.login_status, "success")) {
                        FirebaseUtils.logEvent("SERVICE_LOGIN_REGISTER${Constant.USSD}")
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

    override fun onSignUp() {
        super.onSignUp()
        FirebaseUtils.logEvent("CLICK_REGISTER${Constant.USSD}")
    }
}