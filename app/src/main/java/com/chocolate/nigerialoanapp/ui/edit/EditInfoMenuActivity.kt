package com.chocolate.nigerialoanapp.ui.edit

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseActivity

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

        basicView?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                EditInfoActivity.showActivity(this@EditInfoMenuActivity)
            }

        })
        workView?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                EditInfoActivity.showActivity(this@EditInfoMenuActivity)
            }

        })
        contactView?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                EditInfoActivity.showActivity(this@EditInfoMenuActivity)
            }

        })
        receiveView?.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                EditInfoActivity.showActivity(this@EditInfoMenuActivity)
            }

        })

    }
}