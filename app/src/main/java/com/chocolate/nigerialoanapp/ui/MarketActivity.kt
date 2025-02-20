package com.chocolate.nigerialoanapp.ui

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.ui.loan.Loan0NewProductFragment
import com.chocolate.nigerialoanapp.ui.login.LoginActivity

class MarketActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.acticity_market)
        val frament = Loan0NewProductFragment()
        toFragment(frament)
    }

    fun toLogin() {
        val welcomeIntent = Intent(this@MarketActivity, LoginActivity::class.java)
        startActivity(welcomeIntent)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_marget_container
    }
}