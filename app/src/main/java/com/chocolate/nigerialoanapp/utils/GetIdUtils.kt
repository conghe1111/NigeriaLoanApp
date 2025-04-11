package com.chocolate.nigerialoanapp.utils

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient

object GetIdUtils {

    fun getGaid(context: Context): String {
        try {
            val advertisingId: String =
                AdvertisingIdClient.getAdvertisingIdInfo(context).id.toString()
            return advertisingId
            // 使用 advertisingId 和 limitAdTrackingEnabled
        } catch (e: Exception) {
            // 处理异常，例如 Google Play services 不可用或发生错误等。
        }
        return ""
    }

}