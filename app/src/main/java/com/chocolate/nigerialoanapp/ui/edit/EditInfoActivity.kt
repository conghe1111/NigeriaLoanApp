package com.chocolate.nigerialoanapp.ui.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.response.EditProfileBean
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
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

    private var ivBack : AppCompatImageView? = null
    private var tvTitle : AppCompatTextView? = null

    companion object {

        private const val TAG = "EditInfoActivity"
        private const val KEY_FROM = "key_edit_info_from"
        private const val KEY_STEP = "key_edit_info_step"

        const val STEP_1 = 1111
        const val STEP_2 = 1112
        const val STEP_3 = 1113
        const val STEP_4 = 1114

        const val FROM_WORK_MENU = 111
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
        setContentView(R.layout.activity_edit_info)
        ivBack =  findViewById<AppCompatImageView>(R.id.iv_edit_info_back)
        tvTitle =  findViewById<AppCompatTextView>(R.id.tv_edit_info_title)
        getProfileInfo()
        mFrom = intent.getIntExtra(KEY_FROM, FROM_WORK_MENU)
        mStep = intent.getIntExtra(KEY_STEP, STEP_1)
        when(mStep) {
            (STEP_1) -> {
                tvTitle?.text = resources.getString(R.string.basic_information)
                mCurFragment = EditBasic1Fragment()
            }
            (STEP_2) -> {
                tvTitle?.text = resources.getString(R.string.work_information)
                mCurFragment = EditWork2Fragment()
            }
            (STEP_3) -> {
                tvTitle?.text = resources.getString(R.string.contact_information)
                mCurFragment = EditContact3Fragment()
            }
            (STEP_4) -> {
                tvTitle?.text = resources.getString(R.string.bank_information)
                mCurFragment = EditBank4Fragment()
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
                    if (mCurFragment is EditBasic1Fragment) {
                        (mCurFragment as EditBasic1Fragment).bindData(profileInfo)
                    } else if (mCurFragment is EditWork2Fragment) {
                        (mCurFragment as EditWork2Fragment).bindData(profileInfo)
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
        if (editProfileBean.current_phase == 111){
            // TODO 完成
            return
        }
        when (editProfileBean.next_phase) {
            (101) -> {  //基本信息填写完成（第一页）
                tvTitle?.text = resources.getString(R.string.basic_information)
                mCurFragment = EditBasic1Fragment()
            }
            (102) -> {  //工作信息填写完成（第二页）
                tvTitle?.text = resources.getString(R.string.work_information)
                mCurFragment = EditWork2Fragment()

            }
            (103) -> {  //联系人信息填写完成（第三页）
                tvTitle?.text = resources.getString(R.string.contact_information)
                mCurFragment = EditContact3Fragment()
            }
            (104) -> {  //收款信息填写完成（第四页）
                tvTitle?.text = resources.getString(R.string.bank_information)
                mCurFragment = EditBank4Fragment()
            }
            (105) -> {  //活体信息上传完成（第五页）

            }
            (111) -> {  //完成首贷KYC流程

            }
        }
        toFragment(mCurFragment)
    }
}