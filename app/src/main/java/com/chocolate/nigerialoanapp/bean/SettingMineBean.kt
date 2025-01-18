package com.chocolate.nigerialoanapp.bean

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.chocolate.nigerialoanapp.ui.mine.PageType

class SettingMineBean {
    @DrawableRes
    var leftIconRes: Int
    @StringRes
    var title: Int
    @PageType
    var type: Int

    constructor(@DrawableRes icon: Int, @StringRes title: Int, @PageType type :Int) {
        this.leftIconRes = icon
        this.title = title
        this.type = type
    }

}