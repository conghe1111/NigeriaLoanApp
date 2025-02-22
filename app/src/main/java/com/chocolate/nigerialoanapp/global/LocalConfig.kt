package com.chocolate.nigerialoanapp.global

import android.content.Context
import com.chocolate.nigerialoanapp.R

object LocalConfig {

    /**
     * 是否已放款
     * 订单历史那块，在没有放款之前，逾期天数；还款日；应还总额；这三个不展示；
     */
    fun isLoanMoney(status: Int?): Boolean {
        if (status == null) {
            return false
        }
        when (status!!) {
            (1), (2), (3), (4), (5), (6) -> {
                return true
            }
        }
        return false
    }

    /**
     *      * //        1提交审核Under Review
     *      * //        2 审核拒绝  Reject Application
     *      * //                3 等待电核  Under Review
     *      * //                4等待放款 Loan in progress
     *      * //        5 放款中  Loan in progress
     *      * //        6 放款失败 Disbursement failed
     *      * //                7等待还款 Active
     *      * //        8 已结清  Finish
     *      * //        9 逾期  Overdue
     *      * //        10 还款中  Pending Repayment
     */
    fun getLoanStr(context: Context, status: Int?) : String {
        if (status == null){
            return context.resources.getString(R.string.under_review)
        }
        when (status!!) {
            (1), (3) -> {
                return context.resources.getString(R.string.under_review)
            }
            (2) -> {
                return context.resources.getString(R.string.reject_application)
            }
            (4), (5) -> {
                return context.resources.getString(R.string.loan_in_progress)
            }
            (6) -> {
                return context.resources.getString(R.string.disbursement_fail)
            }
            (7) -> {
                return context.resources.getString(R.string.active)
            }
            (8) -> {
                return context.resources.getString(R.string.finish)
            }
            (9) -> {
                return context.resources.getString(R.string.overdue)
            }
            (10) -> {
                return context.resources.getString(R.string.pending_repayment)
            }
        }
        return context.resources.getString(R.string.under_review)
    }

    const val LC_ACCOUNT_ID = "account_id"
    const val LC_ACCOUNT_TOKEN = "account_token"

    const val LC_PHONE_NUM = "lc_phone_num"
    const val LC_PASSWORD = "lc_password"



    const val LC_IMEI = "lc_imei"
    const val LC_DEVICE_ID = "device_id"
    const val LC_OS = "lc_os"
    const val LC_LONGITUDE = "lc_longitude"
    const val LC_LATITUDE = "lc_latitude"
    const val LC_UTMSOURCE = "lc_utmsource"
    const val LC_UTMMEDIUM = "lc_utmmedium"
    const val LC_APPSFLYER_ID = "lc_appsflyer_id"
    const val LC_GOOGLE_AD_ID = "google_advertising_id"
    const val LC_CAMPAIGN = "campaign"
    const val LC_FIREBASE_INSTANCE_ID: String = "firebase_instance_id" //firebase_instance_id
    const val LC_FCM_TOKEN: String = "lc_fcm_token"
    const val LC_PUSH: String = "lc_push"
}