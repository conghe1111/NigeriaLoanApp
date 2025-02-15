package com.chocolate.nigerialoanapp.ui.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.LoginResponse
import com.chocolate.nigerialoanapp.collect.utils.NetworkUtil
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.global.LocalConfig
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
            val loginIp = NetworkUtil.getIpAddress(context)
            val fcm_token = SPUtils.getInstance().getString(LocalConfig.LC_FCM_TOKEN)
            val source = SPUtils.getInstance().getString(LocalConfig.LC_UTMSOURCE)
            val medium = SPUtils.getInstance().getString(LocalConfig.LC_UTMMEDIUM)
            val appsflyer_id = SPUtils.getInstance().getString(LocalConfig.LC_APPSFLYER_ID)
            val google_advertising_id = SPUtils.getInstance().getString(LocalConfig.LC_GOOGLE_AD_ID)
            val campaign = SPUtils.getInstance().getString(LocalConfig.LC_CAMPAIGN)

            jsonObject.put("mobile", "234$phoneNum")
            jsonObject.put("verify_code", verfiyCode)
            jsonObject.put("password", password)
            jsonObject.put("confirm_password", password)
            jsonObject.put("appsflyer_id", appsflyer_id)
            jsonObject.put("google_advertising_id", google_advertising_id)
            jsonObject.put("campaign", campaign)
            jsonObject.put("source", source)    //广告来源（source）
            jsonObject.put("medium", medium)    //广告媒介（medium）
            jsonObject.put("login_ip", loginIp)  //登录IP
            jsonObject.put("fcm_token", fcm_token) //FCM Token

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.e("okhttp", " login register ... = ${jsonObject.toString()}")
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
                    showOrHideLoading(false)
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