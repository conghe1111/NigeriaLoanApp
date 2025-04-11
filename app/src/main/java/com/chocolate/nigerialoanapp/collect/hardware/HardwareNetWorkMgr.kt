package com.chocolate.nigerialoanapp.collect.hardware

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import android.text.TextUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.PhoneUtils
import com.chocolate.nigerialoanapp.bean.data.DeviceData
import com.chocolate.nigerialoanapp.collect.HardWareUtils


object HardwareNetWorkMgr {

    fun getNetWork(context: Context): DeviceData.NetWork {
        val netWork = DeviceData.NetWork()
        // 手机国家代码
        netWork.phone_country_code = NetworkUtils.getNetworkOperatorName()
        netWork.ip = NetworkUtils.getIPAddress(true)
        // 设备MAC地址
        netWork.device_mac = NetworkUtils.getNetMaskByWifi()
        // 1代表未插卡
        netWork.sim_state = PhoneUtils.isSimCardReady().toString()
        // 电话卡数量
        netWork.phonecard_count = getSimCount(context).toString()
        // 运营商名称
        netWork.operator_name = getOperatorName(context)
        // 设备通话时网络类型
        netWork.telephony_signal_type = getNetworkType()
        // 信号强度
        netWork.signal_strength = "4"
        // 网络类型
        netWork.network_type = getNetworkType()
        netWork.current_wifi = NetworkUtils.getSSID()
        // 是否设置wifi代理
        netWork.is_wifi_proxy = "" + isUsingProxy(context)
        // 路由器mac地址
        netWork.router_mac = NetworkUtils.getIpAddressByWifi()
        return netWork
    }

    /**
     * 是否开启了代理
     *
     * @return
     */
    private fun isUsingProxy(context: Context): Boolean {
        val proxyAddress = System.getProperty("http.proxyHost")
        var proxyPort = 0
        var portStr = System.getProperty("http.proxyPort");
        if (!TextUtils.isEmpty(portStr)) {
            proxyPort = Integer.parseInt(portStr);
        }
        val wifiProxy = !TextUtils.isEmpty(proxyAddress) && proxyPort != 0;
        return wifiProxy;
    }


    private fun getNetworkType(): String {
        var netWorkType: String = ""
        when (NetworkUtils.getNetworkType()) {
            (NetworkUtils.NetworkType.NETWORK_ETHERNET) -> {
                netWorkType = "ethernet"
            }

            (NetworkUtils.NetworkType.NETWORK_WIFI) -> {
                netWorkType = "wifi"
            }

            (NetworkUtils.NetworkType.NETWORK_5G) -> {
                netWorkType = "5g"
            }

            (NetworkUtils.NetworkType.NETWORK_4G) -> {
                netWorkType = "4g"
            }

            (NetworkUtils.NetworkType.NETWORK_3G) -> {
                netWorkType = "3g"
            }

            (NetworkUtils.NetworkType.NETWORK_2G) -> {
                netWorkType = "2g"
            }

            (NetworkUtils.NetworkType.NETWORK_UNKNOWN) -> {
                netWorkType = "unknow"
            }

            (NetworkUtils.NetworkType.NETWORK_NO) -> {
                netWorkType = "no"
            }

            else -> {
                netWorkType = "unknow"
            }
        }
        return netWorkType
    }

    private fun getOperatorName(context: Context): String? {
        var result: String? = HardWareUtils.getSimOperatorName(context)
        if (TextUtils.isEmpty(result)) {
            result = HardWareUtils.getSimOperator(context)
        }
        return result
    }

    @SuppressLint("MissingPermission")
    private fun getSimCount(context: Context?): Int {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionManager = SubscriptionManager.from(context)
                // 对于双SIM设备，返回2；否则，返回1。
                return subscriptionManager?.activeSubscriptionInfoCount ?: 1
            }
        } catch (e: Exception) {

        }
        return 1
    }
}