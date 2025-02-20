package com.chocolate.nigerialoanapp.base

import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.BaseResponseBean
import com.chocolate.nigerialoanapp.collect.utils.AESUtil
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.dialog.ErrorStateDialog
import com.chocolate.nigerialoanapp.ui.loading.ProgressDialogFragment
import com.chocolate.nigerialoanapp.ui.login.LoginActivity.Companion.TAG
import com.chocolate.nigerialoanapp.ui.setting.ConsumerHotlineActivity
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

open class BaseActivity : AppCompatActivity() {

    protected fun <T> checkResponseSuccess(response: Response<String>, clazz: Class<T>): T? {
        return NetworkUtils.checkResponseSuccess(response, clazz)
    }

    fun toFragment(fragment: BaseFragment?) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction() // 开启一个事务
            transaction.replace(getFragmentContainerRes(), fragment)
            transaction.commitAllowingStateLoss()
        }
    }

    fun addFragment(fragment: BaseFragment?, tag : String) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction() // 开启一个事务
            transaction.add(getFragmentContainerRes(), fragment, tag)
            transaction.addToBackStack(tag)
            transaction.commitAllowingStateLoss()
        }
    }

    @IdRes
    protected open fun getFragmentContainerRes(): Int {
        return -1
    }

    protected fun initializeTitle() {
        var ivBack : AppCompatImageView? = findViewById<AppCompatImageView>(R.id.iv_back)
        var tvTitle : AppCompatTextView? =  findViewById<AppCompatTextView>(R.id.tv_title)
        var ivConsumer : AppCompatImageView? = findViewById<AppCompatImageView>(R.id.iv_consumer)

        ivBack?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                finish()
            }

        })
        ivConsumer?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                ConsumerHotlineActivity.startActivity(this@BaseActivity)
            }

        })
        tvTitle?.text = getTitleStr()
    }

   open fun getTitleStr() : String {
        return ""
    }

   private var progressDialogFragment: ProgressDialogFragment? = null

    fun showProgressDialogFragment(message: String? = null, cancelable: Boolean = false) {
        var message = message
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.str_loading)
        }
        dismissProgressDialogFragment()
        progressDialogFragment = ProgressDialogFragment(cancelable, message)
        progressDialogFragment?.showAllowingStateLoss(supportFragmentManager, "ProgressDialogFragment")
    }


    fun dismissProgressDialogFragment() {
        if (progressDialogFragment != null) {
            progressDialogFragment?.dismissAllowingStateLoss()
        }
    }

    fun checkNetWork(callBack: CallBack) {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, " check NetWork ... = $jsonObject")
        }
        OkGo.post<String>(Api.LIVE).tag(localClassName)
            .params("data", AESUtil.encrypt(jsonObject.toString()))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    var responseBean: BaseResponseBean? = null
                    try {
                        responseBean = com.alibaba.fastjson.JSONObject.parseObject(
                            response.body().toString(),
                            BaseResponseBean::class.java
                        )
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e
                        }
                    }
                    if (responseBean?.isRequestSuccess() == true) {
                        callBack.onSuccess()
                    } else {
                        callBack?.onFailure()
                        showDialog()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    callBack?.onFailure()
                    showDialog()
                }
            })

    }
    private var dialog: ErrorStateDialog? = null

    private fun showDialog() {
        val descStr = this.resources.getString(R.string.network_abnormal_please_try_again)
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        dialog = ErrorStateDialog(this, descStr, this)
        dialog?.show()
    }

    interface CallBack {
        fun onSuccess()

        fun onFailure()
    }

    override fun onDestroy() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        OkGo.getInstance().cancelTag(localClassName)
        super.onDestroy()
    }
}