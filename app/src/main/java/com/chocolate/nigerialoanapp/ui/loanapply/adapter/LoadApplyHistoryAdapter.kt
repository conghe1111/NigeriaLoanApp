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

class LoadApplyHistoryAdapter(val mList : List<Trial>) : RecyclerView.Adapter<LoadApplyHistoryAdapter.LoadApplyHistoryHolder>() {

    private var mOnItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadApplyHistoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.include_item_schedule, parent, false)
        return LoadApplyHistoryHolder(view)
    }

    override fun getItemCount(): Int {
        if (mList == null) {
            return 0
        }
        return mList!!.size
    }

    override fun onBindViewHolder(holder: LoadApplyHistoryHolder, position: Int) {
        val trial = mList.get(position)
        val period = mList.get(position).toString()
        holder.tvDueDay?.text = trial.repay_date
        holder.tvDueAmount?.text = SpanUtils.getShowText1(trial.total.toLong())
        holder.itemView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                mOnItemClickListener?.onItemClick(period, position)
            }

        })
    }

    inner class LoadApplyHistoryHolder : RecyclerView.ViewHolder{

        var tvDueDay: TextView? = null
        var tvDueAmount: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvDueDay = itemView.findViewById<TextView>(R.id.tv_item_1_due_day)
            tvDueAmount = itemView.findViewById<TextView>(R.id.tv_item_1_due_amount)
        }

    }
}