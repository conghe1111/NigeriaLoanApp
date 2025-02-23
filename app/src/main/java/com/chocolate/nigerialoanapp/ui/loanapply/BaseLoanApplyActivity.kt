package com.chocolate.nigerialoanapp.ui.loanapply

import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.BarUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.data.LoanData
import com.chocolate.nigerialoanapp.bean.response.OrderCheekBean
import com.chocolate.nigerialoanapp.bean.response.ProductBeanResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.dialog.LoanRetentionDialog
import com.chocolate.nigerialoanapp.ui.loanapply.LoanApplyActivity.Companion.KEY_ORDER_ID
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

abstract class BaseLoanApplyActivity : BaseActivity() {

    companion object {

        private const val TAG = "BaseLoanApplyActivity"

    }

    var mProductType: String? = null
    var mPeriodList: ArrayList<LoanData> = ArrayList<LoanData>()
    var mAmountList: ArrayList<LoanData> = ArrayList<LoanData>()
    var mAmountIndex: Int = 0
    var mPeriodIndex: Int = 0
    var mOrderId: String = ""

    private var dialog : LoanRetentionDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
         intent.getStringExtra(KEY_ORDER_ID)?.let {
            mOrderId = it
        }
        FirebaseUtils.logEvent("SYSTEM_LOAN_ENTER")
    }

    fun getProducts() {
        if (Constant.mAccountId == null) {
            return
        }
        showOrHideLoading(true)
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.e("okHttpClient", " marketing product = $jsonObject")
        }
        OkGo.post<String>(Api.PRODUCT_LIST).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    showOrHideLoading(false)
                    val productBean: ProductBeanResponse? =
                        checkResponseSuccess(response, ProductBeanResponse::class.java)
                    if (productBean == null || productBean.product == null) {
                        Log.e(TAG, " marketing product ." + response.body())
                        return
                    }
                    mProductType = productBean.product.product_type.toString()
                    mAmountList.clear()
                    for (amountItem in productBean.product.amount) {
                        mAmountList.add(LoanData(amountItem.toString(),false))
                    }

                    mPeriodList.clear()
                    for (periodItem in productBean.product.period) {
                        mPeriodList.add(LoanData(periodItem.toString(), false))
                    }
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    showOrHideLoading(false)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update contact = " + response.body())
                    }
                }
            })
    }

    open fun bindData() {
        FirebaseUtils.logEvent("SYSTEM_LOAN_LOAD")
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }

    fun showBackRetentionDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        if (dialog == null) {
            dialog = LoanRetentionDialog(this)
        }
        dialog?.setOnItemClickListener(object : LoanRetentionDialog.OnItemClickListener() {
            override fun onClickAgree() {

            }

            override fun onClickCancel() {
                super.onClickCancel()
                finish()
            }
        })
        dialog?.show()
    }

    fun showOrHideLoading(showFlag : Boolean) {
        if (showFlag) {
            showProgressDialogFragment()
        } else {
            dismissProgressDialogFragment()
        }
    }

    override fun useLogout(): Boolean {
        return true
    }
}