package com.chocolate.nigerialoanapp.ui.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.blankj.utilcode.util.BarUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class LoginActivity : BaseActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    var mPhoneNum: String? = null

    private var ivBack : AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this, true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_login)

        ivBack = findViewById<AppCompatImageView>(R.id.iv_login_back)
        val loginRegisterFragment = LoginRegisterFragment()
        toFragment(loginRegisterFragment)
        setBackVisible(false)

        ivBack?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                onBackPressed()
                setBackVisible(false)
                FirebaseUtils.logEvent("SYSTEM_LOGIN_REGISTER_BACK_SMS")
            }

        })
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_container
    }

    fun toLoginFragment() {
        setBackVisible(true)
        val registerFragment = LoginFragment()
        addFragment(registerFragment, LoginFragment.TAG)
    }

    fun toRegisterFragment(isFirstRegister: Boolean = false) {
        val registerFragment =
            if (isFirstRegister) FirstRegisterFragment() else ForgetRegisterFragment()
        setBackVisible(true)
        addFragment(registerFragment, if (isFirstRegister) FirstRegisterFragment.TAG else ForgetRegisterFragment.TAG)
    }




    fun setBackVisible(visibleFlag : Boolean) {
        ivBack?.visibility = if (visibleFlag) View.VISIBLE else View.INVISIBLE
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount >= 1) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            super.onBackPressed()
        }
    }

}