package com.chocolate.nigerialoanapp.global

import android.app.Application
import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.GooglePlaySdk
import com.lzy.okgo.OkGo
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.lzy.okgo.model.HttpHeaders
import okhttp3.OkHttpClient
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class App : Application() {

    companion object {
        @JvmStatic
        var instance: App? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initializeOkGo()

        LanguageUtils.applyLanguage(Locale.ENGLISH, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val s = SPUtils.getInstance().getString(LocalConfig.LC_UTMSOURCE, "")
        val s1 = SPUtils.getInstance().getString(LocalConfig.LC_UTMMEDIUM, "")
        val instanceId = SPUtils.getInstance().getString(LocalConfig.LC_FIREBASE_INSTANCE_ID, "")
        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(s1) || TextUtils.isEmpty(instanceId)) {
            GooglePlaySdk.getInstance(this)?.start()
        }
    }

    private fun initializeOkGo() {
        val builder = OkHttpClient.Builder()
        //全局的读取超时时间
        builder.readTimeout(20000, TimeUnit.MILLISECONDS)
        //全局的写入超时时间
        builder.writeTimeout(20000, TimeUnit.MILLISECONDS)
        //全局的连接超时时间
        builder.connectTimeout(20000, TimeUnit.MILLISECONDS)
        //使用数据库保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(CookieJarImpl(DBCookieStore(this)))
        val loggingInterceptor = HttpLoggingInterceptor("OkHttpClient")
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO)
        builder.addInterceptor(loggingInterceptor)
        OkGo.getInstance().init(this).okHttpClient = builder.build()
        val httpHeaders = HttpHeaders()
//        httpHeaders.put("APP-Language", "en")
//        httpHeaders.put("APP-ID", "1111")
        httpHeaders.put("Accept", "application/json")
        httpHeaders.put("User-Agent", "retrofit")
        OkGo.getInstance().addCommonHeaders(httpHeaders)
//        EncodeUtils.mainTest()
    }

}