package com.chocolate.nigerialoanapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.BuildConfig
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.bean.SettingMineBean
import com.chocolate.nigerialoanapp.ui.mine.MineAdapter
import com.chocolate.nigerialoanapp.ui.mine.NorItemDecor
import com.chocolate.nigerialoanapp.ui.mine.PageType
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.INFORMATION

class MineFragment : BaseFragment() {

    private var mList: ArrayList<SettingMineBean> = ArrayList()

    private var rvMine : RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMine = view.findViewById<RecyclerView>(R.id.rv_content_mine)
        initializeView()
    }

    private fun initializeView() {
        buildSettingList()
        val adapter = MineAdapter(mList)
        rvMine?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvMine?.adapter = adapter
        rvMine?.addItemDecoration(NorItemDecor())
        adapter?.setOnClickListener(object :MineAdapter.OnClickListener {
            override fun OnClick(pos: Int, settingBean: SettingMineBean) {
                when (settingBean.type) {
                    INFORMATION -> {
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                    PageType.CUSTOMER_SERVICE -> {
                        if (checkClickFast()) {
                            return
                        }
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                    PageType.HISTORY_RECORD -> {

                    }
                    PageType.PRIVACY -> {

                    }
                    PageType.TERM_CONDITION -> {

                    }
                    PageType.VERSION -> {

                    }
                    PageType.TEST_1 -> {

                    }
                }
            }

        })
    }

    private fun buildSettingList() {
        mList.clear()
        mList.add(
            SettingMineBean(
                R.drawable.ic_setting_info,
                R.string.setting_information,
                PageType.INFORMATION
            )
        )
        mList.add(
            SettingMineBean(
                R.drawable.ic_setting_custom_service,
                R.string.setting_customer_service,
                PageType.CUSTOMER_SERVICE
            )
        )
        mList.add(
            SettingMineBean(
                R.drawable.ic_setting_history_record,
                R.string.setting_history_record  ,
                PageType.HISTORY_RECORD
            )
        )
        mList.add(
            SettingMineBean(
                R.drawable.ic_setting_privacy,
                R.string.setting_privacy,
                PageType.PRIVACY
            )
        )
        mList.add(SettingMineBean(R.drawable.ic_setting_term, R.string.setting_term_condition,
            PageType.TERM_CONDITION))
        mList.add(SettingMineBean(R.drawable.ic_setting_version, R.string.setting_version,
            PageType.VERSION))

//        if (BuildConfig.DEBUG && false) {
        if (BuildConfig.DEBUG) {
            mList.add(SettingMineBean(R.drawable.ic_setting_version, R.string.setting_debug, PageType.TEST_1))

        }
    }
}