package com.chocolate.nigerialoanapp.ui.dialog.selectamount

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.data.LoanData
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class SelectAmountAdapter(val mList: List<LoanData>, val mListener: OnItemClickListener) :
    RecyclerView.Adapter<SelectAmountAdapter.SelectAmountHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAmountHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_amount, parent, false)
        return SelectAmountHolder(view)
    }

    override fun getItemCount(): Int {
        if (mList == null) {
            return 0
        }
        return mList!!.size
    }

    override fun onBindViewHolder(holder: SelectAmountHolder, position: Int) {
        val data = mList.get(position)
        if (mListener.getSelectPos() == position) {
            holder.flContainer?.setBackgroundResource(R.color.color_3040B950)
        } else {
            holder.flContainer?.setBackgroundResource(R.color.white)
        }
        holder.tvAmount?.text = "₦" + SpanUtils.getShowText(data.amount!!.toLong())
        if (data.lockFlag) {
            holder.tvAmount?.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                holder.itemView.resources.getDrawable(R.drawable.ic_lock), null)
            holder.tvAmount?.setTextColor(holder.itemView.resources.getColor(R.color.color_999999))
        } else {
            holder.tvAmount?.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            holder.tvAmount?.setTextColor(holder.itemView.resources.getColor(R.color.color_333333))
        }
        holder.itemView.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (data.lockFlag) {
                    return
                }
                mListener?.onItemClick(data.amount!!, position)
            }

        })
    }

    inner class SelectAmountHolder : RecyclerView.ViewHolder {

        var tvAmount: TextView? = null
//        var tvAmountSelect: TextView? = null
        var flContainer: View? = null

        constructor(itemView: View) : super(itemView) {
            tvAmount = itemView.findViewById(R.id.tv_item_select_amount)
//            tvAmountSelect = itemView.findViewById(R.id.tv_item_select_amount_select)
            flContainer = itemView.findViewById(R.id.fl_select_container)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(str: String, pos: Int)

        fun getSelectPos(): Int
    }
}