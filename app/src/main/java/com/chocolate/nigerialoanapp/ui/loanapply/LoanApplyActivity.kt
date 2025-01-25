package com.chocolate.nigerialoanapp.ui.loanapply

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.dialog.SelectAmountDialog
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyPeriodAdapter
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoanApplyActivity : BaseLoanApplyActivity() {

    companion object {

        private const val TAG = "LoanApplyActivity"

        fun startActivity(context: Context) {
            val intent =
                if (Constant.isAuditMode()) {
                    Intent(context, LoanApplyMockActivity::class.java)
                } else {
                    Intent(context, LoanApplyActivity::class.java)
                }
            context.startActivity(intent)
        }
    }

    private var tvAmount: AppCompatTextView? = null
    private var rvContent: RecyclerView? = null
    private var loanContainer: View? = null
    private var viewDisburseFee : View? = null
    private var viewItemSchedule : View? = null
    private var mAdapter: LoadApplyPeriodAdapter? = null

    private var mAmountIndex: Int = 0
    private var mPeriodIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        initialView()
        getProducts()
    }

    private fun initialView() {
        tvAmount = findViewById<AppCompatTextView>(R.id.tv_loan_apply_amount)
        loanContainer = findViewById<View>(R.id.fl_loan_apply_container)
        rvContent = findViewById<RecyclerView>(R.id.rv_repayment_term)

        rvContent?.layoutManager =
            LinearLayoutManager(this@LoanApplyActivity, LinearLayoutManager.HORIZONTAL, false)
        mAdapter = LoadApplyPeriodAdapter(mPeriodList)
        mAdapter?.setOnItemClickListener(object : LoadApplyPeriodAdapter.OnItemClickListener {
            override fun onItemClick(period: String, pos: Int) {
                if (TextUtils.isEmpty(mProductType)) {
                    return
                }
                if (mPeriodList.isEmpty() || mAmountList.isEmpty()) {
                    return
                }
                if (mAmountIndex >= mAmountList.size) {
                    return
                }
                mPeriodIndex = pos
                if (mPeriodIndex >= mPeriodList.size) {
                    return
                }
                productTrial(mProductType!!, mAmountList[mAmountIndex], mPeriodList[mPeriodIndex])
            }

        })
        rvContent?.adapter = mAdapter
        loanContainer?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val dialog = SelectAmountDialog(this@LoanApplyActivity, mAmountList)
                dialog.setOnItemClickListener(object : SelectAmountDialog.OnItemClickListener() {
                    override fun onItemClick(str: String, pos: Int) {
                        mAmountIndex = pos
                    }

                })
                dialog.show()
            }

        })
        viewDisburseFee = findViewById<View>(R.id.container_disburse_fee)
        viewItemSchedule = findViewById<View>(R.id.container_item_schedule)

    }

    private fun productTrial(productType: String, amount: String, period: String) {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
            jsonObject.put("product_type", productType) //产品类型：1.首贷  2. 复贷  3. 展期  4. Google合规 5 营销
            jsonObject.put("amount", amount)   //产品金额
            jsonObject.put("period", period)   //产品期限
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkhttpClient", jsonObject.toString())
        }
        OkGo.post<String>(Api.PRODUCT_TRIAL).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    val productTrial =
                        checkResponseSuccess(response, ProductTrialResponse::class.java)
                    if (productTrial == null) {
                        return
                    }
                    bindItem1(productTrial)
                    bindItem2(productTrial)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                }
            })
    }

    override fun bindData() {
        super.bindData()
        if (mPeriodList.isNotEmpty()) {
            mAdapter?.notifyDataSetChanged()
        }
        tvAmount?.text = mAmountList[mAmountIndex].toString()
    }

    private fun bindItem1(productTrial : ProductTrialResponse) {
        val container = viewDisburseFee
        val tvDisburalAmount = container?.findViewById<TextView>(R.id.tv_item_1_disbural_amount)
        val tvInterest = container?.findViewById<TextView>(R.id.tv_item_1_interest)
        val tvProcessFee = container?.findViewById<TextView>(R.id.tv_item_1_process_fee)
        val tvLoanAmount = container?.findViewById<TextView>(R.id.tv_item_1_loan_amount)

        tvDisburalAmount?.text = productTrial?.disburse_amount.toString()
        tvInterest?.text = productTrial?.interest.toString()
        tvProcessFee?.text = productTrial?.service_fee.toString()
        tvLoanAmount?.text = productTrial?.amount.toString()
    }

    private fun bindItem2(productTrial : ProductTrialResponse) {
        val container = viewItemSchedule
        val tvDueDay = container?.findViewById<TextView>(R.id.tv_item_1_due_day)
        val tvDueAmount = container?.findViewById<TextView>(R.id.tv_item_1_due_amount)

        tvDueDay?.text = productTrial?.repay_date.toString()
        tvDueAmount?.text = productTrial?.amount.toString()
    }
}