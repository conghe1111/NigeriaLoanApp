package com.chocolate.nigerialoanapp.ui.mine

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class NorItemDecor : ItemDecoration() {

   override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        super.getItemOffsets(outRect, itemPosition, parent)
        outRect.left = 20
        outRect.right = 20
        outRect.top = 10
        outRect.bottom = 10
////        //如果不是第一个，则设置top的值。
//        if (parent.getChildAdapterPosition(view) != 0){
//            //这里直接硬编码为1px
//            outRect.top = 1;
//        }
    }

}