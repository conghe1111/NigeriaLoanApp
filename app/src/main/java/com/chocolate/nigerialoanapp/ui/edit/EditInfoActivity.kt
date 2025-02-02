package com.chocolate.nigerialoanapp.ui.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.ui.graphics.Color
import com.blankj.utilcode.util.BarUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.response.EditProfileBean
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.banklist.BankListActivity
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

/**
 * 101	基本信息填写完成（第一页）
 * 102	工作信息填写完成（第二页）
 * 103	联系人信息填写完成（第三页）
 * 104	收款信息填写完成（第四页）
 * 105	活体信息上传完成（第五页）
 * 111	完成首贷KYC流程
 *
 */
class EditInfoActivity : BaseActivity() {

    private var ivBack: AppCompatImageView? = null
    private var tvTitle: AppCompatTextView? = null

    companion object {

        private const val TAG = "EditInfoActivity"
        private const val KEY_FROM = "key_edit_info_from"
        private const val KEY_STEP = "key_edit_info_step"

        const val STEP_1 = 1111
        const val STEP_2 = 1112
        const val STEP_3 = 1113
        const val STEP_4 = 1114

        const val FROM_WORK_MENU = 111
        const val FROM_APPLY_LOAD = 112
        fun showActivity(context: Context, step: Int = STEP_1, from: Int = FROM_WORK_MENU) {
            val intent = Intent(context, EditInfoActivity::class.java)
            intent.putExtra(KEY_FROM, from)
            intent.putExtra(KEY_STEP, step)
            context.startActivity(intent)
        }
    }

    private var mCurFragment: BaseEditFragment? = null
    private var mFrom: Int? = null
    private var mStep: Int = STEP_1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_edit_info)
        ivBack = findViewById<AppCompatImageView>(R.id.iv_edit_info_back)
        tvTitle = findViewById<AppCompatTextView>(R.id.tv_edit_info_title)
        getProfileInfo()
        mFrom = intent.getIntExtra(KEY_FROM, FROM_WORK_MENU)
        mStep = intent.getIntExtra(KEY_STEP, STEP_1)
        when (mStep) {
            (STEP_1) -> {
                tvTitle?.text = resources.getString(R.string.basic_information)
                mCurFragment = Edit1BasicFragment()
            }

            (STEP_2) -> {
                tvTitle?.text = resources.getString(R.string.work_information)
                mCurFragment = Edit2WorkFragment()
            }

            (STEP_3) -> {
                tvTitle?.text = resources.getString(R.string.contact_information)
                mCurFragment = Edit3ContactFragment()
            }

            (STEP_4) -> {
                tvTitle?.text = resources.getString(R.string.receive_account)
                mCurFragment = Edit4BankFragment()
            }
        }
        mCurFragment?.let {
            toFragment(mCurFragment)
        }
        ivBack?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                finish()
            }

        })
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_edit_info_container
    }

    private fun getProfileInfo() {
//        pbLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken) //FCM Token
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " get profile info = " + jsonObject.toString())
        }
        //        Log.e(TAG, "111 id = " + Constant.mAccountId);
        OkGo.post<String>(Api.PROFILE_INFO).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (isDestroyed()) {
                        return
                    }
                    val profileInfo: ProfileInfoResponse? =
                        checkResponseSuccess(response, ProfileInfoResponse::class.java)
                    if (profileInfo == null) {
                        Log.e(TAG, " profile info error ." + response.body())
                        return
                    }
                    if (mCurFragment is Edit1BasicFragment) {
                        (mCurFragment as Edit1BasicFragment).bindData(profileInfo)
                    } else if (mCurFragment is Edit2WorkFragment) {
                        (mCurFragment as Edit2WorkFragment).bindData(profileInfo)
                    }

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroyed()) {
                        return
                    }
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "order profile failure = " + response.body())
                    }
                }
            })
    }

    fun nextStep(editProfileBean: EditProfileBean) {
        if (editProfileBean.current_phase == 111) {
            // TODO 完成
            return
        }
        when (editProfileBean.next_phase) {
            (101) -> {  //基本信息填写完成（第一页）
                tvTitle?.text = resources.getString(R.string.basic_information)
                mCurFragment = Edit1BasicFragment()
            }

            (102) -> {  //工作信息填写完成（第二页）
                tvTitle?.text = resources.getString(R.string.work_information)
                mCurFragment = Edit2WorkFragment()

            }

            (103) -> {  //联系人信息填写完成（第三页）
                tvTitle?.text = resources.getString(R.string.contact_information)
                mCurFragment = Edit3ContactFragment()
            }

            (104) -> {  //收款信息填写完成（第四页）
                tvTitle?.text = resources.getString(R.string.bank_information)
                mCurFragment = Edit4BankFragment()
            }

            (105) -> {  //活体信息上传完成（第五页）
                // TODO
            }

            (111) -> {  //完成首贷KYC流程

            }
        }
        toFragment(mCurFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null){
            return
        }
        if (resultCode == BankListActivity.START_RESULT_CODE && requestCode == BankListActivity.START_REQUEST_CODE) {
            val bankName = data.getStringExtra(BankListActivity.KEY_BANK_NAME)
            val bankCode = data.getStringExtra(BankListActivity.KEY_BANK_CODE)
            (mCurFragment as? Edit4BankFragment)?.onBankActivityResult(bankName, bankCode)
        }
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}