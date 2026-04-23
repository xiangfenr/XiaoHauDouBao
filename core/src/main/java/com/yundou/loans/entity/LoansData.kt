package com.yundou.loans.entity

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class DaikuanData(
    val feedback_url: String? = null,
    val user_url: String? = null,
    val channel_url: String? = null,
) : Serializable

@Keep
data class DaikuanUrlData(
    val app_check_status: Int = 0, //审核版：1=审核中,2=已上架
    val form_status: Int = -1, //表单状态：1=关闭，2=打开
    val loan_status: Int = -1,  //贷超状态：1=关闭,2=打开
    val recognize_id_card: Int = -1,    //身份证识别：1=关闭,2=打开
    val partner_id: Int = -1, //合作方ID
    val funding_provider: String? = null, //资金提供方
    val partner_url: String? = null, //魔力的BaseUrl地址


    //旧的版本
    val url: DaikuanData,
    val contact_us: Array<String>? = null,
    val list: Array<String>? = null,
    val zxd_click_deal: Int = 0,  //是否点击协议后在进件---1是，2否
    val loan_overdue_status: Int = 0,  //1关闭  2打开
    /**
     * key=1 value= 微融宝 18
     * key=2 value =智享贷 17
     */
    val shrimp_channel: Map<String, Int>? = null,
    //sink 贷超刷新沉底规则-----1沉底到同价格底部，2沉底到所有贷超底部
    val sink: Int = 0,
    val timeout_second: Int = 0, //api助贷撞库超时
    val shrimp_channel_concurrency: List<Int>? = null, //api助贷撞库超时
    var jiyong_price: Double? = null,//吉用钱包
) : Serializable

@Keep
data class AppConfinData(
    val app_status: String? = null,
    val form_status: String? = null,
    val loan_status: String? = null,
    val partner_id: Int = -1,
) : Serializable

