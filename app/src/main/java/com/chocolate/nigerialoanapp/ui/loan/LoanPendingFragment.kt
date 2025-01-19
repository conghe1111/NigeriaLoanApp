package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R

class LoanPendingFragment : BaseRepaymentFragment() {

    private var tvPending : AppCompatTextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvPending =  view.findViewById<AppCompatTextView>(R.id.tv_loan_pending)
        tvPending?.visibility = View.VISIBLE
    }
}