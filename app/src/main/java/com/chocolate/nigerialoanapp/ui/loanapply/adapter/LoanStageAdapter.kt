package com.chocolate.nigerialoanapp.ui.loanapply.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse.Trial
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyPeriodAdapter.OnItemClickListener
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class LoanStageAdapter(val mList : List<Trial>) : RecyclerView.Adapter<LoanStageAdapter.LoanStageHolder>() {

    private var mOnItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanStageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stage_mock, parent, false)
        return LoanStageHolder(view)
    }

    override fun getItemCount(): Int {
        if (mList == null) {
            return 0
        }
        return mList!!.size
    }

    override fun onBindViewHolder(holder: LoanStageHolder, position: Int) {
        val trial = mList.get(position)
        holder.tvTitle?.text = holder.itemView.resources.getString(R.string.stage_x_x, (position + 2).toString(),
            (mList.size + 1).toString())
        holder.tvAmount?.text = SpanUtils.getShowText1(trial.amount.toLong())
        holder.tvDate?.text = trial.repay_date
//        holder.itemView?.setOnClickListener(object : NoDoubleClickListener() {
//            override fun onNoDoubleClick(v: View?) {
//                mOnItemClickListener?.onItemClick(trial.repay_date, position)
//            }
//
//        })
    }

    inner class LoanStageHolder : RecyclerView.ViewHolder{

        var tvTitle: TextView? = null
        var tvDate: TextView? = null
        var tvAmount: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvTitle = itemView.findViewById<TextView>(R.id.tv_stage_title)
            tvDate = itemView.findViewById<TextView>(R.id.tv_loan_repay_date)
            tvAmount = itemView.findViewById<TextView>(R.id.tv_loan_repay_amount)
        }

    }
}