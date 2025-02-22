package com.chocolate.nigerialoanapp.widget

import android.R.attr.maxLength
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class EditClearContainer : FrameLayout {

    private var etPwd : AppCompatEditText? = null
    private var ivClear : AppCompatImageView? = null
    private var ivPwdMode : AppCompatImageView? = null

    private var hintStr: String? = null

    private var isPwdMode : Boolean = false

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
                hintStr = typedArray.getString(R.styleable.edit_view_edit_view_hint)
            }
        }
        val view: View =
            LayoutInflater.from(getContext()).inflate(R.layout.view_edit_clear, this, false)
        addView(view)
        initializeView()
    }

    private fun initializeView() {
        etPwd = findViewById<AppCompatEditText>(R.id.et_pwd)
        ivClear = findViewById<AppCompatImageView>(R.id.iv_pwd_clear)
        ivPwdMode = findViewById<AppCompatImageView>(R.id.iv_pwd_mode)

        if (!TextUtils.isEmpty(hintStr)) {
            etPwd?.setHint(hintStr)
            var endLength = Math.min(etPwd!!.text!!.length, maxLength)
            etPwd!!.setSelection(endLength)
        }
        ivClear?.setOnClickListener(OnClickListener {
            if (etPwd != null) {
                etPwd!!.setText("")
            }
        })
        ivPwdMode?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                isPwdMode = !isPwdMode
                setPassWordMode(isPwdMode)
            }

        })
    }

    private val textChangeWatcher : TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            if (!TextUtils.isEmpty(s) && s!!.isNotEmpty()) {
                if (ivClear != null) {
                    ivClear!!.visibility = VISIBLE
                }
            } else {
                if (ivClear != null) {
                    ivClear!!.visibility = INVISIBLE
                }
            }
        }

    }

    fun setPassWordMode(pwd : Boolean) {
        isPwdMode = pwd
        if (etPwd != null) {
            etPwd!!.transformationMethod =
                if (isPwdMode) PasswordTransformationMethod.getInstance() else HideReturnsTransformationMethod.getInstance()
            val editTextStr = etPwd!!.text.toString()
            if (!TextUtils.isEmpty(editTextStr)) {
                etPwd!!.setText(editTextStr)
                var endLength = Math.min(etPwd!!.text!!.length, maxLength)
                etPwd!!.setSelection(endLength)
            }
        }
        ivPwdMode?.setImageResource(if (isPwdMode) R.drawable.ic_pwd_mode else R.drawable.ic_pwd_mode_un)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        etPwd?.addTextChangedListener(textChangeWatcher)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        etPwd?.removeTextChangedListener(textChangeWatcher)
    }

    private var mOnEditClearCallBack : OnEditClearCallBack? = null

    fun setOnEditClearCallBack(onEditClearCallBack: OnEditClearCallBack) {
        mOnEditClearCallBack = onEditClearCallBack
    }

    interface OnEditClearCallBack {
        fun onPwdModeChange(pwdMode : Boolean)
    }

    fun getEditText() : AppCompatEditText? {
        return etPwd
    }
}