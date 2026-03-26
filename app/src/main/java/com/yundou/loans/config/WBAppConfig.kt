package com.yundou.loans.config

import com.yundou.loans.utils.Constants

/**
 * app相关的 所有的配置
 */
class WBAppConfig : AppConfig {
    //https://t.zfrskj.top 生产
    //https://test.zfrskj.top 测试环境
    override val baseUrl = "https://t.zfrskj.top"  //TODO测试
    //小花逗包 19
    override val appId = "19"
    override val version = "1.00.02"
    //隐私政策 https://t.zfrskj.top/protocol/privacy/1/0
    override val yinsi = "$baseUrl/protocol/privacy/$appId/0"
    //注册协议 https://t.zfrskj.top/protocol/reg/1
    override val zhuhceXieyi = "$baseUrl/protocol/reg/$appId"
    //应用商店渠道
    override val storeid = Constants.CHANNEL_VIVO
    //笙融的分类--未用
    override val srpackageId = "4_$storeid"
    
    // ***************合作方域名***************

    //魔力
    override val molierbaUrl = "https://api-xy.ttgg123.cn"
    //暂未使用的
    override val weimiaoyongUrl = "https://hapi.srloan.cn"
    override val shengrongH5Url = "https://h5.srloan.cn"
    override val zhixiangdaiUrl = "http://www.zhixiangjinfu.com:8083"
    override val kuaiyidaiUrl = "https://app.beihua.site"

    override val twoHeRuiUrl = "https://app.jybj.info"
    override val jiLoanUrl = "https://zloanapi.jishiyu2019.com"
    override val wqbOrangeUrl = "http://apitest.grjrong.com"
    override val jiYongBaseUrl = "https://japp.yihua16888.com"
    override val jiYongBangUrl = "https://sbqb.jinnuodai.com.cn"
    override val yqqbBaseUrl = "https://yql-api.chongdong.cc"
    override val tianxiaFenQiBaseUrl = "https://new.txfqdk.com"
    override val zxdNewBaseUrl = "http://www.zhixiangjinfu.com:8083"
    override val yuanXiaoHuaBaseUrl = "https://openapi.fangxiny.com"
    override val qiDaiBaseUrl = "https://www.qidaiapp.com"
    override val weiRongBaoUrl = "https://home.weirongbao.com"
    override val jiDaiBaseUrl = "http://jidaiapi.jishiyu2019.com/"
    override val xiaoFuBaseUrl = "https://api.lzkjxf.com"
    override val longYanUrl = "https://api.zishenglongyan.com"
    override val weiYinBaseUrl = "https://api.bthx.cc"
    override val yueXiangBaseUrl = "https://yxapi.yuexiangkj.cn"
    override val jiYiHuaBaseUrl = "https://api.jiyhua.com"
    override val baJieBaseUrl = "https://bjjfapi.zbjjf.com"
    override val shanDaiMiaoBaseUrl = "https://vapi.suozhikeji.cn" //闪贷喵
    override val jiYongQianBaoBaseUrl = "https://japp.jrh365.com" //吉用钱包

    // MMKV 相关 - 可变属性

    //魔力的渠道设置
    override var mlAPPKEY = "DVR5KqyunSJ50114"
    override var mlAPPSECRET = "KaPbhWX4dvnwIump"

    override var imsi = "Zcxsd23436"
    override var imei = "ad"
    
    init {
        // 根据渠道设置不同的 KEY
//        when (storeid) {
//            Constants.CHANNEL_HUAWEI -> {
//                mlAPPKEY = "ZudE8taL0OcQ7iMW"
//                mlAPPSECRET = "aYHZFqU15Xe3P08Q"
//            }
//            Constants.CHANNEL_VIVO -> {
//                mlAPPKEY = "ZudE8taL0OcQ7iMW"
//                mlAPPSECRET = "aYHZFqU15Xe3P08Q"
//            }
//            Constants.CHANNEL_RONGYAO -> {
//                mlAPPKEY = "hiYlyaTt2ELNuCX5"
//                mlAPPSECRET = "2osA6BZlhv7HGMYF"
//            }
//            Constants.CHANNEL_XIAOMI -> {
//                mlAPPKEY = "sXkAcOVJuGQv9gex"
//                mlAPPSECRET = "uqvknStsCJ8gExpj"
//            }
//            Constants.CHANNEL_OPPO -> {
//                mlAPPKEY = "7rcxz8KOojZb39Ek"
//                mlAPPSECRET = "LN6oh5iFaVcUWR0g"
//            }
//            Constants.CHANNEL_QQ -> {
//                mlAPPKEY = "73MxKoDhjkLiOzH4"
//                mlAPPSECRET = "2oldZwsUy0hI5S6T"
//            }
//        }
    }
}
