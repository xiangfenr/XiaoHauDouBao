package com.yundou.loans.entity

data class ShanDaiMiaoSaveData(
    var name: String? = null,
    var idCard: String? = null,
    var phone: String? = null,
    var gender: Int? = null,
    var age: Int? = null,
    var city: String? = null,
    var loanTerm: Int? = null,
    var occupation: Int? = null,
    var sesameScore: Int? = null,
    var housingFund: Int? = null,
    var socialInsurance: Int? = null,
    var commercialInsurance: Int? = null,
    var realEstate: Int? = null,
    var creditCard: Int? = null,
    var creditRecord: Int? = null,
    var carProperty: Int? = null,
    var loanAmount: Int? = null,
    var huabeiLimit: Int? = null,
    var companySituation: Int? = null,
    var deviceType: Int? = null,
    var monthIncome: Int? = null,
    var education: Int? = null,
    var client_ip: String? = null
)

data class ShanDaiMiaoProductResult(
    var code: Int? = null,
    var msg: String? = null,
    var order_no: String? = null,
    var price: String? = null,
    var discountPrice: String? = null,
    var product_name: String? = null,
    var company_name: String? = null,
    var logo: String? = null,
    var searchId: String? = null,
    var bidType: String? = null,
    var transType: String? = null,
    var accountId: String? = null,
    var planId: String? = null,
    var agreements: List<ShanDaiMiaoAgreement>? = null,
    var md5List: List<String>? = null
)

data class ShanDaiMiaoAgreement(
    var name: String? = null,
    var url: String? = null
)

data class ShanDaiMiaoPushResult(
    var code: Int? = null,
    var msg: String? = null,
    var price: String? = null,
)

