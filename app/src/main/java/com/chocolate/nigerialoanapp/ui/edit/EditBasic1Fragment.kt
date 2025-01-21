package com.chocolate.nigerialoanapp.ui.edit

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.global.ConfigMgr
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.widget.InfoEditView
import com.chocolate.nigerialoanapp.widget.InfoSelectView
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class EditBasic1Fragment : BaseFragment() {

    companion object {
        private const val TAG = "EditBasicInfoFragment"
    }

    private var editFirstName: InfoEditView? = null
    private var editMiddleName: InfoEditView? = null
    private var editLastName: InfoEditView? = null
    private var editBvn: InfoEditView? = null

    private var selectBirth: InfoSelectView? = null
    private var selectGender: InfoSelectView? = null
    private var selectMarital: InfoSelectView? = null
    private var selectEducation: InfoSelectView? = null

    private var editEmail: InfoEditView? = null

    private var selectAddress: InfoSelectView? = null
    private var editStreet: InfoEditView? = null
    private var tvNext: AppCompatTextView? = null
    private var scrollView: ScrollView? = null

    private var firstName: String? = null
    private var middleName: String? = null
    private var lastName: String? = null
    private var genderPos: Int? = null
    private var bvn: String? = null
    private var email: String? = null
    private var state: String? = null
    private var area: String? = null
    private var homeAddress: String? = null
    private var mBirthday: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_basic_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollView = view.findViewById<ScrollView>(R.id.sv_content)
        editFirstName = view.findViewById<InfoEditView>(R.id.edit_first_name)
        editMiddleName = view.findViewById<InfoEditView>(R.id.edit_middle_name)
        editLastName = view.findViewById<InfoEditView>(R.id.edit_last_name)
        editBvn = view.findViewById<InfoEditView>(R.id.edit_bvn)
        selectBirth = view.findViewById<InfoSelectView>(R.id.select_birth)
        selectGender = view.findViewById<InfoSelectView>(R.id.select_gender)
        selectMarital = view.findViewById<InfoSelectView>(R.id.select_marital)
        selectEducation = view.findViewById<InfoSelectView>(R.id.select_education)
        editEmail = view.findViewById<InfoEditView>(R.id.edit_email)
        selectAddress = view.findViewById<InfoSelectView>(R.id.select_address)
        editStreet = view.findViewById<InfoEditView>(R.id.edit_street)
        tvNext = view.findViewById<AppCompatTextView>(R.id.tv_edit_basic_next)

        tvNext?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                val check = checkProfileParams()
                if (check) {
                    uploadBase()
                }
            }

        })
        tvNext?.isEnabled = true
    }

    private fun checkProfileParams(): Boolean {
        if (editFirstName == null || TextUtils.isEmpty(editFirstName!!.getText())) {
            scrollView?.scrollTo(0,0)
            editFirstName?.setSelectState()
//            ToastUtils.showShort("Please fill correct fist name")
            return false
        }
        if (editLastName == null || TextUtils.isEmpty(editLastName!!.getText())) {
            ToastUtils.showShort("Please fill correct last name")
            return false
        }
        if (mBirthday == null) {
            ToastUtils.showShort("Please select birthday")
            return false
        }
        if (state == null || area == null) {
            ToastUtils.showShort("Please fill correct Residential address")
            return false
        }
        if (editBvn == null || TextUtils.isEmpty(editBvn!!.getText())) {
            ToastUtils.showShort("Please fill correct bvn")
            return false
        }
        if (editEmail == null || TextUtils.isEmpty(editEmail!!.getText())) {
            ToastUtils.showShort("Please fill correct email")
            return false
        }
        if (editStreet == null || TextUtils.isEmpty(editStreet!!.getText())) {
            ToastUtils.showShort("street is null")
            ToastUtils.showShort("Please fill correct street")
            return false
        }
        return true
    }

    private fun uploadBase() {
        //        pbLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken) //FCM Token
            jsonObject.put("first_name", Constant.mToken) //FCM Token
            jsonObject.put("middle_name", Constant.mToken) //FCM Token
            jsonObject.put("last_name", Constant.mToken) //FCM Token
            jsonObject.put("bvn", Constant.mToken) //FCM Token
            jsonObject.put("gender", Constant.mToken) //FCM Token
            jsonObject.put("birthday", Constant.mToken) //FCM Token
            jsonObject.put("marital_status", Constant.mToken) //FCM Token
            jsonObject.put("education", Constant.mToken) //FCM Token
            jsonObject.put("email", Constant.mToken) //FCM Token
            jsonObject.put("home_address", Constant.mToken) //FCM Token
            jsonObject.put("home_street", Constant.mToken) //FCM Token

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
                    if (isDestroy()) {
                        return
                    }
                    val profileInfo: ProfileInfoResponse? =
                        checkResponseSuccess(response, ProfileInfoResponse::class.java)
                    if (profileInfo == null) {
                        Log.e(TAG, " profile info error ." + response.body())
                        return
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
                        Log.e(TAG, "order profile failure = " + response.body())
                    }
                }
            })
    }

    fun bindData(profile1Bean: ProfileInfoResponse?) {
        updateData(profile1Bean)
        bindDataInternal()
    }

    private fun updateData(profile1Bean: ProfileInfoResponse?) {
        if (profile1Bean == null) {
            return
        }
        if (TextUtils.isEmpty(firstName)) {
            firstName = profile1Bean.account_profile.first_name
        }
        if (TextUtils.isEmpty(middleName)) {
            middleName = profile1Bean.account_profile.middle_name
        }
        if (TextUtils.isEmpty(lastName)) {
            lastName = profile1Bean.account_profile.last_name
        }

        if (genderPos != 1 && genderPos != 2 && genderPos != 3) {
            if (TextUtils.equals(profile1Bean.account_profile.gender.toString(), "1")) {
                genderPos = 1
            } else if (TextUtils.equals(profile1Bean.account_profile.gender.toString(), "2")) {
                genderPos = 2
            } else if (TextUtils.equals(profile1Bean.account_profile.gender.toString(), "3")) {
                genderPos = 3
            }
        }
        if (TextUtils.isEmpty(bvn)) {
            bvn = profile1Bean.account_profile.bvn
        }
        if (TextUtils.isEmpty(email)) {
            email = profile1Bean.account_profile.email
        }
        if (state == null || area == null) {
            if (!TextUtils.isEmpty(profile1Bean.account_profile.home_address)) {
                val stateArr = profile1Bean.account_profile.home_address.split(":")
                state = stateArr[0]
                if (stateArr.size > 1) {
                    area = stateArr[1]
                }
            }
        }
        if (TextUtils.isEmpty(homeAddress)) {
            homeAddress = profile1Bean.account_profile.home_street
        }
        if (TextUtils.isEmpty(mBirthday)) {
            mBirthday = profile1Bean.account_profile.birthday
        }
    }

    private fun bindDataInternal() {
        if (!TextUtils.isEmpty(firstName)) {
            editFirstName?.setEditTextAndSelection(firstName!!)
        }
        if (!TextUtils.isEmpty(middleName)) {
            editMiddleName?.setEditTextAndSelection(middleName!!)
        }
        if (!TextUtils.isEmpty(lastName)) {
            editLastName?.setEditTextAndSelection(lastName!!)
        }
        if (genderPos != 0) {
            when (genderPos) {
                (1) -> {
                    selectGender?.setText("male")
                }

                (2) -> {
                    selectGender?.setText("female")
                }

                (3) -> {
                    selectGender?.setText("third gender")
                }
            }
        }
        if (!TextUtils.isEmpty(bvn)) {
            editBvn?.setEditTextAndSelection(bvn!!)
        }
        if (!TextUtils.isEmpty(email)) {
            editEmail?.setEditTextAndSelection(email!!)
        }
        if (area != null && !TextUtils.isEmpty(area)
            && state != null && !TextUtils.isEmpty(state)
        ) {
            selectAddress?.setText((state + ":" + area))
        }
        if (!TextUtils.isEmpty(homeAddress)) {
            editStreet?.setEditTextAndSelection(homeAddress!!)
        }
        if (!TextUtils.isEmpty(mBirthday)) {
            selectBirth?.setText(mBirthday)
        }

        selectMarital
        selectEducation
        selectAddress

    }

    private fun showTimePicker(listener: OnTimeSelectListener?) {
        if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
            KeyboardUtils.hideSoftInput(requireActivity())
        }
        //时间选择器
        val pvTime = TimePickerBuilder(context, listener).setSubmitText("ok")
            .setCancelText("cancel").build()
        // TODO
