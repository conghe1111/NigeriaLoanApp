package com.chocolate.nigerialoanapp.ui.loan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse

class RepaymentAdapter(val stages : List<OrderDetailResponse.Stage>) : RecyclerView.Adapter<RepaymentAdapter.RepaymentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepaymentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan_repayment, parent, false)
        return RepaymentHolder(view)
    }

    override fun getItemCount(): Int {
        return stages.size
    }

    override fun onBindViewHolder(holder: RepaymentHolder, position: Int) {

    }

    inner class RepaymentHolder : RecyclerView.ViewHolder {
        var tvTotalPrice: TextView? = null
        var tvUpdateAccount: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price)
            tvUpdateAccount = itemView.findViewById(R.id.tv_update_account)
        }
    }
}