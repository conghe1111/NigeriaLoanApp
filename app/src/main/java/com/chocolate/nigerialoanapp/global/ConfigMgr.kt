package com.chocolate.nigerialoanapp.global

import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.View
import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.data.ConsumerData
import com.chocolate.nigerialoanapp.bean.response.BankBeanResponse
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.edit.EditInfoActivity
import com.chocolate.nigerialoanapp.ui.edit.EditInfoActivity.Companion
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

object ConfigMgr {

    private val TAG = "ConfigMgr"

    private const val KEY_DATA_CONFIG = "key_data_config"
    private const val KEY_BANK_LIST = "key_bank_list"
    private const val KEY_STATIC_DATA_CONFIG = "key_static_data_config"
    private const val KEY_PROFILE_INFO = "key_profile_info"

    val mDebtList = ArrayList<Pair<String, String>>()
    val mGenderList = ArrayList<Pair<String, String>>()
    val mEducationList = ArrayList<Pair<String, String>>()
    val mEmploymentList = ArrayList<Pair<String, String>>()
    val mMonthlyIncomeList = ArrayList<Pair<String, String>>()
    val mMaritalList = ArrayList<Pair<String, String>>()
    val mRelationShipList = ArrayList<Pair<String, String>>()
    val mPositionList = ArrayList<Pair<String, String>>()
    val mPayPeriodList = ArrayList<Pair<String, String>>()
    val mHaveOtherDebtList = ArrayList<Pair<String, String>>()
    val mAreaMap = HashMap<String, ArrayList<String>>()

    val mBankList = ArrayList<BankBeanResponse.Bank>()
    var mConsumerData: ConsumerData? = null

    fun getAllConfig() {
        mDebtList.clear()
        mDebtList.add(Pair("yes", "0"))
        mDebtList.add(Pair("no", "1"))

        mHaveOtherDebtList.clear()
        mHaveOtherDebtList.add(Pair("not have", "1"))
        mHaveOtherDebtList.add(Pair("have", "2"))

        initPayPeriod()

        mGenderList.clear()
        mGenderList.add(Pair("male", "1"))
        mGenderList.add(Pair("female", "2"))
        mGenderList.add(Pair("third gender", "3"))

        val dataConfig = SPUtils.getInstance().getString(KEY_DATA_CONFIG)
        if (!TextUtils.isEmpty(dataConfig)) {
            handleDataByJson(dataConfig)
        }
        val bankListJson = SPUtils.getInstance().getString(KEY_BANK_LIST)
        if (!TextUtils.isEmpty(bankListJson)) {
            updateBankList(bankListJson)
        }


        if (!TextUtils.isEmpty(dataConfig) && BuildConfig.DEBUG) {

        } else {
            getProfileConfig()
        }

        if (!TextUtils.isEmpty(bankListJson) && BuildConfig.DEBUG) {

        } else {
            getBankList()
        }
    }

    fun getStaticConfig() {
        val staticDataConfig = SPUtils.getInstance().getString(KEY_STATIC_DATA_CONFIG)
        if (!TextUtils.isEmpty(staticDataConfig)) {
            handleStaticConfig(staticDataConfig)
        } else {
            mConsumerData = ConsumerData()
            val list1 = ArrayList<String>()
            list1.add("support@owocredit.com")
            list1.add("customer@owocredit.com")
            mConsumerData!!.email = list1

            val list2 = ArrayList<String>()
            list2.add("2341234567890")
            list2.add("2341234567891")
            list2.add("2341234567892")
            mConsumerData!!.phone = list2

            val list3 = ArrayList<String>()
            list3.add("2341234567890")
            list3.add("2341234567891")
            mConsumerData!!.whatsapp = list3
        }
        if (!TextUtils.isEmpty(staticDataConfig) && BuildConfig.DEBUG) {

        } else {
            getStaticConfigInternal()
        }
    }

    private fun initPayPeriod() {
        mPayPeriodList.clear()
        for (index in 1 until 32) {
            mPayPeriodList.add(Pair(index.toString(), index.toString()))
        }
    }

