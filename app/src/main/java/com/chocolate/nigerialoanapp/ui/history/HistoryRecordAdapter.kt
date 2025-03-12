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
        holder?.tvLoanAmount1?.text = SpanUtils.getShowText1(history.loan_amount?.toLong())
        holder?.tvDueAmount2?.text = SpanUtils.getShowText1(history.loan_amount?.toLong())
        holder?.tvDateApp1?.text = history.apply_date?.toString()
        holder?.tvLoanAMount?.text = SpanUtils.getShowText1(history.loan_amount?.toLong())
        holder?.tvDateApp2?.text = history.overdue_day?.toString()
        //逾期天数；还款日；应还总额；这三个不展示；
        if (LocalConfig.isLoanMoney(history?.status)) {
//            holder?.viewDateApp?.visibility = View.GONE
//            holder?.viewDateAppDiv?.visibility = View.GONE
            holder?.llDueContainer?.visibility = View.GONE
            holder?.llDueContainerDesc?.visibility = View.GONE
            holder?.viewLine2?.visibility = View.GONE
        } else {
//            holder?.viewDateApp?.visibility = View.VISIBLE
//            holder?.viewDateAppDiv?.visibility = View.VISIBLE
            holder?.llDueContainer?.visibility = View.VISIBLE
            holder?.llDueContainerDesc?.visibility = View.VISIBLE
            holder?.viewLine2?.visibility = View.VISIBLE
        }
        holder?.tvLoanStatus?.text = LocalConfig.getLoanStr(holder.itemView.context, history.status)

    }

    inner class HistoryRecordHolder : RecyclerView.ViewHolder {
        var tvLoanAmount1: TextView? = null
        var tvDateApp1: TextView? = null

        var tvDueAmount2: TextView? = null
        var tvDateApp2: TextView? = null

        var tvLoanStatus: TextView? = null
        var tvLoanAMount: TextView? = null
        var llDueContainer: View? = null
        var llDueContainerDesc: View? = null
        var viewLine2: View? = null

        constructor(itemView: View) : super(itemView) {
            llDueContainer = itemView.findViewById(R.id.ll_due_day_container)
            llDueContainerDesc = itemView.findViewById(R.id.ll_due_day_container_desc)
            tvLoanAmount1 = itemView.findViewById(R.id.tv_item_loan_amount_1)
            tvDateApp1 = itemView.findViewById(R.id.tv_item_date_app_1)
            tvDueAmount2 = itemView.findViewById(R.id.tv_item_loan_amount_2)
            tvDateApp2 = itemView.findViewById(R.id.tv_item_date_app_2)

            tvLoanStatus = itemView.findViewById(R.id.tv_item_loan_status)
            tvLoanAMount = itemView.findViewById(R.id.tv_item_loan_amount)
            viewLine2 = itemView.findViewById(R.id.view_line2)

        }
    }


}