package com.chocolate.nigerialoanapp.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.utils.SpanUtils

class InfoSelectView : LinearLayout {

    private var titleStr: String? = null
    private var hintStr: String? = null
    private var defLine: Int = 1

    private var tvTitle: AppCompatTextView? = null
    private var tvDesc: AppCompatTextView? = null
    private var ivBack: AppCompatImageView? = null

    private var openRedState : Boolean = true

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
                defLine = typedArray.getInteger(R.styleable.edit_view_edit_view_title_line, 1)
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
        val topView = findViewById<View>(R.id.view_top)

        SpanUtils.setInfoTitleString2(tvTitle, titleStr)
        if (!TextUtils.isEmpty(hintStr)) {
            tvDesc?.text = hintStr
        }
        val layoutParams = topView.layoutParams
        if (defLine == 2) {
            layoutParams.height = resources.getDimension(R.dimen.dp27).toInt()
        } else {
            layoutParams.height = resources.getDimension(R.dimen.dp11).toInt()
        }
        topView.layoutParams = layoutParams
    }

    fun setText(str : String?) {
        if (str == null) {
            return
        }
        tvDesc?.text = str
    }

    private var isRedSelectState : Boolean = false

    fun setSelectState(selectFlag : Boolean) {
        if (!openRedState) {
            return
        }
        if (selectFlag) {
            isRedSelectState = true
            tvDesc?.setTextColor(resources.getColor(R.color.color_dd0000))
            tvDesc?.setBackgroundResource(R.drawable.bg_edit_red)
        } else {
            if (isRedSelectState == false) {
                return
            }
            tvDesc?.setTextColor(resources.getColor(R.color.color_333333))
            tvDesc?.setBackgroundResource(R.drawable.bg_edit_grey)
        }
    }

    fun setOpenRedState(openRedState : Boolean) {
        this.openRedState = openRedState
    }
}