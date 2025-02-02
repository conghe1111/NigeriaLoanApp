package com.chocolate.nigerialoanapp.ui.loan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.ui.HomeFragment
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

open class BaseProcessedFragment : BaseLoanStatusFragment() {
    private var tvNext: AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_process_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvNext = view.findViewById<AppCompatTextView>(R.id.tv_pay_button)

        tvNext?.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (parentFragment is HomeFragment) {
                    (parentFragment as HomeFragment).refreshData()
                }
            }

        })
    }
}