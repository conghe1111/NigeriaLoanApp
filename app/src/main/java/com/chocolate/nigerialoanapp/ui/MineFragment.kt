package com.chocolate.nigerialoanapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.bean.SettingMineBean
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.global.LocalConfig
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.dialog.LoanRetentionDialog
import com.chocolate.nigerialoanapp.ui.edit.EditInfoActivity
import com.chocolate.nigerialoanapp.ui.edit.EditInfoMenuActivity
import com.chocolate.nigerialoanapp.ui.history.HistoryRecordActivity
import com.chocolate.nigerialoanapp.ui.loanapply.LoanApplyActivity
import com.chocolate.nigerialoanapp.ui.login.LoginActivity
import com.chocolate.nigerialoanapp.ui.mine.MineAdapter
import com.chocolate.nigerialoanapp.ui.mine.NorItemDecor
import com.chocolate.nigerialoanapp.ui.mine.PageType
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.INFORMATION
import com.chocolate.nigerialoanapp.ui.setting.ConsumerHotlineActivity
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.google.firebase.messaging.FirebaseMessaging
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONObject

class MineFragment : BaseFragment() {

    companion object {
        const val TAG = "MineFragment"
    }

    private var mList: ArrayList<SettingMineBean> = ArrayList()

    private var rvMine : RecyclerView? = null
    private var ivConsumer : AppCompatImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMine = view.findViewById<RecyclerView>(R.id.rv_content_mine)
        ivConsumer = view.findViewById<AppCompatImageView>(R.id.iv_main_top_consumer)
        initializeView()
    }

    private fun initializeView() {
        buildSettingList()
        ivConsumer?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                context?.let {
                    ConsumerHotlineActivity.startActivity(it)
                }
            }

        })

        val adapter = MineAdapter(mList)
        rvMine?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvMine?.adapter = adapter
        rvMine?.addItemDecoration(NorItemDecor())
        adapter?.setOnClickListener(object :MineAdapter.OnClickListener {
            override fun OnClick(pos: Int, settingBean: SettingMineBean) {
                when (settingBean.type) {
                    INFORMATION -> {
                        val intent = Intent(context, EditInfoMenuActivity::class.java)
                        startActivity(intent)
                    }
                    PageType.CUSTOMER_SERVICE -> {
                        context?.let {
                            ConsumerHotlineActivity.startActivity(it)
                        }
                    }
                    PageType.HISTORY_RECORD -> {
                        context?.let {
                            HistoryRecordActivity.startActivity(it)
                        }
                    }
                    PageType.PRIVACY -> {

                    }
                    PageType.TERM_CONDITION -> {

                    }
                    PageType.VERSION -> {

                    }
                    PageType.TEST_1 -> {
//                        activity?.let {
//                            EditInfoActivity.showActivity(it, EditInfoActivity.STEP_5)
//                        }
//                        activity?.let {
//                            val dialog = LoanRetentionDialog(it)
//                            dialog.show()
//                        }
                        activity?.let {
                           LoanApplyActivity.startActivity(it)
                        }
                    }
                    PageType.LOGOUT -> {
                        logout()
                    }
                }
            }

        })
    }

    private fun buildSettingList() {
        mList.clear()
        mList.add(
            SettingMineBean(
                R.drawable.ic_setting_info,
                R.string.setting_information,
                PageType.INFORMATION
            )
        )
        mList.add(
            SettingMineBean(
                R.drawable.ic_setting_custom_service,
                R.string.setting_customer_service,
                PageType.CUSTOMER_SERVICE
            )
        )
        mList.add(
            SettingMineBean(
                R.drawable.ic_setting_history_record,
                R.string.setting_history_record  ,
                PageType.HISTORY_RECORD
            )
        )
        mList.add(
            SettingMineBean(
                R.drawable.ic_setting_privacy,
                R.string.setting_privacy,
                PageType.PRIVACY
            )
        )
        mList.add(SettingMineBean(R.drawable.ic_setting_term, R.string.setting_term_condition,
            PageType.TERM_CONDITION))
        mList.add(SettingMineBean(R.drawable.ic_setting_version, R.string.setting_version,
            PageType.VERSION))

//        if (BuildConfig.DEBUG && false) {
        if (BuildConfig.DEBUG) {
            mList.add(SettingMineBean(R.drawable.ic_setting_version, R.string.setting_debug, PageType.TEST_1))

        }
        mList.add(SettingMineBean(R.drawable.ic_setting_version, R.string.logout,
            PageType.LOGOUT))
    }

    private fun logout() {
//        if (BuildConfig.DEBUG) {
//            quitLogout()
//            return
//        }
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        jsonObject.put("account_id", Constant.mAccountId)
        jsonObject.put("access_token", Constant.mToken)
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " logout = $jsonObject")
        }
        OkGo.post<String>(Api.LOGOUT).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (isDestroy()) {
                        return
                    }
                    val baseResponseBean = NetworkUtils.checkResponseSuccess2(response)
                    if (baseResponseBean?.isRequestSuccess() == true) {
                        quitLogout()
                    } else {
                        if (baseResponseBean != null) {
                            ToastUtils.showShort(baseResponseBean.getMsg())
                        }
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
                        Log.e(TAG, " marketing page= " + response.body())
                    }
                }
            })
        }

    private fun quitLogout() {
        Constant.mToken = ""
        Constant.mAccountId = ""
        Constant.mLaunchOrderInfo = null
        SPUtils.getInstance().put(LocalConfig.LC_ACCOUNT_ID, "")
        SPUtils.getInstance().put(LocalConfig.LC_ACCOUNT_TOKEN, "")
        // TODO
//        FirebaseMessaging.getInstance().deleteToken()
        val intent = Intent(activity, LoginActivity::class.java)
        activity?.startActivity(intent)
        activity?.finish()
    }
}