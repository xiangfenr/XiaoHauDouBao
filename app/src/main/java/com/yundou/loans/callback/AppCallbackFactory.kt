package com.yundou.loans.callback

import android.app.Activity
import com.yundou.loans.callback.AppNavigationCallback
import com.yundou.loans.callback.AppStateManagerImpl

/**
 * App 模块的回调工厂实现
 */
class AppCallbackFactory : CallbackFactory {
    
    override fun createNavigationCallback(activity: Activity): NavigationCallback {
        return AppNavigationCallback(activity)
    }
    
    override fun createAppStateManager(): AppStateManager {
        return AppStateManagerImpl()
    }
}
