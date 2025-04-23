package com.chocolate.nigerialoanapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.global.LocalConfig
import com.chocolate.nigerialoanapp.log.LogSaver
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging

class GooglePlaySdk {
    var referrerClient: InstallReferrerClient? = null
    fun start() {
        referrerClient = InstallReferrerClient.newBuilder(mContext).build()
        referrerClient?.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Connection established.
                        var response: ReferrerDetails? = null
                        try {
                            if (referrerClient != null) {
                                response = referrerClient!!.installReferrer
                            }
                            if (response != null) {
                                val referrerUrl = response.installReferrer
                                if (!TextUtils.isEmpty(referrerUrl)) {
                                    // utmsource
                                    if (BuildConfig.DEBUG) {
                                        Log.e("Test", " url = $referrerUrl")
                                    }
                                    if (Constant.IS_COLLECT) {
                                        LogSaver.logToFile(" refer url = " + referrerUrl)
                                    }
                                    val temp = SPUtils.getInstance().getString(LocalConfig.LC_UTMSOURCE)
                                    if (TextUtils.isEmpty(temp)) {
                                        var utmSource = tryGetUtmSource(referrerUrl)
                                        if (!TextUtils.isEmpty(utmSource)) {
                                            SPUtils.getInstance().put(LocalConfig.LC_UTMSOURCE, utmSource)
                                        }
                                    }
//                                        OkGo.getInstance().addCommonHeaders(BuildRequestJsonUtils.buildUtmSource(utmSource!!))
                                    var utmMedium = tryGetUtmMedium(referrerUrl)
                                    if (TextUtils.isEmpty(utmMedium)) {
                                        utmMedium = tryGetGCLID(referrerUrl)
                                    }
                                    if (!TextUtils.isEmpty(utmMedium)) {
                                        SPUtils.getInstance()
                                            .put(LocalConfig.LC_UTMMEDIUM, utmMedium)
                                    }
                                }
                                try {
                                    initInstanceId(referrerUrl)
                                } catch (e: Exception) {

                                }
                                FirebaseUtils.logEvent("SYSTEN_INSTALL_${Constant.APP_ID}")
                            }
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        }

                        referrerClient?.endConnection()

                    }

                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                    }

                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
//                start()
            }
        })
        startFcm()
    }

    private fun startFcm() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                SPUtils.getInstance().put(LocalConfig.LC_FIREBASE_INSTANCE_ID, token)

            }
        } catch (e: java.lang.Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    //gclid=Cj0KCQiA99ybBhD9ARIsALvZavXgHa7-8tWDN5VPj2_f2GRsN8aLHbt7CUDkAvjo9EYwpeohYLqFci0aApFDEALw_wcB
    private fun tryGetGCLID(referrerUrl: String?): String? {
        if (TextUtils.isEmpty(referrerUrl) || referrerUrl?.contains("gclid") != true) {
            return null
        }
        val result = referrerUrl.replace("gclid=", "")
        return result
    }

    private fun tryGetUtmSource(referrerUrl: String?): String? {
        if (TextUtils.isEmpty(referrerUrl)) {
            return null
        }
        var refererMap = getSplitData(referrerUrl!!)
        if (refererMap != null) {
            var map = refererMap.get("utm_source")
            return map
        }
        return null
    }

    private fun tryGetUtmMedium(referrerUrl: String?): String? {
        if (TextUtils.isEmpty(referrerUrl)) {
            return null
        }
        var refererMap = getSplitData(referrerUrl!!)
        if (refererMap != null) {
            var map = refererMap.get("utm_medium")
            return map
        }
        return null
    }

    /**
     * 把格式：utm_source=testSource&utm_medium=testMedium&utm_term=testTerm&utm_content=11
     * 这种格式的数据切割成key,value的形式并put进JSONObject对象，用于上传
     *
     * @param referer
     * @return
     */
    private fun getSplitData(referer: String): MutableMap<String, String> {
        val map: MutableMap<String, String> = HashMap()
        if (referer.length > 2 && referer.contains("&")) {
            for (data in referer.split("&".toRegex()).toTypedArray()) {
                if (data.length > 2 && data.contains("=")) {
                    val split = data.split("=".toRegex()).toTypedArray()
                    for (i in split.indices) {
                        map[split[0]] = split[1]
                    }
                }
            }
        }
        return map
    }

    companion object {
        private var instance: GooglePlaySdk? = null
        var mContext: Context? = null
        fun getInstance(context: Context?): GooglePlaySdk? {
            if (instance == null) {
                instance = GooglePlaySdk()
            }
            mContext = context
            return instance
        }
    }

    @SuppressLint("MissingPermission")
    private fun initInstanceId(referrerUrl: String) {
        if (mContext == null) {
            return
        }
        FirebaseAnalytics.getInstance(mContext!!).appInstanceId.addOnCompleteListener { p0 ->
            val analyticId = p0.result
            if (!TextUtils.isEmpty(analyticId) && !TextUtils.isEmpty(referrerUrl)) {
                uploadInstall(analyticId, referrerUrl)
                if (BuildConfig.DEBUG) {
                    LogSaver.logToFile("firebase analytic id =  $analyticId")
                }
            }
        }
    }

    private fun uploadInstall(instanceId: String?, referrerUrl: String?) {
        val mediumStr = tryGetUtmMedium(referrerUrl)
        var sourceStr = "other"
        val gclid = tryGetGCLID(referrerUrl)
        if (!TextUtils.isEmpty(gclid)) {
            sourceStr = "google play"
        } else {
            if (!TextUtils.isEmpty(mediumStr)) {
                sourceStr = mediumStr!!
            } else {
                val tempStr = tryGetUtmSource(referrerUrl)
                if (!TextUtils.isEmpty(tempStr)) {
                    sourceStr = tempStr!!
                }
            }
        }
//        val request = InstallRequest()
//        request.source = sourceStr
//        request.instanceId = instanceId
//        request.channel = BuildConfig.CREDIT_CHANNEL
//        KvStorage.put(LocalConfig.LC_CHANNEL, mediumStr)
//        if (Constant.IS_COLLECT) {
//            LogSaver.logToFile("request install " + GsonUtils.toJson(request))
//        }
    }
}