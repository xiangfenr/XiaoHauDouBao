package com.yundou.loans.entity

/**
 * 吉贷
 */
data class JIDaiProductData(
    val errorCode: Int,
    val errorMsg: String,
    val fcPrice: String,
    val inInsOrderId: String,
    val insId: Int,
    val logoUrl: String,
    val orderId: String,
    val price: String,
    val productName: String,
    val productOrgName: String,
    val protocolList: List<Protocol>,
    val resFlag: String,
    val resMsg: String,
    val secretKey: String,
    val url: String
)

data class Protocol(
    val protocolName: String,
    val protocolUrl: String
)

