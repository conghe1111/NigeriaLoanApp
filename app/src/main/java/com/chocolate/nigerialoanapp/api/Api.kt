package com.chocolate.nigerialoanapp.api

object Api {
    private val FORMAL_HOST = "https://srv.owocredit.com"
    private val TEST_HOST = "http://srvtest.owocredit.com"
    private val HOST = TEST_HOST

    val LIVE = "$HOST/app/v1/monitor/live"
    //验证手机号码是否注册
    val CHECK_PHONE_NUMBER = "$HOST/app/v1/check_phone_number"
    //发送短信验证码
    val SEND_VERIFY_CODE = "$HOST/app/v1/send_verify_code"
    //验证验登录
    val VERIFY_CODE_LOGIN = "$HOST/app/v1/verify_code_login"
    //密码登录
    val PASSWORD_LOGIN = "$HOST/app/v1/password_login"
    //找回密码登录
    val PASSWORD_UPDATE = "$HOST/app/v1/password_update"
    //登出
    val LOGOUT = "$HOST/app/v1/logout"
    //获取客户基本配置信息
    val PROFILE_CONFIG = "$HOST/app/v1/account/profile_config"
    //获取客户信息
    val PROFILE_INFO = "$HOST/app/v1/account/profile_info"
    //上传客户基本信息
    val UPDATE_BASE = "$HOST/app/v1/account/update_base"
    //上传客户工作信息
    val UPDATE_WORK = "$HOST/app/v1/account/update_work"
    //上传联系人信息
    val UPDATE_CONTACT = "$HOST/app/v1/account/update_contact"
    //上传收款信息
    val UPDATE_RECEIVE = "$HOST/app/v1/account/update_receive"
    //上传证件相关信息
    val UPDATE_LIVE = "$HOST/app/v1/account/update_live"
    //上传客户端信息
    val UPLOAD_CLIENT_INFO = "$HOST/app/v1/account/upload_client_info"
    //上传授信信息
    val UPLOAD_AUTH_INFO = "$HOST/app/v1/account/upload_auth_info"

    //检查授信信息是否有效
//    @Deprecated
    val CHECK_AUTH_INFO = "$HOST/app/v1/account/check_auth_info"

    //营销产品
    val PRODUCT_MARKETING = "$HOST/app/v1/product/marketing"
    //产品列表
    val PRODUCT_LIST = "$HOST/app/v1/product/list"
    //产品试算
    val PRODUCT_TRIAL = "$HOST/app/v1/product/trial"
    //获取订单详情
    val ORDER_DETAIL = "$HOST/app/v1/order/detail"
    //验证客户是否可以借贷
    val ORDER_CHECK = "$HOST/app/v1/order/check"
    //申请订单
    val ORDER_APPLY = "$HOST/app/v1/order/apply"
    //还款
    val ORDER_REPAY = "$HOST/app/v1/order/repay"
    //静态配置信息
    val STATIC_CONFIG = "$HOST/app/v1/static_config"

    val BANK_LIST = "$HOST/app/v1/account/bank_list"
    //营销页配置信息
    val MARKETING_PAGE = "$HOST/app/v1/product/marketing_page"
    //订单历史记录
    val ORDER_HISTORY = "$HOST/app/v1/order/history"
    //确认银行信息
    val BANK_INFO = "$HOST/app/v1/account/bank_info"
    //FaceId
    val FACE_ID = "$HOST/app/v1/account/face_id"

    // TODO
    val GET_POLICY: String = "https://www.baidu.com"

    val GET_TERMS: String = "https://www.baidu.com"

    val GET_ALL: String = "https://www.baidu.com"
}