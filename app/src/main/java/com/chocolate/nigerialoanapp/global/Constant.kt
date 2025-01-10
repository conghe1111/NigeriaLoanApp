package com.chocolate.nigerialoanapp.global

import com.chocolate.nigerialoanapp.BuildConfig

object Constant {

    fun isAabBuild(): Boolean {
        return if (BuildConfig.DEBUG) false else BuildConfig.IS_AAB_BUILD
    }

    const val IS_COLLECT= !BuildConfig.IS_AAB_BUILD
}