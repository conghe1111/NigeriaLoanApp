package com.chocolate.nigerialoanapp.ui.edit

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.KeyboardUtils
import com.chocolate.nigerialoanapp.base.BaseFragment
import com.chocolate.nigerialoanapp.bean.response.ProfileInfoResponse
import com.chocolate.nigerialoanapp.ui.dialog.selectdata.SelectDataDialog
import com.chocolate.nigerialoanapp.utils.FirebaseUtils


abstract class BaseEditFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (this is Edit1BasicFragment) {
            FirebaseUtils.logEvent("SYSTEM_BASIC_INF_ENTER")
        } else if (this is Edit2WorkFragment) {
            FirebaseUtils.logEvent("SYSTEM_WORK_INF_ENTER")
        } else if (this is Edit3ContactFragment) {
            FirebaseUtils.logEvent("SYSTEM_WORK_INF_ENTER")
        } else if (this is Edit4BankFragment) {
            FirebaseUtils.logEvent("SYSTEM_WORK_INF_ENTER")
        } else if (this is Edit5FaceRecognitionFragment) {
            FirebaseUtils.logEvent("SYSTEM_WORK_INF_ENTER")
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected fun showListDialog(
        list: ArrayList<Pair<String, String>>,
        observer: SelectDataDialog.Observer
    ) {
        val dialog = SelectDataDialog(requireContext())
        val tempList = ArrayList<Pair<String, String>>()
        tempList.addAll(list)
        dialog.setList(tempList, observer)
        dialog.show()
    }

    protected fun getPairByValue(
        list: ArrayList<Pair<String, String>>,
        value: String
    ): Pair<String, String>? {
        for (pair in list) {
            if (TextUtils.equals(pair.second, value)) {
                return pair
            }
        }
        return null
    }

    protected fun scrollToPos(pos: Int, scrollView: NestedScrollView?) {
        val height = resources.getDimension(com.chocolate.nigerialoanapp.R.dimen.dp_80) * (pos - 1)
        scrollView?.scrollTo(0, height.toInt())
    }

    abstract fun bindData(profile1Bean: ProfileInfoResponse?)

    protected fun showCustomPicker(
        list: List<Pair<String, String>>,
        title: String,
        observer: SelectDataDialog.Observer
    ) { //条件选择器初始化，自定义布局
        if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
            KeyboardUtils.hideSoftInput(requireActivity())
        }
        val pvOptions = OptionsPickerBuilder(context) { options1, options2, options3, v ->
            if (options1 != -1) {
                val itemPair = list[options1]
                observer?.onItemClick(itemPair, options1)
            }
        }

        val view: OptionsPickerView<*> = pvOptions.setSubmitText("ok") //确定按钮文字
            .setCancelText("cancel") //取消按钮文字
            .setTitleText(title) //标题
            .setSubCalSize(18) //确定和取消文字大小
            .setTitleSize(20) //标题文字大小
            .setTitleColor(Color.BLACK) //标题文字颜色
            .setSubmitColor(resources.getColor(com.chocolate.nigerialoanapp.R.color.theme_color)) //确定按钮文字颜色
            .setCancelColor(resources.getColor(com.chocolate.nigerialoanapp.R.color.theme_color)) //取消按钮文字颜色
            .setContentTextSize(18) //滚轮文字大小
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setCyclic(false, false, false) //循环与否
            .isRestoreItem(true) //切换时是否还原，设置默认选中第一项。
            .build<Any>()
        val resultList = ArrayList<String>()
        for (item in list) {
            resultList.add(item.first)
        }
        view.setPicker(resultList as List<Nothing>?)
        view.setSelectOptions(0, 0, 0)
        view.show()
    }

    fun tryBindData() {
        if (activity is EditInfoActivity) {
            val profile1Bean = (activity as EditInfoActivity).mProfileInfo
            profile1Bean?.let {
                bindData(it)
            }
        }
    }

    open fun onClickSubmit(submitFlag: Boolean) {

    }

    open fun updateNextBtnStatus(needSelect: Boolean = true) {

    }

    private var hasSoftShow: Boolean = false
    fun onSoftInputChanged(softInputVisible: Boolean) {
        if (isDestroy()) {
            return
        }
        if (!isAdded) {
            return
        }
        if (softInputVisible) {
            hasSoftShow = true
        } else {
            if (hasSoftShow) {
                updateNextBtnStatus()
            }
            hasSoftShow = false
        }
    }

}