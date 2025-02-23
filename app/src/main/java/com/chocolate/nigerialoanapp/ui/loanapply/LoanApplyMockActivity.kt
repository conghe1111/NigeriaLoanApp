package com.chocolate.nigerialoanapp.ui.loanapply

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.data.LoanData
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse.Trial
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoanAmountMockAdapter
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyPeriodAdapter
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoanAmountDateMockAdapter
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoanStageAdapter
import com.chocolate.nigerialoanapp.ui.mine.NorItemDecor3
import com.chocolate.nigerialoanapp.ui.mine.NorItemDecor4
import com.chocolate.nigerialoanapp.ui.mine.NorMockItemDecor3
import com.chocolate.nigerialoanapp.utils.DateUtils
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoanApplyMockActivity : BaseLoanApplyActivity() {

    companion object {

        private const val TAG = "LoanApplyMockActivity"

    }
    private var ivBack : AppCompatImageView? = null

    private var rvAmount : RecyclerView? = null
    private var rvLoanTerm : RecyclerView? = null
    private var rvStage : RecyclerView? = null

    private var tvStage1 : AppCompatTextView? = null
    private var tvRepayment1 : AppCompatTextView? = null
    private var rvDate : RecyclerView? = null
    private var tvRepaymentAmount : AppCompatTextView? = null
    private var tvAmount : AppCompatTextView? = null
    private var tvInterest : AppCompatTextView? = null
    private var tvDisburalAmount : AppCompatTextView? = null
    private var tvFee : AppCompatTextView? = null
    private var tvMockNext : AppCompatTextView? = null
    private var stage1View: View? = null
    private var llStageLoadingError: View? = null

    private var mAmountAdapter : LoanAmountMockAdapter? = null
    private var mTermAdapter : LoanAmountMockAdapter? = null
    private var mDateAdapter : LoanAmountDateMockAdapter? = null

    private var mLoanAmountList: ArrayList<LoanData> = ArrayList<LoanData>()
    private var mLoanTermList: ArrayList<LoanData> = ArrayList<LoanData>()
    private var mLoanDateList: ArrayList<LoanData> = ArrayList<LoanData>()

    private var mLoanStageList: ArrayList<Trial> = ArrayList<Trial>()

    private var mSelectDatePos : Int = 0
    private var mStageAdapter : LoanStageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply_mock)
        initialView()
        getProducts()
    }

    private fun initialView() {
        ivBack = findViewById<AppCompatImageView>(R.id.iv_back)
        ivBack?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                finish()
            }

        })

        rvAmount = findViewById<RecyclerView>(R.id.rv_loan_amount_mock)
        rvLoanTerm = findViewById<RecyclerView>(R.id.rv_loan_term)
        rvStage = findViewById<RecyclerView>(R.id.rv_mock_stage)
        tvMockNext = findViewById<AppCompatTextView>(R.id.tv_loan_apply_mock_next)
        stage1View = findViewById<View>(R.id.stage_1)
        llStageLoadingError = findViewById<View>(R.id.ll_loading_error)
        val ivConsumer = findViewById<View>(R.id.iv_consumer)
        ivConsumer?.visibility = View.GONE

        rvAmount?.layoutManager = GridLayoutManager(this@LoanApplyMockActivity,2)
        mAmountAdapter = LoanAmountMockAdapter(mLoanAmountList)
        mAmountAdapter?.setOnItemClickListener(object : LoadApplyPeriodAdapter.OnItemClickListener{
            override fun onItemClick(period: String, pos: Int) {
                mAmountIndex = pos
                requestProductTrial()
            }

        })
        rvAmount?.adapter = mAmountAdapter
        rvAmount?.addItemDecoration(NorMockItemDecor3())

        rvLoanTerm?.layoutManager = GridLayoutManager(this@LoanApplyMockActivity,2)
        mTermAdapter = LoanAmountMockAdapter(mLoanTermList, true)
        mTermAdapter?.setOnItemClickListener(object : LoadApplyPeriodAdapter.OnItemClickListener{
            override fun onItemClick(period: String, pos: Int) {
                mPeriodIndex = pos
                requestProductTrial()
            }

        })
        rvLoanTerm?.adapter = mTermAdapter
        rvLoanTerm?.addItemDecoration(NorMockItemDecor3())

        initialStage1()

        rvStage?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mStageAdapter = LoanStageAdapter(mLoanStageList)
        rvStage?.adapter = mStageAdapter
        rvStage?.addItemDecoration(NorItemDecor4())
        tvMockNext?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                finish()
            }

        })
    }

    private fun initialStage1() {
        tvStage1 = findViewById<AppCompatTextView>(R.id.tv_stage_1)
        tvRepayment1 = findViewById<AppCompatTextView>(R.id.tv_repayment_1)
        rvDate = findViewById<RecyclerView>(R.id.rv_repayment_date)
        tvRepaymentAmount = findViewById<AppCompatTextView>(R.id.tv_repayment_amount)
        tvAmount = findViewById<AppCompatTextView>(R.id.tv_loan_amount)
        tvInterest = findViewById<AppCompatTextView>(R.id.tv_interest)
        tvDisburalAmount = findViewById<AppCompatTextView>(R.id.tv_disbural_amount)
        tvFee = findViewById<AppCompatTextView>(R.id.tv_service_fee)

        rvDate?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mDateAdapter = LoanAmountDateMockAdapter(mLoanDateList, mSelectDatePos)
        rvDate?.adapter = mDateAdapter
        rvDate?.addItemDecoration(NorItemDecor4())
    }

    override fun bindData() {
        super.bindData()
        initData()
        bindDataInternal()
    }

    private fun bindDataInternal() {
        mAmountAdapter?.notifyDataSetChanged()
        mTermAdapter?.notifyDataSetChanged()
    }

    private fun initData() {
        mLoanAmountList.clear()
        mLoanAmountList.addAll(mAmountList)
        mLoanAmountList.add(LoanData("100000"))

        mLoanTermList.clear()
        for (period in mPeriodList) {
            mLoanTermList.add(period)
        }
        mLoanTermList.add(LoanData("180"))
        requestProductTrial()
    }

    private fun requestProductTrial() {
        if (mAmountList.size > mAmountIndex && mPeriodList.size > mPeriodIndex) {
            executeRequestProductTrial(mAmountList[mAmountIndex].data!!, mLoanTermList[mPeriodIndex].data!!,mProductType!!)
        } else {
            // TODO error
        }
    }

    private fun executeRequestProductTrial(amount: String, period: String, productType : String = "5") {
        showOrHideLoading(true)
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
                    showOrHideLoading(false)
                    if (productTrial == null || productTrial.trials == null || productTrial.trials.size == 0) {
                        stage1View?.visibility = View.GONE
                        llStageLoadingError?.visibility = View.VISIBLE
                        return
                    } else {
                        stage1View?.visibility = View.VISIBLE
                        llStageLoadingError?.visibility = View.GONE
                    }
                    val firstTrial = productTrial.trials.removeAt(0)
                    bindFirstItem(firstTrial, productTrial.trials)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    showOrHideLoading(false)
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun bindFirstItem(firstTrail: Trial, trials: List<Trial>) {
        tvStage1?.text = resources.getString(R.string.stage_x_x, "1",
            (trials.size + 1).toString())
        tvRepayment1?.text = "" + firstTrail.repay_date
        if (BuildConfig.DEBUG) {
            mLoanDateList.clear()
            val array = DateUtils.getDateArray(firstTrail.repay_date_time)
            for (index in 0 until array.size) {
                if (index <= 1){
                    mLoanDateList.add(LoanData(array[index], false))
                } else {
                    mLoanDateList.add(LoanData(array[index], true))
                }
            }
        }

        mDateAdapter?.notifyDataSetChanged()
        tvRepaymentAmount = findViewById<AppCompatTextView>(R.id.tv_repayment_amount)
        tvAmount = findViewById<AppCompatTextView>(R.id.tv_loan_amount)
        tvInterest = findViewById<AppCompatTextView>(R.id.tv_interest)
        tvDisburalAmount = findViewById<AppCompatTextView>(R.id.tv_disbural_amount)
        tvFee = findViewById<AppCompatTextView>(R.id.tv_service_fee)

        tvRepaymentAmount?.text = SpanUtils.getShowText2(firstTrail.total.toLong())
        tvAmount?.text = SpanUtils.getShowText2(firstTrail.amount.toLong())
        tvInterest?.text = SpanUtils.getShowText2(firstTrail.interest.toLong())
        tvDisburalAmount?.text = SpanUtils.getShowText2(firstTrail.disburse_amount.toLong())
        tvFee?.text = SpanUtils.getShowText2(firstTrail.service_fee.toLong())

        mLoanStageList.clear()
        mLoanStageList.addAll(trials)
        mStageAdapter?.notifyDataSetChanged()
    }
}