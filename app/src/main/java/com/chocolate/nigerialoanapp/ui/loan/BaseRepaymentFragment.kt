package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse.Stage
import com.chocolate.nigerialoanapp.ui.loan.adapter.RepaymentAdapter
import com.chocolate.nigerialoanapp.ui.loan.adapter.RepaymentDetailAdapter


open class BaseRepaymentFragment : BaseLoanStatusFragment() {

    private var rvRepayment : RecyclerView? = null
    private var rvRepaymentDetail : RecyclerView? = null

    private var tvPending : AppCompatTextView? = null
    private var tvOverdue : AppCompatTextView? = null
    private val mStages = ArrayList<Stage>()

    private var mRepaymentAdapter : RepaymentAdapter? = null
    private var mRepaymentDetailAdapter : RepaymentDetailAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_pending_repayment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRepayment = view.findViewById<RecyclerView>(R.id.rv_repayment)
        tvPending = view.findViewById<AppCompatTextView>(R.id.tv_loan_pending)
        tvOverdue = view.findViewById<AppCompatTextView>(R.id.tv_loan_overdue)
        rvRepaymentDetail =   view.findViewById<RecyclerView>(R.id.rv_repayment_detail)
        initializeView()
    }

    private fun initializeView() {
        mStages.clear()
        mRepaymentAdapter = RepaymentAdapter(mStages)
        mRepaymentDetailAdapter = RepaymentDetailAdapter(mStages)

        rvRepayment?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvRepayment?.adapter = mRepaymentAdapter
        rvRepaymentDetail?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvRepaymentDetail?.adapter = mRepaymentDetailAdapter
    }
}