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
    var mPeriodList: ArrayList<String> = ArrayList<String>()
    var mAmountList: ArrayList<LoanData> = ArrayList<LoanData>()
    var mAmountIndex: Int = 0
    var mPeriodIndex: Int = 0

    private var dialog : LoanRetentionDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
    }

    fun getProducts(marketingFlag: Boolean = false) {
        if (Constant.mAccountId == null) {
            return
        }
        showOrHideLoading(true)
        //        pbLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            if (marketingFlag) {
//                jsonObject.put("account_id", Constant.mAccountId!!.toLong())
            } else {
//                jsonObject.put("account_id", Constant.mAccountId)
            }
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.e("okHttpClient", " marketing product = $jsonObject")
        }
        val url = if (marketingFlag) Api.PRODUCT_MARKETING else Api.PRODUCT_LIST
        OkGo.post<String>(url).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    val productBean: ProductBeanResponse? =
                        checkResponseSuccess(response, ProductBeanResponse::class.java)
                    if (productBean == null) {
                        Log.e(TAG, " marketing product ." + response.body())
                        showOrHideLoading(false)
                        return
                    }
                    productBean.product?.let {

                        mProductType = it.product_type.toString()
                        mAmountList.clear()
                        for (amountItem in it.amount) {
                            mAmountList.add(LoanData(amountItem.toString(),false))
                        }

                        mPeriodList.clear()
                        for (periodItem in it.period) {
                            mPeriodList.add(periodItem.toString())
                        }
                    }
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    showOrHideLoading(false)
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update contact = " + response.body())
                    }
                }
            })
    }

    open fun bindData() {

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

        })
        dialog?.show()
    }

    abstract fun showOrHideLoading(showFlag : Boolean)
}