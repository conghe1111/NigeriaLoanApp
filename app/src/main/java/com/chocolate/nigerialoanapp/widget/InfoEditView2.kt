package com.chocolate.nigerialoanapp.widget

import android.R.attr.maxLength
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.utils.SpanUtils

class InfoEditView2 : FrameLayout {

    private var titleStr: String? = null
    private var hintStr: String? = null
    private var optionalFlag: Boolean = false

    private var tvTitle: AppCompatTextView? = null
    private var etDesc: EmailAutoCompleteTextView? = null
    private var ivClear: AppCompatImageView? = null

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
                optionalFlag = typedArray.getBoolean(R.styleable.edit_view_edit_view_optional, false)
            }
        }
        val view: View =
            LayoutInflater.from(getContext()).inflate(R.layout.view_edit_info_2, this, false)
        addView(view)
        initializeView()
    }

    private fun initializeView() {
        tvTitle = findViewById<AppCompatTextView>(R.id.tv_edit_title)
        etDesc = findViewById<EmailAutoCompleteTextView>(R.id.et_edit_desc)
        ivClear = findViewById<AppCompatImageView>(R.id.iv_edit_clear)
        etDesc?.addTextChangedListener(mTextWatcher)

        if (!TextUtils.isEmpty(hintStr)) {
            etDesc?.setHint(hintStr)
            var endLength = Math.min(etDesc!!.text!!.length, maxLength)
            etDesc!!.setSelection(endLength)
        }
        ivClear?.setOnClickListener(OnClickListener {
            if (etDesc != null) {
                etDesc!!.setText("")
            }
        })
        if (optionalFlag) {
            SpanUtils.setInfoTitleString(tvTitle,titleStr)
        } else {
            SpanUtils.setInfoTitleString2(tvTitle,titleStr)
        }
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!TextUtils.isEmpty(s) && s.length > 0) {
                if (ivClear != null) {
                    ivClear!!.visibility = VISIBLE
                }
            } else {
                if (ivClear != null) {
                    ivClear!!.visibility = INVISIBLE
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (openRedState && isRedSelectState) {
                isRedSelectState = false
                etDesc?.setHintTextColor(resources.getColor(R.color.color_c7c7c7))
                etDesc?.setBackgroundResource(R.drawable.bg_edit_grey)
//                tv?.setBackgroundResource(R.color.white)
            }
            mListener?.onTextChange()
        }
    }

    private var isRedSelectState : Boolean = false

    fun setText(str : String) {
        etDesc?.setText(str)
    }

    fun setSelectState() {
        isRedSelectState = true
        etDesc?.setHintTextColor(resources.getColor(R.color.color_dd0000))
        etDesc?.setBackgroundResource(R.drawable.bg_edit_red)
//        llContainer?.setBackgroundResource(android.R.color.transparent)
    }

    fun getText() : String {
        if (etDesc == null || etDesc?.text == null) {
            return ""
        }
        return etDesc!!.text.toString()
    }

    fun setEditTextAndSelection(editTextStr: String) {
        post {
            if (etDesc != null && !TextUtils.isEmpty(editTextStr)) {
                etDesc!!.setText(editTextStr)
                etDesc!!.setSelection(etDesc!!.text!!.length)
            }
        }
    }

    fun setOpenRedState(openRedStateWhenEdit : Boolean) {
        this.openRedState = openRedStateWhenEdit
    }

    private var mListener : TextChangeListener? = null
    fun setOnTextChangeListener(listener: TextChangeListener) {
        mListener = listener
    }

    interface TextChangeListener {
        fun onTextChange()
    }

    fun getEditText() : EditText? {
        return etDesc
    }

    fun setInputNum() {
        if (etDesc != null) {
            etDesc!!.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }
}