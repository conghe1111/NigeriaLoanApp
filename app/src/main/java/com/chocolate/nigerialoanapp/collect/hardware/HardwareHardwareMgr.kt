package com.chocolate.nigerialoanapp.collect.hardware

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import com.blankj.utilcode.util.SDCardUtils
import com.chocolate.nigerialoanapp.bean.data.DeviceData

object HardwareHardwareMgr {

    fun getData(context : Context): DeviceData.Hardware {
        val hardware = DeviceData.Hardware()
        hardware.cpu_speed = ""
        hardware.nfc_function = "false"

        val phoneRam = getPhoneRam(context)
        hardware.phone_total_ram = phoneRam.first.toString()
        hardware.phone_available_ram =  phoneRam.second.toString()
        hardware.runtime_max_memory =  Runtime.getRuntime().maxMemory().toString()
        hardware.runtime_available_memory =  Runtime.getRuntime().totalMemory().toString()
        hardware.total_storage = SDCardUtils.getInternalTotalSize().toString()
        hardware.available_storage = SDCardUtils.getInternalAvailableSize().toString()
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