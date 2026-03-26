package com.yundou.loans.entity

import java.math.BigInteger

/**
 * 快易贷 Bean
 */
data class KydCodeBean(
    var lostTime: Long = 0
)

data class KydTokenData(
    var loginOrReg: Int = -1,
    var token: String? = null,
    var jumpType: String? = null,
    var jumpH5Url: String? = null,
)

data class KydFormBean(
    var totalPage: Int = 0,
    var formConfigInfoList: List<FormGroup> = ArrayList()
)

data class FormGroup(
    var currentPage: Int = 0,
    var totalPage: Int = 0,
    var groupFromItem: List<GroupBean> = ArrayList()
)

data class GroupBean(
    var groupName: String? = null,
    var groupTips: String? = null,
    var groupIcon: String? = null,
    var itemList: List<IssueBean> = ArrayList()
)

data class IssueBean(
    var label: String? = null,
    var tips: String? = null,
    var name: String? = null,
    var type: String? = null,
    var required: Boolean = false,
    var value: String? = null,
    var selectList: List<SelectItemBean> = ArrayList()
)

data class SelectItemBean(
    var label: String? = null,
    var id: Int = -1,
    var value: String? = null,
    var quota: String? = null,
    var selectedIsShow: Boolean = false,
)

data class KydProvinceBean(
    var id: BigInteger? = null,
    var planId: BigInteger? = null,
    var provinceCode: String? = null,
    var provinceName: String? = null,
    var cityCode: String? = null,
    var cityName: String? = null,
    var cityList: List<KydCityBean> = ArrayList()
)

data class KydCityBean(
    var id: BigInteger? = null,
    var provinceCode: String? = null,
    var provinceName: String? = null,
    var cityCode: String? = null,
    var cityName: String? = null,
    var jumpLoanapp: Int = 0,
)

data class KydSubmitData(
    var channelSign: String = "4f5z",
    var source: Int? = null,
    var currentPage: Int? = null,
    var occupation: Int? = null,
    var socialSecurity: Int? = null,
    var accumulation: Int? = null,
    var car: Int? = null,
    var house: Int? = null,
    var sesameSeed: Int? = null,
    var userName: String? = null,
    var age: String? = null,
    var sex: Int? = null,
    var workCity: String? = null,
    var overdueSituation: Int? = null,
)

data class SaveedBean(
    var nextKey: String? = null,
    var nextPage: Int? = null,
    var matchMode: String? = null,
)

data class MatchiingBean(
    val estimatedAmount: String,
    val isCallback: Boolean,
    val jumpH5Url: String,
    val jumpType: String,
    val matchingInstitutionsList: List<MatchingInstitutionsX>
)

data class MatchingInstitutionsX(
    val companyAddress: String,
    val companyLogo: String,
    val companyName: String,
    val id: Int,
    val informationServicePlatform: String,
    val maxLimit: String,
    val maxNumberPeriods: String,
    val maxYearInterestRate: String,
    val productName: String,
    val protocolAddressList: List<ProtocolAddressX>,
    val url: String
)

data class ProtocolAddressX(
    val protocolId: String,
    val showName: String,
    val showUrl: String
)

data class XieYiData(
    val content: String,
)

data class KydPushData(
    val applyResult: String,
    val applyStatus: Boolean,
    val isCallback: Boolean,
    val jumpH5Url: String,
    val jumpType: String
)

data class PlanIdListRequest(
    val planIdList: List<Int>
)

