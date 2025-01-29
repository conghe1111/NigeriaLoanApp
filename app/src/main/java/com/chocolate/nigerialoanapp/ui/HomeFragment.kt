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
import com.chocolate.nigerialoanapp.ui.loan.BaseLoanStatusFragment
import com.chocolate.nigerialoanapp.ui.loan.Loan0NewProductFragment
import com.chocolate.nigerialoanapp.ui.loan.Loan10PayProcessingFragment
import com.chocolate.nigerialoanapp.ui.loan.Loan1VerifyFragment
import com.chocolate.nigerialoanapp.ui.loan.Loan2DeclineFragment
import com.chocolate.nigerialoanapp.ui.loan.Loan5ProcessFragment
import com.chocolate.nigerialoanapp.ui.loan.Loan6PayFailureFragment
import com.chocolate.nigerialoanapp.ui.loan.Loan7PendingFragment
import com.chocolate.nigerialoanapp.ui.loan.Loan8PaidProcessedFragment
import com.chocolate.nigerialoanapp.ui.loan.Loan9OverdueFragment
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
    private var mCurFragment : BaseLoanStatusFragment? = null

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
                    if (orderDetail.order_id == null || orderDetail.order_id == 0L) {
                        mCurFragment = Loan0NewProductFragment()
                        toFragment(mCurFragment)
                    } else {
                        when(orderDetail.check_status) {
                            (1) -> {  //1	提交审核
                                mCurFragment = Loan1VerifyFragment()
                                toFragment(mCurFragment)
                            }
                            (2) -> {  //2	审核拒绝
                                mCurFragment = Loan2DeclineFragment()
                                toFragment(mCurFragment)
                            }
                            (3) -> {  //3	等待电核

                            }
                            (4) -> {  //4	等待放款
                                mCurFragment = Loan5ProcessFragment()
                                toFragment(mCurFragment)
                            }
                            (5) -> {  //5	放款中
                                mCurFragment = Loan5ProcessFragment()
                                toFragment(mCurFragment)
                            }
                            (6) -> {  //6	放款失败
                                mCurFragment = Loan6PayFailureFragment()
                                toFragment(mCurFragment)
                            }
                            (7) -> {  //7	等待还款
                                mCurFragment = Loan7PendingFragment()
                                toFragment(mCurFragment)
                            }
                            (8) -> {  //8	已结清
                                mCurFragment = Loan8PaidProcessedFragment()
                                toFragment(mCurFragment)
                            }
                            (9) -> {  //9	逾期
                                mCurFragment = Loan9OverdueFragment()
                                toFragment(mCurFragment)
                            }
                            (10) -> {  //10	还款中
                                mCurFragment = Loan10PayProcessingFragment()
                                toFragment(mCurFragment)
                            }
                        }
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

   private fun toFragment(fragment: BaseLoanStatusFragment?) {
        if (fragment != null) {
            val fragmentManager = childFragmentManager
            val transaction = fragmentManager.beginTransaction() // 开启一个事务
            transaction.replace(R.id.fl_home_container, fragment)
            transaction.commitAllowingStateLoss()
        }
    }

}