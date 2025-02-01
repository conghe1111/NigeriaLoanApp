package com.chocolate.nigerialoanapp.ui.loanapply.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.data.LoanData
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyPeriodAdapter.OnItemClickListener
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class LoanAmountMockAdapter(val mList : List<LoanData>) : RecyclerView.Adapter<LoanAmountMockAdapter.LoadApplyHistoryHolder>() {

    private var mOnItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadApplyHistoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan_apply_mock, parent, false)
        return LoadApplyHistoryHolder(view)
    }

    override fun getItemCount(): Int {
        if (mList == null) {
            return 0
        }
        return mList!!.size
    }

    override fun onBindViewHolder(holder: LoadApplyHistoryHolder, position: Int) {
        val loanData = mList.get(position)
        holder.tvLoanApply?.text = loanData.amount
        if (loanData.lockFlag){
            holder.flContainer?.setBackgroundResource(R.drawable.bg_gray_bg_2)
            holder.ivLock?.visibility = View.VISIBLE
            holder.tvLoanApply?.setTextColor(holder.itemView.resources.getColor(R.color.color_a1a1a1))
        } else {
            holder.flContainer?.setBackgroundResource(R.drawable.bg_green_bg)
            holder.ivLock?.visibility = View.GONE
            holder.tvLoanApply?.setTextColor(holder.itemView.resources.getColor(R.color.white))
        }
        holder.itemView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (loanData.amount == null) {
                    return
                }
                mOnItemClickListener?.onItemClick(loanData.amount!!, position)
            }

        })
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    inner class LoadApplyHistoryHolder : RecyclerView.ViewHolder{

        var tvLoanApply: TextView? = null
        var flContainer: View? = null
        var ivLock: AppCompatImageView? = null

        constructor(itemView: View) : super(itemView) {
            tvLoanApply = itemView.findViewById<TextView>(R.id.tv_item_loan_apply)
            flContainer = itemView.findViewById<View>(R.id.fl_item_container)
            ivLock = itemView.findViewById<AppCompatImageView>(R.id.iv_item_lock)
        }

    }
}