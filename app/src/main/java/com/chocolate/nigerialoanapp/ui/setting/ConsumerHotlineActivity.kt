package com.chocolate.nigerialoanapp.ui.setting

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ThreadUtils.SimpleTask
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.ZipUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.global.ConfigMgr
import com.chocolate.nigerialoanapp.global.Constant
import com.chocolate.nigerialoanapp.log.LogSaver
import com.chocolate.nigerialoanapp.ui.login.LoginRegisterFragment
import com.chocolate.nigerialoanapp.ui.setting.adapter.ConsumerHotlineAdapter
import com.chocolate.nigerialoanapp.utils.JumpUtils
import java.io.File

class ConsumerHotlineActivity : BaseActivity() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, ConsumerHotlineActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var ivBack: AppCompatImageView? = null
    private var rvPhone: RecyclerView? = null
    private var rvWhatApp: RecyclerView? = null
    private var rvEmail: RecyclerView? = null

    private val mPhoneList : ArrayList<String> = ArrayList<String>()
    private val mWhatappList : ArrayList<String> = ArrayList<String>()
    private val mEmailList : ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consumer_hotline)
        initialView()
    }

    private fun initialView() {
        ivBack = findViewById<AppCompatImageView>(R.id.iv_hotline_back)
        rvPhone = findViewById<RecyclerView>(R.id.rv_phone_content)
        rvWhatApp = findViewById<RecyclerView>(R.id.rv_whatapp_content)
        rvEmail = findViewById<RecyclerView>(R.id.rv_email_content)

        ConfigMgr.mConsumerData?.phone?.let {
            mPhoneList.clear()
            mPhoneList.addAll(it)
        }
        rvPhone?.layoutManager = GridLayoutManager(this, 2)
        val phoneAdapter = ConsumerHotlineAdapter(mPhoneList)
        phoneAdapter.setOnItemClickListener(object : ConsumerHotlineAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int, str: String) {
                executeCallPhone(str)
            }

        })
        rvPhone?.adapter = phoneAdapter

        ConfigMgr.mConsumerData?.whatsapp?.let {
            mWhatappList.clear()
            mWhatappList.addAll(it)
        }
        rvWhatApp?.layoutManager = GridLayoutManager(this, 2)
        val whatappAdapter = ConsumerHotlineAdapter(mWhatappList)
        whatappAdapter.setOnItemClickListener(object : ConsumerHotlineAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int, str: String) {
                checkAndToWhatApp(this@ConsumerHotlineActivity, str)
            }

        })
        rvWhatApp?.adapter = whatappAdapter

        ConfigMgr.mConsumerData?.email?.let {
            mEmailList.clear()
            mEmailList.addAll(it)
        }
        rvEmail?.layoutManager = GridLayoutManager(this, 2)
        val emailAdapter = ConsumerHotlineAdapter(mEmailList)
        emailAdapter.setOnItemClickListener(object : ConsumerHotlineAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int, email: String) {
                ThreadUtils.executeByCached(object : SimpleTask<String?>() {
                    @Throws(Throwable::class)
                    override fun doInBackground(): String? {
                        var logFoldPath = File(LogSaver.getLogFileFolder())
                        if (logFoldPath.listFiles().isNotEmpty()) {
                            val srcFile = logFoldPath.listFiles()[0]
                            val traceFile =
                                File(this@ConsumerHotlineActivity.filesDir.absolutePath + "/log/", "trace")
                            FileUtils.createFileByDeleteOldFile(traceFile)
                            val success = ZipUtils.zipFile(srcFile, traceFile)
                            if (success) {
                                return traceFile.absolutePath
                            }
                        }
                        return null
                    }

                    override fun onSuccess(result: String?) {
                        startFeedBackEmail(result, email)
                    }
                })
            }

        })
        rvEmail?.adapter = emailAdapter
    }

    private fun executeCallPhone(phoneNum: String) {
        var hasPermissions: Boolean = PermissionUtils.isGranted(Manifest.permission.CALL_PHONE)
        if (hasPermissions) {
            executeCallPhoneInternal(phoneNum)
        } else {
            PermissionUtils.permission(Manifest.permission.CALL_PHONE)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        executeCallPhoneInternal(phoneNum)
                    }

                    override fun onDenied() {
                        ToastUtils.showShort("Please allow permission")
                    }
                }).request()
        }
    }

    @SuppressLint("MissingPermission")
    private fun executeCallPhoneInternal(phoneNum: String) {
        PhoneUtils.call(phoneNum)
    }

    private fun checkAndToWhatApp(context: Context?, mobile: String) {
        if (context == null) {
            return
        }
        var isInstall = JumpUtils.isAppInstall(context, "com.whatsapp")
        if (isInstall) {
            JumpUtils.chatInWhatsApp(context, mobile)
        } else {
            ToastUtils.showShort("not exist whatsapp.")
        }
    }

    private fun startFeedBackEmail(traceFile: String?, email : String) {
        try {
            val data = Intent(Intent.ACTION_SEND)
            data.data = Uri.parse(email)
            data.setType("text/plain")
            val addressEmail = arrayOf<String>(email!!)
            data.putExtra(Intent.EXTRA_EMAIL, addressEmail)
            if (!Constant.isAabBuild()){
                val addressCC = arrayOf<String>("wang867103701@gmail.com")
                data.putExtra(Intent.EXTRA_CC, addressCC)
            }
            val appName = this.resources.getString(R.string.app_name) + ""
            data.putExtra(Intent.EXTRA_SUBJECT, "$appName Feedback")
            val mobile = SPUtils.getInstance().getString(LoginRegisterFragment.KEY_PHONE_NUM)
            data.putExtra(Intent.EXTRA_TEXT, "Hi:  num $mobile,")
            if (!TextUtils.isEmpty(traceFile)) {
                data.putExtra(
                    Intent.EXTRA_STREAM,
                    getFileUri(this, File(traceFile!!), getAuthority(this))
                )
            }
            startActivity(Intent.createChooser(data, "$appName Feedback:"))
        } catch (e: Exception) {
            if ((e is ActivityNotFoundException)) {
                ToastUtils.showShort(" not exist email app")
            }
            if (BuildConfig.DEBUG) {
                throw e
            }
        }
    }

    private fun getFileUri(context: Context, file: File, authority: String): Uri? {
        var useProvider = false
        var canAdd = false
        if (file.exists()) {
            useProvider = true
            canAdd = true
        }
        return if (canAdd) {
            if (useProvider) {
                FileProvider.getUriForFile(getDPContext(context), authority, file)
            } else {
                Uri.fromFile(file)
            }
        } else null
    }

    private fun getDPContext(context: Context): Context {
        var storageContext: Context = context
        if (Build.VERSION.SDK_INT >= 24) {
            if (!context.isDeviceProtectedStorage) {
                val deviceContext = context.createDeviceProtectedStorageContext()
                storageContext = deviceContext
            }
        }
        return storageContext
    }

    private fun getAuthority(context: Context) =
        context.applicationInfo.packageName + ".fileprovider"
}