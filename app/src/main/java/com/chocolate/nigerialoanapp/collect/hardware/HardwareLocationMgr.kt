package com.chocolate.nigerialoanapp.collect.hardware

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.bean.data.DeviceData
import com.chocolate.nigerialoanapp.global.LocalConfig
import org.json.JSONObject

object HardwareLocationMgr {

    fun getData(context : Context): DeviceData.Location {
        val location = DeviceData.Location()
        location.lag = SPUtils.getInstance().getString(LocalConfig.LC_LATITUDE, "")
        location.lng = SPUtils.getInstance().getString(LocalConfig.LC_LONGITUDE, "")

        val addressStr : String? = SPUtils.getInstance().getString(LocalConfig.LC_LOCATION_ADDRESS, "")

        try {
//            {
//                "adminArea" : "Guangdong",
//                "countryCode" : "CN",
//                "countryName" : "China",
//                "extras" : {
//                "empty" : false,
//                "stability" : 0
//            },
//                "featureName" : "NO. No.1340 Shenhui Road Gaotu Nengyuan Filling Station",
//                "latitude" : 22.684919,
//                "locale" : "en",
//                "locality" : "Shenzhen",
//                "longitude" : 114.223356,
//                "maxAddressLineIndex" : 0,
//                "stability" : 0,
//                "subLocality" : "Longgang",
//                "subThoroughfare" : "No.1340",
//                "thoroughfare" : "Shenhui Road"
//            }
            var address: JSONObject? = JSONObject(addressStr)
            // // 定位的详细地址
            location.detail_address = addressStr
            // 定位的城市
            location.location_city = address?.optString("locality")
            // 定位的地理位置信息
            location.location_address = address?.optString("subLocality") +  address?.optString("thoroughfare")
        } catch (e : Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            }
        }

        // 是否打开位置模拟
        location.is_allow_mock_location = "" + isMockLocationEnabled(context)
        // 是否模拟位置
        location.is_mock = "false"
        // 模拟位置权限
        location.has_mock_apps = ""
        return location
    }

    private fun isMockLocationEnabled(context: Context): Boolean {
        try {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.ALLOW_MOCK_LOCATION
                ) == 1
            } else {
                Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.ALLOW_MOCK_LOCATION,
                    0
                ) == 1
            }
        } catch (e : Exception) {

        }
        return false
    }

}