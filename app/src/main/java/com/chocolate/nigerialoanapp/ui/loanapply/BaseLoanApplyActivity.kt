package com.chocolate.nigerialoanapp.ui.loanapply

import android.util.Log
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.response.ProductBeanResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
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
    var mAmountList: ArrayList<String> = ArrayList<String>()

    fun getProducts(marketingFlag: Boolean = false) {
        if (Constant.mAccountId == null) {
            return
        }
        //        pbLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId!!)
            jsonObject.put("access_token", Constant.mToken)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " marketing product = $jsonObject")
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
                        return
                    }
                    productBean.product?.let {

                        mProductType = it.product_type.toString()
                        mAmountList.clear()
                        for (amountItem in it.amount) {
                            mAmountList.add(amountItem.toString())
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
}