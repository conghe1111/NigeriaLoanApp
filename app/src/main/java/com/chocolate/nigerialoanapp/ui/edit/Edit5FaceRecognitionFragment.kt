package com.chocolate.nigerialoanapp.ui.edit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.api.Api
import com.chocolate.nigerialoanapp.bean.response.EditProfileBean
import com.chocolate.nigerialoanapp.bean.response.FaceIdResponse
import com.chocolate.nigerialoanapp.bean.response.OrderApplyResponse
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.network.NetworkUtils
import com.chocolate.nigerialoanapp.ui.loanapply.LoanApplyActivity
import com.chocolate.nigerialoanapp.ui.loanapply.LoanApplyActivity.Companion
import com.chocolate.nigerialoanapp.ui.loanapply.LoanApplyActivity.Companion.RESULT_CODE
import com.chocolate.nigerialoanapp.utils.JumpPermissionUtils
import com.chocolate.nigerialoanapp.utils.SpanUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener
import com.chocolate.nigerialoanapp.utils.luban.Luban
import com.chocolate.nigerialoanapp.utils.luban.OnCompressListener
import com.easeid.opensdk.EaseID
import com.easeid.opensdk.model.EaseRequest
import com.easeid.opensdk.model.EaseResponse
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.Locale

class Edit5FaceRecognitionFragment : BaseEditFragment() {

    companion object {
        private const val TAG = "Edit5FaceRecogFragment"
        const val REQUEST_CAMERA_RECOGNITION = 1112
//        const val RESULT_CAMERA_RECOGNITION = 1117
    }

    private var tvNext: AppCompatTextView? = null
    private var tvDesc: AppCompatTextView? = null
    private var ivStatus: AppCompatImageView? = null
    private var ivDebugPic: AppCompatImageView? = null
    private var tvStatus: AppCompatTextView? = null
    private var tvRetry: AppCompatTextView? = null

    private var includeStart: View? = null
    private var includeLoading: View? = null

    private var mCurPath: String? = null

    private val STATUS_START: Int = 110
    private val STATUS_WAIT: Int = 111
    private val STATUS_PASS: Int = 112
    private val STATUS_FAIL: Int = 113

    private var mStatus: Int = STATUS_START

    private val mHandler = Handler(
        Looper.getMainLooper()
    ) { message ->
        when (message.what) {

        }
        false
    }

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
        SpanUtils.setPrivacyString(tvDesc, activity)

        ivDebugPic = view.findViewById<AppCompatImageView>(R.id.iv_debug_pic)
        if (BuildConfig.DEBUG) {
            ivDebugPic?.visibility = View.VISIBLE
        }
        includeStart = view.findViewById<View>(R.id.include_start)
        includeLoading = view.findViewById<View>(R.id.include_loading)