//        pvTime.setDate()
        pvTime.show()
    }

    fun showAreaPicker() {
        if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
            KeyboardUtils.hideSoftInput(requireActivity())
        }
        initAreaPickerData()
        if (provinceList.size == 0 || stateList.size == 0) {
            ConfigMgr.getAllConfig()
            ToastUtils.showShort("area data error , please wait a monment and try again")
            return
        }
        val pvOptions = OptionsPickerBuilder(context) { options1, options2, options3, v ->
            if (options1 != -1) {
                state = provinceList.get(options1)
            }
            if (options1 != -1 && options2 != -1) {
                area = stateList.get(options1).get(options2)
            }
            if (selectAddress != null && state != null && area != null) {
                selectAddress?.setText("$state-$area")
            }
            Log.i(TAG, " select province = $state select state = $area")
        }
        val view: OptionsPickerView<*> = pvOptions.setSubmitText("ok") //确定按钮文字
            .setCancelText("cancel") //取消按钮文字
            .setTitleText("city picker") //标题
            .setSubCalSize(18) //确定和取消文字大小
            .setTitleSize(20) //标题文字大小
            .setTitleColor(Color.BLACK) //标题文字颜色
            .setSubmitColor(resources.getColor(R.color.theme_color)) //确定按钮文字颜色
            .setCancelColor(resources.getColor(R.color.theme_color)) //取消按钮文字颜色
            .setContentTextSize(18) //滚轮文字大小
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setCyclic(false, false, false) //循环与否
            .isRestoreItem(true) //切换时是否还原，设置默认选中第一项。
            .build<Any>()

        var tempProvinceList: ArrayList<String> = ArrayList<String>()
        var tempStateList: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()
        for (i in 0 until provinceList.size) {
            tempProvinceList.add(provinceList[i].toString())
        }
        for (i in 0 until stateList.size) {
            var itemList = ArrayList<String>()
            var temp1: ArrayList<String> = stateList[i]
            for (j in 0 until temp1.size) {
                itemList.add(temp1[j])
            }
            tempStateList.add(itemList)
        }
        view.setPicker(tempProvinceList as List<Nothing>?, tempStateList as List<Nothing>?)
        view.setSelectOptions(0, 0, 0)
        view.show()
    }

    private val provinceList = ArrayList<String>()
    private val stateList = ArrayList<ArrayList<String>>()
    private fun initAreaPickerData() {
        provinceList.clear()
        stateList.clear()
        val areaData: HashMap<String, ArrayList<String>> = ConfigMgr.mAreaMap
        val iterator: Iterator<Map.Entry<String, ArrayList<String>>> = areaData.entries.iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            provinceList.add(key)
            val stateItemList = java.util.ArrayList<String>()
            stateItemList.addAll(value)
            stateList.add(stateItemList)
        }
    }
}