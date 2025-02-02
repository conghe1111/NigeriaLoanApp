package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.ui.edit.EditInfoActivity
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class Loan6PayFailureFragment : BaseLoanStatusFragment() {

    private var tvUpdateAccount : AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_pay_failure, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUpdateAccount = view.findViewById<AppCompatTextView>(R.id.tv_update_account)
        tvUpdateAccount?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                context?.let {
                    EditInfoActivity.showActivity(it, EditInfoActivity.STEP_4)
                }
            }

        })
    }
}