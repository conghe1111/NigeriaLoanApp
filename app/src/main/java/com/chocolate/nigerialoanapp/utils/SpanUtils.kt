package com.chocolate.nigerialoanapp.utils

import android.app.Activity
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.ui.webview.WebViewActivity
import java.text.DecimalFormat


object SpanUtils {

    fun setPrivacyString(tv : AppCompatTextView?, activity : Activity?) {
        if (tv == null || activity == null) {
            return
        }
        if (activity?.isFinishing == true || activity?.isDestroyed == true) {
            return
        }
        val text = tv.context.resources.getString(R.string.please_read_all)
        val spannableString = SpannableString(text)
        val themeColor = tv.context.resources.getColor(R.color.theme_color)

        val startIndex = text.indexOf("<")
        val endIndex = text.indexOf(">")
        spannableString.setSpan(ForegroundColorSpan(themeColor), startIndex, endIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //点击1
        val serveClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                WebViewActivity.launchWebView(activity, Api.GET_POLICY, WebViewActivity.TYPE_PRIVACY)
            }
        }
        spannableString.setSpan(
            serveClickableSpan,
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.text = spannableString
    }

    fun setAmountString(tv : AppCompatTextView?, amount: String?) {
        if (tv == null || amount == null) {
            return
        }
        val text = "NGN$amount"
        val spannableString = SpannableString(text)
        spannableString.setSpan(RelativeSizeSpan(26f / 16), 3, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.text = spannableString
    }


    fun getShowText(amount : Long) : String {
        val decimalFormat: DecimalFormat = DecimalFormat("#,##0")
        val formattedAmount: String = decimalFormat.format(amount)
        return formattedAmount
    }

    fun getShowText1(amount : Long?) : String {
        if (amount == null) {
            return "₦0"
        }
        val decimalFormat: DecimalFormat = DecimalFormat("#,##0")
        val formattedAmount: String = decimalFormat.format(amount)
        return "₦$formattedAmount"
    }

    fun getShowText2(amount : Long?) : String {
        if (amount == null) {
            return "₦0"
        }
        val decimalFormat: DecimalFormat = DecimalFormat("#,##0.00")
        val formattedAmount: String = decimalFormat.format(amount)
        return "₦$formattedAmount"
    }
}