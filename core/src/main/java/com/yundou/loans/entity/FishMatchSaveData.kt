package com.yundou.loans.entity

data class FishMatchSaveData(

    var channel_id: Int? = null,
    var realname: String? = null,
    var mobile: String? = null,
    var id_card_no: String? = null,
    var sex: Int? = null,
    var age: Int? = null,
    var city_name: String? = null,
    var city_code: String? = null,
    var career: Int? = null,
    var monthly_income_type: Int? = null,
    var monthly_income: Int? = null,
    var social: Int? = null,
    var fund: Int? = null,
    var house: Int? = null,
    var car: Int? = null,
    var insurance: Int? = null,
    var zhima_score: Int? = null,
    var education_level: Int? = null,
    var business_license: Int? = null,
    var credit_card_overdue: Int? = null,
    var ip: String? = null,
    var demand_amount: String? = null,

    //进件所需
    var channel_match_no: String? = null,//	第三方撞库号
    var auth_url: String? = null,//授权地址

)

data class FishMatchResult(
    var code: Int? = null,
    var channel_match_no: String? = null,
    var price: Double? = null,
    var product_name: String? = null,
    var company_name: String? = null,
    var protocol_list: List<FishMatchProtocol>? = null,
    var md5_list: List<String>? = null
)

data class FishMatchProtocol(
    var name: String,
    var url: String
)

data class FishMatchPushResult(
    var order_sn: String? = null,
    var error_msg: String? = null
)

