package com.yundou.loans.entity

import androidx.annotation.Keep
import java.io.Serializable


@Keep
data class AddressArrayData(
    var array: List<AddressListData>? = null,//姓名

) : Serializable


@Keep
data class AddressListData(
    var name: String? = null,//姓名
    var city: ArrayList<CityData>? = null,

) : Serializable

@Keep
data class CityData(
    var name: String? = null,//姓名
    var area: ArrayList<String>? = null,

) : Serializable

