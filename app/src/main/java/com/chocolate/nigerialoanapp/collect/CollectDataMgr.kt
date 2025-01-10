package com.chocolate.nigerialoanapp.collect


class CollectDataMgr : BaseCollectDataMgr(){


    companion object {
        private const val TAG = "CollectDataMgr"

        val sInstance by lazy(LazyThreadSafetyMode.NONE) {
            CollectDataMgr()
        }
    }

    override fun getTag(): String {
        return TAG
    }

    override fun getLogTag(): String {
        return "new "
    }
}