package com.chocolate.nigerialoanapp.ui.mine

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class NorItemDecor3 : ItemDecoration() {

   override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        super.getItemOffsets(outRect, itemPosition, parent)
        outRect.left = 15
        outRect.right = 15
        outRect.top = 4
        outRect.bottom = 4
////        //如果不是第一个，则设置top的值。
//        if (parent.getChildAdapterPosition(view) != 0){
//            //这里直接硬编码为1px
//            outRect.top = 1;
//        }
    }

}