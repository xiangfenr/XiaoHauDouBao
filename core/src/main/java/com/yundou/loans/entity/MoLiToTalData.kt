package com.yundou.loans.entity

import java.io.Serializable

/**
 * 魔力28 实体
 */
data class MoLiTokenData(
    val user: String,
    val access_token: String
)

data class MoLiFormData(
    var id_card_no: String? = null,
    var realname: String? = null,
    var current_district_id: String? = null,
    var zhima_score: String? = null,
    var other_assets: String? = null
) : Serializable

data class MoLiProvince(
    val child: List<MoliCity>,
    val code: Int,
    val full_name: String,
    val id: Int,
    val index: String,
    val name: String,
    val p_id: Int,
    val type: Int
)

data class MoliCity(
    val child: List<MoliArea>,
    val code: Int,
    val full_name: String,
    val id: Int,
    val index: String,
    val name: String,
    val p_id: Int,
    val type: Int
)

data class MoliArea(
    val code: Int,
    val full_name: String,
    val id: Int,
    val index: String,
    val name: String,
    val p_id: Int,
    val type: Int
)

data class MoLiFormSubmit(
    val access_token: String,
    val app_id: Int,
    val ddqb_tips: String,
    val flow_state: Int,
    val form_id: String,
    val is_callback: Int,
    val is_new: Int,
    val match_info: MatchInfo,
    val message: String,
    val order_sn: String,
    val pass_state: Int,
    val project_id: Int,
    val skip_url: String,
    val state: Int,
    val today_channel_form_number: Int
)

data class MatchInfo(
    val agreement_list: List<MoLiAgreement>,
    val assistant_key: String,
    val estimate_amount: Int,
    val product_id: Int,
    val product_info: MoliProductInfo,
    val product_list_type: Int,
    val redirect_url: String,
    val skip_type: Int,
    val state: Int,
    val step_id: String,
    val step_number: Int,
    val type: Int
)

data class MoliProductInfo(
    val amount_format: String,
    val amount_tips: String,
    val contact_mode: String,
    val handle_mode: String,
    val href: String,
    val id: Int,
    val logo: String,
    val name: String,
    val operating_entity: String,
    val product_tips: String,
    val rate_format: String,
    val rate_tips: String,
    val time_format: String,
    val time_tips: String,
    val wechat_gzh: String,
    val wechat_gzh_qrcode: String
)

data class MoLiAgreement(
    val code: String,
    val name: String
)

data class MoliGetXieyi(
    val handle_id: Int,
    val name: String,
    val content: String,
)

