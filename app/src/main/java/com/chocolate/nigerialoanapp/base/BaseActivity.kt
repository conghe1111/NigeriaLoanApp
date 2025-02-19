package com.chocolate.nigerialoanapp.base

import android.text.TextUtils
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.loading.ProgressDialogFragment
import com.chocolate.nigerialoanapp.ui.setting.ConsumerHotlineActivity
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.model.Response

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
}