        ivStatus = view.findViewById<AppCompatImageView>(R.id.iv_face_recognition_status)
        tvStatus = view.findViewById<AppCompatTextView>(R.id.tv_face_recognition_status)
        tvRetry = view.findViewById<AppCompatTextView>(R.id.tv_face_recognition_retry)
        tvRetry?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                mStatus = STATUS_START
                updateStatus()
                mCurPath = null
                tvNext?.text = resources.getString(R.string.start_x_s, "5")
                tvNext?.isSelected = false
                startTimer()
            }

        })
        updateStatus()
        startTimer()
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

    private fun uploadLive(file: File) {
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
//                    live_photo	file	N	活体照片，如果有就上传，没有就忽略 (与data同级)
            jsonObject.put("live_verify", "1")     //活体验证 0 未验证 1已验证
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkHttpClient", " update live = $jsonObject")
            Log.i("OkHttpClient", " update live 1 = ${file.exists()}")
        }
        OkGo.post<String>(Api.UPDATE_LIVE).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .params("live_photo", file)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    val editProfileBean: EditProfileBean? =
                        checkResponseSuccess(response, EditProfileBean::class.java)
                    if (editProfileBean == null) {
                        mStatus = STATUS_FAIL
                        Log.e(TAG, " update live ." + response.body())
                    } else {
                        mStatus = STATUS_PASS
                        mHandler?.postDelayed(Runnable {
                            if (isDestroy()) {
                                return@Runnable
                            }
                            if (activity is EditInfoActivity) {
                                (activity as EditInfoActivity).nextStep(editProfileBean)
                            }
                        }, 2000)
                    }
                    updateStatus()
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (isDestroy()) {
                        return
                    }
                    mStatus = STATUS_FAIL
                    updateStatus()
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, " update live = " + response.body())
                    }
                }
            })
    }

    private fun startCamera() {
        val isGranted = PermissionUtils.isGranted(Manifest.permission.CAMERA)
        if (isGranted) {
//            startCameraInternal(REQUEST_CAMERA_RECOGNITION)
            startFaceRecognitionInternal()
        } else {
            PermissionUtils.permission(Manifest.permission.CAMERA)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
//                        startCameraInternal(REQUEST_CAMERA_RECOGNITION)
                        startFaceRecognitionInternal()
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

    private fun startFaceRecognitionInternal() {
        if (context == null || activity == null) {
            return
        }
        showProgressDialogFragment()
        val jsonObject: JSONObject = NetworkUtils.getJsonObject()
        try {
            jsonObject.put("account_id", Constant.mAccountId)
            jsonObject.put("access_token", Constant.mToken)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (BuildConfig.DEBUG) {
            Log.i("OkhttpClient", " FaceIdResponse =" + jsonObject.toString())
        }
        OkGo.post<String>(Api.FACE_ID).tag(TAG)
            .params("data", NetworkUtils.toBuildParams(jsonObject))
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    if (isDestroy()) {
                        return
                    }
                    dismissProgressDialogFragment()
                    val faceIdResponse =
                        checkResponseSuccess(response, FaceIdResponse::class.java)
                    if (faceIdResponse == null || faceIdResponse.face_id == null) {
                        mStatus = STATUS_FAIL
                        updateStatus()
                        return
                    }
                    startFace(faceIdResponse.face_id.toString())
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    dismissProgressDialogFragment()
                    mStatus = STATUS_FAIL
                    updateStatus()
                }
            })
    }

    private fun startFace(faceId : String) {
        EaseID.startFace(requireContext(), EaseRequest(bizId = faceId, userId = Constant.mAccountId), object : EaseID.ILiveIDListener {
            override fun onCompleted(response: EaseResponse) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "onCompleted : ${response.livenessFilePath}")
                }
                mCurPath = response.livenessFilePath
                onActivityResultInternal(0, null)
                // 活体成功, 后续业务逻辑代码
            }

            override fun onInterrupted(code: String?, error: String?) {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "onInterrupted : $code $error")
                }
                // 活体失败, 业务处理
                mStatus = STATUS_FAIL
                updateStatus()
            }
        })
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
                BuildConfig.APPLICATION_ID + ".fileprovider",
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
        mHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    fun onActivityResultInternal(requestCode: Int, data: Intent?) {
        if (TextUtils.isEmpty(mCurPath)) {
            return
        }
        val curFile = File(mCurPath)
        if (!curFile.exists()) {
            return
        }
        mStatus = STATUS_WAIT
        if (BuildConfig.DEBUG) {
            ivDebugPic?.let {
                Glide.with(this@Edit5FaceRecognitionFragment).load(curFile).into(it)
            }
            Log.e(TAG, " cur path = " + mCurPath + " exists = " + curFile.exists())
        }
        updateStatus()
        val curFile1 = File(mCurPath)
        Luban.with(context)
            .load(curFile1)
            .ignoreBy(100)
            .setTargetDir(curFile1.parent)
            .filter { path ->
                !(TextUtils.isEmpty(path) || path.lowercase(Locale.getDefault())
                    .endsWith(".gif"))
            }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {
                }

                override fun onSuccess(file: File) {
                    if (BuildConfig.DEBUG) {
                        Log.e("Okhttp", " unzip size = " + FileUtils.getSize(file) + " path .  " + file.absolutePath)
                    }
                    uploadLive(file)
                }

                override fun onError(e: Throwable) {
                    if (BuildConfig.DEBUG) {
                        Log.e("Okhttp", " error = " + e.message)
                    }
                    mStatus = STATUS_FAIL
                    updateStatus()
                }
            }).launch()

    }

    private fun updateStatus() {
        when (mStatus) {
            (STATUS_START) -> {
                includeStart?.visibility = View.VISIBLE
                includeLoading?.visibility = View.GONE
                tvNext?.isSelected = false
                tvNext
            }
            (STATUS_WAIT) -> {
                includeStart?.visibility = View.GONE
                includeLoading?.visibility = View.VISIBLE
                ivStatus?.visibility = View.GONE
                tvStatus?.text = resources.getString(R.string.please_wait)
                tvRetry?.visibility = View.GONE
            }
            (STATUS_PASS) -> {
                includeStart?.visibility = View.GONE
                includeLoading?.visibility = View.VISIBLE
                ivStatus?.visibility = View.VISIBLE
                ivStatus?.setImageResource(R.drawable.ic_confirm)
                tvStatus?.text = resources.getString(R.string.pass)
                tvRetry?.visibility = View.GONE
            }
            (STATUS_FAIL) -> {
                includeStart?.visibility = View.GONE
                includeLoading?.visibility = View.VISIBLE
                ivStatus?.visibility = View.VISIBLE
                ivStatus?.setImageResource(R.drawable.ic_failure)
                tvStatus?.text = resources.getString(R.string.fail)
                tvRetry?.visibility = View.VISIBLE
            }
        }
    }

    private var mTimeCount: TimeCount? = null

    private fun startTimer() {
        mTimeCount?.cancel()
        mTimeCount = TimeCount(5 * 1000, 1000)
        mTimeCount?.start()
    }

    inner class TimeCount(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        @SuppressLint("StringFormatMatches")
        override fun onTick(l: Long) {
            val countDownTime = l / 1000
            val startXStr = resources.getString(R.string.start_x_s, countDownTime)
            tvNext?.text = startXStr
        }

        override fun onFinish() {
            val startStr = resources.getString(R.string.start)
            tvNext?.text = startStr
            tvNext?.isSelected = true
        }
    }

}