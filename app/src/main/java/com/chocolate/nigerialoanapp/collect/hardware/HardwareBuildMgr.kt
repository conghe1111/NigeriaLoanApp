package com.chocolate.nigerialoanapp.collect.hardware

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.PhoneUtils
import com.chocolate.nigerialoanapp.bean.data.DeviceData


object HardwareBuildMgr {

//    jsonObject.put("osDevice", Build.DEVICE) //获取设备驱动名称
//    jsonObject.put("osDisplay", Build.DISPLAY) //获取设备显示的版本包（在系统设置中显示为版本号）和ID一样
//    jsonObject.put("osHardware", Build.HARDWARE) //设备硬件名称,一般和基板名称一样（BOARD）
//    jsonObject.put("osHost", Build.HOST) //设备主机地址
//    jsonObject.put("osId", Build.ID) //设备版本号
//    jsonObject.put("osModel", DeviceUtils.getModel()) //获取手机的型号 设备名称
//    jsonObject.put("osManufacturer", DeviceUtils.getManufacturer()) //获取设备制造商
//    jsonObject.put("osProduct", Build.PRODUCT) //整个产品的名称
//    jsonObject.put("osTags", Build.TAGS) //设备标签。如release-keys 或测试的 test-keys
//    jsonObject.put("osTime", Build.TIME) //时间
//    jsonObject.put("osType", Build.TYPE) //设备版本类型  主要为"user" 或"eng".
//    jsonObject.put("osUser", Build.USER) //设备用户名 基本上都为android-build
//    jsonObject.put("osTime", Build.TIME) //时间
//    jsonObject.put("osvRelease", Build.VERSION.RELEASE) //获取系统版本字符串。如4.1.2 或2.2 或2.3等
//    jsonObject.put("osvCodename", Build.VERSION.CODENAME) //设备当前的系统开发代号，一般使用REL代替
//    jsonObject.put("osvIncremental", Build.VERSION.INCREMENTAL) //系统源代码控制值，一个数字或者git hash值
//    jsonObject.put("osvSdk", Build.VERSION.SDK) //系统的API级别 一般使用下面大的SDK_INT 来查看
//    jsonObject.put("osvSdkInt", DeviceUtils.getSDKVersionCode()) //系统的API级别 数字

    @SuppressLint("MissingPermission")
    fun getData(context: Context): DeviceData.Build {
        val build = DeviceData.Build()
        // 手机名
        build.mobile_name = Build.BRAND + "_" +  Build.ID
        // 操作系统
        build.os =  DeviceUtils.getSDKVersionCode().toString()
        // 产品的名称
        build.product = Build.PRODUCT
        // 设备型号
        build.model = Build.MANUFACTURER + "_" + DeviceUtils.getModel()
        // 出厂签名
        build.finger_print = ""
        // 序列号
        try {
            build.device_serial = PhoneUtils.getSerial().toString()
        } catch (e : Exception) {

        }

        // 版本类型user,eng
        build.device_version_type = Build.TYPE
        return build
    }

}