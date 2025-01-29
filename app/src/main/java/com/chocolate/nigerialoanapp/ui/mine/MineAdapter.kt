package com.chocolate.nigerialoanapp.ui.mine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.SettingMineBean
import com.chocolate.nigerialoanapp.utils.interf.NoDoubleClickListener

class MineAdapter : RecyclerView.Adapter<ViewHolder> {

    private var mList: List<SettingMineBean>? = null
    private var mListener: OnClickListener? = null

    private val TYPE_1 = 111
    private val TYPE_2 = 112

    constructor(list: List<SettingMineBean>?) {
        this.mList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_2) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_setting_logout, parent, false)
            return SettingLogoutHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
            return SettingHolder(view)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is SettingHolder) {
            bindMineSetting(holder, position)
        } else if (holder is SettingLogoutHolder) {
            bindMineLogout(holder, position)
        }
    }

    private fun bindMineLogout(holder: SettingLogoutHolder, position: Int) {
        var settingBean = mList!!.get(position)
        holder.tvLogout?.setText(settingBean.title)
        holder.flContainer?.setOnClickListener(object : NoDoubleClickListener(){
            override fun onNoDoubleClick(v: View?) {
                mListener?.OnClick(position, settingBean)
            }

        })
    }


    private fun bindMineSetting(holder: SettingHolder, position: Int) {
        var settingBean = mList!!.get(position)
        holder.ivLeftIcon?.setImageResource(settingBean.leftIconRes)
        holder.tvTitle?.setText(settingBean.title)
        holder.flContainer?.setOnClickListener(object : NoDoubleClickListener(){
            override fun onNoDoubleClick(v: View?) {
                mListener?.OnClick(position, settingBean)
            }

        })
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList == null) {
            TYPE_1
        } else {
            if (mList!![position].type == PageType.LOGOUT) {
                TYPE_2
            } else {
                TYPE_1
            }
        }
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    interface OnClickListener {
        fun OnClick(pos: Int, settingBean: SettingMineBean)
    }

    inner class SettingHolder : RecyclerView.ViewHolder {
        var llContainer: LinearLayout? = null
        var flContainer: FrameLayout? = null
        var ivLeftIcon: ImageView? = null
        var tvTitle: TextView? = null

        constructor(itemView: View) : super(itemView) {
            llContainer = itemView.findViewById(R.id.ll_setting_container)
            ivLeftIcon = itemView.findViewById(R.id.iv_setting_left_icon)
            tvTitle = itemView.findViewById(R.id.tv_setting_left_title)
            flContainer = itemView.findViewById(R.id.fl_setting_container)
        }
    }

    inner class SettingLogoutHolder : RecyclerView.ViewHolder {
        var flContainer: FrameLayout? = null
        var tvLogout: TextView? = null

        constructor(itemView: View) : super(itemView) {
            flContainer = itemView.findViewById(R.id.fl_setting_container)
            tvLogout = itemView.findViewById(R.id.tv_setting_logout)
        }
    }
}