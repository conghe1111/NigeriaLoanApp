package com.chocolate.nigerialoanapp.bean.response

class RepayResponse {
    var order_id : Long? = null
    var status : Int? = null    //0 还款失败 1 表示还款请求已被接受， 等待三方处理
    var checkout_url : String? = null

}