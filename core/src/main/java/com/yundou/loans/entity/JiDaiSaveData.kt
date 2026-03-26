package com.yundou.loans.entity

data class JiDaiUserInfo(
    var xinyong: String? = null,
    var occupation: String? = null,
    var zhima: String? = null,
    var shebao: String? = null,
    var gjj: String? = null,
    var house: String? = null,
    var car: String? = null,
    var baodan: String? = null,
    var creditCard: String? = null,
    var salaryType: String? = null,
    var monthIncome: String? = null,
    var name: String? = null,
    var idNum: String? = null,
    var city: String? = null,
    var province: String? = null
)

data class JiDaiProductInfo(
    var apiType: String? = null,
    var pipeiStatus: String? = null,
    var productOrgName: String? = null,
    var productName: String? = null,
    var logoUrl: String? = null,
    var protocolList: MutableList<JiDaiProtocolItem>? = null
)

data class JiDaiProtocolItem(
    var protocolName: String? = null,
    var protocolUrl: String? = null
)

