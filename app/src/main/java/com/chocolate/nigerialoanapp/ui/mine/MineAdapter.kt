package com.chocolate.nigerialoanapp.ui.mine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocolate.nigerialoanapp.R
import com.chocolate.nigerialoanapp.bean.SettingMineBean

class MineAdapter : RecyclerView.Adapter<MineAdapter.SettingHolder> {

    private var mList : List<SettingMineBean>? = null
    private var mListener : OnClickListener?  = null

    constructor(list : List<SettingMineBean>?){
        this.mList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
        return SettingHolder(view)
    }


    override fun onBindViewHolder(holder: SettingHolder, position: Int) {
        var settingBean = mList!!.get(position)
        holder.ivLeftIcon?.setImageResource(settingBean.leftIconRes)
        holder.tvTitle?.setText(settingBean.title)
        holder.flContainer?.setOnClickListener(View.OnClickListener {

            mListener?.OnClick(position, settingBean)
        })
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    fun setOnClickListener(listener: OnClickListener){
        mListener = listener
    }

    interface OnClickListener {
        fun OnClick(pos : Int, settingBean: SettingMineBean)
    }

   inner class SettingHolder : RecyclerView.ViewHolder {
        var llContainer : LinearLayout? = null
        var flContainer : FrameLayout? = null
        var ivLeftIcon : ImageView? = null
        var tvTitle : TextView? = null

        constructor(itemView :View) : super(itemView) {
            llContainer = itemView.findViewById(R.id.ll_setting_container)
            ivLeftIcon =  itemView.findViewById(R.id.iv_setting_left_icon)
            tvTitle =  itemView.findViewById(R.id.tv_setting_left_title)
            flContainer =  itemView.findViewById(R.id.fl_setting_container)
        }
    }
}