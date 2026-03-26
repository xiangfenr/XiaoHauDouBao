package com.yundou.loans.callback

import android.app.Activity

/**
 * 回调工厂接口
 * 用于在 core-ui 模块中创建回调实例
 */
interface CallbackFactory {
    /**
     * 创建导航回调
     */
    fun createNavigationCallback(activity: Activity): NavigationCallback
    
    /**
     * 创建应用状态管理器
     */
    fun createAppStateManager(): AppStateManager
}

/**
 * 回调工厂管理器
 */
object CallbackFactoryManager {
    private var factory: CallbackFactory? = null
    
    fun setFactory(callbackFactory: CallbackFactory) {
        factory = callbackFactory
    }
    
    fun getFactory(): CallbackFactory? {
        return factory
    }
}
