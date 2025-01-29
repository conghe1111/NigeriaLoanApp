package com.chocolate.nigerialoanapp.ui.mine

import androidx.annotation.IntDef
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.CUSTOMER_SERVICE
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.HISTORY_RECORD
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.INFORMATION
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.LOGOUT
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.PRIVACY
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.TERM_CONDITION
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.TEST_1
import com.chocolate.nigerialoanapp.ui.mine.PageType.Companion.VERSION


@IntDef(INFORMATION, CUSTOMER_SERVICE, HISTORY_RECORD, PRIVACY, TERM_CONDITION, VERSION, TEST_1, LOGOUT)
@Retention(AnnotationRetention.SOURCE)
annotation class PageType {
    companion object {
        const val INFORMATION = 0
        const val CUSTOMER_SERVICE = 1
        const val HISTORY_RECORD = 2
        const val PRIVACY = 3
        const val TERM_CONDITION = 4
        const val VERSION = 5
        const val TEST_1 = 6

        const val LOGOUT = 111

    }
}