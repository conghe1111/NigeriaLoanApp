package com.chocolate.nigerialoanapp.ui

import android.os.Bundle
import android.util.Log
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.BaseResponseBean
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkMobilePhone()
//        sendVerifyCode()
    }


    private fun checkMobilePhone() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            // TODO
            jsonObject.put("mobile","2341234567890")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        var dataStr = jsonObject.toString()
        if (BuildConfig.DEBUG) {
            Log.i(TAG, " launcher activity ... = " + dataStr)
        }

        OkGo.post<String>(Api.CHECK_PHONE_NUMBER).tag(TAG)
            .params("data",  NetworkUtils.toBuildParams(dataStr))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    var successEnter = false
//                    val body = response.body()
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

                    } else {

                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                }
            })
    }

    private fun sendVerifyCode() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("mobile","2341234567890")
            jsonObject.put("service_type","1")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, " launcher activity 2 ... = " + jsonObject.toString())
        }
        OkGo.post<String>(Api.SEND_VERIFY_CODE).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
//                    val body = response.body()
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

                    } else {

                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                }
            })
    }
}