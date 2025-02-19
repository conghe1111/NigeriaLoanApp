package com.chocolate.nigerialoanapp.base

import android.content.Intent
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.LoginResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.global.LocalConfig
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.MainActivity
import com.chocolate.nigerialoanapp.ui.loading.ProgressDialogFragment
import com.lzy.okgo.model.Response

open class BaseFragment : Fragment() {

    var progressDialogFragment: ProgressDialogFragment? = null

    fun showProgressDialogFragment(message: String? = null, cancelable: Boolean = false) {
        var message = message
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.str_loading)
        }
        dismissProgressDialogFragment()
        progressDialogFragment = ProgressDialogFragment(cancelable, message)
        progressDialogFragment?.showAllowingStateLoss(fragmentManager, "ProgressDialogFragment")
    }


    fun dismissProgressDialogFragment() {
        if (progressDialogFragment != null) {
            progressDialogFragment?.dismissAllowingStateLoss()
        }
    }

    fun <T> checkResponseSuccess(response: Response<String>, clazz: Class<T>?): T? {
        return NetworkUtils.checkResponseSuccess(response, clazz)
    }

    fun checkResponseSuccess(response: Response<String>): String? {
        return NetworkUtils.checkResponseSuccess(response)
    }

    private var lastClickMillions: Long = 0

    protected fun checkClickFast(): Boolean {
        val delay = System.currentTimeMillis() - lastClickMillions
        if (delay < 3000) {
            ToastUtils.showShort("click too fast. please wait a monment")
            return true
        }
        lastClickMillions = System.currentTimeMillis()
        return false
    }

    protected fun checkShortClickFast(): Boolean {
        val delay = System.currentTimeMillis() - lastClickMillions
        if (delay < 500) {
            ToastUtils.showShort("click too fast. please wait a monment")
            return true
        }
        lastClickMillions = System.currentTimeMillis()
        return false
    }

    protected fun isDestroy(): Boolean {
        if (activity == null || activity?.isDestroyed == true || activity?.isFinishing == true) {
            return true
        }
        if (isRemoving || isDetached) {
            return true
        }
        return false
    }

    fun toMainPage(loginResponse: LoginResponse, password: String) {
        if (isDestroy()) {
            return
        }
        if (loginResponse == null ||
            loginResponse.account_id == null || loginResponse.access_token == null) {
            return
        }
        SPUtils.getInstance().put(LocalConfig.LC_PASSWORD, password)

        Constant.mAccountId = loginResponse.account_id.toString()
        Constant.mToken = loginResponse.access_token
        SPUtils.getInstance().put(LocalConfig.LC_ACCOUNT_ID, Constant.mAccountId!!)
        SPUtils.getInstance().put(LocalConfig.LC_ACCOUNT_TOKEN, Constant.mToken)
        val intent = Intent(this@BaseFragment.context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}