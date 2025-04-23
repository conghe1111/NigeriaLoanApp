package com.chocolate.nigerialoanapp.api

import com.chocolate.nigerialoanapp.BuildConfig

object Api {
    private val ONLINE_HOST = "https://srv.afrokash.com"
    private val ONLINE_URL = "https://www.afrokash.com"
    private val TEST_HOST = "https://srv.accecash.com"
    private val HOST = if (BuildConfig.USE_ONLINE_API) ONLINE_HOST else TEST_HOST

    val LIVE = "$HOST/api/v1/monitor/live"
    //验证手机号码是否注册
    val CHECK_PHONE_NUMBER = "$HOST/rest/v1/check_mobile"
    //发送短信验证码
    val SEND_VERIFY_CODE = "$HOST/rest/v1/send_code"
    //验证验登录
    val VERIFY_CODE_LOGIN = "$HOST/rest/v1/code_login"
    //密码登录
    val PASSWORD_LOGIN = "$HOST/rest/v1/password_login"
    //找回密码登录
    val PASSWORD_UPDATE = "$HOST/rest/v1/password_update"
    //登出
    val LOGOUT = "$HOST/rest/v1/logout"
    //获取客户基本配置信息
    val PROFILE_CONFIG = "$HOST/rest/v1/get/base/config"
    //获取客户信息
    val PROFILE_INFO = "$HOST/rest/v1/get/profile_info"
    //上传客户基本信息
    val UPDATE_BASE = "$HOST/rest/v1/upload/base"
    //上传客户工作信息
    val UPDATE_WORK = "$HOST/rest/v1/upload/work"
    //上传联系人信息
    val UPDATE_CONTACT = "$HOST/rest/v1/upload/contact"
    //上传收款信息
    val UPDATE_RECEIVE = "$HOST/rest/v1/upload/receive"
    //上传证件相关信息
    val UPDATE_LIVE = "$HOST/rest/v1/upload/live_photo"
    //上传客户端信息
    val UPLOAD_CLIENT_INFO = "$HOST/rest/v1/upload/client"
    //上传授信信息
    val UPLOAD_AUTH_INFO = "$HOST/rest/v1/upload/auth"

    //产品列表
    val PRODUCT_LIST = "$HOST/rest/v1/prod/list"
    //产品试算
    val PRODUCT_TRIAL = "$HOST/rest/v1/prod/trial"
    //获取订单详情
    val ORDER_DETAIL = "$HOST/rest/v1/get/order_detail"
    //验证客户是否可以借贷
    val ORDER_CHECK = "$HOST/rest/v1/apply/check"
    //申请订单
    val ORDER_APPLY = "$HOST/rest/v1/apply_order"
    //还款
    val ORDER_REPAY = "$HOST/rest/v1/repay_order"
    //静态配置信息
    val STATIC_CONFIG = "$HOST/rest/v1/contact_config"

    val BANK_LIST = "$HOST/rest/v1/get/bank_list"
    //营销页配置信息
    val MARKETING_PAGE = "$HOST/rest/v1/prod/market_home"
    //订单历史记录
    val ORDER_HISTORY = "$HOST/rest/v1/order_history"
    //确认银行信息
    val BANK_INFO = "$HOST/rest/v1/get/bank_info"
    //FaceId
    val FACE_ID = "$HOST/rest/v1/get/face_id"

    val GET_POLICY: String = "$ONLINE_URL/privacy-policy.html"
    val GET_TERMS: String = "$ONLINE_URL/user-terms.html"
    val PERMISSION: String = "$ONLINE_URL/user-permission.html"

}