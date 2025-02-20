package com.chocolate.nigerialoanapp.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.response.HistoryRecordResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class HistoryRecordActivity : BaseActivity() {

    companion object {
        const val TAG = "HistoryRecordActivity"

        fun startActivity(context: Context) {
            val intent = Intent(context, HistoryRecordActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var rvRecord : RecyclerView? = null
    private var flNoRecord : View? = null
    private val mList : ArrayList<HistoryRecordResponse.History> = ArrayList()

    private var mHistoryAdapter : HistoryRecordAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_history_record)
        initializeView()
    }

    private fun initializeView() {
        initializeTitle()
        rvRecord = findViewById<RecyclerView>(R.id.rv_history_record)
        flNoRecord = findViewById<View>(R.id.fl_no_record)
        rvRecord?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mHistoryAdapter = HistoryRecordAdapter(mList)
        rvRecord?.adapter = mHistoryAdapter
        getOrderHistory()
        updateList()
    }

    override fun getTitleStr(): String {
        return resources.getString(R.string.setting_history_record)
    }

    private fun getOrderHistory() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken) //FCM Token

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " getOrderHistory = $jsonObject")
        }
        showProgressDialogFragment()
        OkGo.post<String>(Api.ORDER_HISTORY).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    dismissProgressDialogFragment()
                    val historyResponse: HistoryRecordResponse? = NetworkUtils.checkResponseSuccess(
                        response,
                        HistoryRecordResponse::class.java
                    )
                    historyResponse?.order_history?.let {
                        mList.clear()
                        mList.addAll(it)
                        updateList()
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    dismissProgressDialogFragment()
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update contact = " + response.body())
                    }
                }
            })
    }

    private fun updateList() {
        if (mList.size == 0) {
            rvRecord?.visibility = View.GONE
            flNoRecord?.visibility = View.VISIBLE
        } else {
            mHistoryAdapter?.notifyDataSetChanged()
            rvRecord?.visibility = View.VISIBLE
            flNoRecord?.visibility = View.GONE
        }
    }
}