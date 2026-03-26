package com.yundou.loans.entity

import java.io.Serializable

data class JiYBangTokenData(
    var token: String? = null
)

data class JIYBangSaveData(
    var realName: String? = null, //姓名
    var idCardNo: String? = null, //身份证
    var sex: String? = null, // 性别 1-男，2-女
    var age: String? = null,  //年龄
    var realPhone: String? = null,  //手机号

    var socialInsurance: String? = null, //社保
    var accumulationFund: String? = null, //公积金
    var houseProperty: String? = null, //房
    var carProperty: String? = null, //车
    var businessInsurance: String? = null, //商业保险
    var businessOwners: String? = null, //企业主

    var zmScore: String? = null, //芝麻分
    var spendBaiLimit: String? = null, //花呗--借呗额度
    var jdbtLimit: String? = null, //京东白条额度
    var creditSituation: String? = null, //是否逾期

    var location: String? = null, ///城市 上海市
    var loanLimit: String? = null, //申请金额
    var monthlyIncome: String? = null, //月收入
    var loanLongTime: String? = null, //借款期限


) : Serializable

