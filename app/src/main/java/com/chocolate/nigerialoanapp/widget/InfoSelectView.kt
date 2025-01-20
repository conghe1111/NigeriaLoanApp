package com.chocolate.nigerialoanapp.widget

import android.R.attr.maxLength
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R

class InfoSelectView : LinearLayout {

    private var titleStr: String? = null
    private var hintStr: String? = null

    private var tvTitle: AppCompatTextView? = null
    private var tvDesc: AppCompatTextView? = null
    private var ivBack: AppCompatImageView? = null

    constructor(context: Context) : super(context) {
        initializeAttr(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initializeAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initializeAttr(context, attrs)
    }

    private fun initializeAttr(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.edit_view)
            if (typedArray != null) {
                titleStr = typedArray.getString(R.styleable.edit_view_edit_view_title)
                hintStr = typedArray.getString(R.styleable.edit_view_edit_view_hint)
            }
        }
        val view: View =
            LayoutInflater.from(getContext()).inflate(R.layout.view_select_info, this, false)
        addView(view)
        initializeView()
    }

    private fun initializeView() {
        tvTitle = findViewById<AppCompatTextView>(R.id.tv_edit_title)
        tvDesc = findViewById<AppCompatTextView>(R.id.tv_edit_desc)
        ivBack = findViewById<AppCompatImageView>(R.id.iv_edit_back)

        tvTitle?.text = titleStr
        if (!TextUtils.isEmpty(hintStr)) {
            tvDesc?.text = hintStr
        }

    }


}