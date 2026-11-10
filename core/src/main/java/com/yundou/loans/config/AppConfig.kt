package com.yundou.loans.config

/**
 * 应用配置接口
 * 用于解耦 core 模块和 app 模块的配置依赖
 * 当前版本仅保留本部服务与魔力合作方所需配置。
 */
interface AppConfig {
    val baseUrl: String
    val appId: String
    val version: String
    val yinsi: String
    val zhuhceXieyi: String
    val storeid: String
    val srpackageId: String

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
