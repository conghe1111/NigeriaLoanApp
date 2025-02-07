package com.chocolate.nigerialoanapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse
import com.chocolate.nigerialoanapp.global.ConfigMgr
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class LoanDetailDialog(context: Context, val mProductTrial: ProductTrialResponse?): Dialog(context)   {

    private var hasSelect : Boolean = false
    private var ivSelect: ImageView? = null

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
        val tvBankName: TextView = findViewById<TextView>(R.id.tv_bank_name)
        val tvBankNum: TextView = findViewById<TextView>(R.id.tv_bank_num)
        ivSelect = findViewById<ImageView>(R.id.iv_loan_detail_select)
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
            tvBankName.text = accountReceive?.bank_name
            tvBankNum.text = accountReceive?.bank_account_num
        }

        ivSelect?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                hasSelect = !hasSelect
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
                dismiss()
            }

        })
        ivClose?.setOnClickListener(object :NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (mCallBack != null) {
                    mCallBack!!.onClickCancel()
                }
                dismiss()
            }

        })
        updateSelect()
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
}