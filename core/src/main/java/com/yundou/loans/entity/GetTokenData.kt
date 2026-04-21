package com.yundou.loans.entity

import androidx.annotation.Keep
import java.io.Serializable


@Keep
data class GetTokenData(
    val token: String? = null,
    val user_id: String? = null,
) : Serializable


@Keep
data class GetUserInfoData(
    val username: String? = null,
    val mobile: String? = null,
    val is_test: String? = null,
    val apply_status: String? = null,
) : Serializable


@Keep
data class DaikuanUrlDatas(
    val url: DaikuanData,
    val app_config: AppConfinData? = null,
) : Serializable


@Keep
data class ChannerList(
    var last_id: String? = null,
    val list: List<ChannerItem>? = null,
    val apply: String? = null,
) : Serializable


@Keep
data class ChannerItem(
    val icon_image: String? = null,
    val title: String? = null,
    val loan_limit: String? = null,
    val daily: String? = null,
    val month: String? = null,
    val service_start_times: String? = null,
    val service_end_times: String? = null,
    val id: Int? = null,
    val loan_id: String? = null,
    val brief_content: String? = null,
    val sort: Int = 0,
    val url_type: Int=0, //1非联登，2联登
) : Serializable


@Keep
data class applyId(
    val applyId: String? = null,
    val url: String? = null,
    val apply: String? = null,
    val app_status: String? = null,
    val loan_id: String? = null,

    ) : Serializable


@Keep
data class FeedbackData(
    val feedback_id: String? = null,
) : Serializable

@Keep
data class LogOff(
    /**
     * 0、新号
     * 1、注销账号(客户端拦截登陆)
     * 2、应用商店审核人员账号(客户端切到本服)
     * 3、自测手机号(客户端自己测试使用，不用切到本服)
     * 4、提交给应用商店的测试账号(客户端切到本服)
     * 5、历史账号(客户端切到本服)
     */
    val type: Int = 0
)

