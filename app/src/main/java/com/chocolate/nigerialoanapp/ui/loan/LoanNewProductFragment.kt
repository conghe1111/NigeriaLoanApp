package com.chocolate.nigerialoanapp.ui.loan

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.MarketingPageResponse
import com.chocolate.nigerialoanapp.bean.response.ProductBeanResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class LoanNewProductFragment : BaseLoanStatusFragment() {

    companion object {
        private const val TAG = "LoanNewProductFragment"
    }

    private var tvMaxAmount : AppCompatTextView? = null
    private var tvApplyNow : AppCompatTextView? = null
    private var tvDesc1 : AppCompatTextView? = null
    private var tvDesc2 : AppCompatTextView? = null

    private var mPageResponse : MarketingPageResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_new_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvMaxAmount = view.findViewById<AppCompatTextView>(R.id.tv_loan_max_amount)
        tvApplyNow = view.findViewById<AppCompatTextView>(R.id.tv_loan_apply_now)
        tvDesc1 = view.findViewById<AppCompatTextView>(R.id.tv_product_1_desc)
        tvDesc2 = view.findViewById<AppCompatTextView>(R.id.tv_product_2_desc)
        if( mPageResponse == null) {
            getMarketingPage()
        } else {
            bindData()
        }

        tvApplyNow?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {

            }

        })
    }

    private fun getMarketingPage() {
        //        pbLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " marketing page = $jsonObject")
        }
        OkGo.post<String>(Api.MARKETING_PAGE).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (isDestroy()) {
                        return
                    }
                    mPageResponse =
                        checkResponseSuccess(response, MarketingPageResponse::class.java)
                    if (mPageResponse == null) {
                        Log.e(TAG, " marketing page ." + response.body())
                        return
                    }
                    bindData()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " marketing page= " + response.body())
                    }
                }
            })
    }

    private fun getProducts(marketingFlag : Boolean) {
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
                    if (isDestroy()) {
                        return
                    }
                    val productBean: ProductBeanResponse? =
                        checkResponseSuccess(response, ProductBeanResponse::class.java)
                    if (productBean == null) {
                        Log.e(TAG, " marketing product ." + response.body())
                        return
                    }

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
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

    @SuppressLint("SetTextI18n")
    private fun bindData() {
        if (mPageResponse == null) {
            return
        }
        tvMaxAmount?.text = mPageResponse?.max_amount.toString()
        tvDesc1?.text = "₦" +mPageResponse?.min_amount.toString() + "\n" + "|" + "\n" + "₦" + mPageResponse?.max_amount
        tvDesc2?.text = resources.getString(R.string.up_to_day)
    }
}