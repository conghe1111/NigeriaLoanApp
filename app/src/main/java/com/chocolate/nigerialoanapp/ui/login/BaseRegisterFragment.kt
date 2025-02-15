package com.chocolate.nigerialoanapp.ui.login

import android.R.attr.maxLength
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.bean.response.VerifyCodeResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.DateUtils
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

abstract class BaseRegisterFragment : BaseFragment() {

    companion object {
        private const val TAG = "RegisterFragment"
    }

    private var sendOtpNum : Int = 0


    private val mHandler = Handler(Looper.getMainLooper(), object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {

            return false
        }

    })

    private var tvUssd: AppCompatTextView? = null
    private var tvSms: AppCompatTextView? = null

    private var etGetCode: AppCompatEditText? = null
    private var etSetPwd: AppCompatEditText? = null
    private var etAgainPwd: AppCompatEditText? = null

    private var ivSetPwdMode: AppCompatImageView? = null
    private var ivAgainPwdMode: AppCompatImageView? = null
    private var tvSignUp: AppCompatTextView? = null
    private var tvUserDesc: AppCompatTextView? = null
    private var tvGetCode: AppCompatTextView? = null
    private var flLoading: FrameLayout? = null

    private var isUssd: Boolean = true

    private var setPwdIsPwdMode: Boolean = true
    private var againPwdIsPwdMode: Boolean = true

    private var isCounting: Boolean = false
    private var mTimeCount: TimeCount? = null
    private var startUssdFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvUssd = view.findViewById<AppCompatTextView>(R.id.tv_ussd)
        tvSms = view.findViewById<AppCompatTextView>(R.id.tv_sms)
        etGetCode = view.findViewById<AppCompatEditText>(R.id.et_get_code)
        etSetPwd = view.findViewById<AppCompatEditText>(R.id.et_set_pwd)
        etAgainPwd = view.findViewById<AppCompatEditText>(R.id.et_again_pwd)
        ivSetPwdMode = view.findViewById<AppCompatImageView>(R.id.iv_set_pwd_mode)
        ivAgainPwdMode = view.findViewById<AppCompatImageView>(R.id.iv_again_pwd_mode)
        tvSignUp = view.findViewById<AppCompatTextView>(R.id.tv_sign_up)
        tvUserDesc = view.findViewById<AppCompatTextView>(R.id.tv_user_desc)
        tvGetCode = view.findViewById<AppCompatTextView>(R.id.tv_get_code)
        flLoading = view.findViewById<FrameLayout>(R.id.fl_loading)

        sendOtpNum = SPUtils.getInstance().getInt(DateUtils.getDateStr())

        initView(view)
        updateUssdSms()
    }

    open fun initView(view: View) {
        tvSms?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                isUssd = false
                updateUssdSms()
            }

        })
        tvUssd?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                isUssd = true
                updateUssdSms()
            }

        })
        ivSetPwdMode?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                isUssd = true
                updateUssdSms()
            }

        })
        ivSetPwdMode?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                setPwdIsPwdMode = !setPwdIsPwdMode
                updatePwdMode(ivSetPwdMode, setPwdIsPwdMode)
                setPassWordMode(etSetPwd, setPwdIsPwdMode)
            }

        })
        ivAgainPwdMode?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                againPwdIsPwdMode = !againPwdIsPwdMode
                updatePwdMode(ivAgainPwdMode, againPwdIsPwdMode)
                setPassWordMode(etAgainPwd, againPwdIsPwdMode)
            }

        })
        tvSignUp?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                if (etSetPwd == null || etAgainPwd == null || etGetCode == null) {
                    ToastUtils.showShort(resources.getString(R.string.str_Illegal_verification_code))
                    return
                }
                val getCodeStr = etGetCode!!.text.toString()
                val setPwdStr = etSetPwd!!.text.toString()
                val againPwdStr = etAgainPwd!!.text.toString()

                if (TextUtils.isEmpty(getCodeStr) || TextUtils.isEmpty(setPwdStr)
                    || TextUtils.isEmpty(againPwdStr)
                ) {
                    ToastUtils.showShort(resources.getString(R.string.str_login_password_error))
                    return
                }
                if (!TextUtils.equals(setPwdStr, againPwdStr)) {
                    ToastUtils.showShort(resources.getString(R.string.str_login_password_error))
                    return
                }
                onSignUp()
                verifyCodeLogin(getCodeStr, setPwdStr)
            }

        })
        tvGetCode?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                if (isUssd) {
                    toSendUssdCodeActivity()
                } else {
                    if (isCounting) {

                    } else {
                        if (sendOtpNum > 2) {
                            FirebaseUtils.logEvent("SERVICE_OTP_OUT_OF_TIMES${Constant.USSD}")
                        } else {
                            SPUtils.getInstance().put(DateUtils.getDateStr(), sendOtpNum++)
                            sendVerifyCode()
                        }
                    }
                }
            }

        })
        setPassWordMode(etSetPwd, setPwdIsPwdMode)
        setPassWordMode(etAgainPwd, againPwdIsPwdMode)
        if (activity is LoginActivity) {
            val phoneNum = (activity as LoginActivity).mPhoneNum
            if (!TextUtils.isEmpty(phoneNum)) {
                tvUserDesc?.text = resources.getString(R.string.dears_234_x, phoneNum)
            }
        }
    }

    open fun onSignUp() {

    }

    private fun updateUssdSms() {
        tvUssd?.isSelected = isUssd
        tvSms?.isSelected = !isUssd
    }

    private fun updatePwdMode(iv: AppCompatImageView?, isPasswordMode: Boolean) {
        iv?.setImageResource(if (isPasswordMode) R.drawable.ic_pwd_mode else R.drawable.ic_pwd_mode_un)
    }

    fun setPassWordMode(et: AppCompatEditText?, isPasswordMode: Boolean) {
        if (et != null) {
            et!!.transformationMethod =
                if (isPasswordMode) PasswordTransformationMethod.getInstance() else HideReturnsTransformationMethod.getInstance()
            val editTextStr = et!!.text.toString()
            if (!TextUtils.isEmpty(editTextStr)) {
                et!!.setText(editTextStr)
                var endLength = Math.min(et!!.text!!.length, maxLength)
                et!!.setSelection(endLength)
            }
        }
    }

    private fun sendVerifyCode() {
        FirebaseUtils.logEvent("CLICK_SEND_OTP${Constant.USSD}")
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            var phoneNum: String = ""
            if (activity is LoginActivity) {
                (activity as LoginActivity).mPhoneNum?.let {
                    phoneNum = it
                }
            }
            jsonObject.put("mobile", "234$phoneNum")
            //1 登录注册 2 重置密码
            val logicType = if (this@BaseRegisterFragment is FirstRegisterFragment) "1" else "2"
            jsonObject.put("service_type", logicType)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.SEND_VERIFY_CODE).tag(LoginActivity.TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val verifyCode = checkResponseSuccess(response, VerifyCodeResponse::class.java)
                    if (verifyCode == null) {
                        return
                    }
                    if (verifyCode?.send_status == 1) {
                        mTimeCount = TimeCount(60 * 1000, 1000)
                        mTimeCount?.start()
                    } else {

                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                }
            })
    }

    inner class TimeCount(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(l: Long) {
            isCounting = true
            val countDownTime = l / 1000
//            Log.e("Test", " on tick  = " + l)
            SpanUtils.getSendTextSpan(activity, countDownTime)?.let {
                tvGetCode?.text = it
            }
        }

        override fun onFinish() {
            isCounting = false
            tvGetCode?.text = resources.getString(R.string.get_code)
        }
    }

    private fun toSendUssdCodeActivity() {
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        val tel = Uri.encode("*347*8#")
        intent.data = Uri.parse("tel:$tel")
        startActivity(intent)
        startUssdFlag = true
    }



    override fun onDestroy() {
        isCounting = false
        mTimeCount?.cancel()
        super.onDestroy()
    }

    abstract fun verifyCodeLogin(verfiyCode: String = "5555", password: String)

    fun showOrHideLoading(showFlag : Boolean) {
        flLoading?.visibility = if (showFlag) View.VISIBLE else View.GONE
    }
}