package com.yundou.loans.base

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.yundou.loans.config.AppConfigManager
import com.yundou.loans.http.EasyHttp.Companion.getInstance
import com.yundou.loans.http.EasyHttpConfig.Companion.get
import com.tencent.mmkv.MMKV

open class BaseApp : Application() {

    companion object {
        lateinit var context: BaseApp
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
    
    protected fun initHttp(baseUrl: String) {
        get().setApplication(this).setBaseUrl(baseUrl)
        get().debug("vpaas")
        get().init()
        getInstance().initConfig(get())
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
    }
    
    // 便捷访问配置的属性
    val appConfig get() = AppConfigManager.getConfig()
    val baseUrl get() = appConfig.baseUrl
    val app_id get() = appConfig.appId
    val version get() = appConfig.version
    val yinsi get() = appConfig.yinsi
    val zhuhceXieyi get() = appConfig.zhuhceXieyi
    val storeid get() = appConfig.storeid
    val srpackage_id get() = appConfig.srpackageId
    
    var mlAPPKEY 
        get() = appConfig.mlAPPKEY
        set(value) { appConfig.mlAPPKEY = value }
    var mlAPPSECRET 
        get() = appConfig.mlAPPSECRET
        set(value) { appConfig.mlAPPSECRET = value }
    var imsi 
        get() = appConfig.imsi
        set(value) { appConfig.imsi = value }
    var imei 
        get() = appConfig.imei
        set(value) { appConfig.imei = value }
        
    // 合作方域名
    val weimiaoyongUrl get() = appConfig.weimiaoyongUrl
    val shengrongH5Url get() = appConfig.shengrongH5Url
    val zhixiangdaiUrl get() = appConfig.zhixiangdaiUrl
    val kuaiyidaiUrl get() = appConfig.kuaiyidaiUrl
    val molierbaUrl get() = appConfig.molierbaUrl
    val twoHeRuiUrl get() = appConfig.twoHeRuiUrl
    val jiLoanUrl get() = appConfig.jiLoanUrl
    val wqbOrangeUrl get() = appConfig.wqbOrangeUrl
    val jiYongBaseUrl get() = appConfig.jiYongBaseUrl
    val jiYongBangUrl get() = appConfig.jiYongBangUrl
    val yqqbBaseUrl get() = appConfig.yqqbBaseUrl
    val tianxiaFenQiBaseUrl get() = appConfig.tianxiaFenQiBaseUrl
    val zxdNewBaseUrl get() = appConfig.zxdNewBaseUrl
    val yuanXiaoHuaBaseUrl get() = appConfig.yuanXiaoHuaBaseUrl
    val qiDaiBaseUrl get() = appConfig.qiDaiBaseUrl
    val weiRongBaoUrl get() = appConfig.weiRongBaoUrl
    val jiDaiBaseUrl get() = appConfig.jiDaiBaseUrl
    val xiaoFuBaseUrl get() = appConfig.xiaoFuBaseUrl
    val longYanUrl get() = appConfig.longYanUrl
    val weiYinBaseUrl get() = appConfig.weiYinBaseUrl
    val yueXiangBaseUrl get() = appConfig.yueXiangBaseUrl
    val jiYiHuaBaseUrl get() = appConfig.jiYiHuaBaseUrl
    val baJieBaseUrl get() = appConfig.baJieBaseUrl
    val shanDaiMiaoBaseUrl get() = appConfig.shanDaiMiaoBaseUrl
    val jiYongQianBaoBaseUrl get() = appConfig.jiYongQianBaoBaseUrl
}

