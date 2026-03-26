package com.yundou.loans

import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mmkv.MMKV
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.OrgMatchResStore
import com.yundou.loans.callback.AppCallbackFactory
import com.yundou.loans.callback.CallbackFactoryManager
import com.yundou.loans.callback.CallbackManager
import com.yundou.loans.config.AppConfigManager
import com.yundou.loans.config.WBAppConfig
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil

/**
 * @Author: fenr
 * 时间: 2024/12/24
 * 类名: ACTIVITY
 * 简述:
 */
class MyApplication : BaseApp() {

    override fun onCreate() {
        super.onCreate()

        // 初始化应用配置
        AppConfigManager.init(WBAppConfig())

        // 初始化回调工厂（全局设置）
        val factory = AppCallbackFactory()
        CallbackFactoryManager.setFactory(factory)
        
        // 初始化应用状态管理器（全局设置）
        CallbackManager.setAppStateManager(factory.createAppStateManager())

        // 初始化 HTTP
        MMKV.initialize(this)
        ARouter.init(this)
        initHttp(baseUrl)
    }


    companion object {
        var isForm: Boolean = false
            set(value) {
                MmkvUtil.getInstance().encode(Constants.IS_EDIT_FORM, value)
                field = value
            }
        /**
         * key=1 value= 微融宝 18
         * key=2 value =智享贷 17
         * 弃用
         */
        //var shrimp_channel: Map<String, Int>? = null
        var shrimp_channel_concurrency: List<Int>? = null
        var timeout_second: Int = 10

        //用于撞库--用完需要清理
        val orgMatchRes = OrgMatchResStore.orgMatchRes

        var jiyong_price: Double = 0.00
    }
}
