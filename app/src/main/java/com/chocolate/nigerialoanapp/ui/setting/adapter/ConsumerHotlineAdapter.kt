package com.chocolate.nigerialoanapp.ui.setting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class ConsumerHotlineAdapter(val list: List<String>) :
    RecyclerView.Adapter<ConsumerHotlineAdapter.ConsumerHotlineHolder>() {

    private var mListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsumerHotlineHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consumer_hotline, parent, false)
        return ConsumerHotlineHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ConsumerHotlineHolder, position: Int) {
        val str = list.get(position)
        if (str.startsWith("234")) {
            holder.tvHotline?.text = "+$str"
        } else {
            holder.tvHotline?.text = str
        }
        holder.itemView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                mListener?.onItemClick(position,str)
            }

        })
    }

    inner class ConsumerHotlineHolder : RecyclerView.ViewHolder {
        var tvHotline: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvHotline = itemView.findViewById(R.id.tv_consumer_hotline)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(pos: Int, str: String)
    }
}