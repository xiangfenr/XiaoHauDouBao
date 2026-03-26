package com.yundou.loans.entity

import androidx.annotation.Keep
import java.io.Serializable


data class YXHSendEMSData(
    val mobile: String? = null,
    val scene: String? = null,
    val channel: String? = null,
    val tid: String? = null
)

data class YXHCodeData(
    val isRegister: Int = 0,
    val isAppFirstRegister: Int = 0,
    val isNewUser: Int = 0,
    val registerTime: String? = null,
    val userId: String? = null,
    val idcardRiskRescode: Int = 0,
    val sex: Int = 0,
    val age: Int = 0,
    val username: String? = null,
    val isNeedCommitStepTwo: Int = 0,
    val channel: String? = null,
    val isBlackCityUser: Int = 0,
    val token: String? = null
)

data class YXHLocationInfo(
    val nation: String? = null,
    val province: String? = null,
    val city: String? = null,
    val district: String? = null,
    val adcode: Int = 0,
    val cityCode: Int = 0,
    val provinceCode: Int = 0,
    val nationCode: Int = 0
)

@Keep
data class YXHuaSaveData(
    var realName: String? = null,//姓名
    var idCard: String? = null,//身份证号码
    var city: String? = null,//工作城市,结尾不要带‘市’
    var zhima: ContentValueBean = ContentValueBean(),
    var car: ContentValueBean = ContentValueBean(),
    var cityOfWorkCode: String? = null,
    var house: ContentValueBean = ContentValueBean(),
    var insurance: ContentValueBean = ContentValueBean(),
    var loanAmount: Int? = null,
    var profession: ContentValueBean = ContentValueBean(),
    var reservedFunds: ContentValueBean = ContentValueBean(),
    var revenue: ContentValueBean = ContentValueBean(),
    var socialSecurity: ContentValueBean = ContentValueBean(),
) : Serializable


data class ContentValueBean(
    var id: String? = null,
    var content: String? = null
) : Serializable

data class YXHIdentData(
    val result: Int = 0,
    val info: YXHIdentResultData? = null
)

data class YXHIdentResultData(
    val age: Int = 0,
    val sex: Int = 0,
    val errMsg: String? = null,
    val idcardRiskRescode: Int = 0
)

data class MatchData(
    val otherInfo: OtherInfo,
    val planMatchNormalGoods: PlanMatchNormalGoods,
    val planMatchOrgans: PlanMatchOrgans? = null,
    val planMatchPlatforms: PlanMatchPlatforms? = null,
    val planMatchResult: Int = 0,
    val planMatchToken: String? = null
)

data class OtherInfo(
    val maxQuota: Int = 0,
    val minQuota: Int = 0,
    val quotaDesc: String? = null,
    val quotaRate: String? = null,
    val quotaRateDesc: String? = null,
    val termList: List<Term>? = null
)

data class Term(
    val term: Int = 0,
    val termDesc: String? = null
)

data class PlanMatchNormalGoods(
    val protocolList: List<ProtocolData>,
    val resultList: List<NormalGoods>? = null
)

data class NormalGoods(
    val goodsId: Int = 0,
    val label: List<Label>,
    val logo: String? = null,
    val name: String? = null,
    val quotaDesc: String? = null,
    val quotaSymbolDesc: String? = null,
    val rateDesc: String? = null,
    val rateSymbolDesc: String
)

data class Label(
    val color: String? = null,
    val id: Int = 0,
    val labelName: String? = null
)

data class ProtocolData(
    val protocolName: String? = null,
    val protocolUrl: String? = null,
    val readType: Int = 0
)

data class PlanMatchOrgans(
    val planMatchBusinessType: Int = 0,
    val planMatchGoodsId: Int = 0,
    val planMatchGoodsLogo: String? = null,
    val planMatchGoodsName: String? = null,
    val planMatchGoodsType: Int = 0,
    val planMatchOrgansId: Int = 0,
    val planMatchOrgansName: String? = null,
    val planMatchProtocol: List<ProtocolData>? = null
)

data class PlanMatchPlatforms(
    val platformType: Int = 0,
    val protocolList: List<PlatformProtocol>,
    val resultList: List<Platform> = ArrayList()
)

data class PlatformProtocol(
    val isList: Int = 0,
    val protocolName: String? = null,
    val protocolUrl: String? = null,
    val readType: Int = 0
)

data class Platform(
    val organsName: String? = null,
    val platformAccountNo: Long,
    val platformAuthType: Int = 0,
    val platformLogo: String? = null,
    val platformName: String? = null,
    val platformProtocolList: List<PlatformProtocolDetail>,
    val weight: Int = 0
)

data class PlatformProtocolDetail(
    val protocolContent: String? = null,
    val protocolName: String? = null,
    val protocolOpenType: Int = 0,
    val protocolUrl: String? = null
)

data class OrgansApplyData(
    val result: Int = 0,
    val planMatchOrgans: PlanMatchOrgansBean? = null
)

data class PlanMatchOrgansBean(
    val planMatchOrgansId: Int = 0,
    val planMatchGoodsId: Int = 0,
    val planMatchOrgansName: String? = null,
    val planMatchGoodsLogo: String? = null,
    val planMatchProtocol: List<ProtocolData>,
    val planMatchGoodsType: Int = 0,
    val planMatchBusinessType: Int = 0,
    val planMatchGoodsName: String? = null,
    val quotaDesc: String? = null,
    val quotaSymbolDesc: String? = null,
    val rateSymbolDesc: String? = null,
    val rateDesc: String? = null,
    val termDesc: String? = null
)

