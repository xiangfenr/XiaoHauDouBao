package com.yundou.loans.entity

/**
 * 微融宝 SaveData
 */
data class WrbSaveData(
    var phoneMd5: String? = null,
    var idCard: String? = null,
    var city: String? = null,
    var loanAmount: String? = null,
    var sex: Int? = null,
    var age: Int? = null,
    var device: Int? = null,
    var ip: String? = null,
    var sesameScore: Int? = null,
    var car: Int? = null,
    var house: Int? = null,
    var fund: Int? = null,
    var social: Int? = null,
    var insurance: Int? = null,
    var baiTiao: Int? = null,
    var huaBei: Int? = null,
    var creditCard: Int? = null,
    var overdue: Int? = null,
    var occupation: Int? = null,
    var education: Int? = null,
    var salary: Int? = null,
    var paymentForm: Int? = null,
    var yearsService: Int? = null,
    var businessLicense: Int? = null,

    //进件所需要的参数
    var phone: String? = null,
    var orderId: String? = null,
    var name: String? = null,
    var agreeProtocol: String? = null

)

data class WrbFormResultBean(
    var price: Double? = null,
    var orderId: String? = null,
    var productName: String? = null,
    var companyName: String? = null,
    var logo: String? = null,
    var agreements: List<WrbAgreeMentBean> = ArrayList(),
)

data class WrbAgreeMentBean(
    var agreementName: String? = null,
    var agreementUrl: String? = null,
)

