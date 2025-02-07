package com.chocolate.nigerialoanapp.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.bean.response.CheckPhoneNumResponse
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.widget.BlankTextWatcher
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoginRegisterFragment : BaseFragment() {

    companion object {
        const val TAG = "LoginRegisterFragment"
        const val KEY_PHONE_NUM = "key_sign_in_phone_num"
    }

    private var tvApply : AppCompatTextView? = null
    private var tvNext : AppCompatTextView? = null
    private var etMobileNum : AppCompatEditText? = null
    private var ivClear : AppCompatImageView? = null

    private var mPhoneNum : String?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etMobileNum = view.findViewById<AppCompatEditText>(R.id.et_mobile_num)
        ivClear = view.findViewById<AppCompatImageView>(R.id.iv_signin_phonenum_clear)
        tvApply = view.findViewById<AppCompatTextView>(R.id.tv_apply)
        tvNext = view.findViewById<AppCompatTextView>(R.id.tv_login_register_next_desc)
        initializeView()
        SpanUtils.setPrivacyString(tvNext, activity)
    }

    private fun initializeView() {
        tvApply?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (activity is LoginActivity) {
                    (activity as LoginActivity).checkNetWork(object : LoginActivity.CallBack {
                        override fun onSuccess() {
                            if (isDestroy()) {
                                return
                            }
                            schedualMobilePhone()
                        }
                    })
                }
            }

        })

        var et = BlankTextWatcher(etMobileNum!!)
        etMobileNum?.addTextChangedListener(et)

        etMobileNum?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val text: String = editable.toString()
                if (!TextUtils.isEmpty(text)) {
                    ivClear?.visibility = View.VISIBLE
                    tvApply?.isEnabled = text.length >= 12
                } else {
                    ivClear?.visibility = View.GONE
                }
            }
        })

        var phoneNum = SPUtils.getInstance().getString(KEY_PHONE_NUM, "")
        if (TextUtils.isEmpty(phoneNum)) {
            phoneNum = mPhoneNum
        }
        if (etMobileNum != null && !TextUtils.isEmpty(phoneNum)) {
            etMobileNum!!.setText(phoneNum)
            tvApply?.isEnabled = true
        } else {
            tvApply?.isEnabled = false
        }
        ivClear?.setOnClickListener {
            etMobileNum?.setText("")
            etMobileNum?.setSelection(0)
        }
        updateState()
    }

    private fun schedualMobilePhone() {
        if (etMobileNum == null) {
            return
        }
        val phoneNum = etMobileNum!!.text.toString().replace(" ","")
        if (RegexUtils.isTel(phoneNum)){
            ToastUtils.showShort(resources.getString(R.string.str_login_phone_error))
            return
        }
        checkMobilePhone(phoneNum)
    }

    private fun checkMobilePhone(mobileNum : String) {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("mobile", "234$mobileNum")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        var dataStr = jsonObject.toString()
        if (BuildConfig.DEBUG) {
            Log.i(TAG, " login register ... = $dataStr")
        }

        OkGo.post<String>(Api.CHECK_PHONE_NUMBER).tag(TAG)
            .params("data",  NetworkUtils.toBuildParams(dataStr))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val response = checkResponseSuccess(response, CheckPhoneNumResponse::class.java)
                    if (response == null) {
                        ToastUtils.showShort(resources.getString(R.string.str_login_phone_error))
                        return
                    }
                    try {
                        if (response.mobile.startsWith("234")) {
                            val mobileStr = response.mobile.substring(3, response.mobile.length)
                            if (activity is LoginActivity) {
                                (activity as LoginActivity).mPhoneNum = mobileStr
                            }
                            SPUtils.getInstance().put(KEY_PHONE_NUM, mobileStr)
                        }
                        if (response.is_registered == 1) {
                            if (activity is LoginActivity) {
                                (activity as LoginActivity).toLoginFragment()
                            }
                        } else {
                            if (activity is LoginActivity) {
                                (activity as LoginActivity).toRegisterFragment(true)
                            }
                        }
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e
                        }
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    ToastUtils.showShort(resources.getString(R.string.str_login_phone_error))
                }
            })
    }

    private fun updateState(){

    }

    override fun onDestroy() {
        super.onDestroy()
        OkGo.getInstance().cancelTag(TAG)
    }
}