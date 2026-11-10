package com.yundou.loans.callback

/**
 * 导航回调接口
 * 用于 core-ui 模块回调到 app 模块进行页面跳转
 */
interface NavigationCallback {
    /**
     * 跳转到成功页面
     */
    fun navigateToSuccess()
}

/**
 * 应用状态管理接口
 * 用于访问 app 模块的全局状态
 */
interface AppStateManager {
    /**
     * 获取表单状态
     */
    fun isFormSubmitted(): Boolean

    /**
     * 设置表单状态
     */
    fun setFormSubmitted(submitted: Boolean)
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
