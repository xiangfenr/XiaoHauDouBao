package com.yundou.loans.entity

data class JiYiHuaSaveData(
    var phoneMd5: String? = null,
    var userName: String? = null,
    var sex: Int? = -1,
    var age: Int = -1,
    var ip: String? = null,
    var loanMoney: Int? = -1,
    var cityCode: Int? = -1,
    var cityName: String? = null,
    var job: Int? = -1,
    var monthlyIncome: Int? = -1,
    var busLicense: Int? = -1,
    var has: Int? = -1,
    var fund: Int? = -1,
    var house: Int? = -1,
    var car: Int? = -1,
    var policy: Int? = -1,
    var zhiMa: Int? = -1,
    var education: Int? = -1,
    var platform: String? = null,
    var phoneMask: String? = null
)

data class JiYiHuaMaskSaveData(
    var phoneMask: String? = null,
    var userName: String? = null,
    var sex: Int? = -1,
    var age: Int = -1,
    var ip: String? = null,
    var loanMoney: Int? = -1,
    var cityCode: Int? = -1,
    var cityName: String? = null,
    var job: Int? = -1,
    var monthlyIncome: Int? = -1,
    var busLicense: Int? = -1,
    var has: Int? = -1,
    var fund: Int? = -1,
    var house: Int? = -1,
    var car: Int? = -1,
    var policy: Int? = -1,
    var zhiMa: Int? = -1,
    var education: Int? = -1,
    var platform: String? = null,
)

data class JiYiHuaResult(
    var discountPrice: Double = 0.00,
    var companyName: String? = null,
    var productName: String? = null,
    var logo: String? = null,
    var protocolList: List<JiYiHuaAgreement>? = null,
    var applyNo: String? = null
)

data class JiYiHuaAgreement(
    var protocolName: String? = null,
    var protocolUrl: String? = null
)

data class JiYiHuaProduct(
    val phone: String? = null,
    val applyNo: String? = null,
    var userName: String? = null,
    var sex: Int? = -1,
    var age: Int = -1,
    var ip: String? = null,
    var loanMoney: Int? = -1,
    var cityCode: Int? = -1,
    var cityName: String? = null,
    var job: Int? = -1,
    var monthlyIncome: Int? = -1,
    var busLicense: Int? = -1,
    var has: Int? = -1,
    var fund: Int? = -1,
    var house: Int? = -1,
    var car: Int? = -1,
    var policy: Int? = -1,
    var zhiMa: Int? = -1,
    var education: Int? = -1,
    var platform: String? = null,
)

