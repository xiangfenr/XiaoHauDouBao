package com.yundou.loans.model

import com.google.gson.Gson
import com.yundou.loans.entity.*
import com.yundou.loans.http.ResultBean
import com.yundou.loans.http.YXHResultBean
import com.yundou.loans.utils.Constants
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.*
import java.io.Serializable

interface EasyServiceApi {

    //获取版本号
    @GET("server")
    fun getServer(): Deferred<ResultBean<DaikuanUrlData>>

//    @GET("api/v1/user/getIpLocation")
//    fun getIpLocation(): Deferred<ResultBean<Any>>


    // 发送验证码
    @GET("user/sms")
    fun getMsgCode(
        @Query("mobile") mobile: String,
    ): Deferred<ResultBean<Any>>

    //收集异常logo
    @POST("report/abnormal")
    fun reportAbnormal(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    //注册
    @POST("api/v1/user/register")
    fun getRegister(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<GetTokenData>>


    //登录
    @POST("user/login")
    fun getLogin(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<GetTokenData>>

    //第三方用户注销
//    @POST("api/partner/user/logoff")
//    fun logoffThree(
//        @Body body: HashMap<String, String?>,
//    ): Deferred<ResultBean<Any>>

    @POST("user/logoff")
    fun logoff(): Deferred<ResultBean<String>>

    //检查第三方用户是否注销
//    @GET("api/partner/user/checkOff")
//    fun checkOffIsLogoff(
//        @Query("mobile") mobile: String?,
//    ): Deferred<ResultBean<LogOff>>

    //刷新token
    @GET("api/v1/user/refreshToken")
    fun refreshToken(): Deferred<ResultBean<GetTokenData>>


    @GET("api/v1/user/userInfo")
    fun userInfo(): Deferred<ResultBean<GetUserInfoData>>

//    @POST("user/logout")
//    fun logoOut(): Deferred<ResultBean<String>>


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

    //全流程上报
    @POST("api/partner/report/orgReport")
    fun completeFlowReport(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<Any>>


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

    @Headers("urlname:weimiaoyongUrl")
    @GET("login/channel")
    fun channelAsync(): Deferred<ResultBean<Any>>

    @Headers("urlname:weimiaoyongUrl")
    @GET("user/get/basic/info")
    fun basicAsync(): Deferred<ResultBean<Any>>


    @Headers("urlname:weimiaoyongUrl")
    @POST("login/captcha")
    fun captchaAsync(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    @Headers("urlname:weimiaoyongUrl")
    @POST("login/sms")
    fun captchaLoginAsync(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<GetTokenData>>

    @Headers("urlname:weimiaoyongUrl")
    @GET("user/product/check/into")
    fun intoAsync(): Deferred<ResultBean<WmUserData>>


    @Headers("urlname:weimiaoyongUrl")
    @POST("user/save/basic/info")
    fun saveAsync(
        @Body body: SaveData?,
    ): Deferred<ResultBean<Any>>

    @Headers("urlname:weimiaoyongUrl")
    @GET("config/city/tree")
    fun tree(): Deferred<DataitemData>

    @Headers("urlname:weimiaoyongUrl")
    @POST("user/product/empower/agreement")
    fun agreement(@Body body: ProductIdsData?): Deferred<ResultBean<Any>>

    @Headers("urlname:weimiaoyongUrl")
    @POST("user/product/apply")
    fun wxapply(@Body body: ProductIdsData?): Deferred<ResultBean<Any>>


    @POST("api/partner/report/report")
    fun partnerreport(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    //-------------------智享贷 相关接口----------------------
    //获取验证码
    @Headers("urlname:zhixiangdaiUrl")
    @POST("api/v1/get_sms_code")
    fun zxdGetCode(@Body route: RequestBody): Deferred<ResultBean<Any>>

    //验证码登录
    @Headers("urlname:zhixiangdaiUrl")
    @POST("api/v1/login")
    fun zxdcodeLogin(@Body route: RequestBody): Deferred<ResultBean<GetTokenData>>

    //提交表单
    @Headers("urlname:zhixiangdaiUrl")
    @POST("api/v1/sub_info_v1")
    fun zxdSubmitForm(@Body route: RequestBody): Deferred<ResultBean<ZxdFormResultBean>>


    //-------------------快易贷 相关接口----------------------
    //进入到登录页面就调用
    @Headers("urlname:kuaiyidaiUrl")
    @GET("wzd/truck/iconV2.gif")
    fun iconV2Get(@Query("channelSign") channelSign: String): Deferred<ResultBean<Any>>

    //获取验证码
    @Headers("urlname:kuaiyidaiUrl")
    @POST("wzd/user/api/verify/request/h5sms")
    fun kydGetCode(@Body route: RequestBody): Deferred<ResultBean<KydCodeBean>>

    //验证码登录
    @Headers("urlname:kuaiyidaiUrl")
    @POST("wzd/api/v3/ad/passport/smsCodeLogin")
    fun kydcodeLogin(@Body route: RequestBody): Deferred<ResultBean<KydTokenData>>

    //静默登录
    @Headers("urlname:kuaiyidaiUrl")
    @POST("wzd/api/v3/ad/passport/silenceLogin")
    fun silenceLogin(@Body route: RequestBody): Deferred<ResultBean<KydTokenData>>

    //获取表单数据
    @Headers("urlname:kuaiyidaiUrl")
    @GET("wzd/api/v3/ad/from/getFormConfigAllInfo")
    fun kydGetFormData(@Query("channelSign") channelSign: String): Deferred<ResultBean<KydFormBean>>

    //获取协议
    @Headers("urlname:kuaiyidaiUrl")
    @GET("wzd/api/v3/ad/from/userInformationAuthorizationLetter")
    fun userInformationAuthorizationLetter(
        @Query("channelSign") channelSign: String,
        @Query("protocolType") protocolType: String
    ): Deferred<ResultBean<XieYiData>>

    //获取城市数据
    @Headers("urlname:kuaiyidaiUrl")
    @GET("wzd/user/h5/frontform/getRegion")
    fun kydGetCity(): Deferred<ResultBean<List<KydProvinceBean>>>

    //保存基本信息
    @Headers("urlname:kuaiyidaiUrl")
    // @POST("wzd/api/v3/ad/from/saveBasicInfo")
    @POST("wzd/api/v3/ad/from/batchSaveBasicInfo")
    fun kydSubmitForm(@Body route: RequestBody): Deferred<ResultBean<SaveedBean>>

    //匹配机构
    @Headers("urlname:kuaiyidaiUrl")
    @POST("wzd/api/v3/ad/from/matchingInstitutions")
    fun matchingInstitutions(@Body route: RequestBody): Deferred<ResultBean<MatchiingBean>>

    //推送进件
    @Headers("urlname:kuaiyidaiUrl")
    @POST("wzd/api/v3/ad/from/authorizationApply")
    fun authorizationApply(
        @Query("channelSign") channelSign: String,
        @Body route: RequestBody
    ): Deferred<ResultBean<KydPushData>>

    //********************************魔力28 接口***********************************
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


    //提交表单
    @Headers("urlname:molierbaUrl")
    @Multipart
    @POST("hyj/user/confirm.html")
    fun moliConfirm(
        @Part("app_key") app_key: RequestBody,
        @Part("step_id") base_access_token: RequestBody,
        @Part("base_access_token") current_district_id: RequestBody,
        @Part("sign") sign: RequestBody,
    ): Deferred<ResultBean<MoLiFormSubmit>>


    /***
     *  ********************************二项目 接口***********************************
     */

    //发送验证码
    @Headers("urlname:twoHeRuiUrl")
    @GET("v1/user/sms")
    fun twopSendCode(
        @Header("signature") signature: String,
        @Query("mobile") mobile: String,
    ): Deferred<ResultBean<Any>>

    //登录
    @Headers("urlname:twoHeRuiUrl")
    @POST("v1/user/login")
    fun twopLogin(
        @Header("signature") signature: String,
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<GetTokenData>>

    //提交表单
    @Headers("urlname:twoHeRuiUrl")
    @POST("v1/form/apply")
    fun twopFormPost(
        @Header("signature") signature: String,
        @Body body: TwoPFormData,
    ): Deferred<ResultBean<TwoResultData>>

    //获取渠道列表(暂停)
    @Headers("urlname:twoHeRuiUrl")
    @GET("v1/channel/list")
    fun getChannelList(
        @Header("signature") signature: String
    ): Deferred<ResultBean<List<TwoChannelData>>>


    //上报(暂停)
    @Headers("urlname:twoHeRuiUrl")
    @POST("v1/report/match")
    fun reportTwoMatch(
        @Header("signature") signature: String,
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>


    //获取结果
    @Headers("urlname:twoHeRuiUrl")
    @GET("v1/form/result")
    fun twoPGetResult(
        @Header("signature") signature: String,
        @Query("data_id") data_id: String,
    ): Deferred<ResultBean<TwoPResultData>>



    /***
     *  ********************************阳薪花 接口***********************************
     */
    @Headers("urlname:yangXinHuaUrl")
    @POST("login/getCode")
    fun yangXinHuaSendCode(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    @Headers("urlname:yangXinHuaUrl")
    @POST("login/smsCode")
    fun yangXinHuaLogin(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<YxhTokenData>>

    @Headers("urlname:yangXinHuaUrl")
    @GET("config/city/tree")
    fun yangXinHuaTree(): Deferred<ResultBean<List<YxhProvinceData>>>

    @Headers("urlname:yangXinHuaUrl")
    @POST("user/save/basic/info")
    fun yangXinSaveInfo(@Body body: HashMap<String, String?>): Deferred<ResultBean<Any>>

    @Headers("urlname:yangXinHuaUrl")
    @POST("h5/xdLoan/plan/checkInto")
    fun yangXinPiPei(@Body body: HashMap<String, String?>): Deferred<ResultBean<YxhProductData>>


    @Headers("urlname:yangXinHuaUrl")
    @POST("h5/xdLoan/plan/apply")
    fun yangXinApply(@Body body: HashMap<String, String?>): Deferred<ResultBean<YxhApplyData>>


    /***
     *  ********************************吉用钱包 接口***********************************
     */
    @Headers("urlname:jiYongBaseUrl")
    @POST("channel/feed/apply/sendCode")
    fun jiYongSendCode(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>


    @Headers("urlname:jiYongBaseUrl")
    @POST("channel/feed/apply/login")
    fun jiYongLoginPost(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<GetTokenData>>

    @Headers("urlname:jiYongBaseUrl")
    @POST("channel/feed/apply/check")
    fun jiYongCheckInfo(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    @Headers("urlname:jiYongBaseUrl")
    @POST("channel/feed/apply/customer")
    fun jiYongApplyPost(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<JiyongOrderData>>

    /****---------------------------吉用帮 接口-------------------------***/

    @Headers("urlname:jiYongBangUrl")
    @POST("api/v2/landingPages/send")
    fun jiYongBangSendCode(
        @Query("phone") mobile: String?,
    ): Deferred<ResultBean<Any>>

    @Headers("urlname:jiYongBangUrl")
    @POST("api/v2/landingPages/login-check-sms")
    fun jiYongBangLogin(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<JiYBangTokenData>>

    @Headers("urlname:jiYongBangUrl")
    @POST("api/v2/landingPages/register")
    fun jiYBangApplyPost(
        @Body body: HashMap<String, Serializable>,
    ): Deferred<ResultBean<Any>>


    //-----------------------------有钱钱包 START--------------------------------

    @Headers("urlname:yqqbBaseUrl")
    @POST("api/login/sendPhoneVerifyCode")
    fun yqqbSendCode(
        @Body body: HashMap<String, String>,
    ): Deferred<ResultBean<Any>>


    @Headers("urlname:yqqbBaseUrl")
    @POST("api/login")
    fun yqqbLogin(
        @Body body: HashMap<String, String>,
    ): Deferred<ResultBean<YqqbTokenData>>


    @Headers("urlname:yqqbBaseUrl")
    @POST("api/user/info/save")
    fun yqqbSubmit(
        @Body body: YqChoiceData,
    ): Deferred<ResultBean<YqqbProductData>>

    @Headers("urlname:yqqbBaseUrl")
    @PUT("api/user/info/auth")
    fun yqqbAuth(
        @Body body: HashMap<String, Any>,
    ): Deferred<ResultBean<YqqbProductData>>

    @Headers("urlname:yqqbBaseUrl")
    @GET("api/user/info/protocol/{id}")
    fun yqqbProtocol(
        @Path("id") id: String?,
    ): Deferred<ResultBean<YqqbProtocolContent>>


    //----------------------------有钱钱包 END--------------------------------------

    //----------------------------天下分期 START--------------------------------------
    @Headers("urlname:tianxiaFenQiBaseUrl")
    @POST("prod-api/content/get")
    fun txfqAgreementGet(
        @Body body: HashMap<String, Any>,
    ): Deferred<ResultBean<TxfqAgreementData>>

    @Headers("urlname:tianxiaFenQiBaseUrl")
    @POST("prod-api/h5SendCodeV2")
    fun txfqSendCode(
        @Body body: RequestBody,
    ): Deferred<ResultBean<TxfqSendCode>>

    @Headers("urlname:tianxiaFenQiBaseUrl")
    @POST("prod-api/h5Login")
    fun txfqLogin(
        @Body body: RequestBody,
    ): Deferred<ResultBean<TxfqLoginBean>>

    @Headers("urlname:tianxiaFenQiBaseUrl")
    @GET("prod-api/regions/{id}/children")
    fun txfqGetCity(
        @Path("id") regionId: Int
    ): Deferred<ResultBean<List<TxfqCityBean>>>

    @Headers("urlname:tianxiaFenQiBaseUrl")
    @POST("prod-api/rsa/loan/h5ApplyV2")
    fun txfqApplySubmit(
        @Body body: HashMap<String, Any>,
    ): Deferred<ResultBean<TxfqApplyData>>

    @Headers("urlname:tianxiaFenQiBaseUrl")
    @POST("prod-api/loan/pushApply")
    fun txfqPushApply(
        @Body body: HashMap<String, Any>,
    ): Deferred<ResultBean<Any>>

    @Headers("urlname:tianxiaFenQiBaseUrl")
    @POST("prod-api/loan/pushApply2JQB")
    fun txfqPushApply2JQB(
        @Body body: HashMap<String, Any>,
    ): Deferred<ResultBean<Any>>

    //----------------------------天下分期 END--------------------------------------
    @Headers("urlname:zxdNewBaseUrl")
    @POST("api/v1/match/weimiaochu-md5")
    fun zxdNewMatch(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<ZxdNewResultProduceBean>>

    @Headers("urlname:zxdNewBaseUrl")
    @POST("api/v1/apply/weimiaochu-md5")
    fun zxdNewApply(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    //----------------------------智享贷全接口新增 START--------------------------------------


    //----------------------- 源小花 START ----------------------------
    @Headers("urlname:yuanXiaoHuaBaseUrl")
    @POST("/vest/kkh/sms/send")
    fun yxhSendCode(
        @Body body: RequestBody,
    ): Deferred<YXHResultBean<YXHSendEMSData>>

    @Headers("urlname:yuanXiaoHuaBaseUrl")
    @POST("/vest/kkh/oauth/login/mobile")
    fun yxhCodeLogin(
        @Body body: RequestBody,
    ): Deferred<YXHResultBean<YXHCodeData>>

    @Headers("urlname:yuanXiaoHuaBaseUrl")
    @POST("/vest/kkh/protocol/list")
    fun yxhProtocolList(): Deferred<YXHResultBean<YXHSendEMSData>>

    @Headers("urlname:yuanXiaoHuaBaseUrl")
    @POST("/vest/kkh/tools/location/ip")
    fun yxhAddressIp(): Deferred<YXHResultBean<YXHLocationInfo>>

    @Headers("urlname:yuanXiaoHuaBaseUrl")
    @POST("/vest/kkh/oauth/ident/query")
    fun yxhIdentQueay(@Body body: RequestBody): Deferred<YXHResultBean<YXHIdentData>>

    @Headers("urlname:yuanXiaoHuaBaseUrl")
    @POST("/vest/kkh/stay/form/commit")
    fun yxhFormCommit(@Body body: RequestBody): Deferred<YXHResultBean<Any>>

    @Headers("urlname:yuanXiaoHuaBaseUrl")
    @POST("/vest/kkh/stay/match")
    fun yxhStayMatch(@Body body: RequestBody): Deferred<YXHResultBean<MatchData>>

    @Headers("urlname:yuanXiaoHuaBaseUrl")
    @POST("/vest/kkh/stay/match/organs/apply")
    fun yxhOrgansApply(@Body body: RequestBody): Deferred<YXHResultBean<OrgansApplyData>>

    @Headers("urlname:yuanXiaoHuaBaseUrl")
    @POST("/vest/kkh/stay/match/platform/apply")
    fun yxhPlatformApply(@Body body: RequestBody): Deferred<YXHResultBean<OrgansApplyData>>


    //----------------------------期贷 START--------------------------------------

    @Headers("urlname:qiDaiBaseUrl")
    @POST("v2/platform/outChannelApi/getYzmCode")
    fun qiDaiSendCode(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<String>>


    @Headers("urlname:qiDaiBaseUrl")
    @POST("v2/platform/outChannelApi/login")
    fun qiDaiLogin(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Int>>


    @Headers("urlname:qiDaiBaseUrl")
    @POST("v2/platform/outChannelApi/getAgreement")
    fun qiDaiGetAgreement(
    ): Deferred<ResultBean<List<qiDaiAgreement>>>

    @Headers("urlname:qiDaiBaseUrl")
    @POST("v2/platform/outChannelApi/matchCheck")
    fun qiDaiMatchCheck(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<QiDaiProductObject>>

    @Headers("urlname:qiDaiBaseUrl")
    @POST("v2/platform/outChannelApi/matchRegister")
    fun qiDaiMatchRegister(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    @Headers("urlname:qiDaiBaseUrl")
    @POST("v2/platform/outChannelApi/queryFormStatus")
    fun qiDaiQueryFormStatus(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Int>>

    @Headers("urlname:qiDaiBaseUrl")
    @POST("v2/platform/outChannelApi/queryPlatform")
    fun qiDaiQueryPlatform(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<QiDaiProductObject>>

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






    //----------------------------微融宝 START--------------------------------------

    @Headers("urlname:weiRongBaoUrl")
    @POST("api/process/zk/${Constants.WEIRONGBAO_CHANNWL_CODE}")
    fun wrbBeForeMatch(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<WrbFormResultBean>>

    @Headers("urlname:weiRongBaoUrl")
    @POST("api/process/jj/${Constants.WEIRONGBAO_CHANNWL_CODE}")
    fun wrbApplyForm(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>


    //----------------------------吉贷 START--------------------------------------

    @POST("jidaiapi/login/phoneCode")
    fun jiDaiCodeGet(
        @Header("urlname") urlName: String,
        @Body body: HashMap<String, String>,
    ): Deferred<ResultBean<Any>>


    @POST("jidaiapi/login/registerPhoneCode")
    fun jiDaiLogin(
        @Header("urlname") urlName: String,
        @Body body: HashMap<String, String>,
    ): Deferred<ResultBean<GetTokenData>>


    @POST("jidaiapi/user/updateUserInfo")
    fun jiDaiSaveUserInfo(
        @Header("urlname") urlName: String,
        @Body body: RequestBody,
    ): Deferred<ResultBean<Any>>


    @POST("jidaiapi/jg/productList")
    fun jiDaiProductList(
        @Header("urlname") urlName: String,
        @Body body: RequestBody,
    ): Deferred<ResultBean<List<JiDaiProductInfo>>>


    @POST("jidaiapi/jg/sendProduct")
    fun jiDaiSendProduct(
        @Header("urlname") urlName: String,
        @Body body: HashMap<String, List<String?>>,
    ): Deferred<ResultBean<Any>>





    //----------------------------小福API START--------------------------------------
    @Headers("urlname:xiaoFuBaseUrl")
    @POST("open/support/match/mask/v2")
    fun xiaoFuJKMatch(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<XiaoFuPResult>>


    @Headers("urlname:xiaoFuBaseUrl")
    @POST("open/support/apply/mask/v2")
    fun xiaoFuApply(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<Any>>


    //----------------------------龙炎 START--------------------------------------
    @Headers("urlname:longYanUrl")
    @POST("open/api/n_20116/check")
    fun longYanMatch(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<LongYanFormResultBean>>

    @Headers("urlname:longYanUrl")
    @POST("open/api/n_20116/push")
    fun longYanFormPush(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    //----------------------------微银信用 START--------------------------------------
    @Headers("urlname:weiYinBaseUrl")
    @POST("yxt-api/api/preApply")
    fun weiYinXyMatch(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<WeiYinProductInfo>>


    @Headers("urlname:weiYinBaseUrl")
    @POST("yxt-api/api/apply")
    fun weiYinXyApplyPush(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<Any>>

    //----------------------------吉意花-MD5 START--------------------------------------
    @Headers("urlname:jiYiHuaBaseUrl")
    @POST("jyh/open/v1/match")
    fun jiYiHuaMD5Match(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<JiYiHuaResult>>


    @Headers("urlname:jiYiHuaBaseUrl")
    @POST("jyh/open/v1/apply")
    fun jiYiHuaMD5Apply(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<Any>>


    //----------------------------吉意花-掩码 START--------------------------------------
    @Headers("urlname:jiYiHuaBaseUrl")
    @POST("jyh/open/v1/mask/match")
    fun jiYiHuaMaskMatch(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<JiYiHuaResult>>


    @Headers("urlname:jiYiHuaBaseUrl")
    @POST("jyh/open/v1/mask/apply")
    fun jiYiHuaMaskApply(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<Any>>

    //----------------------------八戒-掩码 START--------------------------------------
    @Headers("urlname:baJieBaseUrl")
    @POST("prod-api/app/openapi/full/check/v2")
    fun bajieMaskMatch(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<List<BaJieProductResult>>>


    @Headers("urlname:baJieBaseUrl")
    @POST("prod-api/app/openapi/full/push/v2")
    fun bajieMaskApply(
        @Body body: HashMap<String, Any?>,
    ): Deferred<ResultBean<Any>>


    //----------------------------闪贷喵 START--------------------------------------
    @Headers("urlname:shanDaiMiaoBaseUrl")
    @POST("api/sendSms/sendCode")
    fun shanDaiMiaoSendCode(
        @Body body: HashMap<String, String>,
    ): Deferred<ResultBean<Any>>


    @Headers("urlname:shanDaiMiaoBaseUrl")
    @POST("api/sendSms/smsLogin")
    fun shanDaiMiaoLogin(
        @Body body: HashMap<String, String>,
    ): Deferred<ResultBean<String>>


    @Headers("urlname:shanDaiMiaoBaseUrl")
    @POST("api/full/loan/sspLuck")
    fun shanDaiMiaoMatch(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<ShanDaiMiaoProductResult>>

    @Headers("urlname:shanDaiMiaoBaseUrl")
    @POST("api/full/loan/put")
    fun shanDaiMiaoPush(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<ShanDaiMiaoPushResult>>


    //本部接口---用户信息上报
//    @POST("api/partner/report/userData")
//    fun benbuReportUserData(
//        @Body body: HashMap<String, String?>,
//    ): Deferred<ResultBean<Any>>


    //----------------------------二项目装 START--------------------------------------

    //所有全流程撞库请求的成功  上报本部
    @Headers("urlname:twoHeRuiUrl")
    @POST("push/shrimp")
    fun qlcPushReport(
        @Body body: RequestBody,
    ): Deferred<ResultBean<Any>>

    @Headers("urlname:twoHeRuiUrl")
    @POST("fish/match")
    fun fishWyLyMatch(
        @Body body: RequestBody,
    ): Deferred<ResultBean<FishMatchResult>>

    @Headers("urlname:twoHeRuiUrl")
    @POST("fish/push")
    fun fishWyLyPush(
        @Body body: RequestBody,
    ): Deferred<ResultBean<FishMatchPushResult>>


    //----------------------------吉用钱包全流程 START--------------------------------------
    @Headers("urlname:jiYongQianBaoBaseUrl")
    @POST("channel/apply/thirdV2/check")
    fun jyqbQlcMatch(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<JiyongOrderData>>


    @Headers("urlname:jiYongQianBaoBaseUrl")
    @POST("channel/apply/thirdV2/submit")
    fun jyqbQlcApply(
        @Body body: HashMap<String, String?>,
    ): Deferred<ResultBean<JYQBPushResult>>





}

