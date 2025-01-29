package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R

class Loan10PayProcessingFragment : BaseProcessedFragment() {

    private var tvDesc : AppCompatTextView? = null
    private var tvPayBtn : AppCompatTextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDesc = view.findViewById<AppCompatTextView>(R.id.tv_pay_desc)
        tvPayBtn = view.findViewById<AppCompatTextView>(R.id.tv_pay_button)

        tvDesc?.text = resources.getString(R.string.you_repayment_current_process)
        tvPayBtn?.text = resources.getString(R.string.refresh)
    }
}