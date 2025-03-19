package com.chocolate.nigerialoanapp.widget

import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.text.InputFilter.LengthFilter
import android.widget.EditText

class LengthTextWatcher : TextWatcher {
    private var mEditText: EditText? = null

    private val filters = arrayOfNulls<InputFilter>(1)

    val length13 = LengthFilter(13)
    val length10 = LengthFilter(10)
    val length11 = LengthFilter(11)


    constructor(editText: EditText?) {
        mEditText = editText

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


    }

    override fun afterTextChanged(s: Editable?) {
        try {
            onTextChangedInternal(s)
        } catch (e: Exception){

        }
    }

    private fun onTextChangedInternal(s: CharSequence?){
        if (!TextUtils.isEmpty(s)) {
            if (s!!.trim().startsWith("0")) {
                filters[0] = length11
            } else if (s!!.trim().startsWith("234")) {
                filters[0] = length13
            } else {
                filters[0] = length10
            }
        }
        mEditText?.filters = filters
    }

}