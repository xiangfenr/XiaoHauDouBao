package com.yundou.loans.entity

class XiaoFuSaveData

data class XiaoFuUserData(
    var phone: String? = null,
    var phone_mask: String? = null,
    var name: String? = null,
    var age: Int = -1,
    var city_name: String? = null,
    var city_code: Int? = -1,
    var gender: Int? = -1,
    var quota: Int? = -1,
    var job: Int? = -1,
    var house: Int? = -1,
    var car: Int? = -1,
    var social: Int? = -1,
    var fund: Int? = -1,
    var insurance: Int? = -1,
    var zm: Int? = -1,
    var credit: Int? = -1,
    var baitiao_huabei: Int? = -1,
    var ip: String? = null,
    var os: Int? = -1,

    )

data class XiaoFuPResult(
    var tid: String? = null,
    var price: Double = 0.00,
    var agreement_list: List<XiaoFuAgreement>? = null,
    var products: List<XiaoFuProduct>? = null,
    var status: Int = -1,
    var freeze_time: String? = null
)

data class XiaoFuProduct(
    var id: Int = -1,
    var name: String? = null,
    var logo: String? = null,
    var platform: String? = null,
    var company_name: String? = null,
    var min_month: Int = -1,
    var max_month: Int = -1,
    var min_price: Double = 0.00,
    var max_price: Double = 0.00,
    var rate: Double = -1.0,
    var max_rate: Double = -1.0,
    var rate_unit: Int = -1,
    var agreement_list: List<XiaoFuAgreement>? = null,
    var source_name: String? = null,
    var channel_settlement_price: Double = 0.0
)

data class XiaoFuAgreement(
    var title: String? = null,
    var uri: String? = null
)

