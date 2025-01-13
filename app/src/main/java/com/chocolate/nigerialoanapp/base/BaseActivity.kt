package com.chocolate.nigerialoanapp.base

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.chocolate.nigerialoanapp.network.NetworkUtils
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

    @IdRes
    protected open fun getFragmentContainerRes(): Int {
        return -1
    }
}