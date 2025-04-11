package com.chocolate.nigerialoanapp.collect

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ThreadUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.data.DeviceData
import com.chocolate.nigerialoanapp.bean.response.UploadHardwareResponse
import com.chocolate.nigerialoanapp.collect.hardware.HardwareBuildMgr
import com.chocolate.nigerialoanapp.collect.hardware.HardwareHardwareMgr
import com.chocolate.nigerialoanapp.collect.hardware.HardwareLocationMgr
import com.chocolate.nigerialoanapp.collect.hardware.HardwareNetWorkMgr
import com.chocolate.nigerialoanapp.collect.hardware.HardwareSystemMgr
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.log.LogSaver
import com.chocolate.nigerialoanapp.utils.GetIdUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.util.*


class CollectHardwareMgr {

    companion object {
        private const val TAG = "CollectHardwareMgr"
        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectHardwareMgr()
        }
    }

    fun collectHardware(activity: Activity?, observer: Observer?) {
        var startMillions = System.currentTimeMillis()
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<Exception?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Exception? {
                try {
                    val duration = (System.currentTimeMillis() - startMillions)
                    logFile(" start collect hardware start = " + duration)

                    val deviceData = buildDeviceData(activity)
                    getHardware(deviceData, observer)
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        throw e
                    }
                    return e
                }
                return null
            }


            override fun onSuccess(result: Exception?) {
                if (result != null) {
                    observer?.failure("collect data exception = " + result.toString())
                }
            }

        })
    }

    private fun buildDeviceData(context: Activity?): DeviceData {
        val deviceData = DeviceData()
        if (context == null) {
            return deviceData
        }
        deviceData.idInfo = DeviceData.IdInfo()
        deviceData.idInfo?.android_id = DeviceUtils.getAndroidID()
        // 谷歌服务框架id
        deviceData.idInfo?.gsf_id = ""
        // google Ad ID
        deviceData.idInfo?.gaid = GetIdUtils.getGaid(context)

        deviceData.locationInfo = HardwareLocationMgr.getData(context)
        deviceData.networkInfo = HardwareNetWorkMgr.getNetWork(context)
        deviceData.buildInfo = HardwareBuildMgr.getData(context)
        deviceData.hardInfo = HardwareHardwareMgr.getData(context)
        deviceData.systemInfo = HardwareSystemMgr.getData(context)

        deviceData.batteryInfo = DeviceData.Battery()
        deviceData.batteryInfo?.battery = "-1"
        deviceData.batteryInfo?.is_charging = isCharging(context).toString()

        deviceData.displayInfo = DeviceData.Display()
        val dm = DisplayMetrics()//屏幕度量
        context?.windowManager?.defaultDisplay?.getMetrics(dm)
        val wm: WindowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val screen_width = display.width
        val screen_height = display.height
        val physicalWidth = screen_width / dm.xdpi
        val physicalHeight = screen_height / dm.ydpi
        val physicalSize = Math.sqrt(
            Math.pow(physicalWidth.toDouble(), 2.0) + Math.pow(
                physicalHeight.toDouble(), 2.0
            )
        )
        deviceData.displayInfo?.resolution = "$screen_width/$screen_height"
        deviceData.displayInfo?.resolution_width = screen_width.toString()
        deviceData.displayInfo?.resolution_height = screen_height.toString()

        deviceData.appInfo = DeviceData.App()
        deviceData.appInfo?.app_version = ""
        deviceData.appInfo?.number_of_applications = getAppListNum(context).toString()

        return deviceData
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun getAppListNum(context: Context) : Int {
        var totalNum = 0
        try {
            val pm: PackageManager = context.getPackageManager()
            val apps: List<ApplicationInfo> =
                pm.getInstalledApplications(0) // 0 表示默认选项，通常包括用户安装的应用和系统应用
            for (app in apps) {
                if ((app.flags and ApplicationInfo.FLAG_SYSTEM) == 0) { // 检查是否为系统应用
                    totalNum++
//                    val appName: String = pm.getApplicationLabel(app).toString()
//                    val packageName = app.packageName
                    // 这里可以获取到非系统应用的名称和包名
                }
            }
        } catch (e : Exception) {

        }
        return totalNum
    }

    fun isCharging(context: Context): Boolean {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val chargePlug =
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        return chargePlug > 0 // 非零值表示正在充电
    }

    @SuppressLint("MissingPermission")
    private fun getHardware(deviceData: DeviceData, observer: Observer?) {
        logFile(" start upload hardware .")
        val startMillions = System.currentTimeMillis()
        val jsonObject = JSONObject()
        val deviceDataStr = JSON.toJSONString(deviceData)
        jsonObject.put("account_id", Constant.mAccountId)
        jsonObject.put("access_token", Constant.mToken)
        jsonObject.put("client_json", deviceDataStr.toString())
        jsonObject.put("request_time", System.currentTimeMillis().toString())
        jsonObject.put("signature", "")
        OkGo.post<String>(Api.UPLOAD_CLIENT_INFO).tag(TAG)
            .params(
                "data",
                com.chocolate.nigerialoanapp.network.NetworkUtils.toBuildParams(jsonObject)
            )
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                        Log.i(TAG, " response success= " + response.body());
                    var authBean: UploadHardwareResponse? =
                        com.chocolate.nigerialoanapp.network.NetworkUtils.checkResponseSuccess(
                            response,
                            UploadHardwareResponse::class.java
                        )
                    val totalDur = (System.currentTimeMillis() - startMillions)
                    logFile(" start upload auth success =  $totalDur")
                    if (authBean != null && TextUtils.equals(authBean?.client_upload, "1")) {
                        observer?.success("")
                        if (Constant.IS_COLLECT) {
                            logFile(deviceDataStr)
                        }
                    } else {
                        var errorMsg: String? = null
                        try {
                            errorMsg = response.body().toString()
                        } catch (e: Exception) {

                        }
                        observer?.failure(errorMsg)
                        logFile("start upload auth failure 2 =  " + (System.currentTimeMillis() - startMillions) + errorMsg)
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    var errorMsg: String? = null
                    try {
                        errorMsg = response.body().toString()
                    } catch (e: Exception) {

                    }
                    observer?.failure(errorMsg)
                    logFile("start upload auth failure =  " + (System.currentTimeMillis() - startMillions) + errorMsg)
                }
            })
    }

    interface Observer {
        fun success(response: String)
        fun failure(response: String?)
    }

    fun logFile(str: String?) {
        if (TextUtils.isEmpty(str)) {
            return
        }
        if (BuildConfig.DEBUG) {
            Log.e("Test", str!!)
        }
        LogSaver.logToFile("Collect hareware  $str")
    }


}