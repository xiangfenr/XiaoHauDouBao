package com.yundou.loans.entity

import java.math.BigDecimal

data class BaJieSaveData(
    var phone: String? = null,
    var channelSignature: String? = null,
    var sex: Int? = null,
    var nameMd5: String? = null,
    var name: String? = null,
    var age: Int? = null,
    var idCardMd5: String? = null,
    var idCard: String? = null,
    var city: String? = null,
    var socialSecurity: Int? = null,
    var accumulationFund: Int? = null,
    var carProduction: Int? = null,
    var estate: Int? = null,
    var unitSocialSecurity: Int? = null,
    var sesame: Int? = null,
    var professionalIdentity: Int? = null,
    var customerCreditCard: Int? = null,
    var highestEducation: Int? = null,
    var monthlyIncome: String? = null,
    var customerFormOfPayroll: String? = null,
    var lengthOfService: String? = null,
    var loanPurpose: Int? = null,
    var ip: String? = null,
    var deviceType: Int? = null,
    var huaBeiQuota: Int? = null,
    var baiTiaoQuota: Int? = null,
    var price: BigDecimal? = null
)

data class BaJieProductResult(
    var loanName: String? = null,
    var hitPhoneList: List<String>? = null,
    var authorizationAgreement: List<BaJieAgreement>? = null,
    var price: Double? = null,
    var sort: Int? = null,
    var check: Boolean? = null,
    var productName: String? = null,
    var serialNo: String? = null
)

data class BaJieAgreement(
    var name: String? = null,
    var url: String? = null
)

