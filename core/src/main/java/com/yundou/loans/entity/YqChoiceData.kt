package com.yundou.loans.entity

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class YqChoiceData(
    var realName: String? = null,
    var mobile: String? = null,
    var identity: String? = null,
    var age: Int? = null,
    var gender: Int? = null,
    var workCity: String? = null,
    var workCityCode: String? = null,

    var job: String? = null,
    var assetSituation: List<Int>? = null,

    var houseProperty: String? = null,
    var carProperty: String? = null,
    var socialSecurity: String? = null,
    var housingFund: String? = null,
    var insurance: String? = null,

    var creditInvestigation: String? = null,
    var workDuration: String? = null,

    var payoffForm: String? = null,
    var monthlyProfit: String? = null,

    var credit: ArrayList<String>? = null,
    var sesameCredit: String? = null,
    var applyLimit: String? = null,
    var adChannelCode: String? = null,
    var isHuaWeiPhone: Boolean? = null,
    var phoneModel: String? = null,
    var flagJuHuiHua: Boolean? = null,
) : Serializable

@Keep
data class YqWorldAreaCode(
    var name: String? = null,
    var city: ArrayList<ArrayList<String>>? = null,

    ) : Serializable

@Keep
data class YqqbTokenData(
    val token: String,
    val jumpUrl: String,
) : Serializable

@Keep
data class YqqbProductData(
    var applyStatus: Int? = null,
    var downStreamChannelId: Int? = null,
    var formProcessStatus: Int? = null,
    var id: Int? = null,
    var mayAuthProductList: Any? = null,
    var productCode: String? = null,
    var productCompany: String? = null,
    var productLogo: String? = null,
    var productName: String? = null,
    var productNextMatchDTO: YqqbProductData? = null,
    var productNickName: String? = null,
    var protocols: List<YqqbProtocol>? = null,
    var rePageShowType: String? = null,
    var serialNo: String? = null,
    var thirdpartyApiType: String? = null,
    var thirdpartyTargetUrl: String? = null,
    var thirdpartyType: Int? = null,
    var todayMatchNum: Int? = null,
    var yqlWholeProcessBumpVO: Any? = null
)

data class YqqbProtocol(
    var id: String? = null,
    var name: String? = null,
    var protocolType: Int? = null
)

data class YqqbProtocolContent(
    var content: String? = null,
    var signFieldFlag: String? = null,
)

