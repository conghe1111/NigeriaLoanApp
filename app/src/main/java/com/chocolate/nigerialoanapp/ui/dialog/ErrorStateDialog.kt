package com.chocolate.nigerialoanapp.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.utils.JumpPermissionUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class ErrorStateDialog(context: Context, val desc : String, activity: Activity?): Dialog(context) {

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
        setContentView(R.layout.dialog_error_state)

        val tvDesc = findViewById<AppCompatTextView>(R.id.tv_state_desc)
        val tvCancel = findViewById<AppCompatTextView>(R.id.tv_state_cancel)
        val tvOpen =  findViewById<AppCompatTextView>(R.id.tv_state_open)

        tvDesc?.text = desc

        tvCancel?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                dismiss()
            }

        })
        tvOpen?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (activity?.isFinishing == true || activity?.isDestroyed == true) {
                    return
                }
                JumpPermissionUtils.toWifiSetting(activity)
            }

        })
    }

}