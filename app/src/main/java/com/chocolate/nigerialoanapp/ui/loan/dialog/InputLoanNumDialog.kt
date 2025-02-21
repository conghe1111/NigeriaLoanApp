package com.chocolate.nigerialoanapp.ui.loan.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class InputLoanNumDialog(
    context: Context,
    activity: Activity?,
    val maxV: Int,
    val observer: Observer
) : Dialog(context) {

    private val mHandler: Handler = Handler(Looper.getMainLooper())

    init {
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.let {
            BarUtils.setStatusBarLightMode(it, false)
        }
        val lp: WindowManager.LayoutParams? = window?.attributes
        if (lp != null) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT //设置宽度
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT //设置高度
            lp.horizontalMargin = 0f
            lp.verticalMargin = 0f
            lp.gravity = Gravity.BOTTOM //dialog从底部弹出
        }
        setContentView(R.layout.dialog_input_loan_num)
        val tvConfirm = findViewById<AppCompatTextView>(R.id.tv_input_confirm)
        val flContainer = findViewById<View>(R.id.fl_container)
        val etInputNum = findViewById<AppCompatEditText>(R.id.et_input_num)

        flContainer?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                    return
                }
                dismiss()
                KeyboardUtils.hideSoftInput(etInputNum)
            }

        })
        tvConfirm?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                    return
                }
                try {
                    val numStr = etInputNum.text.toString()
                    if (TextUtils.isEmpty(numStr)) {
                        KeyboardUtils.hideSoftInput(etInputNum)
                        dismiss()
                        return
                    }
                    val numV = numStr.toLong()
                    if (numV > maxV) {
                        ToastUtils.showShort("Please input less than $maxV")
                        return
                    }
                    if (numV < 100) {
                        ToastUtils.showShort("Please input more than 100")
                        return
                    }
                    observer?.onInputNum(numV)
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        throw e
                    }
                }
                KeyboardUtils.hideSoftInput(etInputNum)
                dismiss()
            }

        })
//        etInputNum?.y = -ScreenUtils.getScreenHeight().toFloat()
        mHandler?.postDelayed(Runnable {
            if (etInputNum == null || context == null) {
                return@Runnable
            }
            KeyboardUtils.showSoftInput(etInputNum, 0)
//            etInputNum?.y = 0f
        }, 150)
    }

    interface Observer {
        fun onInputNum(num: Long)
    }

}