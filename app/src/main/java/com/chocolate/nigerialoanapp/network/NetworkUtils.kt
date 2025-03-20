package com.chocolate.nigerialoanapp.network

import android.text.TextUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.bean.BaseResponseBean
import com.chocolate.nigerialoanapp.collect.utils.AESUtil
import com.chocolate.nigerialoanapp.event.LoginTimeOut
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

object NetworkUtils {

    private var appVersion : String? = null
    private var versionCode : String? = null

    fun addCommonWithoutLogin() {
        if (appVersion == null) {
            appVersion = AppUtils.getAppVersionName()
        }
        if (appVersion == null) {
            appVersion = "1.0.0"
        }
        if (versionCode == null) {
            versionCode = AppUtils.getAppVersionCode().toString()
        }
        if (appVersion == null) {
            appVersion = "10000"
        }
        val header = HttpHeaders()
        header.put("AppId","2")
        header.put("AppName","Afrokash")
        header.put("AppVersion",appVersion)
        header.put("VersionCode",versionCode)
//        AppId	string	Y	客户端APPID	1
//        AppName	string	Y	客户端APP名字	APPName=owo credit 只要传owo
//        AppVersion	string	Y	客户端版本号，字符串类型	例如：1.0.1
//        VersionCode	string	Y	客户端版本号，数字类型	例如：101

        OkGo.getInstance().addCommonHeaders(header)
    }

    fun getJsonObject() : JSONObject {
//        request_time	int64	Y	请求时间，时间戳	例如：1703324938040
//        access_token	string	Y	客户Token
//                signature	string	Y	签名：服务端会验证签名是否正确，测试阶段可以不验证
        val jsonObject = JSONObject()
        jsonObject.put("request_time", System.currentTimeMillis().toString())
        jsonObject.put("access_token", "")
        jsonObject.put("signature", "")
        return jsonObject
    }

    fun <T> checkResponseSuccess(response: Response<String>, clazz: Class<T>?): T? {
        val body = checkResponseSuccess(response)
        return if (TextUtils.isEmpty(body)) {
            null
        } else com.alibaba.fastjson.JSONObject.parseObject(body, clazz)
    }

    fun checkResponseSuccess2(response: Response<String>): BaseResponseBean? {
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
        return responseBean
    }

    fun checkResponseSuccess(response: Response<String>): String? {
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
        //        BaseResponseBean responseBean = gson.fromJson(response.body().toString(), BaseResponseBean.class);
        if (responseBean == null) {
            ToastUtils.showShort("request failure.")
            return null
        }
        if (responseBean.isLogout()){
            EventBus.getDefault().post(LoginTimeOut())
            return null
        }
        if (!responseBean.isRequestSuccess()) {
            ToastUtils.showShort(responseBean.getMsg())
            return null
        }
        if (responseBean.getDataBody() == null) {
            ToastUtils.showShort("request failure 2.")
            return null
        }
        return com.alibaba.fastjson.JSONObject.toJSONString(responseBean.getDataBody())
    }

    fun toBuildParams(jsonObject : JSONObject) : String {
       return toBuildParams(jsonObject.toString())
    }

    fun toBuildParams(content : String) : String {
//        if (BuildConfig.DEBUG) {
//            Log.e("okhttp", content)
//            Log.e("okhttp", "2  = " + AESUtil.encrypt(content))
//        }
        return if (!BuildConfig.USE_ONLINE_API) {
            content
        } else {
            AESUtil.encrypt(content)
        }
    }

    fun clearHeaderToken(): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.put("token", "")
        return httpHeaders
    }
}