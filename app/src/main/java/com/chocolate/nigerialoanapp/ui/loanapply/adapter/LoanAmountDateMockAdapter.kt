package com.chocolate.nigerialoanapp.ui.loanapply.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.data.LoanData
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyPeriodAdapter.OnItemClickListener
import com.chocolate.nigerialoanapp.utils.DateUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class LoanAmountDateMockAdapter(val mList : List<LoanData>, pos : Int) : RecyclerView.Adapter<LoanAmountDateMockAdapter.LoadApplyHistoryHolder>() {

    private var mOnItemClickListener : OnItemClickListener? = null
    private var mPos : Int = pos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadApplyHistoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan_apply_date_mock, parent, false)
        return LoadApplyHistoryHolder(view)
    }

    override fun getItemCount(): Int {
        if (mList == null) {
            return 0
        }
        return mList!!.size
    }

    override fun onBindViewHolder(holder: LoadApplyHistoryHolder, position: Int) {
        val loanData = mList.get(position)

        val array = DateUtils.buildDateFormat( loanData.data!!.toLong())
        if (array == null || array.size < 3) {
            return
        }
        val str = array[2] + "\n" + array[1] + "\n" +array[0]
        holder.tvLoanApply?.text = str
        if (loanData.lockFlag){
            holder.ivLock?.visibility = View.VISIBLE
        } else {
            holder.ivLock?.visibility = View.GONE
        }
        if (mPos == position) {
            holder.flContainer?.setBackgroundResource(R.drawable.bg_green_bg)
            holder.tvLoanApply?.setTextColor(holder.itemView.resources.getColor(R.color.white))
        } else {
            holder.flContainer?.setBackgroundResource(R.drawable.bg_gray_bg_2)
            holder.tvLoanApply?.setTextColor(holder.itemView.resources.getColor(R.color.color_a1a1a1))
        }
        holder.itemView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (loanData.data == null) {
                    return
                }
                if (loanData.lockFlag) {
                    return
                }
                val lastPos = mPos
                mPos = position
                mOnItemClickListener?.onItemClick(loanData.data!!, position)
                notifyItemChanged(lastPos)
                notifyItemChanged(mPos)
            }

        })
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    inner class LoadApplyHistoryHolder : RecyclerView.ViewHolder{

        var tvLoanApply: TextView? = null
        var flContainer: View? = null
        var ivLock: AppCompatImageView? = null

        constructor(itemView: View) : super(itemView) {
            tvLoanApply = itemView.findViewById<TextView>(R.id.tv_item_loan_apply_date)
            flContainer = itemView.findViewById<View>(R.id.fl_item_container)
            ivLock = itemView.findViewById<AppCompatImageView>(R.id.tv_item_loan_apply_date_lock)
        }

    }
}