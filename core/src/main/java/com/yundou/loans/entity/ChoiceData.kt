package com.yundou.loans.entity

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class ChoiceData(
    var real_name: String? = null,//姓名
    var mobile: String? = null,//手机号
    var id_number: String? = null,//身份证
    var work_city: String? = null,//城市
    var credit_card: String? = null,//信用卡:1、无，2、有
    var credit: String? = null,//征信情况:1、信用良好，2、当前逾期
    var professional_identity: String? = null,//职业身份:1、上班族，2、私营企业主，3、自由职业，4、公务员/国企
    var sesame_seed: String? = null,//芝麻信用分:1、600 分以下，2、600-650分，3、650-700分，4、700分以上
    var fund: String? = null,//公积金:1、无公积金，2、有公积金
    var social_insurance: String? = null,//社保:1、无社保，2、有社保
    var business_insurance: String? = null,//商保:1、无商业保单，2、有商业保单
    var house_property: String? = null,//房产:1、无房产 2、有房产
    var car_property: String? = null,//车产:1、无车产，2、有车产
    var salary: String? = null,//工资:1、银行卡，2、现金，3、其他
    var monthly_income: String? = null,//月收入:1、5000以下，2、5000以上
    var apply_limit: String? = null,//申请额度:1、1万-5万，2、5万-10万，3、10万-50万，4、50万-100万，5、100万以上
) : Serializable

@Keep
data class WorldAreaCode(
    var name: String? = null,//姓名
    var city: ArrayList<ArrayList<String>>? = null,

) : Serializable

