package com.chocolate.nigerialoanapp.ui.login

import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity

class LoginActivity : BaseActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    var mPhoneNum : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_login)

        val loginRegisterFragment = LoginRegisterFragment()
        toFragment(loginRegisterFragment)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_container
    }

    fun toLoginFragment() {
        val registerFragment = LoginFragment()
        toFragment(registerFragment)
    }

    fun toRegisterFragment(isFirstRegister : Boolean = false) {
        val registerFragment = if (isFirstRegister) FirstRegisterFragment() else ForgetRegisterFragment()
        toFragment(registerFragment)
    }
}