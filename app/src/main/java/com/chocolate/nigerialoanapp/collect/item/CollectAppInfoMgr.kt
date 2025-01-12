package com.chocolate.nigerialoanapp.collect.item

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.Utils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.collect.BaseCollectDataMgr
import com.chocolate.nigerialoanapp.collect.utils.TimeUtil
import com.loan.icredit.creditng.card.collect.item.AppInfoRequest

class CollectAppInfoMgr {
    companion object {
        private const val TAG = "CollectAppInfoMgr"

        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectAppInfoMgr()
        }
    }

    init {
        getAppInfoAesStr()
    }

    var aesAppInfoStr :String? = null

    fun getAppInfoAesStr() : String{
        if (true) {
            return ""
        }
        if (aesAppInfoStr == null){
            getAppInfoAesStrInternal()
            if (BuildConfig.DEBUG) {
                Log.e("Test", "cache appinfo success = " + aesAppInfoStr?.length)
            }
        }
        return aesAppInfoStr!!
    }

    @VisibleForTesting
    fun getStringForTest() : String {
        return GsonUtils.toJson(readAllAppInfo())
    }

     fun getAppInfoAesStrInternal(){
        val originAppInfo = GsonUtils.toJson(readAllAppInfo())
        val tempAppInfo = originAppInfo
        aesAppInfoStr = if (TextUtils.isEmpty(tempAppInfo)) "" else tempAppInfo
    }

    private fun readAllAppInfo(): ArrayList<AppInfoRequest> {
        val list: HashMap<String, AppInfoRequest> = HashMap<String, AppInfoRequest>()
        val tempList: ArrayList<AppInfoRequest> = ArrayList<AppInfoRequest>()
        try {
            try {
                val appList = getLaunchAllApp()
                list.putAll(appList)
            } catch(e : Exception) {

            }
            val pm = Utils.getApp().packageManager ?: return tempList
            val installedPackages = pm.getInstalledPackages(0)
            for (i in installedPackages.indices) {
                val packageInfo = installedPackages[i]
                val appInfoRequest = AppInfoRequest()
                appInfoRequest.pkgname = BaseCollectDataMgr.encodeData(packageInfo.packageName)
                appInfoRequest.installtime = packageInfo.firstInstallTime
                appInfoRequest.timestamps = TimeUtil.dateToStamp(TimeUtil.stampToDate(packageInfo.firstInstallTime))
                appInfoRequest.installtime_utc =
                    BaseCollectDataMgr.local2UTC(packageInfo.firstInstallTime)
                val ai = packageInfo.applicationInfo
                if (ai != null) {
                    val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
                    appInfoRequest.type = if (isSystem) "0" else "1"
                    try {
                        appInfoRequest.appname =
                            BaseCollectDataMgr.encodeData1(ai.loadLabel(pm).toString())
                        appInfoRequest.versionName = packageInfo.versionName
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            appInfoRequest.versionCode = packageInfo.longVersionCode.toString()
                        } else {
                            appInfoRequest.versionCode = packageInfo.versionCode.toString()
                        }
                    } catch (e: Exception) {
                    }
                }
                list.put(packageInfo.packageName, appInfoRequest)
            }
        }catch (e : Exception ){
            if (BuildConfig.DEBUG){
                throw e
            }
        }
        tempList.addAll(list.values)
        return tempList
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getLaunchAllApp() : HashMap<String, AppInfoRequest> {
        val list: HashMap<String, AppInfoRequest> = HashMap<String, AppInfoRequest>()

        val pManager: PackageManager = Utils.getApp().getPackageManager()
        val packlist = pManager.getInstalledApplications(0)

        for (index in 0 until packlist.size) {
            val appInfo = packlist[index]
            val appInfoRequest = AppInfoRequest()
            appInfoRequest.pkgname = BaseCollectDataMgr.encodeData(appInfo.packageName)
            appInfoRequest.appname = appInfo.name
            appInfoRequest.type = if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) <= 0) "1" else "0"
            list.put(appInfo.packageName, appInfoRequest)
        }
        return list
    }
}