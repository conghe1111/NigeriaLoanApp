package com.chocolate.nigerialoanapp.global

import android.R
import android.app.Activity
import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils.OnAppStatusChangedListener
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.collect.LocationMgr
import com.chocolate.nigerialoanapp.log.LogSaver
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.GooglePlaySdk
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.lzy.okgo.OkGo
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.lzy.okgo.model.HttpHeaders
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator
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
        FirebaseApp.initializeApp(this)
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            //                layout.setPrimaryColorsId(R.color.bg_color, android.R.color.white);//全局设置主题颜色
            layout.setPrimaryColorsId(R.color.white, R.color.white) //全局设置主题颜色
            MaterialHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setDrawableSize(20f)
        }
        initializeOkGo()
        LocationMgr.getInstance().init(this)
        LanguageUtils.applyLanguage(Locale.ENGLISH, false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val s = SPUtils.getInstance().getString(LocalConfig.LC_UTMSOURCE, "")
        val s1 = SPUtils.getInstance().getString(LocalConfig.LC_UTMMEDIUM, "")
        val instanceId = SPUtils.getInstance().getString(LocalConfig.LC_FIREBASE_INSTANCE_ID, "")
        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(s1) || TextUtils.isEmpty(instanceId)) {
            GooglePlaySdk.getInstance(this)?.start()
        }
        LogSaver.init(this)
        if (!Constant.isAabBuild()) {
            CrashHandler.getInstance().init(this)
        }
        if (BuildConfig.DEBUG) {
            LogSaver.enableDebug()
        }

        AppUtils.registerAppStatusChangedListener(mListener)
        if (Constant.isAuditMode()) {
            initTest()
        } else {

        }
    }

    private fun initTest() {
        SPUtils.getInstance().put(LocalConfig.LC_FCM_TOKEN, "test111")
        SPUtils.getInstance().put(LocalConfig.LC_UTMSOURCE, "test222")
        SPUtils.getInstance().put(LocalConfig.LC_UTMMEDIUM, "test333")
        SPUtils.getInstance().put(LocalConfig.LC_APPSFLYER_ID, "test44")
        SPUtils.getInstance().put(LocalConfig.LC_GOOGLE_AD_ID, "test55")
        SPUtils.getInstance().put(LocalConfig.LC_CAMPAIGN, "test66")
    }

    private val mListener = object : OnAppStatusChangedListener {
        override fun onForeground(activity: Activity?) {
            FirebaseUtils.logEvent("SYSTEM_INIT_OPEN_${Constant.APP_ID}")
        }

        override fun onBackground(activity: Activity?) {
            FirebaseUtils.logEvent("SYSTEM_HIDE_${Constant.APP_ID}")
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

    override fun onTerminate() {
        AppUtils.unregisterAppStatusChangedListener(mListener)
        super.onTerminate()
    }
}