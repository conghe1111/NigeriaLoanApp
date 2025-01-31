package com.chocolate.nigerialoanapp.bean.response

class HistoryRecordResponse {

    var order_history: List<History>? = null  //列表对象

    inner class History {
        var order_id: Int? = null  //订单ID
        var overdue_day: Int? = null  //逾期天数
        var apply_date: String? = null  //申请日期
        var loan_amount: Int? = null  //借款金额
        var repay_amount: Int? = null  //已还款金额
        var repay_date: String? = null  //还款日期
        var status: Int? = null  //订单状态 参考订单状态说明
    }

}