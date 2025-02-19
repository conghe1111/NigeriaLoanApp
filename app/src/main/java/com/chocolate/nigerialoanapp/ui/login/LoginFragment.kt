package com.chocolate.nigerialoanapp.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.bean.response.LoginResponse
import com.chocolate.nigerialoanapp.global.LocalConfig
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.widget.EditClearContainer
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoginFragment : BaseFragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    private var tvUserDesc: AppCompatTextView? = null
    private var tvForgetPwd: AppCompatTextView? = null
    private var tvLogin: AppCompatTextView? = null
    private var etPwd: AppCompatEditText? = null
    private var editText: EditClearContainer? = null

    private var loginIsPwdMode: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvUserDesc = view.findViewById<AppCompatTextView>(R.id.tv_user_desc)
        tvForgetPwd = view.findViewById<AppCompatTextView>(R.id.tv_forget_pwd)
        tvLogin = view.findViewById<AppCompatTextView>(R.id.tv_login)
        etPwd = view.findViewById<AppCompatEditText>(R.id.et_pwd)
        editText = view.findViewById<EditClearContainer>(R.id.edit_clear_login)
        initView()
        FirebaseUtils.logEvent("SYSTEM_LOGIN_ENTER")
    }

    private fun initView() {
        if (activity is LoginActivity) {
            val phoneNum = (activity as LoginActivity).mPhoneNum
            if (!TextUtils.isEmpty(phoneNum)) {
                tvUserDesc?.text = resources.getString(R.string.dears_234_x, phoneNum)
            }
        }
        tvForgetPwd?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                if (activity is LoginActivity) {
                    (activity as LoginActivity).toRegisterFragment(false)
                }
            }

        })
        tvLogin?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                FirebaseUtils.logEvent("CLICK_LOGIN")
                if (activity is LoginActivity) {
                    showProgressDialogFragment()
                    (activity as LoginActivity).checkNetWork(object : LoginActivity.CallBack {
                        override fun onSuccess() {
                            if (isDestroy()) {
                                return
                            }
                            pwdLoginInternal()
                        }

                        override fun onFailure() {
                            dismissProgressDialogFragment()
                        }

                    })
                }
            }

        })

        var password = SPUtils.getInstance().getString(LocalConfig.LC_PASSWORD, "")
        if (TextUtils.isEmpty(password)) {
//            password = mPhoneNum
        }
        if (etPwd != null && !TextUtils.isEmpty(password)) {
            etPwd?.setText(password)
            etPwd?.setSelection(password.length - 1)
            tvLogin?.isEnabled = true
        } else {
            tvLogin?.isEnabled = false
        }
        etPwd?.addTextChangedListener(textChangeWatcher)

        editText?.setPassWordMode(loginIsPwdMode)
        editText?.requestFocus2()
        editText?.setOnEditClearCallBack(object : EditClearContainer.OnEditClearCallBack {
            override fun onPwdModeChange(pwdMode: Boolean) {
                loginIsPwdMode = pwdMode
            }

        })
    }

    private fun pwdLoginInternal() {
        if (etPwd == null) {
            return
        }
        val pwd = etPwd!!.text.toString()
        if (TextUtils.isEmpty(pwd)) {
            return
        }
        pwdLogin(pwd)
    }

    private val textChangeWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            if (!TextUtils.isEmpty(s) && s!!.isNotEmpty()) {
                tvLogin?.isEnabled = true

            } else {
                tvLogin?.isEnabled = false
            }
        }

    }

    private fun pwdLogin(password: String) {
        showProgressDialogFragment()
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            var phoneNum: String = ""
            if (activity is LoginActivity) {
                (activity as LoginActivity).mPhoneNum?.let {
                    phoneNum = it
                }
            }
            jsonObject.put("mobile", "234$phoneNum")
            jsonObject.put("password", password)
            val fcmToken = SPUtils.getInstance().getString(LocalConfig.LC_FCM_TOKEN)
            jsonObject.put("fcm_token", fcmToken) //FCM Token

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.PASSWORD_LOGIN).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    dismissProgressDialogFragment()
                    val loginResponse = checkResponseSuccess(response, LoginResponse::class.java)
                    if (loginResponse == null) {
                        return
                    }
                    if (TextUtils.equals(loginResponse.login_status, "success")) {
                        toMainPage(loginResponse, password)
                        FirebaseUtils.logEvent("SERVICE_LOGIN_PASSWORD_SUCCESS")
                    } else {

                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    dismissProgressDialogFragment()
                }
            })
    }

    override fun onDestroy() {
        etPwd?.removeTextChangedListener(textChangeWatcher)
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }


}