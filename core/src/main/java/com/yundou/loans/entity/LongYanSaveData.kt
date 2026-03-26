package com.yundou.loans.entity

/**
 * 龙炎 SaveData
 */
data class LongYanSaveData(

    var phone_code: String? = null,
    var name_md5: String? = null,
    var idno_md5: String? = null,

    var working_city: String? = null,
    var age: Int? = null,
    var sex: Int? = null,
    var gjj: Int? = null,
    var shebao: Int? = null,
    var loan_amount: Int? = null,
    var house: Int? = null,
    var car: Int? = null,
    var car_status: Int? = null,
    var car_price: Int? = null,
    var overdue: Int? = null,
    var zhima: Int? = null,
    var occupation: Int? = null,
    var insurance: Int? = null,
    var ip: String? = null,
)

data class LongYanFormResultBean(
    var order_id: String? = null,
    var id: String? = null,
    var company_name: String? = null,
    var product_name: String? = null,
    var product_logo: String? = null,
    var product_loan: String? = null,
    var product_term: String? = null,
    var product_ratio: String? = null,
    var product_agreement: List<LongYanAgreeMentBean> = ArrayList(),
    var md5_list: List<String> = ArrayList(),
    var price: Double? = null,
)

data class LongYanAgreeMentBean(
    var name: String? = null,
    var url: String? = null,
)

