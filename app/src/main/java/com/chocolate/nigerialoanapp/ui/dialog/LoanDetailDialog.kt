package com.chocolate.nigerialoanapp.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.BankInfoResponse
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse
import com.chocolate.nigerialoanapp.global.ConfigMgr
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoanDetailDialog(context: Context, mProductTrial: ProductTrialResponse?): Dialog(context)   {

    companion object {
        const val TAG = "LoanDetailDialog"
    }

    private var hasSelect : Boolean = false
    private var ivSelect: ImageView? = null

    private var tvBankName: TextView? = null
    private var tvBankNum: TextView? = null
    private var tvAgree: AppCompatTextView? = null

    init {
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp: WindowManager.LayoutParams? = window?.attributes
        if (lp != null) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT //设置宽度
            lp.height = WindowManager.LayoutParams.MATCH_PARENT //设置高度
            lp.horizontalMargin = 0f
            lp.verticalMargin = 0f
            window?.attributes = lp
        }
        setContentView(R.layout.dialog_loan_detail)
        val ivClose : ImageView = findViewById<ImageView>(R.id.iv_close)
        val tvDisbursalAmount: TextView = findViewById<TextView>(R.id.tv_disbursal_amount)
        val tvDuelAmount: TextView = findViewById<TextView>(R.id.tv_due_amount)
        val tvDueDay: TextView = findViewById<TextView>(R.id.tv_due_day)
        tvBankName = findViewById<TextView>(R.id.tv_bank_name)
        tvBankNum = findViewById<TextView>(R.id.tv_bank_num)
        ivSelect = findViewById<ImageView>(R.id.iv_loan_detail_select)
        tvAgree = findViewById<AppCompatTextView>(R.id.tv_agree)
        val tvConfirm: TextView = findViewById<TextView>(R.id.tv_loan_confirm)

        mProductTrial?.let {
            if (it.trials != null && it.trials.size > 0) {
                val trial = it.trials[0]
                tvDisbursalAmount.text = trial.disburse_amount.toString()
                tvDuelAmount.text = trial.total.toString()
                tvDueDay.text = trial.repay_date.toString()
            }
        }

        if (ConfigMgr.mProfileInfo != null && ConfigMgr.mProfileInfo?.account_receive != null) {
            val accountReceive = ConfigMgr.mProfileInfo!!.account_receive
            tvBankName?.text = accountReceive?.bank_name
            tvBankNum?.text = accountReceive?.bank_account_num
        }

        ivSelect?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                hasSelect = !hasSelect
                if (!hasSelect) {
                    FirebaseUtils.logEvent("CLICK_CONTRACT_CANCEL")
                }
                updateSelect()
            }

        })
        tvConfirm?.setOnClickListener(object :NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (!hasSelect) {
                    val toastStr = context.resources.getString(R.string.please_select_loan_contract)
                    ToastUtils.showShort(toastStr)
                    return
                }
                if (mCallBack != null) {
                    mCallBack!!.onClickAgree()
                }
                FirebaseUtils.logEvent("CLICK_LOAN_SUBMIT")
                dismiss()
            }

        })
        ivClose?.setOnClickListener(object :NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (mCallBack != null) {
                    mCallBack!!.onClickCancel()
                }
                FirebaseUtils.logEvent("CLICK_CONFIRM_BACK")
                dismiss()
            }

        })
        if (tvAgree != null && context is Activity) {
            SpanUtils.setLoanContactString(tvAgree!!, context)
        }
        updateSelect()
        getBankInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseUtils.logEvent("SYSTEM_CONFIRM_ENTER")
    }

    private var mCallBack: CallBack? = null
    fun setCallBack(listener: CallBack?) {
        mCallBack = listener
    }

    abstract class CallBack {
        abstract fun onClickAgree()
        fun onClickCancel() {}
    }

    private fun updateSelect() {
        if (hasSelect) {
            ivSelect?.setImageResource(R.drawable.ic_select)
        } else {
            ivSelect?.setImageResource(R.drawable.ic_unselect)
        }
    }

    private fun getBankInfo() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken) //FCM Token

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.BANK_INFO).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    var bankInfo: BankInfoResponse? = NetworkUtils.checkResponseSuccess(
                        response,
                        BankInfoResponse::class.java
                    )
                    if (bankInfo != null) {
                        tvBankName?.text = bankInfo?.bank_name.toString()
                        tvBankNum?.text = bankInfo?.bank_account_num.toString()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update contact = " + response.body())
                    }
                }
            })
    }
}