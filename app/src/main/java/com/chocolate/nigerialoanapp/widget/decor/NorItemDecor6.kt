package com.chocolate.nigerialoanapp.widget.decor

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class NorItemDecor6 : ItemDecoration() {

   override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        super.getItemOffsets(outRect, itemPosition, parent)
        outRect.left = 0
        outRect.right = 0
        outRect.top = 6
        outRect.bottom = 6
////        //如果不是第一个，则设置top的值。
//        if (parent.getChildAdapterPosition(view) != 0){
//            //这里直接硬编码为1px
//            outRect.top = 1;
//        }
    }

}