package com.yundou.loans.entity

import java.io.Serializable

data class JIYongSaveData(
    var mobile: String? = null, //手机号
    var name: String? = null, //姓名
    var sex: String? = null, // 性别 1-男，2-女
    var age: String? = null,  //年龄
    var sfz: String? = null, //身份证
    var city: String? = null, //城市 上海市
    var cityCode: String? = null, ///城市code
    var loanAmount: String? = null, //申请金额
    var period: String? = null, //贷款期限
    var purpose: String? = null, //借款用途
    var clientIp: String? = null, //ip
    var car: String? = null, //车产 1-有，0-无
    var house: String? = null, //房产 1-有，0-无
    var insurance: String? = null, //保单 1-有，0-无
    var salary: String? = null, //社保 1-有，0-无
    var fund: String? = null, //公积金 1-有，0-无
    var sesame: String? = null, //芝麻分
    var owners: String? = null,  //企业主 1-有，0-无

) : Serializable

/**
 * 提交数据完成
 */
data class JiyongOrderData(
    val agreements: List<JiYongAgreements>,
    val companyName: String,
    val logo: String,
    val orderNo: String,
    val productName: String,
    val status: Int
) : Serializable

data class JiYongAgreements(
    val name: String,
    val url: String
) : Serializable

/***********全流程相关实体********/

data class JYQBqlcUserData(
    var md5Mobile: String? = null,          // Md5手机号
    var realName: String? = null,           // 姓名
    var idCard: String? = null,             // md5(IdCard)
    var sex: Int? = null,                   // 性别1-男，2-女
    var age: Int? = null,                   // 年龄
    var cityName: String? = null,           // 城市名称，带“市”；例如：上海市
    var cityCode: String? = null,           // 城市编码（可选）
    var loanAmount: Int? = null,            // 申请金额，单位：万
    var loanPeriod: Int? = null,            // 借款期限，3/6/9/12/24/36期
    var loanPurpose: Int? = null,           // 借款用途，1-日常消费、2-教育培训、3-旅游培训、4-手机数码、5-租房、6-其他，默认:1
    var applyIp: String? = null,            // IP（可选）
    var car: Int? = null,                   // 车产信息，1-有，0-无
    var house: Int? = null,                 // 房产信息，1-有，0-无
    var insurance: Int? = null,             // 保单信息，1-车，0-无
    var social: Int? = null,                // 社保，1-有，0-无
    var gjj: Int? = null,                   // 公积金，1-有，0-无
    var zmf: Int? = null,                   // 芝麻分，1:0-600、2:600-650、3:650-700、4:700以上
    var qyz: Int? = null,                   // 企业主，1-有、0-无
    var protocolUrl: String? = null,         // 协议链接

    //进件参数
    var orderNo: String? = null,          // 订单号
    var mobile: String? = null,           // 姓名

)

data class JYQBPushResult(
    var status: Int? = null,
    var orderSn: String? = null
)