package com.chocolate.nigerialoanapp.collect.item

class AppInfoRequest {
    var appname: String? = null
    var pkgname: String? = null

    var installtime: Long = 0
    var installtime_utc: String? = null
    //0 系统app, 1第三方app
    var type : String? = null
    var timestamps : String? = null
    var versionName : String? = null
    var versionCode : String? = null
}