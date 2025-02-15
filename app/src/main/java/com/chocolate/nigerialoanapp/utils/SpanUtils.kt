package com.chocolate.nigerialoanapp.utils

import android.app.Activity
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.ui.webview.WebViewActivity
import com.chocolate.nigerialoanapp.widget.NoUnderlineClickSpan
import java.text.DecimalFormat


object SpanUtils {

    fun getSendTextSpan(activity: Activity?, countDownTime : Long) : SpannableString? {
        if (activity == null) {
            return null
        }
        val countDownStr = activity.getString(R.string.resend_sms_desc, countDownTime.toString())
        val spannableString = SpannableString(countDownStr)
        val color1 = activity.resources.getColor(R.color.send_code_color1)
        val color2 = activity.resources.getColor(R.color.send_code_color2)
        val startIndex = spannableString.indexOf("in ") + 3
        spannableString.setSpan(
            ForegroundColorSpan(color1),
            0,
            startIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(color2),
            startIndex,
            countDownStr.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    fun setPrivacyString(tv: AppCompatTextView?, activity: Activity?) {
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
        spannableString.setSpan(
            ForegroundColorSpan(themeColor),
            startIndex,
            endIndex + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        //点击1
        val serveClickableSpan = object : NoUnderlineClickSpan() {
            override fun onClick(widget: View) {
                WebViewActivity.launchWebView(
                    activity,
                    Api.GET_POLICY,
                    WebViewActivity.TYPE_PRIVACY
                )
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

    fun buildPrivacySpanString(tv: AppCompatTextView?, activity: Activity?) {
        if (tv == null || activity == null) {
            return
        }
        if (activity?.isFinishing == true || activity?.isDestroyed == true) {
            return
        }
        val agreeStr = tv.context.resources.getString(R.string.i_agree_to)
        val spannableString = SpannableString(agreeStr)
        val themeColor = tv.context.resources.getColor(R.color.theme_color)

        val startIndex1 = agreeStr.indexOf("<")
        val endIndex1 = agreeStr.indexOf(">")
        spannableString.setSpan(
            ForegroundColorSpan(themeColor),
            startIndex1,
            endIndex1 + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            RelativeSizeSpan(15f / 14),
            startIndex1,
            endIndex1 + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val startIndex2 = agreeStr.lastIndexOf("<")
        val endIndex2 = agreeStr.lastIndexOf(">")
        spannableString.setSpan(
            ForegroundColorSpan(themeColor),
            startIndex2,
            endIndex2 + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            RelativeSizeSpan(15f / 14),
            startIndex2,
            endIndex2 + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //点击1
        val serveClickableSpan1 = object : NoUnderlineClickSpan() {
            override fun onClick(widget: View) {
                WebViewActivity.launchWebView(
                    activity,
                    Api.GET_POLICY,
                    WebViewActivity.TYPE_PRIVACY
                )
            }
        }
        spannableString.setSpan(
            serveClickableSpan1,
            startIndex1,
            endIndex1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //点击2
        val serveClickableSpan2 = object : NoUnderlineClickSpan() {
            override fun onClick(widget: View) {
                WebViewActivity.launchWebView(activity, Api.GET_TERMS, WebViewActivity.TYPE_TERMS)
            }
        }
        spannableString.setSpan(
            serveClickableSpan2,
            startIndex2,
            endIndex2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
//        val imageSpan = ImageSpan(activity, if (mHasAgree) R.drawable.ic_select else R.drawable.ic_unselect)
//        spannableString.setSpan(
//            imageSpan,
//            0,
//            1,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        val imageSpanClick = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                buildPrivacySpanString(tv, activity, !hasAgree)
//            }
//        }
//        spannableString.setSpan(
//            imageSpanClick,
//            0,
//            1,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )

        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.text = spannableString
    }

    fun setAmountString(tv: AppCompatTextView?, amount: String?) {
        if (tv == null || amount == null) {
            return
        }
        val text = "NGN$amount"
        val spannableString = SpannableString(text)
        spannableString.setSpan(
            RelativeSizeSpan(26f / 16),
            3,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        tv.text = spannableString
    }


    fun getShowText(amount: Long): String {
        val decimalFormat: DecimalFormat = DecimalFormat("#,##0")
        val formattedAmount: String = decimalFormat.format(amount)
        return formattedAmount
    }

    fun getShowText1(amount: Long?): String {
        if (amount == null) {
            return "₦0"
        }
        val decimalFormat: DecimalFormat = DecimalFormat("#,##0")
        val formattedAmount: String = decimalFormat.format(amount)
        return "₦$formattedAmount"
    }

    fun getShowText2(amount: Long?): String {
        if (amount == null) {
            return "₦0"
        }
        val decimalFormat: DecimalFormat = DecimalFormat("#,##0.00")
        val formattedAmount: String = decimalFormat.format(amount)
        return "₦$formattedAmount"
    }
}