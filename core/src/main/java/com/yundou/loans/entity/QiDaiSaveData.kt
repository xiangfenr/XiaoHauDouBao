package com.yundou.loans.entity

/**
 * 期贷实体类
 */
data class QiDaiSaveData(
    var mobile: String? = null,
    var age: Int? = null,
    var sex: Int? = null,
    var province: String? = null,
    var city: String? = null,
    var name: String? = null,
    var ip: String? = null,
    var amount: Int? = null,
    var loanFor: Int? = null,
    var education: Int? = null,
    var job: Int? = null,
    var shebao: Int? = null,
    var gongjijin: Int? = null,
    var house: Int? = null,
    var vehicle: Int? = null,
    var zhima: Int? = null,
    var overdue: Int? = null,
    var insurance: Int? = null,
    var monthIncome: Int? = null,
    var idCard: String? = null
)

data class QiDaiProductObject(
    var productList: List<QiDaiProductInfo> = ArrayList()
)

data class QiDaiProductInfo(
    var orgId: Int = 0,
    var productName: String = "",
    var companyName: String = "",
    var companyPhone: String = "",
    var companyAddress: String = "",
    var productLogo: String = "",
    var agreement: List<qiDaiAgreement> = ArrayList()
)

data class qiDaiAgreement(
    var agreementName: String? = null,
    var agreementUrl: String? = null,
)

