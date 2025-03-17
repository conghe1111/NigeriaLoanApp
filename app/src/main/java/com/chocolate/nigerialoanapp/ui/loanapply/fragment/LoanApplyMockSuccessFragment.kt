package com.chocolate.nigerialoanapp.ui.loanapply.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseFragment

class LoanApplyMockSuccessFragment : BaseFragment() {

    private var tvOk : TextView? = null
    private var tvCount : TextView? = null
    private var mMyTimeCount : MyTimeCount? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_apply_mock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvOk = view.findViewById<TextView>(R.id.tv_loan_apply_ok)
        tvCount = view.findViewById<TextView>(R.id.tv_count)

        mMyTimeCount = MyTimeCount(10 * 1000, 1000)
        mMyTimeCount?.start()
    }


    inner class MyTimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        @SuppressLint("SetTextI18n")
        override fun onTick(l: Long) {
            val countDownTime = l / 1000
//            Log.e("Test", " on tick  = " + l)
                tvCount?.text = "" +countDownTime + "s"
        }

        override fun onFinish() {
            activity?.finish()
        }
    }
}