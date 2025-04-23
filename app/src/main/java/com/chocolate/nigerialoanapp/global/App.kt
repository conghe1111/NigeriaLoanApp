package com.chocolate.nigerialoanapp.global

import android.R
import android.app.Activity
import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.Utils.OnAppStatusChangedListener
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.collect.LocationMgr
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

        if (BuildConfig.DEBUG) {
            initTest()
        } else {

        }
        //https://docs.easeid.ai/#/zh-cn/liveness-detection/liveness-detection
        val appId = "K6913783808"
        val EASE_CONFIG_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDCMmS1J6Qfu3CGzS5k0IeildTG8ZtIw1pvgQEdqoNOhCHsGT/nastXL4skM/IOmepETdWdoHqaD1qIPuTz9RK4wOpru//GgdXXyl9DN5rPuzZeyKgIBKTCH6cbS71wCb/QHGr6yrVmmu55e/fVDgtj2/KwvvdqD+u5owusmOVaS5+cfw7vxkjnxKRdZHzWpRiPJLYj7WRQH4ygy5OeY4AHOcxSiDFwK3A2Vat2dbcYLGjeoNu0GrnOUQsZ/osXz4koy8TZzvDBHWw8sD6TFG5CMATeNP+11UDy+tBXieIgXJXpLDgAbYVOVmDdDvevBiqDXULzvJ4xD+k7ct7URM9RAgMBAAECggEANCzl8eYcqonpyc4G0P9V8dDfwjPXzMzv11WJvza44LePhaejC8Idr+cOM0PhnRqtXnyrEBa12f6WDFUZnpIR3aG3WgrAxczMb5Xn0l92MKnUCdNPkhftTFrEwwiudROpfXilJYyhAFhQCkPMamnDlajao7IrN0vfwZiU4mKR/JgtBlmy77VZwDL982W2C/mlwcAbZbDoTY827EOE1Xt8dpGBmKlN1cDuOlhC1M7gnW1qO6YmZJ8eSQmtCFrto1b6zcik5aFA5bfTdbN5ctCpeJ0pbBHYyDmsHqkvpZar6v5n1YyQzCC7zqgoCfXezPQReg+reg8xxLanL8E+G0lQAQKBgQDlPY1uFjp+E5AcUI93pGWtIIS+FH/oN7WrxeVyjY/FwVKmk6Pw86jz3wwYua/2PLEskOfSx5Ps8DrrQQTmvNSvliAkagRN8mivIXjD17AYccx89rm1Zq0zZ7xsFb6eOXbdqciXe9W3YjjM9YXRyFZS+RHKOLoSro4JBP/ZhNm08QKBgQDY3Z/1nrBK7DZrWEO6JrzHCR3b1Cm0aX08sKSDkP8zFw8n7I9AJrC4MYIkKyBlm2w8hVBTUpE52TOyBUpGSQA6KG213Or95Mtm9Lu8tH+45GoDwd2eXyvxvaU8Jcmdb+CtX4sUlgRwiJy5cSlpyRosPdypydvfxz2pOIliLTtAYQKBgGjWoZu7Jr0KDVolrbc1xBpNSOf1UBnPgJIySyD8hMgYBciQtOU5Sgdd9pZINaUTVKPhEWRgSdKOeyOj9K3hphM3QTPmGFndhuqxEB3gwnQrdy2fWIcRkhx0UMPNngOWnDn7r/U3eSIJFZkGoFC0omYH6o7lKpPoqKSPWMMN8tLBAoGAI8Vary7/sTwJNeeXNEtTdBki3bG/N0z3JV2VpUXzcj/pdhJlBpXbLGauKr0aWOgsEEeFGllSLxKOnlyVBcbgzLcCca8A6+QjUNYpaVT0hFDY08nUfl0AaJ15ddRdzyQ3ESHjVP/xt4YxI71wsUxEjuudTDxJkj3RA+qZbqI7VcECgYAwvDXRK8IjxNS+GwZ/SmXD+EUgetKffGJuxSnxYoOTrHxW0PO7i74c8220fqJWvA9Llo7LQXDVt7BRNSvPAzkwc2byAnL/uW9d7o2k03yI1qZxr9k42Z3ULd9zNZ9HM0JmRcIotK3NYcEmaphTxgwFkM+yJ2ZZUaRI4pRx7PzbzQ=="
        val config = EaseConfig(appId, EASE_CONFIG_PRIVATE_KEY,
            DeviceUtils.getUniqueDeviceId(), BuildConfig.DEBUG)
        EaseID.initialize(this, config)
    }

    private fun initTest() {
        SPUtils.getInstance().put(LocalConfig.LC_APPSFLYER_ID, "test44")
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