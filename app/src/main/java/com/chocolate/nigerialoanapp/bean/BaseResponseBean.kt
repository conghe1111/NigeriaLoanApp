package com.chocolate.nigerialoanapp.bean

import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy

class BaseResponseBean {
    var data: Any? = null
    var code: Int? = null
    var message: String? = null


    fun isRequestSuccess(): Boolean {
        return code != null && code == 0 && message != null
                && TextUtils.equals(message, "SUCCESS")
    }

    fun isLogout() : Boolean {
//        if (head != null){
//           if (head!!.code == "401" || head!!.code == "405"){
//               return true
//           }
//        }
        return false;
    }

    fun getMsg(): String? {
        return message
    }

    fun getDataBody(): Any? {
        return data
    }

    fun getBodyStr(): String? {
        val gson = GsonBuilder() //             # 将DEFAULT改为STRING
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .serializeNulls().create()
        return gson.toJson(data)
    }
}