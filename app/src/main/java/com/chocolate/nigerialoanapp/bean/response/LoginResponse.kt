package com.chocolate.nigerialoanapp.bean.response

class LoginResponse {

    var account_id: Long? = null    //客户ID
    var access_token: String? = null   //客户AccessToken	服务端未设置失效时间
    var login_status: String? = null   //登录状态
}