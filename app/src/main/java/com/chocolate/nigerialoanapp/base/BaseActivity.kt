package com.chocolate.nigerialoanapp.base

import androidx.appcompat.app.AppCompatActivity
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.lzy.okgo.model.Response

open class BaseActivity : AppCompatActivity() {

    protected fun <T> checkResponseSuccess(response: Response<String>, clazz: Class<T>): T? {
        return NetworkUtils.checkResponseSuccess(response, clazz)
    }
}