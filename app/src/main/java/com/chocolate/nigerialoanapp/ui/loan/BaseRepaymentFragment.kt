package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse.OrderDetail
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse.Stage
import com.chocolate.nigerialoanapp.bean.response.RepayResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.log.LogSaver
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.HomeFragment
import com.chocolate.nigerialoanapp.ui.loan.adapter.RepaymentAdapter
import com.chocolate.nigerialoanapp.ui.loan.adapter.RepaymentDetailAdapter
import com.chocolate.nigerialoanapp.ui.loan.dialog.InputLoanNumDialog
import com.chocolate.nigerialoanapp.ui.webview.WebViewActivity
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.chocolate.nigerialoanapp.widget.decor.NorItemDecor5
import com.chocolate.nigerialoanapp.widget.decor.NorItemDecor6
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject


open class BaseRepaymentFragment : BaseLoanStatusFragment() {

    companion object {
        const val TAG = "BaseRepaymentFragment"
    }

    private var rvRepayment: RecyclerView? = null
    private var rvRepaymentDetail: RecyclerView? = null

    private var tvPending: AppCompatTextView? = null
    private var tvOverdue: AppCompatTextView? = null
    private var tvOrderNum: AppCompatTextView? = null
    private var ivPaste: AppCompatImageView? = null
    private val mStages = ArrayList<Stage>()

    private var mRepaymentAdapter: RepaymentAdapter? = null
    private var mRepaymentDetailAdapter: RepaymentDetailAdapter? = null

    private var mOrderDetail: OrderDetail? = null

    private val mRepayAmountList : ArrayList<Long> = ArrayList<Long>()

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
        tvOrderNum = view.findViewById<AppCompatTextView>(R.id.tv_order_num)
        ivPaste = view.findViewById<AppCompatImageView>(R.id.iv_repayment_paste)
        rvRepaymentDetail = view.findViewById<RecyclerView>(R.id.rv_repayment_detail)
        initializeView()
    }

    private fun initializeView() {
        mStages.clear()
        mRepayAmountList.clear()
        mRepaymentAdapter = RepaymentAdapter(mRepayAmountList)
        mRepaymentDetailAdapter = RepaymentDetailAdapter(mStages)

        rvRepayment?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvRepayment?.adapter = mRepaymentAdapter
        rvRepayment?.addItemDecoration(NorItemDecor6())

        rvRepaymentDetail?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvRepaymentDetail?.adapter = mRepaymentDetailAdapter
        rvRepaymentDetail?.addItemDecoration(NorItemDecor5())

        mRepaymentAdapter?.setOnItemClickListener(object : RepaymentAdapter.OnItemClickListener {
            override fun onItemClickAmount(pos: Int) {
                if (isDestroy() || context == null) {
                    return
                }
                val total = mStages[pos].repay_total
                val dialog = InputLoanNumDialog(context!!, activity,total, object : InputLoanNumDialog.Observer {
                    override fun onInputNum(num: Long) {
                        if (isDestroy() || context == null) {
                            return
                        }
                        mRepayAmountList.set(pos, num)
                        mRepaymentAdapter?.notifyDataSetChanged()
                    }

                })
                dialog.show()
            }

            override fun onItemClickRepay(amount: Long, pos: Int) {
                if (isDestroy() || context == null) {
                    return
                }
                if (mOrderDetail == null) {
                    return
                }
                repaymentLoan(mOrderDetail!!.order_id.toString(), amount.toString())
            }

        })
        if (parentFragment is HomeFragment) {
            mOrderDetail = (parentFragment as HomeFragment).mOrderDetail?.order_detail
            if (mOrderDetail == null) {
                return
            }
            tvOrderNum?.text =  mOrderDetail!!.order_id.toString()
            ivPaste?.setOnClickListener(object : NoDoubleClickListener() {
                override fun onNoDoubleClick(v: View?) {
                    val text = mOrderDetail!!.order_id.toString()
                    if (!TextUtils.isEmpty(text)) {
                        ClipboardUtils.copyText(text)
                        ToastUtils.showShort("Copy $text to clipboard success")
                    }
                }

            })

            mStages.clear()
            mStages.addAll(mOrderDetail!!.stages)

            mRepayAmountList.clear()
            for (stage in mOrderDetail!!.stages) {
                mRepayAmountList.add(stage.repay_total.toLong())
            }

            mRepaymentAdapter?.notifyDataSetChanged()
            mRepaymentDetailAdapter?.notifyDataSetChanged()
        }
    }

    fun repaymentLoan(orderId: String, amount: String) {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
            jsonObject.put("order_id", orderId) //订单ID
            jsonObject.put("amount", amount) //还款的金额（客户填写，整数）
            jsonObject.put("deep_link", Constant.DEEP_LINK)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " repayment Loan = $jsonObject")
        }
        showProgressDialogFragment()
        OkGo.post<String>(Api.ORDER_REPAY).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    dismissProgressDialogFragment()
                    val repay: RepayResponse? = NetworkUtils.checkResponseSuccess(
                        response,
                        RepayResponse::class.java
                    )
                    if (repay?.status == 0) {
                        ToastUtils.showShort("repay process fail")
                        if (parentFragment is HomeFragment) {
                            (parentFragment as HomeFragment).refreshData()
                        }
                    } else {
                        if (TextUtils.isEmpty(repay?.checkout_url)) {
                            ToastUtils.showShort("repay checkout url error")
                            return
                        }
                        context?.let {
                            WebViewActivity.launchWebView(it, repay!!.checkout_url!!, WebViewActivity.TYPE_REPAY)
                        }
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    dismissProgressDialogFragment()
                    ToastUtils.showShort("repay error")
                    try {
                        val content = response.body()
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " repaymentLoan = " + response.body())
                        }
                        LogSaver.logToFile(content)
                    } catch (e : Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e
                        }
                    }
                }
            })
    }
}