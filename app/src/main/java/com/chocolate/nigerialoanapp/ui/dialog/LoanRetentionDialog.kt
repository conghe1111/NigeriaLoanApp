package com.chocolate.nigerialoanapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.TextView
import com.chocolate.nigerialoanapp.R

class LoanRetentionDialog(context: Context): Dialog(context) {
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
        setContentView(R.layout.dialog_retention_loan)
        val tvConfirm : TextView = findViewById<TextView>(R.id.tv_loan_confirm)
        val tvCancel: TextView = findViewById<TextView>(R.id.tv_loan_cancel)
        tvConfirm.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClickAgree()
            }
            dismiss()
        }
        tvCancel.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClickCancel()
            }
            dismiss()
        }
    }

    private var mListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    abstract class OnItemClickListener {
        abstract fun onClickAgree()
        fun onClickCancel() {}
    }
}