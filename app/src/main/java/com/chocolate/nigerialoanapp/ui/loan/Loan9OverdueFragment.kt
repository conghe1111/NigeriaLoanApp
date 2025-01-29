package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R

/**
 * 逾期
 */
class Loan9OverdueFragment : BaseRepaymentFragment() {

    private var tvOverdue : AppCompatTextView? = null
    private var llOverdue : View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvOverdue =  view.findViewById<AppCompatTextView>(R.id.tv_loan_overdue)
        llOverdue =  view.findViewById<View>(R.id.ll_overdue_desc)
        tvOverdue?.visibility = View.VISIBLE
        llOverdue?.visibility = View.VISIBLE
    }
}