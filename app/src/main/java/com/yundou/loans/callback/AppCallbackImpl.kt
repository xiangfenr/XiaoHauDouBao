package com.yundou.loans.callback

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.yundou.loans.MyApplication
import com.yundou.loans.entity.JiyongOrderData
import com.yundou.loans.entity.MatchResData
import com.yundou.loans.ui.loan.WmSuccessActivity
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 导航回调实现
 */
class AppNavigationCallback(private val activity: Activity) : NavigationCallback {
    
    override fun navigateToSuccess() {
        MyApplication.isForm = true
        activity.startActivity(Intent(activity, WmSuccessActivity::class.java))
        activity.setResult(AppCompatActivity.RESULT_OK)
        activity.finish()
    }
    
    override fun navigateToSuccessWithOrder(orderData: JiyongOrderData?) {
        MyApplication.isForm = true
        val intent = Intent(activity, WmSuccessActivity::class.java)
        intent.putExtra("orderData", orderData)
        activity.startActivity(intent)
        activity.setResult(AppCompatActivity.RESULT_OK)
        activity.finish()
    }
}

/**
 * 应用状态管理实现
 */
class AppStateManagerImpl : AppStateManager {
    
    override fun getTimeoutSecond(): Int {
        return MyApplication.timeout_second
    }
    
    override fun isFormSubmitted(): Boolean {
        return MyApplication.isForm
    }
    
    override fun setFormSubmitted(submitted: Boolean) {
        MyApplication.isForm = submitted
    }

    override fun getJYQBPrice(): Double {
        return MyApplication.jiyong_price
    }

    override fun getShrimpChannelConcurrency(): List<Int>? {
        return MyApplication.shrimp_channel_concurrency
    }
    
    override fun getOrgMatchRes(): CopyOnWriteArrayList<MatchResData> {
        return MyApplication.orgMatchRes
    }
}
