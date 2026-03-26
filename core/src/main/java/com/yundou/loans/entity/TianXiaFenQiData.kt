package com.yundou.loans.entity

import java.io.Serializable

/**
 * 天下分期 实体
 */
data class TxfqSaveData(
    var cityId: Int = 0,
    var realName: String? = null,
    var idCard: String? = null,
    var credit: String? = null,
    var jdIous: Int = 0,
    var antCreditPay: Int = 0,
    var assets: List<Int> = mutableListOf(),
    var loanAmount: String? = null,
    var loanPeriod: String? = null,
    var loanPurpose: String? = null,
) : Serializable

data class TxfqAgreementData(
    val id: String,
    val name: String,
    val content: String,
    val timestamps: String
)

data class TxfqSendCode(
    val mobile: String,
    val scene: String,
    val channel: String,
    val tid: String
)

data class TxfqLoginBean(
    val token: String,
    val isSkipLoan: Boolean,
    val applyPageUrl: String,
    val downPageUrl: String,
    val loginType: String,
)

data class TxfqCityBean(
    val id: Int,
    val regionPath: String,
    val regionGrade: Int,
    val localName: String,
    val subLocalName: String,
    val code: String,
    val lng: String,
    val lat: String,
    val sort: Int,
    val inner: Boolean
)

/**
 * 原文件实现了 UI 组件的 IPickerViewData，这里移除 UI 依赖，仅保留数据结构。
 */
data class Region(
    val id: Int,
    val name: String,
    val code: String,
) : Serializable

data class TxfqApplyData(
    var applyId: String? = null,
    var productId: String? = null,
    var hasPushSuccess: Int = -1,
    var agreements: TxfqDefaultAgreementData = TxfqDefaultAgreementData(),
    var apiReqType: Int? = -1,
    var jqbApplyId: String? = null,
    var jqbProductId: String? = null,
)

data class TxfqDefaultAgreementData(
    val defaultAgreements: List<TxfqApplyAgreementData> = mutableListOf()
)

data class TxfqApplyAgreementData(
    var contractName: String? = null,
    var contractContent: String? = null
)

