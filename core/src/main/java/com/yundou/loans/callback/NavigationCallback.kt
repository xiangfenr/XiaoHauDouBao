package com.yundou.loans.callback

import com.yundou.loans.entity.JiyongOrderData
import com.yundou.loans.entity.MatchResData
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 导航回调接口
 * 用于 core-ui 模块回调到 app 模块进行页面跳转
 */
interface NavigationCallback {
    /**
     * 跳转到成功页面
     */
    fun navigateToSuccess()
    
    /**
     * 跳转到成功页面（带订单数据）
     */
    fun navigateToSuccessWithOrder(orderData: JiyongOrderData?)
}

/**
 * 应用状态管理接口
 * 用于访问 app 模块的全局状态
 */
interface AppStateManager {
    /**
     * 获取超时时间（秒）
     */
    fun getTimeoutSecond(): Int
    
    /**
     * 获取表单状态
     */
    fun isFormSubmitted(): Boolean
    
    /**
     * 设置表单状态
     */
    fun setFormSubmitted(submitted: Boolean)

    /**
     * 获取吉用钱包价格
     */
    fun getJYQBPrice(): Double
    
    /**
     * 获取小虾渠道并发列表
     */
    fun getShrimpChannelConcurrency(): List<Int>?
    
    /**
     * 获取机构撞库结果列表
     */
    fun getOrgMatchRes(): CopyOnWriteArrayList<MatchResData>
}

/**
 * 回调管理器单例
 */
object CallbackManager {
    @Volatile
    private var navigationCallback: NavigationCallback? = null
    @Volatile
    private var appStateManager: AppStateManager? = null
    
    fun setNavigationCallback(callback: NavigationCallback) {
        navigationCallback = callback
    }
    
    fun getNavigationCallback(): NavigationCallback? {
        return navigationCallback
    }
    
    fun setAppStateManager(manager: AppStateManager) {
        appStateManager = manager
    }
    
    fun getAppStateManager(): AppStateManager? {
        return appStateManager
    }
}
