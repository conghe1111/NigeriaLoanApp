package com.chocolate.nigerialoanapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.ui.dialog.selectamount.SelectAmountAdapter
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class SelectAmountDialog (context: Context, val list: List<String>): Dialog(context) {

    private var mCurStr : String? = null
    private var mCurPos : Int? = null

    init {
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val lp: WindowManager.LayoutParams? = window?.attributes
        if (lp != null) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT //设置宽度
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT //设置高度
            lp.horizontalMargin = 0f
            lp.verticalMargin = 0f
            window?.attributes = lp
        }
        setContentView(R.layout.dialog_select_amount)
        val rvContent: RecyclerView = findViewById<RecyclerView>(R.id.rv_select_amount)
        var tvConfirm: TextView  = findViewById<TextView>(R.id.tv_amount_confirm)

        rvContent?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = SelectAmountAdapter(list, object : OnItemClickListener() {
            override fun onItemClick(str: String, pos: Int) {
                mCurStr = str
                mCurPos = pos
            }

        })
        rvContent?.adapter = adapter
        tvConfirm?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (!TextUtils.isEmpty(mCurStr) && mCurPos != null) {
                    mListener?.onItemClick(mCurStr!!, mCurPos!!)
                }
                dismiss()
            }

        })
    }

    private var mListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    abstract class OnItemClickListener {
        abstract fun onItemClick(str : String, pos : Int)
    }
}