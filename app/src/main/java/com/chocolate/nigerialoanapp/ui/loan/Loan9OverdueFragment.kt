package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.ui.HomeFragment

/**
 * 逾期
 */
class Loan9OverdueFragment : BaseRepaymentFragment() {

    private var tvOverdue : AppCompatTextView? = null
    private var tvOverdueDay : AppCompatTextView? = null

    private var llOverdue : View? = null
    private var llPending : View? = null
    private var llOverdueDesc : View? = null
    private var llOrderNumView: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvOverdue =  view.findViewById<AppCompatTextView>(R.id.tv_loan_overdue)
        llOverdue =  view.findViewById<View>(R.id.ll_loan_overdue)
        llPending =  view.findViewById<View>(R.id.ll_loan_pending)
        llOverdueDesc =  view.findViewById<View>(R.id.ll_overdue_desc)
        tvOverdueDay =  view.findViewById<AppCompatTextView>(R.id.tv_due_day)
        llOrderNumView = view.findViewById<View>(R.id.ll_order_num)

        llOrderNumView?.visibility = View.GONE
        llPending?.visibility = View.GONE
        tvOverdue?.visibility = View.VISIBLE
        llOverdue?.visibility = View.VISIBLE
        llOverdueDesc?.visibility = View.VISIBLE
        if (parentFragment is HomeFragment) {
            val orderDetail = (parentFragment as HomeFragment).mOrderDetail?.order_detail
            if (orderDetail == null) {
                return
            }
//            val dueDay = "3"
            tvOverdue?.text = resources.getString(R.string.overdue_x_days)
            tvOverdueDay?.text = resources.getString(R.string.your_loan_x_day)
        }
    }
}