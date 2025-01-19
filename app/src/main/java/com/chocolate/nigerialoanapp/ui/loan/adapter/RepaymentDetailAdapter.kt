package com.chocolate.nigerialoanapp.ui.loan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse

class RepaymentDetailAdapter(val stages : List<OrderDetailResponse.Stage>) : RecyclerView.Adapter<RepaymentDetailAdapter.RepaymentDetailHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepaymentDetailHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan_repayment, parent, false)
        return RepaymentDetailHolder(view)
    }

    override fun getItemCount(): Int {
        return stages.size
    }

    override fun onBindViewHolder(holder: RepaymentDetailHolder, position: Int) {

    }

    inner class RepaymentDetailHolder : RecyclerView.ViewHolder {
        var tvRepaymentTime: TextView? = null
        var tvDueAmount: TextView? = null
        var tvLoanAmount: TextView? = null
        var tvInterest: TextView? = null
        var tvServiceFee: TextView? = null
        var tvAmountReduction: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvRepaymentTime = itemView.findViewById(R.id.tv_repayment_time)
            tvDueAmount = itemView.findViewById(R.id.tv_due_amount)
            tvLoanAmount = itemView.findViewById(R.id.tv_loan_amount)
            tvInterest = itemView.findViewById(R.id.tv_interest)
            tvServiceFee = itemView.findViewById(R.id.tv_service_fee)
            tvAmountReduction = itemView.findViewById(R.id.tv_amount_reduction)
        }
    }
}