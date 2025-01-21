package com.chocolate.nigerialoanapp.ui.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject

class EditInfoActivity : BaseActivity() {

    companion object {

        private const val TAG = "EditInfoActivity"

        private const val STEP_1 = "step_1"
        private const val STEP_2 = "step_2"
        private const val STEP_3 = "step_3"

        fun showActivity(context: Context) {
            val intent = Intent(context, EditInfoActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var mCurFragment : EditBasic1Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_info)
        getProfileInfo()
        val basicFragment = EditBasic1Fragment()
        mCurFragment = basicFragment
        toFragment(basicFragment)
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
                    mCurFragment?.bindData(profileInfo)
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
}