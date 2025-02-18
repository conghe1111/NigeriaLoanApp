package com.chocolate.nigerialoanapp.global

import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse
import com.chocolate.nigerialoanapp.ui.login.LoginRegisterFragment

object Constant {

    fun isAabBuild(): Boolean {
        return if (BuildConfig.DEBUG) false else BuildConfig.IS_AAB_BUILD
    }

    const val IS_COLLECT = !BuildConfig.IS_AAB_BUILD

    var mLaunchOrderInfo: OrderDetailResponse? = null

    var mAccountId: String? = null

    var mToken: String? = null
    val APP_ID: String = "oyo"
    val USSD: String = "_oyo"

    var mPhoneNum : Int? = null

    //是否是审核账号
    fun isAuditMode(): Boolean {
        if (true) {
            return true
        }
        val phoneNum = SPUtils.getInstance().getString(LoginRegisterFragment.KEY_PHONE_NUM)
        if (phoneNum.startsWith("123456789")) {
            return true
        } else {
            return false
        }
    }
}