package com.chocolate.nigerialoanapp.utils

import android.content.Context
import android.text.TextUtils
import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.collect.utils.MD5Util
import com.chocolate.nigerialoanapp.global.LocalConfig
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.util.UUID

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


    fun getPhoneId(): String? {
        var androidId = SPUtils.getInstance().getString(LocalConfig.LC_ANDROID_ID)
        if (TextUtils.isEmpty(androidId)) {
            androidId = MD5Util.StringInMd5(UUID.randomUUID().toString())
            SPUtils.getInstance().put(LocalConfig.LC_ANDROID_ID, androidId)
        }
        return androidId
    }
}