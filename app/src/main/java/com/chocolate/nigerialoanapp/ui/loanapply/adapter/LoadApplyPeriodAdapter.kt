package com.chocolate.nigerialoanapp.ui.loanapply.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class LoadApplyPeriodAdapter(val mList : List<String>) : RecyclerView.Adapter<LoadApplyPeriodAdapter.LoadApplyPeriodHolder>() {

    private var mOnItemClickListener : OnItemClickListener? = null

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

    override fun onBindViewHolder(holder: LoadApplyPeriodHolder, position: Int) {
        val period = mList.get(position).toString()
        holder.tvTerm?.text =  period
        holder.itemView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                mOnItemClickListener?.onItemClick(period, position)
            }

        })
    }


    inner class LoadApplyPeriodHolder : RecyclerView.ViewHolder{

        var tvTerm: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvTerm = itemView.findViewById(R.id.tv_item_loan_term)
        }

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(period : String, pos : Int)
    }
}