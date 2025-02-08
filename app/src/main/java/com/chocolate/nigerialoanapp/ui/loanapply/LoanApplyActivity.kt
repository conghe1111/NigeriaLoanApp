package com.chocolate.nigerialoanapp.ui.loanapply

import android.Manifest
import android.app.Activity
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
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.OrderApplyResponse
import com.chocolate.nigerialoanapp.bean.response.OrderCheekBean
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse
import com.chocolate.nigerialoanapp.bean.response.ProductTrialResponse.Trial
import com.chocolate.nigerialoanapp.bean.response.UploadAuthResponse
import com.chocolate.nigerialoanapp.collect.BaseCollectDataMgr
import com.chocolate.nigerialoanapp.collect.CollectDataMgr
import com.chocolate.nigerialoanapp.global.ConfigMgr
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.dialog.LoanDetailDialog
import com.chocolate.nigerialoanapp.ui.dialog.SelectAmountDialog
import com.chocolate.nigerialoanapp.ui.edit.EditInfoActivity
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyHistoryAdapter
import com.chocolate.nigerialoanapp.ui.loanapply.adapter.LoadApplyPeriodAdapter
import com.chocolate.nigerialoanapp.ui.mine.NorItemDecor2
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoanApplyActivity : BaseLoanApplyActivity() {

    companion object {

        private const val TAG = "LoanApplyActivity"

        const val REQUEST_CODE = 1112
        const val RESULT_CODE = 1117

        fun startActivity(context: Activity) {
            val intent =
                if (Constant.isAuditMode()) {
                    Intent(context, LoanApplyMockActivity::class.java)
                } else {
                    Intent(context, LoanApplyActivity::class.java)
                }
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private var tvAmount: AppCompatTextView? = null
    private var ivBack: AppCompatImageView? = null
    private var rvContent: RecyclerView? = null
    private var loanContainer: View? = null
    private var viewDisburseFee: View? = null
    private var rvContainer: RecyclerView? = null
    private var tvNext: AppCompatTextView? = null

    private var mAdapter: LoadApplyPeriodAdapter? = null
    private var mHistoryAdapter: LoadApplyHistoryAdapter? = null


    private var mTrialList: ArrayList<Trial> = ArrayList()
    private var hasRetention: Boolean = false
    private var mProductTrial: ProductTrialResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_apply)
        initialView()
        getProducts()
        ConfigMgr.getProfileInfo()
    }

    private fun initialView() {
        tvAmount = findViewById<AppCompatTextView>(R.id.tv_loan_apply_amount)
        ivBack = findViewById<AppCompatImageView>(R.id.iv_apply_info_back)
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
                requestProductTrial(
                    mProductType!!,
                    mAmountList[mAmountIndex].amount!!,
                    mPeriodList[mPeriodIndex]
                )
            }

        })
        rvContent?.adapter = mAdapter
        loanContainer?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (mAmountList.size == 0) {
                    return
                }
                val dialog = SelectAmountDialog(this@LoanApplyActivity, mAmountList, mAmountIndex)
                dialog.setOnItemClickListener(object : SelectAmountDialog.OnItemClickListener {
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
        val tvDesc = findViewById<AppCompatTextView>(R.id.tv_loan_apply_desc)
        rvContainer?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mHistoryAdapter = LoadApplyHistoryAdapter(mTrialList)
        rvContainer?.adapter = mHistoryAdapter
        rvContainer?.addItemDecoration(NorItemDecor2())
        ivBack?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                finish()
            }

        })
        tvNext?.isSelected = true
        tvNext?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                PermissionUtils.permission(
//                    Manifest.permission.READ_CALL_LOG,
//                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_SMS,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.READ_PHONE_STATE,
                ).callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        orderCheek()
                    }

                    override fun onDenied() {
                        ToastUtils.showShort("please allow permission for apply order.")
                    }
                }).request()
            }

        })
        SpanUtils.setPrivacyString(tvDesc, this@LoanApplyActivity)
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
        val amount = mAmountList[mAmountIndex].amount.toString()
        SpanUtils.setAmountString(tvAmount, SpanUtils.getShowText(amount.toLong()))
        if (mPeriodList.isEmpty() || mAmountList.isEmpty()) {
            return
        }
        if (mAmountIndex >= mAmountList.size) {
            return
        }
        if (mPeriodIndex >= mPeriodList.size) {
            return
        }
        requestProductTrial(
            mProductType!!,
            mAmountList[mAmountIndex].amount!!,
            mPeriodList[mPeriodIndex]
        )
    }

    private fun bindItem1(productTrial: ProductTrialResponse) {
        if (productTrial.trials == null || productTrial.trials.size == 0) {
            return
        }
        val trial = productTrial.trials[0]
        val container = viewDisburseFee
        val tvDisburalAmount = container?.findViewById<TextView>(R.id.tv_item_1_disbural_amount)
        val tvInterest = container?.findViewById<TextView>(R.id.tv_item_1_interest)
        val tvProcessFee = container?.findViewById<TextView>(R.id.tv_item_1_process_fee)
        val tvLoanAmount = container?.findViewById<TextView>(R.id.tv_item_1_loan_amount)

        tvDisburalAmount?.text = SpanUtils.getShowText1(trial?.disburse_amount?.toLong())
        tvInterest?.text = SpanUtils.getShowText1(trial?.interest?.toLong())
        tvProcessFee?.text = SpanUtils.getShowText1(trial?.service_fee?.toLong())
        tvLoanAmount?.text = SpanUtils.getShowText1(trial?.amount?.toLong())
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
                    val data = NetworkUtils.checkResponseSuccess2(response)
                    if (data == null) {
                        return
                    }
                    val orderCheekBean = com.alibaba.fastjson.JSONObject.parseObject(
                        data?.getBodyStr(),
                        OrderCheekBean::class.java
                    )
                    if (orderCheekBean == null) {
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

                        (111) -> {  //全部完成,申请
                            if (orderCheekBean.has_upload == 0) {
                                showLoanDetail(orderCheekBean)
                                CollectDataMgr.sInstance.collectAuthData(orderCheekBean.order_id.toString(), object : BaseCollectDataMgr.Observer {
                                    override fun success(response: UploadAuthResponse?) {
                                        if (orderCheekBean.order_id == 0L) {
                                            orderCheek()
                                        } else {
                                            showLoanDetail(orderCheekBean)
                                        }
                                    }

                                    override fun failure(response: String?) {
                                        ToastUtils.showShort("upload auth fail")
                                    }

                                })
                                return
                            }
                            if (orderCheekBean.order_id == 0L) {
                                ToastUtils.showShort("need loan apply orderId")
                            } else {
                                showLoanDetail(orderCheekBean)
                            }
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

    private var mDialog: LoanDetailDialog? = null
    private fun showLoanDetail(orderCheekBean: OrderCheekBean) {
        if (mDialog?.isShowing == true) {
            mDialog?.dismiss()
        }
        if (mDialog == null) {
            mDialog = LoanDetailDialog(this@LoanApplyActivity, mProductTrial)
        }
        mDialog?.setCallBack(object : LoanDetailDialog.CallBack() {
            override fun onClickAgree() {
                if (isFinishing || isDestroyed) {
                    return
                }
                orderApply(orderCheekBean.order_id.toString())
            }

        })
        mDialog?.show()

    }

    private fun orderApply(orderId: String) {
        if (mAmountIndex >= mAmountList.size) {
            return
        }
        if (mPeriodIndex >= mPeriodList.size) {
            return
        }
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
            jsonObject.put("order_id", orderId)
            jsonObject.put("amount", mAmountList[mAmountIndex])
            jsonObject.put("product_type", mProductType)
            jsonObject.put("period", mPeriodList[mPeriodIndex])
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkhttpClient orderCheek = ", jsonObject.toString())
        }
        OkGo.post<String>(Api.ORDER_APPLY).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    val orderApplyResponse =
                        checkResponseSuccess(response, OrderApplyResponse::class.java)
                    if (orderApplyResponse == null) {
                        return
                    }
                    if (orderApplyResponse.order_id == null || orderApplyResponse.order_id!! == 0L) {
                        return
                    }
                    if (orderApplyResponse.order_create == null || orderApplyResponse.order_create!! == 0L) {
                        return
                    }
                    ToastUtils.showShort("apply success")
                    setResult(RESULT_CODE)
                    finish()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                }
            })
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!hasRetention) {
            hasRetention = true
            showBackRetentionDialog()
            return
        }
        super.onBackPressed()
    }
}