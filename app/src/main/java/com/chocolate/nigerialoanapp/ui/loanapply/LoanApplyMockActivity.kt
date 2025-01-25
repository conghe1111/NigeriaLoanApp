package com.chocolate.nigerialoanapp.ui.loanapply

import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R

class LoanApplyMockActivity : BaseLoanApplyActivity() {

    companion object {

        private const val TAG = "LoanApplyMockActivity"

    }
    private var tvAmount : AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        initialView()
    }

    private fun initialView() {
        tvAmount = findViewById<AppCompatTextView>(R.id.tv_loan_apply_amount)
    }

}