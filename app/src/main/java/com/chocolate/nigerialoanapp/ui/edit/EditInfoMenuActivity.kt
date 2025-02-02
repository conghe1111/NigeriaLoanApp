package com.chocolate.nigerialoanapp.ui.edit

import android.os.Bundle
import android.view.View
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class EditInfoMenuActivity : BaseActivity() {

    private var basicView : View? = null
    private var workView : View? = null
    private var contactView : View? = null
    private var receiveView : View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_info_menu)
        basicView =  findViewById<View>(R.id.ll_basic_container)
        workView =  findViewById<View>(R.id.ll_work_container)
        contactView = findViewById<View>(R.id.ll_contact_container)
        receiveView = findViewById<View>(R.id.ll_receive_container)
        val ivBack = findViewById<View>(R.id.iv_menu_back)

        ivBack?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                finish()
            }

        })

        basicView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                EditInfoActivity.showActivity(this@EditInfoMenuActivity, EditInfoActivity.STEP_1)
            }

        })
        workView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                EditInfoActivity.showActivity(this@EditInfoMenuActivity, EditInfoActivity.STEP_2)
            }

        })
        contactView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                EditInfoActivity.showActivity(this@EditInfoMenuActivity, EditInfoActivity.STEP_3)
            }

        })
        receiveView?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                EditInfoActivity.showActivity(this@EditInfoMenuActivity, EditInfoActivity.STEP_4)
            }

        })

    }
}