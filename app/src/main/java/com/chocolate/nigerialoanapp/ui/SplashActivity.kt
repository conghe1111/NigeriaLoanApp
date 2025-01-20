package com.chocolate.nigerialoanapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.BuildConfig
import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.BaseResponseBean
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse
import com.chocolate.nigerialoanapp.collect.utils.AESUtil
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.global.LocalConfig
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.login.LoginActivity
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class SplashActivity : BaseActivity() {

    companion object {
        private const val TAG = "SplashActivity"
        private const val TO_LOGIN_PAGE = 111

        private const val TO_MAIN_PAGE = 112
    }

    var mHandler: Handler? = null

    private var hasEnterFlag = false

    init {
        mHandler = Handler(
            Looper.getMainLooper()
        ) { message ->
            when (message.what) {
                TO_LOGIN_PAGE -> {
                    if (hasEnterFlag) {
                        false
                    }
                    hasEnterFlag = true
                    mHandler?.removeCallbacksAndMessages(null)
                    OkGo.getInstance().cancelTag(TAG)
                    val welcomeIntent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(welcomeIntent)
                    finish()
                }

                TO_MAIN_PAGE -> {
                    if (hasEnterFlag) {
                        false
                    }
                    hasEnterFlag = true
                    mHandler?.removeCallbacksAndMessages(null)
                    OkGo.getInstance().cancelTag(TAG)
                    val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            }
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        requestLive()
        val accountId = SPUtils.getInstance().getString(LocalConfig.LC_ACCOUNT_ID)
        val token = SPUtils.getInstance().getString(LocalConfig.LC_ACCOUNT_TOKEN)
        NetworkUtils.addCommonWithoutLogin()

        if (accountId == null || TextUtils.isEmpty(token)) {
//        if (BuildConfig.DEBUG) {
            mHandler?.sendEmptyMessageDelayed(TO_LOGIN_PAGE, 1000)
        } else {
            orderDetail(accountId, token!!)
            mHandler?.sendEmptyMessageDelayed(TO_LOGIN_PAGE, 3000)
        }
    }

    private fun orderDetail(accountId: String, token: String) {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", accountId.toString())
            jsonObject.put("access_token", token) //FCM Token
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (com.chocolate.nigerialoanapp.BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " orderDetail = " + jsonObject.toString())
        }
        OkGo.post<String>(Api.ORDER_DETAIL).tag(HomeFragment.TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    var successEnter = false
                    val orderDetail = checkResponseSuccess(response, OrderDetailResponse::class.java)
                    if (orderDetail != null) {
                        Constant.mLaunchOrderInfo = orderDetail
                        successEnter = true
                        Constant.mAccountId = accountId.toString()
                        Constant.mToken = token
                    }
                    mHandler?.sendEmptyMessageDelayed(if (successEnter) TO_MAIN_PAGE else TO_LOGIN_PAGE,100)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    mHandler?.sendEmptyMessage(TO_LOGIN_PAGE)
                }
            })
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