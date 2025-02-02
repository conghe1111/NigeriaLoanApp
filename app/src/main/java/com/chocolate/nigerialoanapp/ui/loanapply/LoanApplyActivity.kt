package com.chocolate.nigerialoanapp.ui.loanapply

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.OrderCheekBean
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse.Trial
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.dialog.SelectAmountDialog
import com.chocolate.nigerialoanapp.ui.edit.EditInfoActivity
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyHistoryAdapter
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyPeriodAdapter
import com.chocolate.nigerialoanapp.ui.mine.NorItemDecor2
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
    private var ivBack: AppCompatImageView? = null
    private var rvContent: RecyclerView? = null
    private var loanContainer: View? = null
    private var viewDisburseFee : View? = null
    private var rvContainer : RecyclerView? = null
    private var tvNext : AppCompatTextView? = null

    private var mAdapter: LoadApplyPeriodAdapter? = null
    private var mHistoryAdapter: LoadApplyHistoryAdapter? = null

    private var mAmountIndex: Int = 0
    private var mPeriodIndex: Int = 0
    private var mTrialList: ArrayList<Trial> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        initialView()
        getProducts()
    }

    private fun initialView() {
        tvAmount = findViewById<AppCompatTextView>(R.id.tv_loan_apply_amount)
        ivBack= findViewById<AppCompatImageView>(R.id.iv_apply_info_back)
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
                requestProductTrial(mProductType!!, mAmountList[mAmountIndex], mPeriodList[mPeriodIndex])
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
        rvContainer = findViewById<RecyclerView>(R.id.rv_container_schedule)
        tvNext = findViewById<AppCompatTextView>(R.id.tv_loan_apply_next)
        rvContainer?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mHistoryAdapter = LoadApplyHistoryAdapter(mTrialList)
        rvContainer?.adapter = mHistoryAdapter
        rvContainer?.addItemDecoration(NorItemDecor2())
        ivBack?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                finish()
            }

        })
        tvNext?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                orderCheek()
            }

        })
    }

    private fun requestProductTrial(productType: String, amount: String, period: String) {
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
                    if (productTrial == null || productTrial.trials == null) {
                        return
                    }
                    bindItem1(productTrial)
                    mTrialList.clear()
                    mTrialList.addAll(productTrial.trials)
                    mHistoryAdapter?.notifyDataSetChanged()

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
        if (mPeriodList.isEmpty() || mAmountList.isEmpty()) {
            return
        }
        if (mAmountIndex >= mAmountList.size) {
            return
        }
        if (mPeriodIndex >= mPeriodList.size) {
            return
        }
        requestProductTrial(mProductType!!, mAmountList[mAmountIndex], mPeriodList[mPeriodIndex])
    }

    private fun bindItem1(productTrial : ProductTrialResponse) {
        if (productTrial.trials == null || productTrial.trials.size == 0) {
            return
        }
        val trial = productTrial.trials[0]
        val container = viewDisburseFee
        val tvDisburalAmount = container?.findViewById<TextView>(R.id.tv_item_1_disbural_amount)
        val tvInterest = container?.findViewById<TextView>(R.id.tv_item_1_interest)
        val tvProcessFee = container?.findViewById<TextView>(R.id.tv_item_1_process_fee)
        val tvLoanAmount = container?.findViewById<TextView>(R.id.tv_item_1_loan_amount)

        tvDisburalAmount?.text = trial?.disburse_amount.toString()
        tvInterest?.text = trial?.interest.toString()
        tvProcessFee?.text = trial?.service_fee.toString()
        tvLoanAmount?.text = trial?.amount.toString()
    }

    private fun orderCheek() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkhttpClient orderCheek = ", jsonObject.toString())
        }
        OkGo.post<String>(Api.ORDER_CHECK).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    val orderCheekBean =
                        checkResponseSuccess(response, OrderCheekBean::class.java)
                    if (orderCheekBean == null) {
                        return
                    }
                    if (orderCheekBean.order_id == 0) {
                        return
                    }
                    when (orderCheekBean.next_phase) {
                        (101) -> {  //基本信息填写完成（第一页）
                            EditInfoActivity.showActivity(
                                this@LoanApplyActivity, EditInfoActivity.STEP_1,
                                EditInfoActivity.FROM_APPLY_LOAD
                            )
                        }

                        (102) -> {  //工作信息填写完成（第二页）
                            EditInfoActivity.showActivity(
                                this@LoanApplyActivity, EditInfoActivity.STEP_2,
                                EditInfoActivity.FROM_APPLY_LOAD
                            )
                        }

                        (103) -> {  //联系人信息填写完成（第三页）
                            EditInfoActivity.showActivity(
                                this@LoanApplyActivity, EditInfoActivity.STEP_3,
                                EditInfoActivity.FROM_APPLY_LOAD
                            )
                        }

                        (104) -> {  //收款信息填写完成（第四页）
                            EditInfoActivity.showActivity(
                                this@LoanApplyActivity, EditInfoActivity.STEP_4,
                                EditInfoActivity.FROM_APPLY_LOAD
                            )
                        }
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
}