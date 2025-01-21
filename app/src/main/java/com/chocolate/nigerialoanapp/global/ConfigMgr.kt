package com.chocolate.nigerialoanapp.global

import android.text.TextUtils
import android.util.Log
import android.util.Pair
import com.blankj.utilcode.util.SPUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object ConfigMgr {

    private val TAG = "ConfigMgr"

    private const val KEY_DATA_CONFIG = "key_data_config"

    val mDebtList = ArrayList<Pair<String, String>>()
    val mGenderList = ArrayList<Pair<String, String>>()
    val mEducationList = ArrayList<Pair<String, String>>()
    val mSalaryList = ArrayList<Pair<String, String>>()
    val mMaritalList = ArrayList<Pair<String, String>>()
    val mRelationShipList = ArrayList<Pair<String, String>>()
    val mWorkList = ArrayList<Pair<String, String>>()
    val mAreaMap = HashMap<String, ArrayList<String>>()

//        val mBankList = ArrayList<BankResponseBean.Bank>()

    fun getAllConfig() {
        mDebtList.clear()
        mDebtList.add(Pair("yes", "0"))
        mDebtList.add(Pair("no", "1"))

        mGenderList.clear()
        mGenderList.add(Pair("male", "1"))
        mGenderList.add(Pair("female", "2"))
        mGenderList.add(Pair("third gender", "3"))

        val dataConfig = SPUtils.getInstance().getString(KEY_DATA_CONFIG)
        if (!TextUtils.isEmpty(dataConfig)) {
            handleDataByJson(dataConfig)
        }

        getProfileConfig()
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

            val educationList = parseItem("education", dataJSONObject)
            if (educationList.isNotEmpty()) {
                mEducationList.clear()
                mEducationList.addAll(educationList)
            }
            val salaryList = parseItem("monthly_income", dataJSONObject)
            if (salaryList.isNotEmpty()) {
                mSalaryList.clear()
                mSalaryList.addAll(salaryList)
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
                mWorkList.clear()
                mWorkList.addAll(positionList)
            }
            val stateCity = parseCityItem(dataJSONObject.optJSONObject("state_city"))
            if (stateCity != null && stateCity.isNotEmpty()) {
                mAreaMap.clear()
                mAreaMap.putAll(stateCity)
            }

        } catch (e : Exception) {
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
            if (keyJSONObject == null){
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


}