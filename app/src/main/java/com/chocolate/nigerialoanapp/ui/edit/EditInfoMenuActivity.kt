package com.chocolate.nigerialoanapp.ui.edit

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.BarUtils
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
        BarUtils.setStatusBarVisibility(this,true)
        BarUtils.setStatusBarColor(this, resources.getColor(R.color.white))
        BarUtils.setStatusBarLightMode(this, true)
        setContentView(R.layout.activity_edit_info_menu)
        basicView =  findViewById<View>(R.id.ll_basic_container)
        workView =  findViewById<View>(R.id.ll_work_container)
        contactView = findViewById<View>(R.id.ll_contact_container)
        receiveView = findViewById<View>(R.id.ll_receive_container)
        val ivBack = findViewById<View>(R.id.iv_back)
        val tvTitle = findViewById<AppCompatTextView>(R.id.tv_title)
        val ivConsumer = findViewById<View>(R.id.iv_consumer)

        tvTitle?.text = resources.getString(R.string.edit_information)
        ivBack?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                finish()
            }

        })
        ivConsumer?.visibility = View.GONE
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