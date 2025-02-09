package com.chocolate.nigerialoanapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


object DateUtils {

    fun getDateStr(): String {
        val sf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        val str: String = sf.format(date)

        return str
    }

    fun buildDateFormat(millions: Long): Array<String> {
        val sf: SimpleDateFormat = SimpleDateFormat("yyyy")
        val date = Date(millions)
        val yearStr: String = sf.format(date)

        val monthSf: SimpleDateFormat = SimpleDateFormat("MMM")
        val monthStr: String = monthSf.format(date)

        val daySf: SimpleDateFormat = SimpleDateFormat("dd")
        val dayStr: String = daySf.format(date)
        return arrayOf<String>(yearStr, monthStr, dayStr + "Th")
    }

    fun getDateArray(millions: Long): Array<String> {
        val m1 = getDate(millions, 0).time.toString()
        val m2 = getDate(millions, 7).time.toString()
        val m3 = getDate(millions, 14).time.toString()
        val m4 = getDate(millions, 21).time.toString()
        return arrayOf<String>(m1, m2, m3, m4)
    }

    private fun getDate(millions: Long, plusDay: Int): Date {
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = Date(millions)
        // 计算7天后的日期
        calendar.add(Calendar.DATE, plusDay) // 加7天
        return calendar.time
    }
}