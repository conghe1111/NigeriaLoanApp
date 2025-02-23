package com.chocolate.nigerialoanapp.ui.loan

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.ui.graphics.Color
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.response.MarketingPageResponse
import com.chocolate.nigerialoanapp.bean.response.OrderCheekBean
import com.chocolate.nigerialoanapp.bean.response.UploadAuthResponse
import com.chocolate.nigerialoanapp.collect.BaseCollectDataMgr
import com.chocolate.nigerialoanapp.collect.CollectDataMgr
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.MarketActivity
import com.chocolate.nigerialoanapp.ui.edit.EditInfoActivity
import com.chocolate.nigerialoanapp.ui.loanapply.LoanApplyActivity
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class Loan0NewProductFragment : BaseLoanStatusFragment() {

    companion object {
        private const val TAG = "LoanNewProductFragment"
    }

    private var tvMaxAmount : AppCompatTextView? = null
    private var tvApplyNow : AppCompatTextView? = null
    private var tvDescLeft1 : AppCompatTextView? = null
    private var tvDescLeft3 : AppCompatTextView? = null
    private var tvDesc2 : AppCompatTextView? = null
    private var flLoading : View? = null

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
        tvDescLeft1 = view.findViewById<AppCompatTextView>(R.id.tv_product_left_desc1)
        tvDescLeft3 = view.findViewById<AppCompatTextView>(R.id.tv_product_left_desc3)
        tvDesc2 = view.findViewById<AppCompatTextView>(R.id.tv_product_2_desc)
        flLoading = view.findViewById<View>(R.id.fl_loading)
        if (activity is MarketActivity) {
            flLoading?.setBackgroundColor(resources.getColor(android.R.color.transparent))
            flLoading?.isClickable = false
        }
        if(mPageResponse == null) {
            bindData(0,0, 0)
            if (activity is MarketActivity) {
                (activity as MarketActivity).checkNetWork(object : BaseActivity.CallBack {
                    override fun onSuccess() {
                        if (isDestroy()) {
                            return
                        }
                        getMarketingPage()
                    }

                    override fun onFailure() {

                    }

                })
            } else {
                getMarketingPage()
            }

        } else {
            bindData(mPageResponse!!.max_amount, mPageResponse!!.min_amount, mPageResponse!!.max_period)
        }

        tvApplyNow?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                activity?.let {
                    if (it is MarketActivity) {
                        FirebaseUtils.logEvent("CLICK_HOMEPAGE_APPLY")
                        it.toLogin()
                    } else {
                        FirebaseUtils.logEvent("CLICK_INDEX_APPLY")
                        orderCheek()
                    }
                }
            }

        })
        FirebaseUtils.logEvent("SYSTEM_INDEX_ENTER")
    }

    private fun getMarketingPage() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " marketing page = $jsonObject")
        }
        showOrHide(true)
        OkGo.post<String>(Api.MARKETING_PAGE).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    showOrHide(false)
                    mPageResponse =
                        checkResponseSuccess(response, MarketingPageResponse::class.java)
                    if (mPageResponse == null) {
                        Log.e(TAG, " marketing page ." + response.body())
                        return
                    }
                    if (mPageResponse == null) {
                        return
                    }
                    bindData(mPageResponse!!.max_amount, mPageResponse!!.min_amount, mPageResponse!!.max_period)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    showOrHide(false)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " marketing page= " + response.body())
                    }
                }
            })
    }


    @SuppressLint("SetTextI18n")
    private fun bindData(maxV : Int, minV : Int, maxPeriod : Int) {
        SpanUtils.setAmountString2(tvMaxAmount,  SpanUtils.getShowText(maxV.toLong()))
        val min = minV
        val max = maxV
        if (min != null && max != null) {
            try {
                val minStr = "₦" +SpanUtils.getShowText(min!!.toLong())
                val maxStr = "₦" +SpanUtils.getShowText(max!!.toLong())
                tvDescLeft1?.text = minStr
                tvDescLeft3?.text = maxStr
            } catch (e : Exception) {
                if (BuildConfig.DEBUG) {
                    throw e
                }
            }
        }
        tvDesc2?.text = resources.getString(R.string.up_to_day,maxPeriod.toString())
    }

    private fun showOrHide(showFlag : Boolean) {
        flLoading?.visibility = if (showFlag) View.VISIBLE else View.GONE
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
            Log.i("OkhttpClient", " orderCheek = $jsonObject")
        }
        showProgressDialogFragment()
        OkGo.post<String>(Api.ORDER_CHECK).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy() || activity == null) {
                        return
                    }
                    val data = NetworkUtils.checkResponseSuccess2(response)
                    if (data == null) {
                        dismissProgressDialogFragment()
                        return
                    }
                    val orderCheekBean = com.alibaba.fastjson.JSONObject.parseObject(
                        data?.getBodyStr(),
                        OrderCheekBean::class.java
                    )
                    if (orderCheekBean == null) {
                        dismissProgressDialogFragment()
                        return
                    }
                    when (orderCheekBean.current_phase) {
                        (101) -> {  //基本信息填写完成（第一页）
                            dismissProgressDialogFragment()
                            EditInfoActivity.showActivity(
                                activity!!, EditInfoActivity.STEP_1,
                                EditInfoActivity.FROM_APPLY_LOAD
                            )
                        }

                        (102) -> {  //工作信息填写完成（第二页）
                            dismissProgressDialogFragment()
                            EditInfoActivity.showActivity(
                                activity!!, EditInfoActivity.STEP_2,
                                EditInfoActivity.FROM_APPLY_LOAD
                            )
                        }

                        (103) -> {  //联系人信息填写完成（第三页）
                            dismissProgressDialogFragment()
                            EditInfoActivity.showActivity(
                                activity!!, EditInfoActivity.STEP_3,
                                EditInfoActivity.FROM_APPLY_LOAD
                            )
                        }

                        (104) -> {  //收款信息填写完成（第四页）
                            dismissProgressDialogFragment()
                            EditInfoActivity.showActivity(
                                activity!!, EditInfoActivity.STEP_4,
                                EditInfoActivity.FROM_APPLY_LOAD
                            )
                        }
                        (105) -> {  //收款信息填写完成（第五页）
                            dismissProgressDialogFragment()
                            EditInfoActivity.showActivity(
                                activity!!, EditInfoActivity.STEP_5,
                                EditInfoActivity.FROM_APPLY_LOAD
                            )
                        }

                        (0), (111) -> {  //全部完成,申请
                            executeNextOrderCheckStep(orderCheekBean)
                        }
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    dismissProgressDialogFragment()
                    ToastUtils.showShort("order check error")
                }
            })
    }

    private fun executeNextOrderCheckStep(orderCheekBean : OrderCheekBean) {
        if (orderCheekBean.has_upload == 0 && orderCheekBean.order_id == 0L) {
            CollectDataMgr.sInstance.collectAuthData(object : BaseCollectDataMgr.Observer {
                override fun success(response: UploadAuthResponse?) {
                    if (orderCheekBean.order_id == 0L) {
                        orderCheek()
                    } else {
                        dismissProgressDialogFragment()
                        toLoanApplyPage(orderCheekBean.order_id)
                    }
                }

                override fun failure(response: String?) {
                    dismissProgressDialogFragment()
                    ToastUtils.showShort("upload auth fail")
                }

            })
            return
        } else {
            if (orderCheekBean.order_id == 0L) {
                dismissProgressDialogFragment()
                ToastUtils.showShort("need loan apply orderId")
            } else {
                toLoanApplyPage(orderCheekBean.order_id)
            }
        }
    }

    private fun toLoanApplyPage(orderId : Long) {
        if (isDestroy()) {
            return
        }
        dismissProgressDialogFragment()
        activity?.let {
            LoanApplyActivity.startActivity(it, orderId.toString())
        }
    }

    override fun onClickConsumer() {
        if (activity is MarketActivity) {
            FirebaseUtils.logEvent("CLICK_HOMEPAGE_CUSTOMERSERVICE")
        } else {
            super.onClickConsumer()
        }
    }

}