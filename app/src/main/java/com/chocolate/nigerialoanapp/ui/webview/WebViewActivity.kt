package com.chocolate.nigerialoanapp.ui.webview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.BarUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.RouteUtils

class WebViewActivity : BaseActivity() {
    private var ivBack: ImageView? = null
    private var tvTitle: TextView? = null

    companion object {

        private const val EXTRA_URL = "extra_url"
        private const val EXTRA_TYPE = "extra_type"
        const val TYPE_PRIVACY = 111
        const val TYPE_TERMS = 112
        const val TYPE_REPAY = 113

        fun launchWebView(context: Context, url: String, type: Int) {
            var intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            intent.putExtra(EXTRA_TYPE, type)
            when(type) {
                (TYPE_PRIVACY) -> {
                    FirebaseUtils.logEvent("CLICK_PERMISSIONPAGE_PRIVACYPOLICY")
                }
                (TYPE_TERMS) -> {
                    FirebaseUtils.logEvent("CLICK_PERMISSIONPAGE_TERMSCONDITIONS")
                }
            }
            context.startActivity(intent)
        }
    }

    private var mType : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_webview)

        ivBack = findViewById<ImageView>(R.id.iv_back)
        tvTitle = findViewById<AppCompatTextView>(R.id.tv_title)
        val ivConsumer = findViewById<View>(R.id.iv_consumer)
        ivConsumer?.visibility = View.GONE

        ivBack?.setOnClickListener(View.OnClickListener {
            if (mType ==TYPE_REPAY) {
                RouteUtils.toDeeplinkIntent(this)
            }
            finish()
        })
        mType = intent.getIntExtra(EXTRA_TYPE, 0)
        var titleStr : String? = null
        if (mType == TYPE_TERMS){
            titleStr = getString(R.string.about_terms)
        } else if (mType == TYPE_PRIVACY){
            titleStr = getString(R.string.about_privacy)
        } else if (mType == TYPE_REPAY) {
            titleStr = getString(R.string.repayment)
        } else {
            titleStr = getString(R.string.about_terms)
        }
        if (!TextUtils.isEmpty(titleStr)){
            tvTitle?.text = titleStr
        }

        val url = intent.getStringExtra(EXTRA_URL)
        val webViewFragment = WebViewFragment()
        webViewFragment.setUrl(url)
        toFragment(webViewFragment)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_activity_webview_container
    }
}