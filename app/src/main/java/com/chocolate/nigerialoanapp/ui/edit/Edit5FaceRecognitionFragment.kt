package com.chocolate.nigerialoanapp.ui.edit

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.EditProfileBean
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.utils.JumpPermissionUtils
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class Edit5FaceRecognitionFragment : BaseEditFragment() {

    companion object {
        private const val TAG = "Edit5FaceRecognitionFragment"
        const val REQUEST_CAMERA_RECOGNITION = 1112
//        const val RESULT_CAMERA_RECOGNITION = 1117
    }

    private var tvNext: AppCompatTextView? = null
    private var tvDesc: AppCompatTextView? = null

    private var mFile: File? = null
    private var mCurPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_face_recognition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvNext = view.findViewById<AppCompatTextView>(R.id.tv_face_recognition_next)
        tvNext?.text = resources.getString(R.string.start_x_s, "5")
        tvNext?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                startCamera()
            }

        })
        tvDesc = view.findViewById<AppCompatTextView>(R.id.tv_face_recognition_desc)
        SpanUtils.setPrivacyString(tvDesc)
    }

    override fun bindData(profile1Bean: ProfileInfoResponse?) {
        updateData(profile1Bean)
        bindDataInternal()
    }

    private fun updateData(profile1Bean: ProfileInfoResponse?) {
        if (profile1Bean == null || profile1Bean.account_receive == null) {
            return
        }

    }

    private fun bindDataInternal() {

    }

    private fun uploadLive() {
        //        pbLoading?.visibility = View.VISIBLE
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
//                    live_photo	file	N	活体照片，如果有就上传，没有就忽略 (与data同级)
            jsonObject.put("live_verify", "1")     //活体验证 0 未验证 1已验证
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val file = File("")
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " update live = $jsonObject")
        }
        OkGo.post<String>(Api.UPDATE_LIVE).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .params("file", file)
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
//                    pbLoading?.visibility = View.GONE
//                    refreshLayout?.finishRefresh()
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update contact = " + response.body())
                    }
                }
            })
    }

    private fun startCamera() {
        val isGranted = PermissionUtils.isGranted(Manifest.permission.CAMERA)
        if (isGranted) {
            startCameraInternal(REQUEST_CAMERA_RECOGNITION)
        } else {
            PermissionUtils.permission(Manifest.permission.CAMERA)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        startCameraInternal(REQUEST_CAMERA_RECOGNITION)
                    }

                    override fun onDenied() {
                        if (isDestroy()) {
                            return
                        }
                        activity?.let {
                            ToastUtils.showShort("please allow permission.")
                            JumpPermissionUtils.goToSetting(activity)
                        }
                    }
                }).request()
        }
    }

    private fun startCameraInternal(requestCode: Int) {
        if (context == null || activity == null) {
            return
        }
        val path = (requireActivity().filesDir.path
                + "/photo/" + System.currentTimeMillis() + ".jpg")
        val file = File(path)
        FileUtils.createOrExistsFile(path)
        if (!file.exists()) {
            ToastUtils.showShort("can not create file")
            return
        }
        val imageFileUri: Uri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //跳转到相机Activity
        intent.putExtra("camerasensortype", 2) // 调用前置摄像头
        intent.putExtra("autofocus", true) // 自动对焦
        mCurPath = path
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            imageFileUri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".fileProvider",
                file
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri) //告诉相机拍摄完毕输出图片到指定的Uri
        } else {
            imageFileUri = Uri.fromFile(file) //获取文件的Uri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri) //告诉相机拍摄完毕输出图片到指定的Uri
        }
        activity?.startActivityForResult(intent, requestCode)
    }

    override fun onDestroy() {
        OkGo.getInstance().cancelTag(TAG)
        super.onDestroy()
    }

    fun onActivityResultInternal(requestCode: Int, data: Intent?) {
        mCurPath
        Log.e(TAG, " cur path = " + mCurPath)
    }
}