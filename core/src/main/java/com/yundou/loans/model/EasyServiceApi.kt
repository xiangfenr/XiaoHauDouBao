package com.yundou.loans.model

import com.yundou.loans.entity.*
import com.yundou.loans.http.ResultBean
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * 网络接口定义。
 * 当前版本仅保留本部服务与魔力合作方接口，其余渠道接口已全部移除。
 */
interface EasyServiceApi {

    //获取版本号/运行配置
    @GET("server")
    fun getServer(): Deferred<ResultBean<DaikuanUrlData>>

    // 发送验证码（本部）
    @GET("user/sms")
    fun getMsgCode(
        @Query("mobile") mobile: String,
    ): Deferred<ResultBean<Any>>

    //收集异常
    @POST("report/abnormal")
    fun reportAbnormal(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    //登录
    @POST("user/login")
    fun getLogin(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<GetTokenData>>

    //注销
    @POST("user/logoff")
    fun logoff( @Body body: HashMap<String, String?>): Deferred<ResultBean<String>>

    //刷新token
    @GET("api/v1/user/refreshToken")
    fun refreshToken(): Deferred<ResultBean<GetTokenData>>

    //本部反馈
    @POST("form/feedback")
    fun feedback(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<FeedbackData>>

    //表单二要素校验
    @POST("form/verify2meta")
    fun twoElements(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<TwoElementData>>

    //贷超列表
    @GET("loan/list")
    fun channelList(
        @Query("age") age: Int,
    ): Deferred<ResultBean<ChannerList>>

    //贷超-详情
    @GET("loan/view")
    fun viewLoanNew(
        @Query("loan_id") loan_id: String,
        @Query("partner_id") partner_id: String?,
        @Query("mobile") mobile: String?,
    ): Deferred<ResultBean<applyId>>

    @POST("api/v1/user/setPasswd")
    fun setPasswd(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<applyId>>

    @GET("user/mobile")
    fun getMobileInfo(
        @Query("mobile") mobile: String?,
    ): Deferred<ResultBean<LogOff>>

    //表单-拦截重复
    @POST("form/intercept")
    fun oppoApplyIntercept(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<Any>>

    @POST("report/point")
    fun reportPointRequest(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<Any>>

    //********************************魔力 接口***********************************
    //获取验证码
    @Headers("urlname:molierbaUrl")
    @Multipart
    @POST("hyj/code/loginCode.html")
    fun moliGetCode(
        @Part("mobile") mobile: RequestBody,
        @Part("app_key") app_key: RequestBody,
        @Part("sign") sign: RequestBody,
    ): Deferred<ResultBean<Any>>

    //验证码登录
    @Headers("urlname:molierbaUrl")
    @Multipart
    @POST("hyj/user/codeLogin.html")
    fun moliCodeLogin(
        @Part("mobile") mobile: RequestBody,
        @Part("app_key") app_key: RequestBody,
        @Part("sms_code") sms_code: RequestBody,
        @Part("sign") sign: RequestBody,
    ): Deferred<ResultBean<MoLiTokenData>>

    //获取省市区
    @Headers("urlname:molierbaUrl")
    @Multipart
    @POST("hyj/user/cityTreeV1.html")
    fun moliTreeV1(
        @Part("app_key") app_key: RequestBody,
        @Part("sign") sign: RequestBody,
    ): Deferred<ResultBean<List<MoLiProvince>>>

    //提交表单
    @Headers("urlname:molierbaUrl")
    @Multipart
    @POST("hyj/user/submit.html")
    fun moliSubmitForm(
        @Part("assistant_key") assistant_key: RequestBody,
        @Part("app_key") app_key: RequestBody,
        @Part("base_access_token") base_access_token: RequestBody,
        @Part("current_district_id") current_district_id: RequestBody,
        @Part("id_card_no") id_card_no: RequestBody,
        @Part("realname") realname: RequestBody,
        @Part("zhima_score") zhima_score: RequestBody,
        @Part("other_assets") other_assets: RequestBody,
        @Part("sign") sign: RequestBody,
    ): Deferred<ResultBean<MoLiFormSubmit>>

    //获取协议
    @Headers("urlname:molierbaUrl")
    @Multipart
    @POST("hyj/user/protocol.html")
    fun protocolGet(
        @Part("app_key") app_key: RequestBody,
        @Part("code") code: RequestBody,
        @Part("form_id") form_id: RequestBody,
        @Part("product_id") product_id: RequestBody,
        @Part("sign") sign: RequestBody,
    ): Deferred<ResultBean<MoliGetXieyi>>

    //获取注册协议
    @Headers("urlname:molierbaUrl")
    @Multipart
    @POST("hyj/user/protocol.html")
    fun protocolRegisterGet(
        @Part("app_key") app_key: RequestBody,
        @Part("code") code: RequestBody,
        @Part("sign") sign: RequestBody,
    ): Deferred<ResultBean<MoliGetXieyi>>

    //确认推送
    @Headers("urlname:molierbaUrl")
    @Multipart
    @POST("hyj/user/confirm.html")
    fun moliConfirm(
        @Part("app_key") app_key: RequestBody,
        @Part("step_id") base_access_token: RequestBody,
        @Part("base_access_token") current_district_id: RequestBody,
        @Part("sign") sign: RequestBody,
    ): Deferred<ResultBean<MoLiFormSubmit>>
}
