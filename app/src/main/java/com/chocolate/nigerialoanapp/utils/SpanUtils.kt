package com.chocolate.nigerialoanapp.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import java.text.DecimalFormat


object SpanUtils {

    fun setPrivacyString(tv : AppCompatTextView?) {
        if (tv == null) {
            return
        }
        val text = tv.context.resources.getString(R.string.please_read_all)
        val spannableString = SpannableString(text)
        val themeColor = tv.context.resources.getColor(R.color.theme_color)

        val startIndex = text.indexOf("<")
        val endIndex = text.indexOf(">")
        spannableString.setSpan(ForegroundColorSpan(themeColor), startIndex, endIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.text = spannableString
    }


    fun getShowText(amount : Long) : String {
        val decimalFormat: DecimalFormat = DecimalFormat("#,##0.00")
        val formattedAmount: String = decimalFormat.format(amount)
        return formattedAmount
    }
}