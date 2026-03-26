package com.yundou.loans.entity

import androidx.annotation.Keep
import java.io.Serializable


@Keep
data class TwoElementData(
    val code: String? = null,
) : Serializable

/**
 * 二项目表单
 */
@Keep
data class TwoPFormData(
    var mobile: String? = null,
    var id_number: String? = null,
    var name: String? = null,
    var province: String? = null,
    var city: String? = null,
    var district: String? = null,
    var credit_card: Int = -1,
    var credit: Int = -1,
    var profession: Int = -1,
    var sesame_seed: Int = -1,
    var fund: Int = -1,
    var social_insurance: Int = -1,
    var business_insurance: Int = -1,
    var house_property: Int = -1,
    var car_property: Int = -1,
    var salary: Int = -1,
    var monthly_income: Int = -1,
    var apply_limit: Int = -1
) : Serializable

@Keep
data class TwoResultData(
    var data_id: String? = null
)

@Keep
data class TwoChannelData(
    var id: Int = 0,
    var title: String? = null
)

/**
 * 二项目,获取结果Data
 */
@Keep
data class TwoPResultData(
    var code: Int = 0,
    var channel_id: Int = 0,
    var channel_url: String? = null
)

