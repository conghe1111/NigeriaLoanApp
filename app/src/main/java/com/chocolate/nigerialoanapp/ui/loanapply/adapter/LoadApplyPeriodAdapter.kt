package com.chocolate.nigerialoanapp.ui.loanapply.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.data.LoanData
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class LoadApplyPeriodAdapter(val mList : List<LoanData>, pos : Int) : RecyclerView.Adapter<LoadApplyPeriodAdapter.LoadApplyPeriodHolder>() {

    private var mOnItemClickListener : OnItemClickListener? = null
    private var mPos : Int = pos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadApplyPeriodHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan_title, parent, false)
        return LoadApplyPeriodHolder(view)
    }

    override fun getItemCount(): Int {
        if (mList == null) {
            return 0
        }
        return mList!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LoadApplyPeriodHolder, position: Int) {
        val loanData = mList.get(position)
        holder.tvLoanApply?.text = loanData.data?.toString()+ "days"

        if (loanData.lockFlag){
            holder.flContainer?.setBackgroundResource(R.drawable.bg_green_bg_21)
            holder.ivLock?.visibility = View.VISIBLE
            holder.tvLoanApply?.setTextColor(holder.itemView.resources.getColor(R.color.color_a1a1a1))
        } else {
            if (mPos == position) {
                holder.flContainer?.setBackgroundResource(R.drawable.bg_green_bg)
                holder.tvLoanApply?.setTextColor(holder.itemView.resources.getColor(R.color.color_333333))
            } else {
                holder.flContainer?.setBackgroundResource(R.drawable.bg_green_bg_21)
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


    inner class LoadApplyPeriodHolder : RecyclerView.ViewHolder{

        var tvLoanApply: TextView? = null
        var flContainer: View? = null
        var ivLock: AppCompatImageView? = null

        constructor(itemView: View) : super(itemView) {
            tvLoanApply = itemView.findViewById<TextView>(R.id.tv_item_loan_term)
            flContainer = itemView.findViewById<View>(R.id.fl_item_container)
            ivLock = itemView.findViewById<AppCompatImageView>(R.id.iv_item_lock)
        }

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(period : String, pos : Int)
    }
}