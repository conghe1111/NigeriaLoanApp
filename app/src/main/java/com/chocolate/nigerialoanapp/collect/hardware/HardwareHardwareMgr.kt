package com.chocolate.nigerialoanapp.collect.hardware

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.text.format.Formatter
import com.blankj.utilcode.util.SDCardUtils
import com.chocolate.nigerialoanapp.bean.data.DeviceData

object HardwareHardwareMgr {

    fun getData(context : Context): DeviceData.Hardware {
        val hardware = DeviceData.Hardware()
        hardware.cpu_speed = ""
        hardware.nfc_function = "false"
        try {
            val phoneRam = getPhoneRam(context)
            hardware.phone_total_ram = Formatter.formatFileSize(context, phoneRam.first).toString()
            hardware.phone_available_ram =  Formatter.formatFileSize(context,phoneRam.second).toString()
            hardware.runtime_max_memory =  Formatter.formatFileSize(context,Runtime.getRuntime().maxMemory()).toString()
            hardware.runtime_available_memory =  Formatter.formatFileSize(context,Runtime.getRuntime().totalMemory()).toString()
            hardware.total_storage = Formatter.formatFileSize(context,SDCardUtils.getInternalTotalSize()).toString()
            hardware.available_storage = Formatter.formatFileSize(context,SDCardUtils.getInternalAvailableSize()).toString()
        } catch (e : Exception) {

        }
        return hardware
    }


    private fun getPhoneRam(context : Context) : Pair<Long, Long> {
        val am : ActivityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val outInfo = ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo)
        val availMem = outInfo.availMem
        val totalMem = outInfo.totalMem
        return Pair(totalMem, availMem)
    }
}