    private fun getProfileConfig() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken) //FCM Token
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " get profile config = " + jsonObject.toString())
        }
        //        Log.e(TAG, "111 id = " + Constant.mAccountId);
        OkGo.post<String>(Api.PROFILE_CONFIG).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val body = response.body().toString()
                    val bodyJsonObject = JSONObject(body)
                    val dataJsonObject = bodyJsonObject.optJSONObject("data")
                    val json = dataJsonObject.toString()
                    if (!TextUtils.isEmpty(json)) {
                        SPUtils.getInstance().put(KEY_DATA_CONFIG, json)
                        handleDataByJson(json)
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "order config failure = " + response.body())
                    }
                }
            })
    }

    private fun handleDataByJson(json: String) {
        try {
            val dataJSONObject = JSONObject(json)
            val employmentList = parseItem("employment", dataJSONObject)
            if (employmentList.isNotEmpty()) {
                mEmploymentList.clear()
                mEmploymentList.addAll(employmentList)
            }

            val educationList = parseItem("education", dataJSONObject)
            if (educationList.isNotEmpty()) {
                mEducationList.clear()
                mEducationList.addAll(educationList)
            }
            val salaryList = parseItem("monthly_income", dataJSONObject)
            if (salaryList.isNotEmpty()) {
                mMonthlyIncomeList.clear()
                mMonthlyIncomeList.addAll(salaryList)
            }
            val maritalList = parseItem("marital_status", dataJSONObject)
            if (maritalList.isNotEmpty()) {
                mMaritalList.clear()
                mMaritalList.addAll(maritalList)
            }
            val relationshipList = parseItem("relationship", dataJSONObject)
            if (relationshipList.isNotEmpty()) {
                mRelationShipList.clear()
                mRelationShipList.addAll(relationshipList)
            }
            val positionList = parseItem("position", dataJSONObject)
            if (positionList.isNotEmpty()) {
                mPositionList.clear()
                mPositionList.addAll(positionList)
            }
            val stateCity = parseCityItem(dataJSONObject.optJSONObject("state_city"))
            if (stateCity != null && stateCity.isNotEmpty()) {
                mAreaMap.clear()
                mAreaMap.putAll(stateCity)
            }

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            }
        }
    }


    private fun parseCityItem(cityJSONObject: JSONObject?): HashMap<String, ArrayList<String>> {
        val map: HashMap<String, ArrayList<String>> =
            HashMap<String, ArrayList<String>>()
        try {
            if (cityJSONObject == null) {
                return map
            }
            val iterator = cityJSONObject.keys()
            while (iterator.hasNext()) {
                //州
                val state = iterator.next()
                val array = cityJSONObject.optJSONArray(state)
                val list = ArrayList<String>()
                for (i in 0 until array.length()) {
                    val jsonObject: JSONObject = array.optJSONObject(i)
                    val city = jsonObject.optString("city")
                    list.add(city)
                }
                map.put(state, list)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return map
    }

    private fun parseItem(
        key: String,
        bodyJSONObject: JSONObject
    ): ArrayList<Pair<String, String>> {
        val list: ArrayList<Pair<String, String>> = ArrayList<Pair<String, String>>()
        try {
            val keyJSONObject = bodyJSONObject.optJSONObject(key)
            if (keyJSONObject == null) {
                return list
            }
            val iterator = keyJSONObject.keys()
            while (iterator.hasNext()) {
                val value = iterator.next()
                val key = keyJSONObject.optString(value)
                list.add(Pair(key, value))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Collections.sort(list, object : Comparator<Pair<String, String>> {
            override fun compare(t1: Pair<String, String>, t2: Pair<String, String>): Int {
                val first = t1.second
                val second = t2.second
                var firstInt = 0
                var secondInt = 0
                try {
                    if (!TextUtils.isEmpty(first)) {
                        firstInt = first.toInt()
                    }
                    if (!TextUtils.isEmpty(second)) {
                        secondInt = second.toInt()
                    }
                } catch (e: Exception) {
                }
                return firstInt - secondInt
            }
        })
        return list
    }

    fun getBankList() {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.BANK_LIST).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val json = NetworkUtils.checkResponseSuccess(response).toString()
                    SPUtils.getInstance().put(KEY_BANK_LIST, json)
                    updateBankList(json)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update contact = " + response.body())
                    }
                }
            })
    }


    private fun updateBankList(json: String) {
        val responseBean: BankBeanResponse? =
            com.alibaba.fastjson.JSONObject.parseObject(json, BankBeanResponse::class.java)
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

        mBankList.clear()
        mBankList.addAll(responseBean.bank_list!!)
    }

    private fun handleStaticConfig(json: String) {
        val consumerData: ConsumerData? =
            com.alibaba.fastjson.JSONObject.parseObject(json, ConsumerData::class.java)
        if (consumerData == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, " handleStaticConfig .")
            }
            return
        }
        mConsumerData = consumerData

    }

    private fun getStaticConfigInternal() {
        val jsonObject: JSONObject = JSONObject()
        try {
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        OkGo.post<String>(Api.STATIC_CONFIG).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val json = NetworkUtils.checkResponseSuccess(response).toString()
                    SPUtils.getInstance().put(KEY_STATIC_DATA_CONFIG, json)
                    handleStaticConfig(json)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update contact = " + response.body())
                    }
                }
            })
    }

    var mProfileInfo: ProfileInfoResponse? = null

    fun getProfileInfo() {
        val profileInfoJson = SPUtils.getInstance().getString(KEY_PROFILE_INFO)
        if (!TextUtils.isEmpty(profileInfoJson)) {
            handleProfileInfo(profileInfoJson)
        }
        if (!TextUtils.isEmpty(profileInfoJson) && BuildConfig.DEBUG) {

        } else {
            getProfileInfoInternal()
        }
    }

    private fun handleProfileInfo(json : String) {
        val profileInfo: ProfileInfoResponse? =
            com.alibaba.fastjson.JSONObject.parseObject(json, ProfileInfoResponse::class.java)
        if (profileInfo == null) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, " getProfileInfoInternal .")
            }
            return
        }
        mProfileInfo = profileInfo
    }

    private fun getProfileInfoInternal() {
//        pbLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken) //FCM Token
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " get profile info = $jsonObject")
        }
        //        Log.e(TAG, "111 id = " + Constant.mAccountId);
        OkGo.post<String>(Api.PROFILE_INFO).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val json = NetworkUtils.checkResponseSuccess(response).toString()
                    SPUtils.getInstance().put(KEY_PROFILE_INFO, json)
                    handleProfileInfo(json)
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "order profile failure = " + response.body())
                    }
                }
            })
    }
}