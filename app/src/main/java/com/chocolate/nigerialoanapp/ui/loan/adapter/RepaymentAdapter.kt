package com.chocolate.nigerialoanapp.ui.loan.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class RepaymentAdapter(val repayAmountList : List<Long>) : RecyclerView.Adapter<RepaymentAdapter.RepaymentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepaymentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan_repayment, parent, false)
        return RepaymentHolder(view)
    }

    override fun getItemCount(): Int {
        return repayAmountList.size
    }

    override fun onBindViewHolder(holder: RepaymentHolder, position: Int) {
        val amount = repayAmountList[position]
        val text = SpanUtils.getShowText(amount)
        val spanString = SpannableString(text)
        try {
            spanString.setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } catch (e : Exception) {

        }
        holder.tvTotalPrice?.text = spanString

        holder.tvTotalPrice?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                mListener?.onItemClickAmount(position)
            }

        })
//        tv_item_total_price
        holder.tvRepayment?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                mListener?.onItemClickRepay(amount,position)
            }

        })
    }

    private var mListener : OnItemClickListener? = null

    fun setOnItemClickListener(listener : OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClickAmount(pos : Int)
        fun onItemClickRepay(amount : Long, pos: Int)
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