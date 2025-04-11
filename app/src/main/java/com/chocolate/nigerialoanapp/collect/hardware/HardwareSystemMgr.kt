package com.chocolate.nigerialoanapp.collect.hardware

import android.content.Context
import android.os.Build
import android.os.SystemClock
import com.blankj.utilcode.util.DeviceUtils
import com.chocolate.nigerialoanapp.bean.data.DeviceData
import com.chocolate.nigerialoanapp.collect.utils.TimeUtil
import java.util.Calendar
import java.util.TimeZone


object HardwareSystemMgr {

    fun getData(context: Context): DeviceData.System {
        val system = DeviceData.System()
        system.simulator = DeviceUtils.isEmulator().toString()
        system.adb_enabled =  DeviceUtils.isAdbEnabled().toString()
        system.rooted = DeviceUtils.isDeviceRooted().toString()
        system.time_zone = getCurrentTimeZone()
        val elapsedTime = Math.max(SystemClock.elapsedRealtime(), Build.TIME)
        system.turn_on_time = TimeUtil.format(elapsedTime)
        system.boot_time = elapsedTime.toString()
        system.up_time = TimeUtil.format(System.currentTimeMillis())
        return system
    }


   private fun getCurrentTimeZone(): String {
        val calendar: Calendar = Calendar.getInstance()
        val timeZone: TimeZone = calendar.getTimeZone()
        return timeZone.getID()
    }
}