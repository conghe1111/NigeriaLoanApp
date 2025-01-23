package com.chocolate.nigerialoanapp.ui.edit

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.EditProfileBean
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.global.ConfigMgr
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.dialog.selectdata.SelectDataDialog
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.chocolate.nigerialoanapp.widget.InfoEditView
import com.chocolate.nigerialoanapp.widget.InfoSelectView
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class EditWork2Fragment : BaseEditFragment() {

    companion object {
        private const val TAG = "EditWork2Fragment"
    }

    private var mSelectEmployment : InfoSelectView? = null
    private var mSelectMonthlyIncome : InfoSelectView? = null
    private var mSelectPosition : InfoSelectView? = null
    private var mSelectPayPeriod : InfoSelectView? = null
    private var mSelectHasOtherDebt : InfoSelectView? = null
    private var mEditCompanyName : InfoEditView? = null
    private var mEditCompanyPhoneNumber : InfoEditView? = null
    private var mEditCompanyAddress : InfoEditView? = null
    private var tvNext: AppCompatTextView? = null
    private var tvNextDesc: AppCompatTextView? = null
    private var scrollView: ScrollView? = null

    private var mEmployment: Pair<String, String>? = null
    private var mMonthlyIncome: Pair<String, String>? = null
    private var mPosition: Pair<String, String>? = null
    private var mPayPeriod: Pair<String, String>? = null
    private var mHasOtherDebt: Pair<String, String>? = null
    private var mCompanyName: String? = null
    private var mCompanyPhoneNum: String? = null
    private var mCompanyAddress: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_work_info, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSelectEmployment = view.findViewById<InfoSelectView>(R.id.select_employment)
        mSelectMonthlyIncome = view.findViewById<InfoSelectView>(R.id.select_monthly_income)
        mSelectPosition =  view.findViewById<InfoSelectView>(R.id.select_position)
        mSelectPayPeriod =  view.findViewById<InfoSelectView>(R.id.select_pay_period)
        mSelectHasOtherDebt =  view.findViewById<InfoSelectView>(R.id.select_has_other_outstand)
        mEditCompanyName =  view.findViewById<InfoEditView>(R.id.edit_company_name)
        mEditCompanyPhoneNumber =  view.findViewById<InfoEditView>(R.id.edit_company_phone_number)
        mEditCompanyAddress =  view.findViewById<InfoEditView>(R.id.edit_company_address)
        tvNext = view.findViewById<AppCompatTextView>(R.id.tv_edit_basic_next)
        scrollView = view.findViewById<ScrollView>(R.id.sv_content)

        mSelectEmployment?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                showListDialog(ConfigMgr.mEmploymentList, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        if (content == null) {
                            return
                        }
                        mEmployment = content
                        if (mEmployment != null) {
                            mSelectEmployment?.setText(mEmployment!!.first)
                        }
                    }

                })
            }

        })
        mSelectMonthlyIncome?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                showListDialog(ConfigMgr.mMonthlyIncomeList, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        if (content == null) {
                            return
                        }
                        mMonthlyIncome = content
                        if (mMonthlyIncome != null) {
                            mSelectMonthlyIncome?.setText(mMonthlyIncome!!.first)
                        }
                    }

                })
            }

        })
        mSelectPosition?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                showListDialog(ConfigMgr.mPositionList, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        if (content == null) {
                            return
                        }
                        mPosition = content
                        if (mPosition != null) {
                            mSelectPosition?.setText(mPosition!!.first)
                        }
                    }

                })
            }

        })
        mSelectPayPeriod?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                showCustomPicker(ConfigMgr.mPayPeriodList, resources.getString(R.string.pay_period), object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        if (content == null) {
                            return
                        }
                        mPayPeriod = content
                        if (mSelectPayPeriod != null) {
                            mSelectPayPeriod?.setText(mPayPeriod!!.first)
                        }
                    }

                })
            }

        })
        mSelectHasOtherDebt?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                showListDialog(ConfigMgr.mHaveOtherDebtList, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        if (content == null) {
                            return
                        }
                        mHasOtherDebt = content
                        if (mHasOtherDebt != null) {
                            mSelectHasOtherDebt?.setText(mHasOtherDebt!!.first)
                        }
                    }

                })
            }

        })
        tvNext?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (checkProfileParams()) {
                    uploadWork()
                }
            }

        })
        tvNextDesc = view.findViewById<AppCompatTextView>(R.id.tv_work_next_desc)
        SpanUtils.setPrivacyString(tvNextDesc)
    }

    override fun bindData(profile1Bean: ProfileInfoResponse?) {
        updateData(profile1Bean)
        bindDataInternal()
    }

    private fun updateData(profile1Bean: ProfileInfoResponse?) {
        if (profile1Bean == null || profile1Bean.account_profile == null){
            return
        }
        mEmployment = getPairByValue(ConfigMgr.mEmploymentList, profile1Bean.account_profile.employment.toString())
        mMonthlyIncome = getPairByValue(ConfigMgr.mMonthlyIncomeList, profile1Bean.account_profile.monthly_income.toString())
        mPosition = getPairByValue(ConfigMgr.mPositionList, profile1Bean.account_profile.position.toString())
        mPayPeriod = getPairByValue(ConfigMgr.mPayPeriodList, profile1Bean.account_profile.salary_day.toString())
        mHasOtherDebt = getPairByValue(ConfigMgr.mHaveOtherDebtList, profile1Bean.account_profile.other_loan.toString())

        if (!TextUtils.isEmpty(profile1Bean.account_profile.company_name)) {
            mCompanyName = profile1Bean.account_profile.company_name
        }
        if (!TextUtils.isEmpty(profile1Bean.account_profile.company_mobile)) {
            mCompanyPhoneNum = profile1Bean.account_profile.company_mobile
        }
        if (!TextUtils.isEmpty(profile1Bean.account_profile.company_address)) {
            mCompanyAddress = profile1Bean.account_profile.company_address
        }
    }

    private fun bindDataInternal() {
        mSelectEmployment?.setText(mEmployment?.first.toString())
        mSelectMonthlyIncome?.setText(mMonthlyIncome?.first.toString())
        mSelectPosition?.setText(mPosition?.first.toString())
        mSelectPayPeriod?.setText(mPayPeriod?.first.toString())
        mSelectHasOtherDebt?.setText(mHasOtherDebt?.first.toString())

        if (!TextUtils.isEmpty(mCompanyName)) {
            mEditCompanyName?.setText(mCompanyName!!)
        }
        if (!TextUtils.isEmpty(mCompanyPhoneNum)) {
            mEditCompanyPhoneNumber?.setText(mCompanyPhoneNum!!)
        }
        if (!TextUtils.isEmpty(mCompanyAddress)) {
            mEditCompanyAddress?.setText(mCompanyAddress!!)
        }
    }

    private fun checkProfileParams(): Boolean {
        if (mEmployment == null) {
            scrollToPos(1, scrollView)
            mSelectEmployment?.setSelectState(true)
            return false
        }
        if (mMonthlyIncome == null) {
            scrollToPos(2, scrollView)
            mSelectMonthlyIncome?.setSelectState(true)
            return false
        }
        if (mPosition == null) {
            scrollToPos(3, scrollView)
            mSelectPosition?.setSelectState(true)
            return false
        }
        if (mPayPeriod == null) {
            scrollToPos(4, scrollView)
            mSelectPayPeriod?.setSelectState(true)
            return false
        }
        if (mHasOtherDebt == null) {
            scrollToPos(5, scrollView)
            mSelectHasOtherDebt?.setSelectState(true)
            return false
        }
//        if (mEditCompanyName == null || TextUtils.isEmpty(mEditCompanyName!!.getText())) {
//            scrollToPos(6, scrollView)
//            mEditCompanyName?.setSelectState()
//            return false
//        }
//        if (mEditCompanyPhoneNumber == null || TextUtils.isEmpty(mEditCompanyPhoneNumber!!.getText())) {
//            scrollToPos(7, scrollView)
//            mEditCompanyPhoneNumber?.setSelectState()
//            return false
//        }
//        if (mEditCompanyAddress == null || TextUtils.isEmpty(mEditCompanyAddress!!.getText())) {
//            scrollToPos(8, scrollView)
//            mEditCompanyAddress?.setSelectState()
//            return false
//        }
        return true
    }

    private fun uploadWork() {
        //        pbLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
            jsonObject.put("salary_day", mPayPeriod?.second)  //发薪日（传入1-31的数字）
            jsonObject.put("employment", mEmployment?.second)     //工作
            jsonObject.put("monthly_income", mMonthlyIncome?.second)   //月收入
            jsonObject.put("position", mPosition?.second)              //职位
            jsonObject.put("other_loan", mHasOtherDebt?.second)          //是否有其他借款 1没有 2有
            jsonObject.put("company_name", mEditCompanyName?.getText())                   //公司名字	没有值传空字符串
            jsonObject.put("company_mobile",  mEditCompanyPhoneNumber?.getText())     //公司电话	没有值传空字符串
            jsonObject.put("company_address", mEditCompanyAddress?.getText())        //公司地址	没有值传空字符串

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " update work = " + jsonObject.toString())
        }
        OkGo.post<String>(Api.UPDATE_WORK).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (isDestroy()) {
                        return
                    }
                    val editProfileBean: EditProfileBean? =
                        checkResponseSuccess(response, EditProfileBean::class.java)
                    if (editProfileBean == null) {
                        Log.e(TAG, " update base ." + response.body())
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
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update base = " + response.body())
                    }
                }
            })
    }
}