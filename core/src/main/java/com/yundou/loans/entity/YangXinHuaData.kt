package com.yundou.loans.entity

import java.io.Serializable


data class YxhTokenData(
    var token: String? =null,
    var userId: String? =null,
    var phone: String? =null,
)


/**
 * @Author: fenr
 * 时间: 2025/4/1
 * 类名: ACTIVITY
 * 简述: 阳薪花  表单
 *
 */
data class YxhSaveData(
    var loanAmount: String? = null, //申请金额
    var loanLimit: String? = null, //贷款期限 1.6个月 2.9个月 3.12个月 4.24个月
    var loanUse: String? = null, //借款用途1.购车贷款 2.购房贷款 3.装修贷款 4.教育贷款 5.消费贷款 6.过桥贷款 7.结婚贷款 8.旅游贷款 9.医疗贷款 10.其他贷款
    var idCardNo: String? = null, //身份证
    var name: String? = null, //姓名
    var cityOfWorkCode: String? = null, ///城市code
    var zhima: String? = null, //芝麻分1.550分以下   2.550-600分   3.600-650分 4.650-700分   5.700分及以上
    var huabei: String? = null, //花呗 0.无   1.5000以下   2.5000-10000   3.10000以上
    var baitiao: String? = null, //白条 0.无   1.2000以下   2.2000-5000   3.5000以上
    var creditStatus: String? = null, //逾期情况 1.当前未逾期   2.当前有逾期
    var education: String? = null, //学历 1.专科以下   2.专科   3.本科   4.硕士及以上
    var reservedFunds: String? = null, //公积金 0.无   1.缴纳未满6个月   2.缴纳满6个月
    var socialSecurity: String? = null, //社保 0.无   1.缴纳未满6个月   2.缴纳满6个月
    var insurance: String? = null, //保单  0.无   1.缴纳未满1年   2.缴纳1年以上
    var house: String? = null, //房产  0.无   1.有房可抵押   2.有房不抵押
    var car: String? = null, //车产  0.无   1.有车可抵押   2.有车不抵押
    var profession: String? = null, //职业 1.公务员 2.事业单位员工 3.其他私企员工 4.企业主 5.个体户
    var revenue: String? = null, //月收入 1.3000及以下  2.3000-5000  3.5000-10000 4.1万-2万   5.2万-5万     6.5万及以上
    var salaryType: String? = null, //工资发放形式 1.银行代发   2.现金发放   3.微信或支付宝转账
    var enterpriseType: String? = null, //企业类型 1.微小型企业一年收入500万以下   2.中小企业一年收入500万-5000万   3.大型企业一年收入5000万以上
    var license: String? = null, //营业执照 1.无   2.有1年以内   3.有1年以上


) : Serializable

/**
 * 省市区
 */
data class YxhProvinceData(
    val children: List<YxhCityData>,
    val id: Int,
    val parentId: Int,
    val title: String,
    val value: String,
    val weight: String
)

data class YxhCityData(
   // val children: List<Any>,
    val id: Int,
    val parentId: Int,
    val pid: Int,
    val title: String,
    val value: String
)

/**
 * 资料保存成功后
 */
data class YxhProductData(
    val access: Boolean,
    val hasFast: String,
    val hasdownload: String,
    val listAgreement: List<YxhAgreement>,
    val plans: List<YxhPlan>,
    val url: String
)

data class YxhPlan(
    val bookMsg: String,
    val busiAddress: String,
    val busiName: String,
    val canApply: Boolean,
    val cityOfWork: String,
    val cityOfWorkCode: String,
    val loanAmountAvg: Int,
    val loanTerm: String,
    val payRule: String,
    val platform: String,
    val productIcon: String,
    val productName: String,
    val productType: String,
    val rateMax: Double,
    val rateMin: Double,
    val showType: Int,
    val subscribeId: Int
)

data class YxhAgreement(
    val name: String,
    val url: String
)

/**
 * 申请产品data
 */
data class YxhApplyData(
    val applyInto: Boolean,
    val contractVoList: Any,
    val downloadURL: Any,
    val ext: Any,
    val fastList: Any,
    val hasFast: String,
    val hasdownload: String,
    val message: String,
    val planIds: List<Int>,
    val remark: Any,
    val url: Any,
    val userToken: Any,
    val wechat: Any
)