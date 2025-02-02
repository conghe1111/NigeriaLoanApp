package com.chocolate.nigerialoanapp.ui.loan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse.Stage
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class RepaymentAdapter(val stages : List<OrderDetailResponse.Stage>) : RecyclerView.Adapter<RepaymentAdapter.RepaymentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepaymentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan_repayment, parent, false)
        return RepaymentHolder(view)
    }

    override fun getItemCount(): Int {
        return stages.size
    }

    override fun onBindViewHolder(holder: RepaymentHolder, position: Int) {
        val stage = stages[position]
        holder.tvTotalPrice?.text = SpanUtils.getShowText(stage.repay_total.toLong())
        holder.tvRepayment?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                mListener?.onItemClick(stage)
            }

        })
    }

    private var mListener : OnItemClickListener? = null

    fun setOnItemClickListener(listener : OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(stage : Stage)
    }

    inner class RepaymentHolder : RecyclerView.ViewHolder {
        var tvTotalPrice: TextView? = null
        var tvRepayment: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvTotalPrice = itemView.findViewById(R.id.tv_item_total_price)
            tvRepayment = itemView.findViewById(R.id.tv_item_repayment)
        }
    }
}