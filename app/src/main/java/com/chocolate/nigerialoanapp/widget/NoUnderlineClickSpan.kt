package com.chocolate.nigerialoanapp.widget

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class NoUnderlineClickSpan(val color : Int? = null) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        if (color != null) {
            ds.setColor(color)
        }
        ds.isUnderlineText = false
    }

    override fun onClick(widget: View) {

    }
}