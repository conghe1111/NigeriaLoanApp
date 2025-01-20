package com.chocolate.nigerialoanapp.ui.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity

class EditInfoActivity : BaseActivity() {

    companion object {
        fun showActivity(context: Context) {
            val intent = Intent(context, EditInfoActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_info)
        val basicFragment = EditBasicInfoFragment()
        toFragment(basicFragment)
    }

    override fun getFragmentContainerRes(): Int {
        return R.id.fl_edit_info_container
    }
}