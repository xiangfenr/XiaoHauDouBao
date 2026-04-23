package com.yundou.loans.config

/**
 * 应用配置接口
 * 用于解耦 core 模块和 app 模块的配置依赖
 */
interface AppConfig {
    val baseUrl: String
    val appId: String
    val version: String
    val yinsi: String
    val zhuhceXieyi: String
    val storeid: String
    val srpackageId: String
    
    // 合作方域名
    val weimiaoyongUrl: String
    val shengrongH5Url: String
    val zhixiangdaiUrl: String
    val kuaiyidaiUrl: String
    var molierbaUrl: String
    val twoHeRuiUrl: String
    val jiLoanUrl: String
    val wqbOrangeUrl: String
    val jiYongBaseUrl: String
    val jiYongBangUrl: String
    val yqqbBaseUrl: String
    val tianxiaFenQiBaseUrl: String
    val zxdNewBaseUrl: String
    val yuanXiaoHuaBaseUrl: String
    val qiDaiBaseUrl: String
    val weiRongBaoUrl: String
    val jiDaiBaseUrl: String
    val xiaoFuBaseUrl: String
    val longYanUrl: String
    val weiYinBaseUrl: String
    val yueXiangBaseUrl: String
    val jiYiHuaBaseUrl: String
    val baJieBaseUrl: String
    val shanDaiMiaoBaseUrl: String
    val jiYongQianBaoBaseUrl: String

    // MMKV 相关
    var mlAPPKEY: String
    var mlAPPSECRET: String
    var imsi: String
    var imei: String
}

/**
 * 配置管理器单例
 */
object AppConfigManager {
    private var config: AppConfig? = null
    
    fun init(appConfig: AppConfig) {
        config = appConfig
    }
    
    fun getConfig(): AppConfig {
        return config ?: throw IllegalStateException("AppConfig not initialized. Call AppConfigManager.init() first.")
    }
}
