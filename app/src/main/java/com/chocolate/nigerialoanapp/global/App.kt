package com.chocolate.nigerialoanapp.global

import android.R
import android.app.Activity
import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils.OnAppStatusChangedListener
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.collect.LocationMgr
import com.chocolate.nigerialoanapp.collect.utils.AESUtil
import com.chocolate.nigerialoanapp.log.LogSaver
import com.chocolate.nigerialoanapp.utils.AppsflyerUtils
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.GetIdUtils
import com.chocolate.nigerialoanapp.utils.GooglePlaySdk
import com.easeid.opensdk.EaseID
import com.easeid.opensdk.config.EaseConfig
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
        AppsflyerUtils.initAppsflyer()
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
        val gaid = GetIdUtils.getGaid(this)
        if (!TextUtils.isEmpty(gaid)) {
            SPUtils.getInstance().put(LocalConfig.LC_GOOGLE_AD_ID, gaid)
        }
        SPUtils.getInstance().put(LocalConfig.LC_APPSFLYER_ID, AppsflyerUtils.AF_DEV_KEY)

        //https://docs.easeid.ai/#/zh-cn/liveness-detection/liveness-detection
        val appDecId = "LcXoMKIG9qXKgvwL02b8bw=="
        val appId = AESUtil.decrypt(appDecId)
        val EASE_CONFIG_PRIVATE_KEY_dec = "E97e74RS68lXybSkt0DKZsvKw8fJZV6S7k7hjJ1HkSrUHXILfhD/iMkMTUK+9aFE0HWWU7MHiIhY\n" +
                "MKyNjqj5P172O+UtNUBmcPLpQrh/YFscQsdSbiuCbWGVfT0mqQ9efVAhKCZZCWFhhKWxkdAFXbDN\n" +
                "O0MaCpMds17XYH29n9ekdZ1tm+j2R8qFtY7/oca7JtvGZJjN5wIxq+xCnli6S58suAfH2Zz39uCw\n" +
                "IN21BQHa4iQUa0bCWgf/DxQk1Cvb1gzXGK35RFK7w529pOJ2wNHZZSs+d5P9lZhU+yrmAqSOZSAN\n" +
                "jcn89lnl4yzB1mDzaD6Q6L+NdejSr+nk6VwDCZHmmv2myTSacaXp8E5STK+ZJ+IQJzD0U2Gqrh6O\n" +
                "+5RpaEtKQ6w0wAXqqcfEV4OKg7vc2m+lONhrF/YJ4rcKXBgRmcs6L9Kpr34/tQdhi+yZ1iC+PWGq\n" +
                "rdFcf+ZQFYddyVxh1mtRUwYfOnLV0ROkRgMcBJDT6lKDkxMVCrGHaYRrmyEvgQy80hXaBFEDOTXY\n" +
                "Z1uUSgbC5KhNcY+EHSHvAYwG4eJId/ucocFpdgyoRbWpjHrk9Ge6NmqOZ21TpcQCMt+vN4GaXK8q\n" +
                "q33gH2wVX2y4cpiH+mqeLyoKENch4XFpYGl/LBlqpg/eIERJ5cnMw1pGpcpcIndNBlBl0rhKlKIy\n" +
                "ez9pMQgLxNB712XagM3XETsDhOP2aHYE04KBYl3LkkdJph4jG5Bi7Bh9UZQhXMDjORDW9KgeTcmF\n" +
                "juOwsn8SsTtmgkxBeb7nlNLlNulsM9i2nLd4hTOyg3k7N125mBld6EGD1Gojm6ZwndixHzwSjlmh\n" +
                "Txeu3HSoV/7wbdnK/VteYQjolbOCPZQCBIiEzk1GwgMumxdHsxOlYwI3WhNk1iPnNydyTu0N2StO\n" +
                "iDP9p7gaUgUNLE/BFGJvi0WObnvSjzXUA5STWKRf6v93TsO/mFrd0kE+33/VnfhLOTp8/M59Bub3\n" +
                "cjFhGi/Ra31oI8zUDWriD7xXcnOEf3kJmvJZTrwTtkPidW1dnhbMZ+2zVHjKHmsc+MWIPCFo4vu4\n" +
                "yrQEksW2PX8WSO0F6kcxfpOfs0Kn22AFG4WUQDJ65m+49tzVrt3C7D7GHam2jFUsW5LGcGnN/sfi\n" +
                "Bjo2TrhGgugeES94rjNbObe1byMG18vWzwKuN6lV1POXPLdgY9c/srHhc08wejk3pLRTCNkxmYWq\n" +
                "37Z0tYx48L3ejcSlNOOXZnjb77Ysvd1OoBsFJ4WjlBQYgnBxl6Tx+j0+6U4iamiurr7ZzAht65fA\n" +
                "99+yyU7AQsz3UnaoaH0Z2kA01WIbMsESqDVWdOJ3WFefyNpjZf5+VRAZx9zDdbeVHmbfgP+05bx5\n" +
                "bsDtiL058FNE1VBzw+seCyeEqPIQbpwBc0YMcFDKgiiZdo9CoMzK7yBBo95q/QP4vyfdZoqyW9uh\n" +
                "+GzgVb0dbrJygnT6zYeQDRQ64Yas7kWF9DoNXrWK/jBbbkCsFaEhHaBch7BkR3ac9ZIguvypF1SM\n" +
                "LfdRkcl9+BvUjURX55C8CcPP3YpCbIAE9RLHuPdC/BNPz53pHZpLkRSLhQK4otzz+ArZ3p0J/rVD\n" +
                "dP0C0ZNvQCoO/1m3OAF8Jv9VEe88hbyRoTdI6yOLj+KzkAFtlUy6r38HXS19LuV+hLUHfWgjztUY\n" +
                "yxRUvozhPgTSDGFkw+Bk/kggRY5m5vcvmfHzG9QPG1pppMPeigoiqiXwRKuYld1FTfs43SE8ns3/\n" +
                "BwTqyNEMgT9j9ywyLVrIyItoDcWlLTQt3GBNv5QbtmIxSeKbobLfwWmset1K5v0PHOsy3roFc37U\n" +
                "ecXbLV7xI/qbkDP0I+FuhJx6oAYIBEXtKa1ZcY2QXwBkDjKTZcvvbXCyHrwzPHVdfBAnOjsFD5QQ\n" +
                "SjAo3yXuOX2KyAtvQGJt+CF33CMbtWjcIBouaLFfQQVAS75PtHXjDAS+LTs1XdGW5q4GQ/wZm1KD\n" +
                "QiHKHhn1V9zLcKxwp0lwfRVnU2AgmZ2Xvc4lXmrSF1aWSwEzGFn0HlCdHXdUVu01cs0Kzgbs8WIq\n" +
                "GMauwZUXxsj+JIRQwg+ywaIRRS6tgXwg2QoYcMTeJa6hTkNuM5ETnGjG1t8hQvweqFzyvWVN9yd+\n" +
                "j0E6Br7fVQRJ+dCNL8pGo3Yydyw2AhYZKB/xcDI2AabreufO"
        val EASE_CONFIG_PRIVATE_KEY = AESUtil.decrypt(EASE_CONFIG_PRIVATE_KEY_dec)
        if (BuildConfig.DEBUG) {
            Log.e("Test", " -----------------------------------------------------    ")
            Log.e("Test", "" + appId)
            Log.e("Test", "" + EASE_CONFIG_PRIVATE_KEY)
        }
        val config = EaseConfig(appId, EASE_CONFIG_PRIVATE_KEY,
            DeviceUtils.getUniqueDeviceId(), BuildConfig.DEBUG)
        EaseID.initialize(this, config)
    }

    private fun initTest() {
        SPUtils.getInstance().put(LocalConfig.LC_CAMPAIGN, "tsdfs6dgsa6")
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