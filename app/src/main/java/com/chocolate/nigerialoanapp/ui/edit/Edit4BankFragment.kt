package com.chocolate.nigerialoanapp.ui.edit

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Pair
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.EditProfileBean
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.ui.banklist.BankListActivity
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.chocolate.nigerialoanapp.widget.InfoEditView
import com.chocolate.nigerialoanapp.widget.InfoSelectView
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class Edit4BankFragment : BaseEditFragment() {

    companion object {
        private const val TAG = "EditBank4Fragment"
    }

    private var mSelectBankName: InfoSelectView? = null
    private var mEditAccountNum: InfoEditView? = null
    private var mEditAccountNumConfirm: InfoEditView? = null

    private var tvNext: AppCompatTextView? = null
    private var tvDesc: AppCompatTextView? = null
    private var scrollView: NestedScrollView? = null

    private var mBankName: Pair<String, String>? = null
    private var mAccountNum: String? = null
    private var mAccountNumConfirm: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_bank_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSelectBankName = view.findViewById<InfoSelectView>(R.id.select_bank_name)
        mEditAccountNum = view.findViewById<InfoEditView>(R.id.edit_bank_account_num)
        mEditAccountNumConfirm = view.findViewById<InfoEditView>(R.id.edit_bank_account_num_confirm)

        scrollView = view.findViewById<NestedScrollView>(R.id.sv_content)
        tvNext = view.findViewById<AppCompatTextView>(R.id.tv_edit_bank_next)
        tvDesc = view.findViewById<AppCompatTextView>(R.id.tv_bank_next_desc)

        mSelectBankName?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                activity?.let {
                    BankListActivity.startActivityResult(it)
                }
            }

        })

        tvNext?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val check = checkProfileParams(true, true)
                if (check) {
                    uploadReceive()
                }
            }

        })

        SpanUtils.setPrivacyString(tvDesc, activity)
        mEditAccountNum?.setInputNum()
        mEditAccountNumConfirm?.setInputNum()
        tryBindData()
    }

    override fun bindData(profile1Bean: ProfileInfoResponse?) {
        super.bindData(profile1Bean)
        updateData(profile1Bean)
        bindDataInternal()
        updateNextBtnStatus(false)
    }

    private fun updateData(profile1Bean: ProfileInfoResponse?) {
        if (profile1Bean == null || profile1Bean.account_receive == null) {
            return
        }
        if (!TextUtils.isEmpty(profile1Bean.account_receive.bank_name) &&
            !TextUtils.isEmpty(profile1Bean.account_receive.bank_code)
        )
            mBankName = Pair(
                profile1Bean.account_receive.bank_name!!,
                profile1Bean.account_receive.bank_code
            )

        if (!TextUtils.isEmpty(profile1Bean.account_receive.bank_account_num)) {
            mAccountNum = profile1Bean.account_receive.bank_account_num
            mAccountNumConfirm = profile1Bean.account_receive.bank_account_num
        }
    }

    private fun bindDataInternal() {
        if (mBankName != null && !TextUtils.isEmpty(mBankName?.first)) {
            mSelectBankName?.setText(mBankName?.first.toString())
        }
        if (!TextUtils.isEmpty(mAccountNum)) {
            mEditAccountNum?.setText(mAccountNum!!)
        }
        if (!TextUtils.isEmpty(mAccountNumConfirm)) {
            mEditAccountNumConfirm?.setText(mAccountNumConfirm!!)
        }
    }

    private fun checkProfileParams(needToast : Boolean = false, needSelect : Boolean): Boolean {
        if (mBankName == null) {
            if (needSelect) {
                scrollToPos(1, scrollView)
                mSelectBankName?.setSelectState(true)
                if (needToast) {
                    ToastUtils.showShort("Please select type of receiving accounts")
                }
            }
            return false
        } else {
            mSelectBankName?.setSelectState(false)
        }
        if (mEditAccountNum == null || TextUtils.isEmpty(mEditAccountNum!!.getText())) {
            if (needToast) {
                ToastUtils.showShort("Please fill in Account Number")
            }
            return false
        }
        if (mEditAccountNumConfirm == null || TextUtils.isEmpty(mEditAccountNumConfirm!!.getText())) {
            if (needToast) {
                ToastUtils.showShort("Please confirm the Account Number")
            }
            return false
        }
        if (needToast) {
            if (!TextUtils.equals(mEditAccountNum?.getText(), mEditAccountNumConfirm?.getText())) {
                ToastUtils.showShort("Please fill in the same account information")
                return false
            }
        }
        return true
    }

    private fun uploadReceive() {
        showProgressDialogFragment()
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)

            jsonObject.put("bank_name", mBankName?.first)     //银行名字
            jsonObject.put("bank_code", mBankName?.second)  //银行代码
            jsonObject.put("account_number", mEditAccountNum?.getText())   //客户收款账号
            jsonObject.put(
                "account_number_confirm",
                mEditAccountNumConfirm?.getText()
            )       //客户收款账号确认           //联系人2电话
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " update receive = " + jsonObject.toString())
        }
        OkGo.post<String>(Api.UPDATE_RECEIVE).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    dismissProgressDialogFragment()
                    val editProfileBean: EditProfileBean? =
                        checkResponseSuccess(response, EditProfileBean::class.java)
                    if (editProfileBean == null) {
                        Log.e(TAG, " update receive ." + response.body())
                        return
                    }
                    if (activity is EditInfoActivity) {
                        (activity as EditInfoActivity).nextStep(editProfileBean)
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    dismissProgressDialogFragment()
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update contact = " + response.body())
                    }
                }
            })
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }

    fun onBankActivityResult(bankName: String?, bankCode: String?) {
        if (TextUtils.isEmpty(bankName) || TextUtils.isEmpty(bankCode)) {
            return
        }
        mSelectBankName?.setSelectState(false)
        mBankName = Pair(bankName, bankCode)
        mSelectBankName?.setText(mBankName?.first.toString())
        updateNextBtnStatus(true)
    }

    override fun updateNextBtnStatus(needSelect: Boolean) {
       val flag = checkProfileParams(false, needSelect)
        tvNext?.isSelected = flag
    }
}