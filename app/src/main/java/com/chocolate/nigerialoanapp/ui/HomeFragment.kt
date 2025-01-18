package com.chocolate.nigerialoanapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.bean.response.OrderDetailResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : BaseFragment() {

    companion object {
        const val TAG = "HomeFragment"
    }

    private var mOrderDetail : OrderDetailResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        orderDetail()
    }

    private fun initializeView() {

    }

    private fun orderDetail() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken) //FCM Token

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " orderDetail = " + jsonObject.toString())
        }
        OkGo.post<String>(Api.ORDER_DETAIL).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val orderDetail = checkResponseSuccess(response, OrderDetailResponse::class.java)
                    mOrderDetail = orderDetail
                    if (orderDetail == null) {
                        return
                    }
                    if (orderDetail.order_id == 0) {
                        // TODO 前往营销界面
                    } else {
                        //1	提交审核
                        //2	审核拒绝
                        //3	等待电核
                        //4	等待放款
                        //5	放款中
                        //6	放款失败
                        //7	等待还款
                        //8	已结清
                        //9	逾期
                        //10	还款中
                        orderDetail.check_status
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                }
            })
    }
}