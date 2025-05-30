package com.chocolate.nigerialoanapp.ui.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.response.EditProfileBean
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.banklist.BankListActivity
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
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

        const val REQUEST_CODE = 1112
        const val RESULT_CODE = 1117

        const val STEP_1 = 1111
        const val STEP_2 = 1112
        const val STEP_3 = 1113
        const val STEP_4 = 1114
        const val STEP_5 = 1115

        const val FROM_WORK_MENU = 111
        const val FROM_APPLY_LOAD = 112
        const val FROM_DISBURSE_6 = 113
        fun showActivity(context: Activity, step: Int = STEP_1, from: Int = FROM_WORK_MENU, needResult : Boolean = false) {
            val intent = Intent(context, EditInfoActivity::class.java)
            intent.putExtra(KEY_FROM, from)
            intent.putExtra(KEY_STEP, step)
            if (needResult) {
                context.startActivityForResult(intent, REQUEST_CODE)
            } else {
                context.startActivity(intent)
            }
        }
    }

    private var mCurFragment: BaseEditFragment? = null
    var mProfileInfo: ProfileInfoResponse? = null
    var mFrom: Int? = null
    private var mStep: Int = STEP_1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_edit_info)
        ivBack = findViewById<AppCompatImageView>(R.id.iv_back)
        tvTitle = findViewById<AppCompatTextView>(R.id.tv_title)
        getProfileInfo()
        mFrom = intent.getIntExtra(KEY_FROM, FROM_WORK_MENU)
        mStep = intent.getIntExtra(KEY_STEP, STEP_1)

        toNextFragment(mStep)
        ivBack?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (mCurFragment is Edit1BasicFragment) {
                    FirebaseUtils.logEvent("SYSTEM_BASIC_INF_BACK")    //个人信息页返回上一页
                } else if (mCurFragment is Edit2WorkFragment) {
                    FirebaseUtils.logEvent("SYSTEM_WORK_INF_BACK")
                } else if (mCurFragment is Edit3ContactFragment) {
                    FirebaseUtils.logEvent("SYSTEM_CONTACT_INF_BACK")
                } else if (mCurFragment is Edit4BankFragment) {
                    FirebaseUtils.logEvent("SYSTEM_BANK_CARD_BACK")
                } else if (mCurFragment is Edit5FaceRecognitionFragment) {
                    FirebaseUtils.logEvent("SYSTEM_LIVENESSTEST_BACK")
                }
                finish()
            }

        })
        KeyboardUtils.registerSoftInputChangedListener(window, mKeyBoardListener)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_edit_info_container
    }

    private fun getProfileInfo() {
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
                    if (isDestroyed()) {
                        return
                    }
                    val profileInfo: ProfileInfoResponse? =
                        checkResponseSuccess(response, ProfileInfoResponse::class.java)
                    if (profileInfo == null) {
                        Log.e(TAG, " profile info error ." + response.body())
                        return
                    }
                    mProfileInfo = profileInfo
                    mCurFragment?.bindData(profileInfo)
                    FirebaseUtils.logEvent("SYSTEM_BASIC_INF_LOAD")
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroyed()) {
                        return
                    }
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "order profile failure = " + response.body())
                    }
                }
            })
    }

    fun nextStep(editProfileBean: EditProfileBean) {
        if (editProfileBean.current_phase == 111) {
            endNext()
            return
        }
        var nextStep : Int? = null
        when (editProfileBean.current_phase) {
            (101) -> {  //基本信息填写完成（第一页）
                nextStep = STEP_1
            }
            (102) -> {  //工作信息填写完成（第二页）
                nextStep = STEP_2
            }
            (103) -> {  //联系人信息填写完成（第三页）
                nextStep = STEP_3
            }
            (104) -> {  //收款信息填写完成（第四页）
                nextStep = STEP_4
            }
            (105) -> {  //活体信息上传完成（第五页）
                nextStep = STEP_5
            }
            (111) -> {  //完成首贷KYC流程
                endNext()
            }
            (0) -> {  //如果当前流程=111，下一步骤为0.
                endNext()
            }
        }
        nextStep?.let {
            toNextFragment(it)
        }

    }

    private fun endNext() {
        if (mFrom == FROM_DISBURSE_6) {
            setResult(RESULT_CODE)
            finish()
        } else if (mFrom == FROM_WORK_MENU) {
            finish()
        } else if (mFrom == FROM_APPLY_LOAD) {
            finish()
        }
        // TODO 完成
    }

    private fun toNextFragment(nextStep : Int) {
        when (nextStep) {
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
            (STEP_5) -> {
                tvTitle?.text = resources.getString(R.string.identity_information)
                mCurFragment = Edit5FaceRecognitionFragment()
            }

        }
        mCurFragment?.let {
            toFragment(mCurFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == BankListActivity.START_RESULT_CODE && requestCode == BankListActivity.START_REQUEST_CODE) {
            val bankName = data?.getStringExtra(BankListActivity.KEY_BANK_NAME)
            val bankCode = data?.getStringExtra(BankListActivity.KEY_BANK_CODE)
            if (mCurFragment is Edit4BankFragment) {
                (mCurFragment as? Edit4BankFragment)?.onBankActivityResult(bankName, bankCode)
            }
        } else if (requestCode == Edit5FaceRecognitionFragment.REQUEST_CAMERA_RECOGNITION) {
//            if (mCurFragment is Edit5FaceRecognitionFragment) {
//                (mCurFragment as? Edit5FaceRecognitionFragment)?.onActivityResultInternal(requestCode, data)
//            }
        }
    }

    private val mKeyBoardListener = object : KeyboardUtils.OnSoftInputChangedListener {
        override fun onSoftInputChanged(height: Int) {
            if (isDestroyed || isFinishing) {
                return
            }
            mCurFragment?.onSoftInputChanged(KeyboardUtils.isSoftInputVisible(this@EditInfoActivity))
        }

    }


    override fun onDestroy() {
        KeyboardUtils.unregisterSoftInputChangedListener(window)

        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }

    override fun useLogout(): Boolean {
        return true
    }
}