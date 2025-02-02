package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R

/**
 * 等待还款
 */
class Loan7PendingFragment : BaseRepaymentFragment() {

    private var tvPending : AppCompatTextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvPending =  view.findViewById<AppCompatTextView>(R.id.tv_loan_pending)
        tvPending?.visibility = View.VISIBLE

    }


}