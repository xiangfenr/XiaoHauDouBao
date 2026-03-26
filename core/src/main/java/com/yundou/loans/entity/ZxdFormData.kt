package com.yundou.loans.entity

/**
 * 智享贷款 表单
 */
data class ZxdFormData(
    var id_card: String? = null,
    var real_name: String? = null,
    var work_province: String? = null,
    var work_province_name: String? = null,
    var work_city: String? = null,
    var work_city_name: String? = null,
    var social_security: Int = -1,
    var sesame_score: Int = -1,
    var accumulation_fund: Int = -1,
    var car_property: Int = -1,
    var house_property: Int = -1,
    var occupation: Int = -1,
    var loan_time: Int = -1,
    var huabei: Int = -1,
    var loan_amount: String? = null,
    var mobile_system: String? = null
)

/**
 * 智享贷API全流程采量 - 用户贷款信息实体类
 */
data class ZxdAPISaveData(
    var phone_md5: String? = null,
    var phone: String? = null,
    var id_card: String? = null,
    var real_name: String? = null,
    var id_card_md5: String? = null,
    var sex: Int? = -1,
    var age: Int? = -1,
    var city_code: String? = null,
    var city_name: String? = null,
    var occupation: Int? = -1,
    var social_security: Int? = -1,
    var sesame_score: Int? = -1,
    var accumulation_fund: Int? = -1,
    var car_property: Int? = -1,
    var house_property: Int? = -1,
    var personal_insurance: Int? = -1,
    var loan_amount: Int? = -1,
    var education: Int? = -1,
    var marital_status: Int? = -1,
    var huabei: Int? = -1,
    var baitiao: Int? = -1,
    var business: Int? = -1,
    var credit: Int? = -1,
    var ip: String? = null,
    var partner_id: Int? = null,
    var price: Double? = null,
    var channel_id: String? = null,
    var province: String? = null,
)

data class ZxdNewResultProduceBean(
    var productOrgName: String? = null,
    var productName: String? = null,
    var logoUrl: String? = null,
    var price: Double? = null,
    var agreementList: List<ZxdNewAgreeBean> = ArrayList(),
)

data class ZxdNewAgreeBean(
    var protocolName: String? = null,
    var protocolUrl: String? = null,
)

