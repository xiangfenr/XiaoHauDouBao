package com.yundou.loans.entity

import androidx.annotation.Keep
import java.io.Serializable


@Keep
data class WmUserData(
    val products: List<WmInfoData>? = null,
) : Serializable


@Keep
data class WmInfoData(
    val lendOrg: String? = null,
    val loanAmountMax: String? = null,
    val loanAmountMin: String? = null,
    val logo: String? = null,
    var name: String? = null,
    val periodMax: String? = null,
    val periodMin: String? = null,
    val rateMax: String? = null,
    val rateMin: String? = null,
    val url: String? = null,
    var applyNum: String? = null,
    val id: Int? = null,
    val protocolList: List<ProtocolListData>? = null,
) : Serializable

@Keep
data class ProtocolListData(
    val protocolName: String? = null,
    val protocolUrl: String? = null,
) : Serializable


@Keep
data class SaveData(
    var car: String? = null,//车产 0.无 1.有
    var cityOfWork: String? = null,//工作城市,结尾不要带‘市’
    var cityOfWorkCode: String? = null,//工作城市代码
    var creditCard: String? = null,//信用卡 0.无 1.有
    var education: String? = null,//学历 0.高中/中专 1.大专 2.本科 3.硕士 4.博士
    var house: String? = null,//房产 0.无 1.有
    var idCardNo: String? = null,//身份证号码
    var insurance: String? = null,//保险保单 0.无 1.有
    var loanAmount: Int? = null,//贷款额度
    var loanLimit: String? = null,//借款期限
    var loanUse: String? = null,//借款用途
    var name: String? = null,//姓名
    var profession: String? = null,//职业
    var reservedFunds: Int? = null,//公积金 0.无 1.有
    var revenue: Int? = null,//月收入
    var socialSecurity: String? = null,//社保 0.无 1.有
    var zhima: String? = null,//芝麻分
) : Serializable


@Keep
data class ProductIdsData(
    var productIds: ArrayList<Int>? = ArrayList(),
) : Serializable


@Keep
data class DataitemData(
    val data: MutableList<AddressData>,
    val code: String? = null,
    val message: String? = null,
) : Serializable

@Keep
data class AddressData(
    val id: String? = null,
    val title: String? = null,
    val value: String? = null,
    val children: List<ChildrenData>? = null,
) : Serializable

@Keep
data class ChildrenData(
    val id: String? = null,
    val title: String? = null,
    val value: String? = null,
    val children: List<String>? = null,
) : Serializable

