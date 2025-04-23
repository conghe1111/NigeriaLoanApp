package com.chocolate.nigerialoanapp.utils

import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.chocolate.nigerialoanapp.global.App

object AppsflyerUtils {


    private const val AF_DEV_KEY = "yoYniZRmUgmoBEhDuzrarX"
    private const val LOG_TAG = "AppsflyerUtils"


    fun initAppsflyer() {
        Log.d(LOG_TAG, "initAppsflyer")
        if (App.instance == null) {
            return
        }
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, null, App.instance!!.applicationContext)

        AppsFlyerLib.getInstance().start(
            App.instance!!.applicationContext,
            AF_DEV_KEY,
            object : AppsFlyerRequestListener {
                override fun onSuccess() {
                    Log.d(LOG_TAG, "Launch sent successfully, got 200 response code from server")
                }

                override fun onError(i: Int, s: String) {
                    Log.d(
                        LOG_TAG, ("""
     Launch failed to be sent:
     Error code: $i
     Error description: $s
     """.trimIndent())
                    )
                }
            })
    }

    // 发送应用内事件
    // 已定义的事件类型在 AFInAppEventType 中可查看
    fun logEvent(event: String) {
        if (App.instance == null) {
            return
        }
        val eventValues: Map<String, Any> = HashMap()

        //eventValues.put(AFInAppEventParameterName.PRICE, 1234.56);
        //eventValues.put(AFInAppEventParameterName.CONTENT_ID,"1234567");
        AppsFlyerLib.getInstance().logEvent(
            App.instance!!.applicationContext,
            event,
            eventValues,
            object : AppsFlyerRequestListener {
                override fun onSuccess() {
                    Log.d(LOG_TAG, "Event sent successfully")
                }

                override fun onError(i: Int, s: String) {
                    Log.d(
                        LOG_TAG, ("""
     Event failed to be sent:
     Error code: $i
     Error description: $s
     """.trimIndent())
                    )
                }
            })
    }

}