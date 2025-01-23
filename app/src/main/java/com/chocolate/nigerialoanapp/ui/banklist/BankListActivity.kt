package com.chocolate.nigerialoanapp.ui.banklist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.response.BankBeanResponse
import com.chocolate.nigerialoanapp.global.ConfigMgr
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.util.Collections

class BankListActivity : BaseActivity() {

    companion object {
        private const val TAG = "BankListActivity"
        const val START_REQUEST_CODE = 1113
        const val START_RESULT_CODE = 1112
        const val KEY_BANK_NAME = "key_bank_name"
        const val KEY_BANK_CODE = "key_bank_code"

        fun startActivityResult(context: Activity) {
            val intent = Intent(context, BankListActivity::class.java)
            context.startActivityForResult(intent, START_REQUEST_CODE)
        }
    }

    private var rvBankList: RecyclerView? = null
    private var ivBack: ImageView? = null
    private val mBankList = ArrayList<BankBeanResponse.Bank>()
    private var mAdapter: BankListAdapter? = null
    private var sideBar: WaveSideBar? = null
    private var flLoading: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.theme_color))
        BarUtils.setStatusBarLightMode(this, false)
        setContentView(R.layout.activity_bank_list)
        initializeView()
        if (ConfigMgr.mBankList.size == 0) {
            getBankList()
        } else {
            mBankList.clear()
            mBankList.addAll(ConfigMgr.mBankList)
            updateList()
        }
    }

    private fun initializeView() {
        rvBankList = findViewById(R.id.rv_bank_list)
        ivBack = findViewById(R.id.iv_bank_list_back)
        sideBar = findViewById(R.id.sidebar_bank_list)
        flLoading = findViewById(R.id.fl_banklist_loading)
        ivBack?.setOnClickListener(View.OnClickListener { finish() })

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvBankList?.layoutManager = manager
        mAdapter = BankListAdapter(mBankList)
        mAdapter?.setOnItemClickListener(object : BankListAdapter.OnItemClickListener {
            override fun onItemClick(bankBean: BankBeanResponse.Bank, pos: Int) {
                val intent = Intent()
                intent.putExtra(KEY_BANK_NAME, bankBean.bank_name)
                intent.putExtra(KEY_BANK_CODE, bankBean.bank_code)
                setResult(START_RESULT_CODE, intent)
                finish()
            }
        })
        rvBankList?.setAdapter(mAdapter)
        sideBar?.setOnSelectIndexItemListener(WaveSideBar.OnSelectIndexItemListener {
            if (mBankList.size == 0) {
                return@OnSelectIndexItemListener
            }
            for (i in 0 until mBankList.size) {
                if (mBankList.get(i).bank_name?.startsWith(it) == true) {
                    (rvBankList?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        i,
                        0
                    )
                    break
                }
            }
            Log.e(
                TAG,
                " test index .. = " + it
            )
        })
    }

    private fun getBankList() {
        flLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " update work = " + jsonObject.toString())
        }
        OkGo.post<String>(Api.BANK_LIST).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isFinishing || isDestroyed) {
                        return
                    }
                    flLoading?.visibility = View.GONE
                    val responseBean: BankBeanResponse? =
                        checkResponseSuccess(response, BankBeanResponse::class.java)
                    if (responseBean?.bank_list == null) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, " get bank list null .")
                        }
                        return
                    }
                    Collections.sort<BankBeanResponse.Bank>(responseBean.bank_list!!,
                        object : Comparator<BankBeanResponse.Bank> {
                            override fun compare(
                                bank1: BankBeanResponse.Bank,
                                bank2: BankBeanResponse.Bank
                            ): Int {
                                if (TextUtils.isEmpty(bank1.bank_name)) {
                                    return -1
                                }
                                if (TextUtils.isEmpty(bank2.bank_name)) {
                                    return 1
                                }
                                val c1: Char = bank1.bank_name!![0]
                                val c2: Char = bank2.bank_name!![0]
                                return c1 - c2
                            }
                        })

                    ConfigMgr.mBankList.clear()
                    ConfigMgr.mBankList.addAll(responseBean.bank_list!!)
                    mBankList.clear()
                    mBankList.addAll(responseBean.bank_list!!)
                    updateList()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    flLoading?.visibility = View.GONE
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update contact = " + response.body())
                    }
                }
            })
    }

    fun updateList() {
        mAdapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}