package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse.OrderDetail
import com.chocolate.nigerialoanapp.ui.HomeFragment
import com.chocolate.nigerialoanapp.ui.webview.WebViewActivity
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class Loan10PayProcessingFragment : BaseProcessedFragment() {

    private var tvDesc : AppCompatTextView? = null
    private var tvPayBtn : AppCompatTextView? = null
    var mOrderDetail: OrderDetail? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDesc = view.findViewById<AppCompatTextView>(R.id.tv_pay_desc)
        tvPayBtn = view.findViewById<AppCompatTextView>(R.id.tv_pay_button)

        tvDesc?.text = resources.getString(R.string.you_repayment_current_process)
        tvPayBtn?.text = resources.getString(R.string.refresh)

        if (parentFragment is HomeFragment) {
            mOrderDetail = (parentFragment as HomeFragment).mOrderDetail?.order_detail
            if (mOrderDetail == null) {
                return
            }
            val tvRepayAgain = view.findViewById<AppCompatTextView>(R.id.tv_repay_again_button)
            if (!TextUtils.isEmpty(mOrderDetail?.checkout_url)) {
                tvRepayAgain?.visibility = View.VISIBLE
            }

            tvRepayAgain?.setOnClickListener(object : NoDoubleClickListener() {
                override fun onNoDoubleClick(v: View?) {
                    if (mOrderDetail == null || TextUtils.isEmpty(mOrderDetail?.checkout_url)) {
                        return
                    }
                    context?.let {
                        WebViewActivity.launchWebView(it, mOrderDetail!!.checkout_url!!, WebViewActivity.TYPE_REPAY)
                    }
                }

            })


        }
    }

}