package com.chocolate.nigerialoanapp.ui.edit

import android.os.Bundle
import android.text.InputFilter

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.util.Pair
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.ui.input.key.Key
import androidx.core.widget.NestedScrollView
import com.blankj.utilcode.util.KeyboardUtils
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
import com.chocolate.nigerialoanapp.widget.LengthTextWatcher
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class Edit3ContactFragment : BaseEditFragment() {

    companion object {
        private const val TAG = "EditContact3Fragment"
    }

    private var mSelectRelationship1: InfoSelectView? = null
    private var mEditMobile1: InfoEditView? = null
    private var mEditName1: InfoEditView? = null
    private var mSelectRelationship2: InfoSelectView? = null
    private var mEditMobile2: InfoEditView? = null
    private var mEditName2: InfoEditView? = null

    private var tvNext: AppCompatTextView? = null
    private var tvDesc: AppCompatTextView? = null
    private var scrollView: NestedScrollView? = null

    private var mRelationship1: Pair<String, String>? = null
    private var mMobile1: String? = null
    private var mName1: String? = null
    private var mRelationship2: Pair<String, String>? = null
    private var mMobile2: String? = null
    private var mName2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_contact_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSelectRelationship1 = view.findViewById<InfoSelectView>(R.id.select_relationship_1)
        mEditMobile1 = view.findViewById<InfoEditView>(R.id.edit_mobile_1)
        mEditName1 = view.findViewById<InfoEditView>(R.id.edit_name_1)

        mSelectRelationship2 = view.findViewById<InfoSelectView>(R.id.select_relationship_2)
        mEditMobile2 = view.findViewById<InfoEditView>(R.id.edit_mobile_2)
        mEditName2 = view.findViewById<InfoEditView>(R.id.edit_name_2)

        scrollView = view.findViewById<NestedScrollView>(R.id.sv_content)
        tvNext = view.findViewById<AppCompatTextView>(R.id.tv_edit_contact_next)
        tvDesc = view.findViewById<AppCompatTextView>(R.id.tv_contact_next_desc)

        tvNext?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val check = checkProfileParams()
                onClickSubmit(check)
                if (check) {
                    uploadContact()
                }
            }

        })
        mSelectRelationship1?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                showListDialog(ConfigMgr.mRelationShipList, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        if (content == null) {
                            return
                        }
                        mRelationship1 = content
                        mSelectRelationship1?.setSelectState(false)
                        mSelectRelationship1?.setText(content.first)
                        updateNextBtnStatus()
                    }

                })
            }

        })
        mSelectRelationship2?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                showListDialog(ConfigMgr.mRelationShipList, object : SelectDataDialog.Observer {
                    override fun onItemClick(content: Pair<String, String>?, pos: Int) {
                        if (content == null) {
                            return
                        }
                        mRelationship2 = content
                        mSelectRelationship2?.setSelectState(false)
                        mSelectRelationship2?.setText(content.first)
                        updateNextBtnStatus()
                    }

                })
            }

        })

        SpanUtils.setPrivacyString(tvDesc, activity)

        mEditMobile1?.getEditText()?.addTextChangedListener(LengthTextWatcher(mEditMobile1?.getEditText()))
        mEditMobile2?.getEditText()?.addTextChangedListener(LengthTextWatcher(mEditMobile2?.getEditText()))
        mEditMobile1?.setInputNum()
        mEditMobile2?.setInputNum()
    }

    override fun bindData(profile1Bean: ProfileInfoResponse?) {
        super.bindData(profile1Bean)
        updateData(profile1Bean)
        bindDataInternal()
        updateNextBtnStatus(false)
    }

    private fun updateData(profile1Bean: ProfileInfoResponse?) {
        if (profile1Bean == null || profile1Bean.account_profile == null) {
            return
        }
        mRelationship1 = getPairByValue(
            ConfigMgr.mRelationShipList,
            profile1Bean.account_profile.contact1_relationship.toString()
        )
        if (!TextUtils.isEmpty(profile1Bean.account_profile.contact1)) {
            mMobile1 = profile1Bean.account_profile.contact1
        }
        if (!TextUtils.isEmpty(profile1Bean.account_profile.contact1_name)) {
            mName1 = profile1Bean.account_profile.contact1_name
        }

        mRelationship2 = getPairByValue(
            ConfigMgr.mRelationShipList,
            profile1Bean.account_profile.contact2_relationship.toString()
        )
        if (!TextUtils.isEmpty(profile1Bean.account_profile.contact2)) {
            mMobile2 = profile1Bean.account_profile.contact2
        }
        if (!TextUtils.isEmpty(profile1Bean.account_profile.contact2_name)) {
            mName2 = profile1Bean.account_profile.contact2_name
        }
    }

    private fun bindDataInternal() {
        if (mRelationship1 != null && !TextUtils.isEmpty(mRelationship1?.first.toString())) {
            mSelectRelationship1?.setText(mRelationship1?.first.toString())
        }
        if (!TextUtils.isEmpty(mMobile1)) {
            mEditMobile1?.setText(mMobile1!!)
        }
        if (!TextUtils.isEmpty(mName1)) {
            mEditName1?.setText(mName1!!)
        }
        if (mRelationship2 != null && !TextUtils.isEmpty(mRelationship2?.first.toString())) {
            mSelectRelationship2?.setText(mRelationship2?.first.toString())
        }
        if (!TextUtils.isEmpty(mMobile2)) {
            mEditMobile2?.setText(mMobile2!!)
        }
        if (!TextUtils.isEmpty(mName2)) {
            mEditName2?.setText(mName2!!)
        }
    }

    private fun checkProfileParams(needSelect : Boolean = true): Boolean {
        if (mRelationship1 == null) {
            if (needSelect) {
                scrollToPos(1, scrollView)
                mSelectRelationship1?.setSelectState(true)
            }
            return false
        }
        if (mRelationship2 == null) {
            if (needSelect) {
                scrollToPos(4, scrollView)
                mSelectRelationship2?.setSelectState(true)
            }
            return false
        }
        if (mEditMobile1 == null || TextUtils.isEmpty(mEditMobile1!!.getText())) {
            if (needSelect) {
                scrollToPos(2, scrollView)
                mEditMobile1?.setSelectState()
            }
            return false
        }
        if (mEditName1 == null || TextUtils.isEmpty(mEditName1!!.getText())) {
            if (needSelect) {
                scrollToPos(3, scrollView)
                mEditName1?.setSelectState()
            }
            return false
        }
        if (mEditMobile2 == null || TextUtils.isEmpty(mEditMobile2!!.getText())) {
            if (needSelect) {
                scrollToPos(8, scrollView)
                mEditMobile2?.setSelectState()
            }
            return false
        }
        if (mEditName2 == null || TextUtils.isEmpty(mEditName2!!.getText())) {
            if (needSelect) {
                scrollToPos(8, scrollView)
                mEditName2?.setSelectState()
            }
            return false
        }
        return true
    }

    private fun uploadContact() {
        showProgressDialogFragment()
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
            jsonObject.put("contact1", mEditMobile1?.getText())  //联系人1电话
            jsonObject.put("contact1_name", mEditName1?.getText())     //联系人1名字
            jsonObject.put("contact1_relationship", mRelationship1?.second)   //联系人1关系
            jsonObject.put("contact2", mEditMobile2?.getText())              //联系人2电话
            jsonObject.put("contact2_name", mEditName2?.getText())          //联系人2名字
            jsonObject.put("contact2_relationship", mRelationship2?.second)  //联系人2关系
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " update work = " + jsonObject.toString())
        }
        OkGo.post<String>(Api.UPDATE_CONTACT).tag(TAG)
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
                        Log.e(TAG, " update contact ." + response.body())
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

    override fun updateNextBtnStatus(needSelect: Boolean) {
        val flag = checkProfileParams(false)
        tvNext?.isSelected = flag
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }
}