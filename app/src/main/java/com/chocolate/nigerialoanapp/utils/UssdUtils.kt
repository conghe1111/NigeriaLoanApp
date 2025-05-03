package com.chocolate.nigerialoanapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri

object UssdUtils {

    fun toSendUssdCodeActivity(activity: Activity, uriStr : String) {
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        val tel = Uri.encode(uriStr)
        intent.data = Uri.parse("tel:$tel")
        activity?.startActivity(intent)
    }
}