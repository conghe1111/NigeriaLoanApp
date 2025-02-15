package com.chocolate.nigerialoanapp.widget

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class NoUnderlineClickSpan : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }

    override fun onClick(widget: View) {

    }
}