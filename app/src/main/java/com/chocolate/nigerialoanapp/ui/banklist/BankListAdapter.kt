package com.chocolate.nigerialoanapp.ui.banklist

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.BankBeanResponse

class BankListAdapter : RecyclerView.Adapter<SelectDataHolder> {

    private var mLists: ArrayList<BankBeanResponse.Bank>? = null
    private var mItemClickListener: OnItemClickListener? = null

    constructor(lists: ArrayList<BankBeanResponse.Bank>?) {
        mLists = lists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectDataHolder {
        return SelectDataHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_data, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SelectDataHolder, position: Int) {
        val bankBean = mLists!![position]
        holder.tvSelectData?.text = bankBean.bank_name
        if (holder.ivIcon != null && !TextUtils.isEmpty(bankBean.bank_url)) {
            Glide.with(holder.ivIcon!!).load(bankBean.bank_url).into(holder.ivIcon!!)
        }
        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(bankBean!!, position)
        }
    }

    override fun getItemCount(): Int {
        return if (mLists == null) 0 else mLists!!.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(bankBean: BankBeanResponse.Bank, pos: Int)
    }
}