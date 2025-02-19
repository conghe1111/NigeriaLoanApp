package com.chocolate.nigerialoanapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.chocolate.nigerialoanapp.global.Constant

object RouteUtils {

    fun toDeeplinkIntent(context: Context?) {
        if (context == null) {
            return
        }
        val intent = Intent()
        intent.setAction("android.intent.action.VIEW")
        intent.setData(Uri.parse( Constant.DEEP_LINK))
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addCategory("android.intent.category.BROWSABLE")
        context.startActivity(intent)

    }
}