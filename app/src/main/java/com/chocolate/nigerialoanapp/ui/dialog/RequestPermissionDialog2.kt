package com.chocolate.nigerialoanapp.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class RequestPermissionDialog2 constructor(context: Context, activity: Activity?): Dialog(context) {

   private var ivSelect: AppCompatImageView? = null
   private var webView: WebView? = null

    val URL = "https://www.baidu.com"

    init {
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp: WindowManager.LayoutParams? = window?.attributes
        if (lp != null) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT //设置宽度
            lp.height = WindowManager.LayoutParams.MATCH_PARENT //设置高度
            lp.horizontalMargin = 0f
            lp.verticalMargin = 0f
            window?.attributes = lp
        }
        setContentView(R.layout.dialog_request_permission_2)
        webView = findViewById<WebView>(R.id.web_view_permission)
        val tvCancel: TextView = findViewById<TextView>(R.id.tv_request_permission_cancel)
        val tvAgree: TextView = findViewById<TextView>(R.id.tv_request_permission_agree)
        val tvPolicyTerm: AppCompatTextView = findViewById<AppCompatTextView>(R.id.tv_request_permission_policy_term)
        ivSelect = findViewById<AppCompatImageView>(R.id.iv_select_state)

        ivSelect?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                mHasAgree = !mHasAgree
                updateState()
            }

        })
        SpanUtils.buildPrivacySpanString(tvPolicyTerm, activity)
        tvAgree.setOnClickListener {
            if (!mHasAgree) {
                val toastStr = context.resources.getString(R.string.please_select_loan_contract)
                ToastUtils.showShort(toastStr)
                return@setOnClickListener
            }
            if (mListener != null) {
                mListener!!.onClickAgree()
            }
            FirebaseUtils.logEvent("CLICK_PERMISSION_AGREE")
            dismiss()
        }
        tvCancel.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClickCancel()
            }
            FirebaseUtils.logEvent("CLICK_PERMISSION_REJECT")
            dismiss()
        }
        updateState()
        initializeData()
        webView?.loadUrl(Api.PERMISSION)
    }

    private var mListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    abstract class OnItemClickListener {
        abstract fun onClickAgree()
       open fun onClickCancel() {}
    }

    private fun updateState() {
        ivSelect?.setImageResource(if (mHasAgree) R.drawable.ic_select else R.drawable.ic_unselect)
    }

    private var mHasAgree : Boolean = false

    private fun initializeData() {
        webView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress > 100 ) {
                    return
                }
                if (newProgress == 100) {
//                    if (pbLoading?.getVisibility() == View.VISIBLE) {
//                        pbLoading?.setVisibility(View.GONE)
//                    }
                } else {
//                    if (pbLoading?.getVisibility() != View.VISIBLE) {
//                        pbLoading?.setVisibility(View.VISIBLE)
//                    }
//                    pbLoading?.setProgress(newProgress)
                }
                //                Log.e("Test", " on progress changed = " + newProgress);
            }
        }
        webView!!.webViewClient = webViewClient
        webView!!.settings.javaScriptEnabled = true
        if (!TextUtils.isEmpty(URL)) {
            webView!!.loadUrl(URL)
            //            Log.e("Test", " url 1 = " + mUrl);
        }

    }

    private val webViewClient: WebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }
    }
}