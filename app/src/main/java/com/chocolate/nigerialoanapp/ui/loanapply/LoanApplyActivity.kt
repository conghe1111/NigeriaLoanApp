package com.chocolate.nigerialoanapp.ui.loanapply

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyPeriodAdapter
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
    private var mAdapter: LoadApplyPeriodAdapter? = null

    private var mAmountIndex : Int = 0
    private var mPeriodIndex : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        initialView()
        getProducts()
    }

    private fun initialView() {
        tvAmount = findViewById<AppCompatTextView>(R.id.tv_loan_apply_amount)
        rvContent = findViewById<RecyclerView>(R.id.rv_repayment_term)

        rvContent?.layoutManager =
            LinearLayoutManager(this@LoanApplyActivity, LinearLayoutManager.HORIZONTAL, false)
        mAdapter = LoadApplyPeriodAdapter(mPeriodList)
        mAdapter?.setOnItemClickListener(object : LoadApplyPeriodAdapter.OnItemClickListener {
            override fun onItemClick(period: String, pos: Int) {
                if (TextUtils.isEmpty(mProductType)){
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
       if (mPeriodList.isNotEmpty()){
           mAdapter?.notifyDataSetChanged()
       }

        mAmountList
    }
}