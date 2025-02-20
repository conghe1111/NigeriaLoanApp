package com.chocolate.nigerialoanapp.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.HistoryRecordResponse
import com.chocolate.nigerialoanapp.global.LocalConfig
import com.chocolate.nigerialoanapp.utils.SpanUtils

class HistoryRecordAdapter(val historys: List<HistoryRecordResponse.History>) :
    RecyclerView.Adapter<HistoryRecordAdapter.HistoryRecordHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryRecordHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_record, parent, false)
        return HistoryRecordHolder(view)
    }

    override fun getItemCount(): Int {
        return historys.size
    }

    override fun onBindViewHolder(holder: HistoryRecordHolder, position: Int) {
        val history = historys.get(position)
        holder?.tvAmountTitle?.text = SpanUtils.getShowText1(history.loan_amount?.toLong())
        holder?.tvLoanAmount?.text = SpanUtils.getShowText1(history.loan_amount?.toLong())
        holder?.tvDateApp?.text = history.apply_date?.toString()
        holder?.tvDueAmount?.text = SpanUtils.getShowText1(history.repay_amount?.toLong())
        holder?.tvDueDay?.text = history.overdue_day?.toString()
        //逾期天数；还款日；应还总额；这三个不展示；
        if (LocalConfig.isLoanMoney(history?.status)) {
//            holder?.viewDateApp?.visibility = View.GONE
//            holder?.viewDateAppDiv?.visibility = View.GONE
            holder?.viewDueAmount?.visibility = View.GONE
            holder?.viewDueAmountDiv?.visibility = View.GONE
            holder?.viewDueDay?.visibility = View.GONE
            holder?.viewDueDayDiv?.visibility = View.GONE
        } else {
//            holder?.viewDateApp?.visibility = View.VISIBLE
//            holder?.viewDateAppDiv?.visibility = View.VISIBLE
            holder?.viewDueAmount?.visibility = View.VISIBLE
            holder?.viewDueAmountDiv?.visibility = View.VISIBLE
            holder?.viewDueDay?.visibility = View.VISIBLE
            holder?.viewDueDayDiv?.visibility = View.VISIBLE
        }
        holder?.tvLoanStatus?.text = LocalConfig.getLoanStr(holder.itemView.context, history.status)

    }

    inner class HistoryRecordHolder : RecyclerView.ViewHolder {
        var tvAmountTitle: TextView? = null
        var tvLoanAmount: TextView? = null
        var tvDateApp: TextView? = null
        var tvDueAmount: TextView? = null
        var tvDueDay: TextView? = null
        var tvLoanStatus: TextView? = null
        var viewDateApp: View? = null
        var viewDateAppDiv: View? = null
        var viewDueAmount: View? = null
        var viewDueAmountDiv: View? = null
        var viewDueDay: View? = null
        var viewDueDayDiv: View? = null

        constructor(itemView: View) : super(itemView) {
            tvAmountTitle = itemView.findViewById(R.id.tv_history_title)
            tvLoanAmount = itemView.findViewById(R.id.tv_item_1_loan_amount)
            tvDateApp = itemView.findViewById(R.id.tv_item_1_date_of_app)
            tvDueAmount = itemView.findViewById(R.id.tv_item_1_due_amount)
            tvDueDay = itemView.findViewById(R.id.tv_item_1_due_day)
            tvLoanStatus = itemView.findViewById(R.id.tv_item_1_loan_status)

            viewDateApp = itemView.findViewById(R.id.ll_date_app)
            viewDateAppDiv = itemView.findViewById(R.id.view_date_app)

            viewDueAmount = itemView.findViewById(R.id.ll_due_amount)
            viewDueAmountDiv = itemView.findViewById(R.id.view_due_amount)

            viewDueDay = itemView.findViewById(R.id.ll_due_day)
            viewDueDayDiv = itemView.findViewById(R.id.view_due_day)
        }
    }


}