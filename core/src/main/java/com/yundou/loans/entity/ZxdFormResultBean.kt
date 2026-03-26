package com.yundou.loans.entity

/**
 * 智享贷 撞库/表单结果
 */
data class ZxdFormResultBean(
    var productOrgName: String? = null,
    var productName: String? = null,
    var logoUrl: String? = null,
    var url: String? = null,
    var price: String? = null,
    var agreementList: List<AgreeMentBean> = ArrayList(),
)

data class AgreeMentBean(
    var protocolName: String? = null,
    var protocolUrl: String? = null,
)

