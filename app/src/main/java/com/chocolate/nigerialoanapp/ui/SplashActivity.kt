package com.chocolate.nigerialoanapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.BaseResponseBean
import com.chocolate.nigerialoanapp.collect.utils.AESUtil
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class SplashActivity : BaseActivity() {

    companion object {
      const val TAG = "SplashActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestLive()
    }

    private fun requestLive() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, " launcher activity ... = " + jsonObject.toString())
        }
        OkGo.post<String>(Api.LIVE).tag(TAG)
            .params("data",  AESUtil.encrypt(jsonObject.toString()))
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
                        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                        startActivity(intent)
                    } else {

                    }
//                    mHandler?.mHandlersendEmptyMessageDelayed(if (successEnter) TO_MAIN_PAGE else TO_WELCOME_PAGE,100)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
//                    mHandler?.sendEmptyMessage(TO_WELCOME_PAGE)
                }
            })
    }
}