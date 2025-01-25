package com.chocolate.nigerialoanapp.ui.dialog.selectamount

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.ui.dialog.SelectAmountDialog.OnItemClickListener
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class SelectAmountAdapter(val mList : List<String>, val mListener : OnItemClickListener) : RecyclerView.Adapter<SelectAmountAdapter.SelectAmountHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAmountHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_select_amount, parent, false)
        return SelectAmountHolder(view)
    }

    override fun getItemCount(): Int {
        if (mList == null) {
            return 0
        }
        return mList!!.size
    }

    override fun onBindViewHolder(holder: SelectAmountHolder, position: Int) {
       val text = mList.get(position)
        holder.tvAmount?.text = text
        holder.itemView.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                mListener?.onItemClick(text, position)
            }

        })
    }

    inner class SelectAmountHolder : RecyclerView.ViewHolder {

        var tvAmount: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvAmount = itemView.findViewById(R.id.tv_item_select_amount)
        }


    }
}