package com.chocolate.nigerialoanapp.bean.data


class DeviceData {
    var idInfo : IdInfo? = null
    var locationInfo : Location? = null
    var networkInfo : NetWork? = null
    var buildInfo : Build? = null
    var hardInfo : Hardware? = null
    var systemInfo : System? = null
    var batteryInfo : Battery? = null
    var displayInfo : Display? = null
    var appInfo : App? = null


    class IdInfo {
        var android_id : String? = null // android编号
        var gsf_id : String? = null // 谷歌服务框架id
        var gaid : String? = null   // google Ad ID

        companion object {
            fun getData() : IdInfo {
                val idInfo = IdInfo()
                return idInfo
            }
        }
    }

    class Location {
        var lag : String? = null // 模糊纬度
        var lng : String? = null // 模糊经度
        var detail_address : String? = null   // 定位的详细地址
        var location_city : String? = null   // 定位的城市
        var location_address : String? = null   // 定位的地理位置信息
        var is_allow_mock_location : String? = null   // 是否打开位置模拟
        var is_mock : String? = null   // 是否模拟位置
        var has_mock_apps : String? = null   // 模拟位置权限
    }

    class NetWork {
        var phone_country_code : String? = null // 手机国家代码
        var ip : String? = null // ip
        var device_mac : String? = null   // 设备MAC地址
        var sim_state : String? = null   // 1代表未插卡
        var phonecard_count : String? = null   // 电话卡数量
        var operator_name : String? = null   // 运营商名称
        var telephony_signal_type : String? = null   // 设备通话时网络类型
        var signal_strength : String? = null   // 信号强度
        var network_type : String? = null   // 网络类型
        var current_wifi : String? = null   //
        var is_wifi_proxy : String? = null   // 是否设置wifi代理
        var router_mac : String? = null   // 路由器mac地址
    }


    class Build {
        var mobile_name : String? = null // 手机名
        var os : String? = null // 操作系统
        var product : String? = null   // 产品的名称
        var model : String? = null   // 设备型号
        var finger_print : String? = null   // 出厂签名
        var device_serial : String? = null   // 序列号
        var device_version_type : String? = null   // 版本类型user,eng
    }

    class Hardware {
        var cpu_speed : String? = null // cpuSpeed主频
        var nfc_function : String? = null // 是否有NFC功能
        var phone_total_ram : String? = null   // 手机总内存
        var phone_available_ram : String? = null   // 手机可用内存
        var runtime_max_memory : String? = null   // apk可分配的最大内存
        var runtime_available_memory : String? = null   // apk 可用内存大小
        var total_storage : String? = null   // 设备总存储
        var available_storage : String? = null   // 设备可用存储
    }

    class System {
        var simulator : String? = null // 是否模拟器
        var adb_enabled : String? = null // 是否usb调试已打开
        var rooted : String? = null   // 是否被root
        var time_zone : String? = null   // 时区
        var turn_on_time : String? = null   // 开机时间
        var boot_time : String? = null   //  开机时的时间戳(毫秒级)
        var up_time : String? = null   // 从开机到当前的时长(包含休眠时间)
    }

    class Battery {
        var is_charging : String? = null // 是否充电中
        var battery : String? = null // 电量百分比
    }

    class App {
        var app_version : String? = null // app版本-自主app的
        var number_of_applications : String? = null // 应用程序数量
    }

    class Display {
        var resolution : String? = null // 分辨率
        var resolution_width : String? = null // 分辨率宽度
        var resolution_height : String? = null // 分辨率高度
    }

}