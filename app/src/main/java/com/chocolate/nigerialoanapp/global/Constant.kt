package com.chocolate.nigerialoanapp.global

import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse

object Constant {

    fun isAabBuild(): Boolean {
        return if (BuildConfig.DEBUG) false else BuildConfig.IS_AAB_BUILD
    }

    const val IS_COLLECT = !BuildConfig.IS_AAB_BUILD

    var mLaunchOrderInfo : OrderDetailResponse? = null

    var mAccountId: Long? = null

    var mToken: String? = null
}