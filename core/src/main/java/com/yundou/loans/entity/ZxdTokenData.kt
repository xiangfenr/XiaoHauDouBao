package com.yundou.loans.entity

/**
 * 智享贷 Bean
 */
data class ZxdTokenData(
    var code: Int = -1,
    var msg: String? = null,
    var data: ZxdToken? = null
)

data class ZxdToken(var token: String? = null)

