package com.chocolate.nigerialoanapp.ui.mine

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class NorMockItemDecor3 : ItemDecoration() {

   override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        super.getItemOffsets(outRect, itemPosition, parent)
       if (itemPosition % 2 == 0) {
           outRect.left = 15
           outRect.right = 24
       } else {
           outRect.left = 24
           outRect.right = 15
       }
        outRect.top = 8
        outRect.bottom = 8
////        //如果不是第一个，则设置top的值。
//        if (parent.getChildAdapterPosition(view) != 0){
//            //这里直接硬编码为1px
//            outRect.top = 1;
//        }
    }

}