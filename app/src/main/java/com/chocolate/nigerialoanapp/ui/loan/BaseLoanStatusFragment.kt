package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.ui.setting.ConsumerHotlineActivity
import com.chocolate.nigerialoanapp.utils.FirebaseUtils
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

open class BaseLoanStatusFragment : BaseFragment() {

    private var ivConsumer: AppCompatImageView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivConsumer = view.findViewById<AppCompatImageView>(R.id.iv_main_top_consumer)
        if (needInitConsumer()) {
            initConsumer()
        }
    }

    private fun initConsumer() {
        ivConsumer?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                context?.let {
                    onClickConsumer()

                    ConsumerHotlineActivity.startActivity(it)
                }
            }

        })
    }

    open fun onClickConsumer() {
        FirebaseUtils.logEvent("CLICK_SINGLEPRODUCTHOMEPAGE_CUSTOMERSERVICE")
    }

    open fun needInitConsumer() : Boolean {
        return true
    }
}