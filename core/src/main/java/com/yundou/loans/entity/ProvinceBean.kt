package com.yundou.loans.entity

import androidx.annotation.Keep
import java.io.Serializable

/**
 * 有code的 省份和城市
 */
@Keep
data class ProvinceBean(
    var name: String? = null,// 身份名称
    var code: Int = 0, //省份code
    var city: ArrayList<CityBean>? = null, //省份下面的城市

) : Serializable


@Keep
data class CityBean(
    var name: String? = null,// 城市名称
    var code: Int = 0, //城市code
    var area: ArrayList<AreaBean>? = null, //城市下的区

) : Serializable

@Keep
data class AreaBean(
    var name: String? = null,//城市名称
    var code: Int = 0

) : Serializable

