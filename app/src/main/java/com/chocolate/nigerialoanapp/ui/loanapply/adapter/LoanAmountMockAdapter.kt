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
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class LoanAmountMockAdapter(val mList : List<LoanData>, val isTerm : Boolean =  false) : RecyclerView.Adapter<LoanAmountMockAdapter.LoadApplyHistoryHolder>() {

    private var mPos : Int = 0
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
        if (isTerm) {
            holder.tvLoanApply?.text = loanData.data?.toString()
        } else {
            holder.tvLoanApply?.text = SpanUtils.getShowText2(loanData.data?.toLong())
        }
        if (loanData.lockFlag){
            holder.flContainer?.setBackgroundResource(R.drawable.bg_green_bg_lock)
            holder.ivLock?.visibility = View.VISIBLE
            holder.tvLoanApply?.setTextColor(holder.itemView.resources.getColor(R.color.color_a1a1a1))
        } else {
            if (mPos == position) {
                holder.flContainer?.setBackgroundResource(R.drawable.bg_green_bg)
                holder.tvLoanApply?.setTextColor(holder.itemView.resources.getColor(R.color.color_333333))
            } else {
                holder.flContainer?.setBackgroundResource(R.drawable.bg_gray_bg_2)
                holder.tvLoanApply?.setTextColor(holder.itemView.resources.getColor(R.color.color_A8BFB3))
            }
            holder.ivLock?.visibility = View.GONE
        }
        holder.itemView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (loanData.data == null) {
                    return
                }
                if (loanData.lockFlag) {
                    return
                }
                val lastPos = mPos
                mPos = position
                mOnItemClickListener?.onItemClick(loanData.data!!, position)
                notifyItemChanged(lastPos)
                notifyItemChanged(mPos)
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