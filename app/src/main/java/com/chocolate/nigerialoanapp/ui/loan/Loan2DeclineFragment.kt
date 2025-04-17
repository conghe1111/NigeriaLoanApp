package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.ui.HomeFragment
import com.chocolate.nigerialoanapp.ui.edit.EditInfoActivity
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

/**
 * 审核拒绝
 */
class Loan2DeclineFragment : BaseLoanStatusFragment() {

    private var tvUpdateAccount : AppCompatTextView? = null
    private var tvRejectMsg : AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_decline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvRejectMsg = view.findViewById<AppCompatTextView>(R.id.tv_verify_loan)
        if (parentFragment is HomeFragment) {
            val orderDetail = (parentFragment as HomeFragment).mOrderDetail?.order_detail
            if (orderDetail == null && TextUtils.isEmpty(orderDetail?.reject_message)) {
                return
            }
            tvRejectMsg?.text = orderDetail?.reject_message
        }



        tvUpdateAccount = view.findViewById<AppCompatTextView>(R.id.tv_update_account)
        tvUpdateAccount?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (isDestroy()) {
                    return
                }
                activity?.let {
                    EditInfoActivity.showActivity(it, EditInfoActivity.STEP_4, EditInfoActivity.FROM_DISBURSE_6, true)
                }
            }

        })
    }
}