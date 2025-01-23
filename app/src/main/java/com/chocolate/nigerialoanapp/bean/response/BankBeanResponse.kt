package com.chocolate.nigerialoanapp.bean.response

class BankBeanResponse {

    var bank_list : List<Bank>? = null

    class Bank {
        var bank_code: String? = null
        var bank_name: String? = null
        var bank_url: String? = null
        var bg_url: String? = null


    }
}