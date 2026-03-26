package com.yundou.loans.entity

/*
微银信用
 */

data class WeiYinSaveData(
    /** 手机号 MD5 加密（小写） */
    var phoneMd5: String? = null,
    var phone: String? = null, //进件使用
    var agreeProtocol: String? = null, //平台授权协议

    /** 用户登录 IP */
    var ip: String? = null,

    /** 用户真实姓名 */
    var name: String? = null,

    /** 城市名称（例如：上海市） */
    var city: String? = null,

    /** 城市编码（例如：北京市 110100） */
    var cityCode: String? = null,

    /** 年龄 */
    var age: Int? = null,

    /** 性别：1-男，2-女，0-未知 */
    var gender: Int? = null,

    /** 身份证前6位 */
    var idCardSixPrefix: String? = null,

    /** 贷款时间：2(6个月)、3(12个月)、4(24个月)、5(36个月) */
    var loanTime: Int? = null,

    /** 职业 */
    var profession: Int? = null,

    /** 芝麻分 */
    var zhima: Int? = null,

    /** 公积金 */
    var providentFund: Int? = null,

    /** 社保 */
    var socialSecurity: Int? = null,

    /** 商业保险 */
    var commericalInsurance: Int? = null,

    /** 名下房产 */
    var house: Int? = null,

    /** 逾期记录 */
    var overdue: Int? = null,

    /** 名下车产 */
    var vehicle: Int? = null,

    /** 贷款额度 */
    var loanAmount: Int? = null,
)

/**
 * 产品信息实体类
 */
data class WeiYinProductInfo(
    var productLogo: String? = null,
    var productName: String? = null,
    var companyName: String? = null,
    var price: String? = null,
    var protocolList: List<WeiYinProtocol>? = null
)

data class WeiYinProtocol(
    var protocolName: String? = null,
    var protocolValue: String? = null
)

