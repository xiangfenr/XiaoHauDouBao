package com.yundou.loans.model


import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.utils.TextUtils
import com.google.gson.Gson
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.BaseViewModel
import com.yundou.loans.base.Message
import com.yundou.loans.base.OrgMatchResStore
import com.yundou.loans.callback.CallbackManager
import com.yundou.loans.entity.*
import com.yundou.loans.http.EasyHttp
import com.yundou.loans.http.ResultBean
import com.yundou.loans.http.YXHResultBean
import com.yundou.loans.utils.*
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import kotlin.code
import kotlin.collections.get
import kotlin.coroutines.resume
import kotlin.toString

class UserViewModel : BaseViewModel() {

    private var retrofit = EasyHttp.getInstance().retrofitCreate(EasyServiceApi::class.java)


    // 配置获取
    fun getServer(
        success: (DaikuanUrlData?) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        launchGo({
            val result = retrofit.getServer().await()
            if (result.code == 0) {
                success.invoke(result.data)
            } else  {
                failed.invoke(result.msg)
            }

            judgeCode(result)
        })
    }

    var twoElementCode = MutableLiveData<String>()

    //二要素校验
    fun twoElements(cardcode: String, name: String, success: () -> Unit) {
        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["paramType"] = "normal"
            hashMap["identifyNum"] = cardcode
            hashMap["userName"] = name
            val result = retrofit.twoElements(hashMap).await()

            result.data.code?.let {
                twoElementCode.value = it
            }

            if (result.data.code != "1") {
                defUI.toastEvent.postValue(result.msg)
                defUI.toastEvent.postValue("身份证与姓名不匹配")
            } else {
                success.invoke()
            }
            judgeCode(result)
        }, {}, {}, false)
    }


    //获取列表数据
    fun getDataList(age: Int, success: (ChannerList) -> Unit) {
        launchGo({
            val result = retrofit.channelList(age).await()
            if (result.code == 0) {
                success.invoke(result.data)
            }

            judgeCode(result)
        })
    }

    //获取产品状态
    fun setPassword(password: String, success: () -> Unit) {
        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["password"] = password
            val result = retrofit.setPasswd(hashMap).await()
            if (result.code == 0) {
                success.invoke()
            }

            judgeCode(result)
        })
    }


    //---------------笙融-微秒用 ---------接口------------------


    //获取渠道信息
    fun getWmchannel(success: () -> Unit) {
        launchGo({
            val result = retrofit.channelAsync().await()
            if (result.code == 200) {
                success.invoke()
            }
            judgeCode(result)
        })
    }

    //产品准入
    fun getWmCheckInto(success: (WmUserData) -> Unit) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "笙融产品准入"
        errorHash["request"] = "user/product/check/into"
        // errorHash["param"] = Gson().toJson("")

        launchGo({
            val result = retrofit.intoAsync().await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    //获取用户资料
    fun getWmInto(success: () -> Unit) {
        launchGo({
            val result = retrofit.basicAsync().await()
            if (result.code == 200) {
                success.invoke()
            }
            judgeCode(result)
        })
    }

    //提交资料
    fun getWmSubmit(data: SaveData?, success: () -> Unit) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "笙融提交资料"
        errorHash["request"] = "user/save/basic/info"
        errorHash["param"] = Gson().toJson(data)

        launchGo({
            val result = retrofit.saveAsync(data).await()
            if (result.code == 200) {
                success.invoke()
            } else {
                Toast.makeText(BaseApp.context, result.message, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    //地址
    fun tree(success: (DataitemData) -> Unit) {
        launchGo({
            val result = retrofit.tree().await()
            success.invoke(result)

        })
    }

    //勾选同意授权协议
    fun agreement(id: String?) {
        launchGo({
            val data = ProductIdsData()
            id?.let { data.productIds?.add(it.toInt()) }
            val result = retrofit.agreement(data).await()
            judgeCode(result)
        })
    }

    //产品申请
    fun wMapply(id: String?, success: (ResultBean<Any>) -> Unit) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "笙融同意协议激活额度进件"
        errorHash["request"] = "user/product/apply"
        errorHash["param"] = id.toString()

        launchGo({
            val data = ProductIdsData()
            id?.let {
                data.productIds?.add(it.toInt())
            }
            val result = retrofit.wxapply(data).await()
            if (result.code == 200) {
                success.invoke(result)
            } else {
                success.invoke(result)
                Toast.makeText(BaseApp.context, result.message, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, false)
    }


    //收集异常情况
    fun reportAbnormal(
        code: Int,
        msg: String,
        mobile: String? = MmkvUtil.getInstance().decodeString("loginphone")
    ) {
        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["partner_id"] = MmkvUtil.getInstance().decodeInt("partner_id").toString()
            hashMap["mobile"] = mobile
            hashMap["code"] = code.toString()
            hashMap["msg"] = msg
            val result = retrofit.reportAbnormal(hashMap).await()
            judgeCode(result)
        }, {}, {}, false)
    }


    //判断是否在登录状态：只发事件，不直接跳转 UI
    fun judgeCode(data: ResultBean<*>) {
        if (data.code == 401) {
            // 清理“已填写表单”状态
            MmkvUtil.getInstance().encode(Constants.IS_EDIT_FORM, false)
            // 通知 UI 层执行跳转到登录页
            defUI.msgEvent.postValue(
                Message(
                    code = 401,
                    msg = "NEED_LOGIN"
                )
            )
        } else if (data.code == 40101) {
            refreshToken()
        }
    }

    //刷新token
    fun refreshToken() {
        launchGo({
            val result = retrofit.refreshToken().await()
            if (result.code == 0) {
                MmkvUtil.getInstance().encode("token", result.data.token)
            }

            judgeCode(result)
        })
    }

    //--------------------智享贷 接口--------------------


    //提交
    fun zxdSubmitForm(data: ZxdFormData, success: (bean: ZxdFormResultBean) -> Unit) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "智享贷提交表单"
        errorHash["request"] = "api/v1/sub_info_v1"
        errorHash["param"] = Gson().toJson(data)

        launchGo({

            val jsons = Gson().toJson(data)
            val body =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)
            val result = retrofit.zxdSubmitForm(body).await()
            when (result.code) {
                200 -> {
                    success.invoke(result.data)
                    defUI.toastEvent.postValue("提交申请成功")
                }

                401 -> {
                    defUI.toastEvent.postValue("请先登录")
                    // 通知 UI 层执行跳转到登录页
                    defUI.msgEvent.postValue(
                        Message(
                            code = 401,
                            msg = "NEED_LOGIN"
                        )
                    )
                }

                else -> {
                    defUI.toastEvent.postValue(result.msg)

                    errorHash["result"] = Gson().toJson(result)
                    reportAbnormal(result.status, Gson().toJson(errorHash))
                }
            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    //--------------------快易贷 接口--------------------


    fun silenceLogin(
        success: (String) -> Unit
    ) {
        launchGo({
            val token = MmkvUtil.getInstance().decodeString("token")
            val map = mapOf(
                "token" to token,
                "code" to "1111",
                "channelSign" to "4f5z"
            )
            val jsons = Gson().toJson(map)
            val body =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)

            val result = retrofit.silenceLogin(body).await()
            if (result.code == 200) {
                success.invoke(result.data?.token.toString())
            } else defUI.toastEvent.postValue(result.msg)
            judgeCode(result)
        })
    }

    fun kydGetFormData(success: (KydFormBean) -> Unit) {
        launchGo({
            val result = retrofit.kydGetFormData("4f5z").await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else defUI.toastEvent.postValue(result.msg)
            judgeCode(result)
        })
    }

    fun userInformationAuthorizationLetter(success: (XieYiData) -> Unit) {
        launchGo({
            val result = retrofit.userInformationAuthorizationLetter("4f5z", "31").await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else defUI.toastEvent.postValue(result.msg)
            judgeCode(result)
        })
    }

    fun kydGetCity(success: (List<KydProvinceBean>) -> Unit) {
        launchGo({
            val result = retrofit.kydGetCity().await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else defUI.toastEvent.postValue(result.msg)
            judgeCode(result)
        })
    }

    fun kydSubmitForm(data: KydSubmitData, success: (SaveedBean) -> Unit) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "快易贷提交表单"
        errorHash["request"] = ""
        errorHash["param"] = Gson().toJson(data)

        launchGo({
            val jsons = Gson().toJson(data)
            val body =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)
            val result = retrofit.kydSubmitForm(body).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else {
                defUI.toastEvent.postValue(result.msg)

                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    fun matchingInstitutions(
        success: (MatchiingBean) -> Unit
    ) {
        launchGo({
            val map = mapOf(
                "estimatedAmount" to "200000",
                "channelSign" to "4f5z"
            )
            val jsons = Gson().toJson(map)
            val body =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)

            val result = retrofit.matchingInstitutions(body).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else defUI.toastEvent.postValue(result.msg)
            judgeCode(result)
        }, {}, {}, false)
    }

    fun authorizationApply(id: Int, success: (KydPushData) -> Unit) {
        launchGo({
            val requestObject = PlanIdListRequest(planIdList = listOf(id))
            val jsons = Gson().toJson(requestObject)
            val body =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)
            val result = retrofit.authorizationApply("4f5z", body).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else defUI.toastEvent.postValue(result.msg)
            judgeCode(result)
        })
    }

    //********************************魔力28 接口***********************************


    fun moliTreeV1(success: (List<MoLiProvince>) -> Unit) {
        launchGo({
            val appKeybody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), BaseApp.context.mlAPPKEY)
            val params =
                "app_key=${BaseApp.context.mlAPPKEY}${BaseApp.context.mlAPPSECRET}"
            val md5Sign = SHA256.encryptMD5(params)
            val sign: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), md5Sign)

            val result = retrofit.moliTreeV1(appKeybody, sign).await()
            if (result.status == 1) {
                success.invoke(result.data)
            } else {
                Toast.makeText(BaseApp.context, result.info, Toast.LENGTH_LONG).show()
            }
        })
    }


    fun moliSubmitForm(formData: MoLiFormData, success: (MoLiFormSubmit) -> Unit) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "魔力提交表单"
        errorHash["request"] = "hyj/user/submit.html"
        errorHash["param"] = Gson().toJson(formData)

        launchGo({
            val assistantKeyBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), "template_h5_9")
            val appKeybody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), BaseApp.context.mlAPPKEY)
            val token = MmkvUtil.getInstance().decodeString("token")
            val tokenBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), token!!)
            val districtBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), formData.current_district_id!!)
            val idCardBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), formData.id_card_no!!)
            val nameBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), formData.realname!!)
            val zhimaBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), formData.zhima_score!!)
            val assetsBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), formData.other_assets!!)

            val params =
                "app_key=${BaseApp.context.mlAPPKEY}&assistant_key=${"template_h5_9"}&base_access_token=$token" +
                        "&current_district_id=${formData.current_district_id}&id_card_no=${formData.id_card_no}&other_assets=${formData.other_assets}" +
                        "&realname=${formData.realname}&zhima_score=${formData.zhima_score}${BaseApp.context.mlAPPSECRET}"
            val md5Sign = SHA256.encryptMD5(params)
            val signBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), md5Sign)

            val result = retrofit.moliSubmitForm(
                assistantKeyBody,
                appKeybody,
                tokenBody,
                districtBody,
                idCardBody,
                nameBody,
                zhimaBody,
                assetsBody,
                signBody
            ).await()
            if (result.status == 1) {
                success.invoke(result.data)
            } else {
                Toast.makeText(BaseApp.context, result.info, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    fun protocolGet(
        code: String,
        form_id: String,
        product_id: String,
        success: (MoliGetXieyi) -> Unit
    ) {
        launchGo({
            val codeBody: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), code)
            val formIdBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), form_id)
            val productIdBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), product_id)
            val appKeybody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), BaseApp.context.mlAPPKEY)

            val params =
                "app_key=${BaseApp.context.mlAPPKEY}&code=${code}&form_id=$form_id&product_id=$product_id${BaseApp.context.mlAPPSECRET}"
            val md5Sign = SHA256.encryptMD5(params)
            val signBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), md5Sign)

            val result =
                retrofit.protocolGet(appKeybody, codeBody, formIdBody, productIdBody, signBody)
                    .await()
            if (result.status == 1) {
                success.invoke(result.data)
            } else {
                Toast.makeText(BaseApp.context, result.info, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun moliConfirm(step_id: String, success: (MoLiFormSubmit) -> Unit) {
        launchGo({
            val appKeybody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), BaseApp.context.mlAPPKEY)
            val token = MmkvUtil.getInstance().decodeString("token")
            val tokenBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), token!!)
            val stepIdBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), step_id)

            val params =
                "app_key=${BaseApp.context.mlAPPKEY}&base_access_token=${token}&step_id=$step_id${BaseApp.context.mlAPPSECRET}"
            val md5Sign = SHA256.encryptMD5(params)
            val signBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), md5Sign)

            val result = retrofit.moliConfirm(appKeybody, stepIdBody, tokenBody, signBody).await()
            if (result.status == 1) {
                success.invoke(result.data)
            } else {
                Toast.makeText(BaseApp.context, result.info, Toast.LENGTH_LONG).show()
            }
        })
    }

    //--------------有钱钱包--------------

    //表单提交
    fun yqqbSubmit(data: YqChoiceData, success: (YqqbProductData?) -> Unit) {
        launchGo({
            val result = retrofit.yqqbSubmit(data).await()
            if (result.code == 200) {
                success.invoke(result.data)
                //  defUI.toastEvent.postValue("提交申请成功")
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }


    //授权
    fun yqqbAuth(protocolIds: String, serialNo: String, success: (YqqbProductData) -> Unit) {

        launchGo({

            val protocolIDarray: ArrayList<String> = arrayListOf(protocolIds)

            val hashMpa: HashMap<String, Any> = hashMapOf(
                "protocolIds" to protocolIDarray,
                "orderNum" to 0, "serialNo" to serialNo
            )

            val result = retrofit.yqqbAuth(hashMpa).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

    //协议获取
    fun yqqbProtocol(id: String, success: (String) -> Unit) {

        launchGo({
            val result = retrofit.yqqbProtocol(id).await()
            if (result.code == 200) {
                success.invoke(result.data.content ?: "")
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

    /***
     * ******************************二项目  表单提交******************************
     *
     */
    //提交
    fun twoPrFormSubmit(data: TwoPFormData, success: (bean: TwoResultData?) -> Unit) {


        launchGo({
            val time = (System.currentTimeMillis() / 1000).toString()
            val channel = BaseApp.context.storeid

            val bodyHash = toHashMap(data)
            bodyHash["channel"] = channel
            bodyHash["t"] = time

            val pinJie = "${generateSortedQueryString(bodyHash)}${Constants.TWOP_SIGB}"
            LogUtils.e("二项目提交表单BODY: $pinJie")
            val signature = SHA256.encryptMD5(SHA256.encryptMD5(pinJie))

            val result = retrofit.twopFormPost(signature, data).await()

            if (result.code == 0) {
                success.invoke(result.data)
                // defUI.toastEvent.postValue("提交申请成功")
            } else {
                defUI.toastEvent.postValue(result.msg)
            }
            judgeCode(result)
        })
    }

    fun toHashMap(user: TwoPFormData): HashMap<String, Any> {
        return hashMapOf(
            "appid" to (BaseApp.context.app_id),
            "channel" to (BaseApp.context.storeid),
            "t" to ((System.currentTimeMillis() / 1000).toString()),
            "mobile" to (user.mobile ?: ""),
            "id_number" to (user.id_number ?: ""),
            "name" to (URLEncoder.encode(user.name ?: "", "UTF-8")),
            "province" to (URLEncoder.encode(user.province ?: "", "UTF-8")),
            "city" to (URLEncoder.encode(user.city ?: "", "UTF-8")),
            "district" to (URLEncoder.encode(user.district ?: "", "UTF-8")),
            "credit_card" to (user.credit_card),
            "credit" to (user.credit),
            "profession" to (user.profession),
            "sesame_seed" to (user.sesame_seed),
            "fund" to (user.fund),
            "social_insurance" to (user.social_insurance),
            "business_insurance" to (user.business_insurance),
            "house_property" to (user.house_property),
            "car_property" to (user.car_property),
            "salary" to (user.salary),
            "monthly_income" to (user.monthly_income),
            "apply_limit" to (user.apply_limit),
        )
    }


    fun generateSortedQueryString(data: Map<String, Any>): String {
        return data.toSortedMap() // 按 key 升序排序
            .map { (key, value) -> "$key=$value" } // 格式化成 key="value"
            .joinToString("&") // 使用 & 连接
    }


    /**
     * 二项目 获取渠道列表(弃用)
     */
    fun getChannelList(
        success: (list: List<TwoChannelData>) -> Unit,
    ) {
        launchGo({
            val time = (System.currentTimeMillis() / 1000).toString()
            val channel = BaseApp.context.storeid

            val pinJie =
                "appid=${BaseApp.context.app_id}&channel=$channel&t=$time${Constants.TWOP_SIGB}"
            val signature = SHA256.encryptMD5(SHA256.encryptMD5(pinJie))

            val result = retrofit.getChannelList(signature).await()
            if (result.code == 0) {
                success.invoke(result.data)
            } else
            //defUI.toastEvent.postValue(result.msg)

                judgeCode(result)
        }, {}, {}, false)
    }

    /**
     * 上报撞库结果(弃用)
     */
    fun reportTwoMatch(
        data_id: String,
        success_channel_id: String,
        fail_channel_id: String,
        extra_data: String,
        success: () -> Unit,
    ) {
        launchGo({
            val time = (System.currentTimeMillis() / 1000).toString()
            val channel = BaseApp.context.storeid

            val pinJie =
                "appid=${BaseApp.context.app_id}&channel=$channel&data_id=$data_id&extra_data=$extra_data" +
                        "&fail_channel_id=$fail_channel_id&success_channel_id=$success_channel_id&t=$time${Constants.TWOP_SIGB}"
            val signature = SHA256.encryptMD5(SHA256.encryptMD5(pinJie))
            LogUtils.e("reportTwoMatch", signature)

            val hashMap = HashMap<String, String?>()
            hashMap["data_id"] = data_id
            hashMap["success_channel_id"] = success_channel_id
            hashMap["fail_channel_id"] = fail_channel_id
            hashMap["extra_data"] = extra_data
            LogUtils.e("reportTwoMatch", hashMap.toString())

            val result = retrofit.reportTwoMatch(signature, hashMap).await()
            LogUtils.e("reportTwoMatch", "请求后后")
            if (result.code == 0) {
                success.invoke()
            } else
            //defUI.toastEvent.postValue(result.msg)

                judgeCode(result)
        }, {}, {}, false)
    }


    /**
     * 获取结果 5秒调用一次
     */
    fun twoPGetResult(
        data_id: String,
        success: (TwoPResultData) -> Unit,
    ) {
        launchGo({
            val time = (System.currentTimeMillis() / 1000).toString()
            val channel = BaseApp.context.storeid

            val pinJie =
                "appid=${BaseApp.context.app_id}&channel=$channel&data_id=$data_id&t=$time${Constants.TWOP_SIGB}"
            val signature = SHA256.encryptMD5(SHA256.encryptMD5(pinJie))
            LogUtils.e("reportTwoMatch", signature)

            val result = retrofit.twoPGetResult(signature, data_id).await()
            if (result.code == 0) {
                success.invoke(result.data)
            } else {
                defUI.toastEvent.postValue(result.msg)
            }

            judgeCode(result)
        }, {}, {}, false)
    }

    //每5秒去请求一次结果
    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _resultUrl = MutableLiveData<String?>()
    val resultUrl: LiveData<String?> = _resultUrl

    fun startRequesting(dataID: String) {
        viewModelScope.launch {
            repeat(5) { index ->
                val result = performNetworkRequest(dataID)
                LogUtils.e("请求的result结果 : $result")
                _progress.value = index + 1 // 更新进度条

                if (result != null) {
                    _resultUrl.value = result
                    return@launch // 请求成功，终止循环
                }
                delay(5000L) // 每5秒请求一次
            }
            // 如果6次都失败
            _resultUrl.value = null
        }
    }


    private suspend fun performNetworkRequest(dataID: String): String? =
        suspendCancellableCoroutine { cont ->
            twoPGetResult(dataID) { resultData ->
                if (resultData.code == 3 && !resultData.channel_url.isNullOrEmpty()) {
                    cont.resume(resultData.channel_url)
                } else {
                    cont.resume(null)
                }
            }
        }


    /******************一项目吉用钱包 撞库******************/

    //吉用三要素接口
    fun jiYongCheckInfo(
        name: String, sfz: String, mobile: String,
        success: () -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉用三要素"
        errorHash["request"] = "channel/feed/apply/check"
        errorHash["param"] = mobile + "/" + sfz + "/" + name

        launchGo({
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val channel = Constants.JIYONG_CHANNEL
            val transSeq = SHA256.generateUniqueUUID()

            val dataHash = HashMap<String, String>()
            dataHash["mobile"] = mobile
            dataHash["sfz"] = sfz
            dataHash["name"] = name
            val data = SHA256.RSAEncrypt(Constants.JIYONG_PUBLICKEY, Gson().toJson(dataHash))
            val signature = SHA256.encryptMD5(channel + data + timestamp + transSeq)

            val bodyHash = HashMap<String, String?>()
            bodyHash["channel"] = channel
            bodyHash["timestamp"] = timestamp
            bodyHash["transSeq"] = transSeq
            bodyHash["signature"] = signature
            bodyHash["data"] = data

            val result = retrofit.jiYongCheckInfo(bodyHash).await()
            when (result.code) {
                200 -> {
                    success.invoke()
                }

                else -> {
                    Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                    errorHash["result"] = Gson().toJson(result)
                    reportAbnormal(result.status, Gson().toJson(errorHash))
                }
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }


    //吉用钱包撞库
    fun jiYongApplyPost(
        formData: JIYongSaveData,
        success: (JiyongOrderData) -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉用撞库"
        errorHash["request"] = "channel/feed/apply/customer"

        launchGo({
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val channel = Constants.JIYONG_CHANNEL
            val transSeq = SHA256.generateUniqueUUID()
            val data = SHA256.RSAEncrypt(Constants.JIYONG_PUBLICKEY, Gson().toJson(formData))
            val signature = SHA256.encryptMD5(channel + data + timestamp + transSeq)

            val bodyHash = HashMap<String, String?>()
            bodyHash["channel"] = channel
            bodyHash["timestamp"] = timestamp
            bodyHash["transSeq"] = transSeq
            bodyHash["signature"] = signature
            bodyHash["data"] = data

            errorHash["param"] =
                channel + "/" + timestamp + "/" + transSeq + "/" + signature + Gson().toJson(
                    formData
                )

            val result = retrofit.jiYongApplyPost(bodyHash).await()
            when (result.code) {
                200 -> {
                    success.invoke(result.data)
                }

                3011 -> {
                    Toast.makeText(BaseApp.context, "已提交过申请,请勿重复提交", Toast.LENGTH_LONG)
                        .show()
                    errorHash["result"] = Gson().toJson(result)
                    reportAbnormal(result.status, Gson().toJson(errorHash))
                }

                else -> {
                    Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                    errorHash["result"] = Gson().toJson(result)
                    reportAbnormal(result.status, Gson().toJson(errorHash))
                }
            }

            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    /****---------------------------吉用帮 接口-------------------------***/

    //吉用撞库
    fun jiYBangApplyPost(
        formData: JIYBangSaveData,
        success: () -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉用帮撞库"
        errorHash["request"] = "api/v2/landingPages/login-check-sms"

        launchGo({
            errorHash["param"] = Gson().toJson(formData)

            val requestData = hashMapOf(
                "userInfo" to hashMapOf(
                    "realName" to formData.realName,
                    "idCardNo" to formData.idCardNo,
                    "age" to formData.age,
                    "sex" to formData.sex,
                    "realPhone" to formData.realPhone
                ),
                "pettyLoanUserBaseInfo" to hashMapOf(
                    "property" to hashMapOf(
                        "houseProperty" to formData.houseProperty,
                        "carProperty" to formData.carProperty,
                        "accumulationFund" to formData.accumulationFund,
                        "socialInsurance" to formData.socialInsurance,
                        "businessInsurance" to formData.businessInsurance,
                        "businessOwners" to formData.businessOwners
                    ),
                    "houseProperty" to formData.houseProperty,
                    "carProperty" to formData.carProperty,
                    "accumulationFund" to formData.accumulationFund,
                    "socialInsurance" to formData.socialInsurance,
                    "businessInsurance" to formData.businessInsurance,
                    "businessOwners" to formData.businessOwners,
                    "location" to formData.location,
                    "zmScore" to formData.zmScore,
                    "spendBaiLimit" to formData.spendBaiLimit,
                    "jdbtLimit" to formData.jdbtLimit,
                    "creditSituation" to formData.creditSituation,
                    "monthlyIncome" to formData.monthlyIncome,
                    "loanLongTime" to formData.loanLongTime,
                    "loanLimit" to formData.loanLimit,
                ),
                "distributorId" to Constants.JYB_distributorId,
                "templateId" to "5,6"
            )

            val result = retrofit.jiYBangApplyPost(requestData).await()
            when (result.code) {
                200 -> {
                    success.invoke()
                }

                403 -> {
                    defUI.toastEvent.postValue("请勿频繁请求")
                    errorHash["result"] = Gson().toJson(result)
                    reportAbnormal(result.status, Gson().toJson(errorHash))
                }

                else -> {
                    defUI.toastEvent.postValue(result.msg)
                    errorHash["result"] = Gson().toJson(result)
                    reportAbnormal(result.status, Gson().toJson(errorHash))
                }
            }

            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }


    /****---------------------------阳薪花 接口-------------------------***/

    //阳薪花 省市
    fun yangXinHuaTree(success: (List<YxhProvinceData>) -> Unit) {
        launchGo({
            val result = retrofit.yangXinHuaTree().await()
            success.invoke(result.data)

        })
    }

    //保存信息
    fun yangXinSaveInfo(
        saveData: YxhSaveData,
        success: () -> Unit,
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "阳薪花保存信息"
        errorHash["request"] = "user/save/basic/info"
        errorHash["param"] = Gson().toJson(saveData)

        launchGo({
            LogUtils.e("阳薪花表单原始数据${Gson().toJson(saveData)}")
            val data = SHA256.AESEncrypt(Constants.YXH_AESKEY, Gson().toJson(saveData))

            val bodyHash = HashMap<String, String?>()
            bodyHash["data"] = data

            val result = retrofit.yangXinSaveInfo(bodyHash).await()
            if (result.code == 200) {
                success.invoke()
            } else {
                defUI.toastEvent.postValue(result.message)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }

            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    //匹配
    fun yangXinPiPei(
        saveData: YxhSaveData,
        success: (YxhProductData) -> Unit,
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "阳薪花匹配"
        errorHash["request"] = "h5/xdLoan/plan/checkInto"
        errorHash["param"] = Gson().toJson(saveData)

        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["borrowLimit"] = saveData.loanLimit
            hashMap["borrowMoney"] = saveData.loanAmount
            hashMap["borrowPurpose"] = saveData.loanUse
            hashMap["channelCode"] = Constants.YXH_ChannelCode
            hashMap["channelType"] = "H5"

            val data = SHA256.AESEncrypt(Constants.YXH_AESKEY, Gson().toJson(hashMap))

            val bodyHash = HashMap<String, String?>()
            bodyHash["data"] = data

            val result = retrofit.yangXinPiPei(bodyHash).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else {
                defUI.toastEvent.postValue(result.message)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }

            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, false)
    }


    //申请产品
    fun yangXinApply(
        id: Int,
        productType: String,
        success: (YxhApplyData) -> Unit,
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "阳薪花申请产品"
        errorHash["request"] = "h5/xdLoan/plan/apply"
        errorHash["param"] = id.toString() + "/" + productType

        launchGo({
            val list = ArrayList<Int>()
            list.add(id)
            val hashMap = HashMap<String, Any>()
            hashMap["applyList"] = list
            hashMap["productType"] = productType

            val data = SHA256.AESEncrypt(Constants.YXH_AESKEY, Gson().toJson(hashMap))

            val bodyHash = HashMap<String, String?>()
            bodyHash["data"] = data

            val result = retrofit.yangXinApply(bodyHash).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else {
                defUI.toastEvent.postValue(result.message)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }

            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    //----------------------------天下分期 START--------------------------------------
    //协议获取
    fun txfqAgreementGet(pagType: Int, success: (TxfqAgreementData) -> Unit) {

        launchGo({
            val hashMpa: HashMap<String, Any> = hashMapOf(
                "pageType" to 1,
                "showClient" to 2,
                "source" to "test"
            )

            val result = retrofit.txfqAgreementGet(hashMpa).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

    //城市获取
    fun txfqGetCity(regionId: Int, success: (List<TxfqCityBean>) -> Unit) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "天下分期-获取城市列表"
        errorHash["request"] = "prod-api/regions/{id}/children"
        errorHash["param"] = regionId.toString()
        launchGo({
            val result = retrofit.txfqGetCity(regionId).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else {
                defUI.toastEvent.postValue(result.msg)
                errorHash["result"] = Gson().toJson(result)
//                reportAbnormal(result.status, Gson().toJson(errorHash))

            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
//            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))

        }, {}, false)
    }


    fun txfqApplySubmit(
        data: TxfqSaveData,
        success: (TxfqApplyData) -> Unit,
        onFail: (String) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "天下分期-提交数据"
        errorHash["request"] = "prod-api/rsa/loan/h5ApplyV2"
        errorHash["param"] = Gson().toJson(data)
        launchGo({

            val hashMpa: HashMap<String, Any> = hashMapOf(
                "cityId" to data.cityId,
                "realName" to (data.realName ?: ""),
                "idCard" to (data.idCard ?: ""),
                "credit" to (data.credit ?: ""),
                "jdIous" to (data.jdIous ?: ""),
                "antCreditPay" to (data.antCreditPay ?: ""),
                "assets" to (data.assets),
                "loanAmount" to (data.loanAmount ?: ""),
                "loanPeriod" to (data.loanPeriod ?: ""),
                "loanPurpose" to (data.loanPurpose ?: "")
            )

            val result = retrofit.txfqApplySubmit(hashMpa).await()
            LogUtils.e("天下分期请求前:---")
            if (result.code == 200) {
                success.invoke(result.data)
                LogUtils.e("天下分期请求后:---")
            } else {
                Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                onFail(result.msg)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }

            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    fun txfqPushApply(applyId: String, productId: String, success: (ResultBean<Any>) -> Unit) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "天下分期-激活额度"
        errorHash["request"] = "prod-api/loan/pushApply"
        errorHash["param"] = productId
        launchGo({
            val hashMpa: HashMap<String, Any> = hashMapOf(
                "applyId" to applyId,
                "productId" to productId
            )

            val result = retrofit.txfqPushApply(hashMpa).await()
            if (result.code == 200) {
                success.invoke(result)
            } else {
                Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
//                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
//            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    fun txfqPushApply2JQ8(applyId: String, productId: String, success: (ResultBean<Any>) -> Unit) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "天下分期-匹配产品成功后推送用户资料"
        errorHash["request"] = "prod-api/loan/pushApply2JQB"
        errorHash["param"] = productId
        launchGo({
            val hashMpa: HashMap<String, Any> = hashMapOf(
                "applyId" to applyId,
                "productId" to productId
            )

            val result = retrofit.txfqPushApply2JQB(hashMpa).await()
            if (result.code == 200) {
                success.invoke(result)
            } else {
                Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
//                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
//            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }


    //----------------------------天下分期 END--------------------------------------

    //----------------------------智享贷全流程 新增 START--------------------------------------


    //撞库
    fun zxdNewMatch(
        orderID: String,
        data: ZxdAPISaveData,
        success: (ResultBean<ZxdNewResultProduceBean>?) -> Unit
    ) {

        launchGo({
            val dataString = Gson().toJson(data)
            LogUtils.e("智享贷表单数据: $dataString")
            val jiamiData = SHA256.AESEncrypt(Constants.ZXD_PUBLICK_KEY, dataString)
            val hashMpa = hashMapOf(
                "order_id" to orderID,
                "channel_code" to Constants.ZXD_CHANNELCODE,
                "data" to jiamiData
            )
            val result = retrofit.zxdNewMatch(hashMpa).await()
            success.invoke(result)

            //全流程上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
            if (result.code == 200) {

                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_ZXD,
                        1,
                        result.data.price.toString(),
                        result.msg
                    )
                )
                completeFlowReport(1)

            } else {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_ZXD,
                        0,
                        "0",
                        result.msg
                    )
                )
                completeFlowReport(1)
            }

        }, {
            success.invoke(null)
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                MatchResData(
                    Constants.SHRIMP_ZXD,
                    0,
                    "0",
                    it.message.toString()
                )
            )
            completeFlowReport(1)

        }, {}, false)
    }


    //申请
    fun zxdNewApply(orderID: String, data: ZxdAPISaveData, success: (ResultBean<Any>?) -> Unit) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "智享贷全接口新-进件"
        errorHash["request"] = "api/v1/apply"
        errorHash["param"] = Gson().toJson(data) + "/orderID=$orderID"

        launchGo({
            val jiamiData = SHA256.AESEncrypt(Constants.ZXD_PUBLICK_KEY, Gson().toJson(data))
            val hashMpa = hashMapOf(
                "order_id" to orderID,
                "channel_code" to Constants.ZXD_CHANNELCODE,
                "data" to jiamiData
            )
            val result = retrofit.zxdNewApply(hashMpa).await()
            success.invoke(result)

            if (result.code == 200) {
                completeFlowReport(2, 1, Constants.SHRIMP_ZXD)
            } else {
                defUI.toastEvent.postValue(result.msg)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
                completeFlowReport(2, -1, Constants.SHRIMP_ZXD)
            }
        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, Constants.SHRIMP_ZXD)
        })
    }

    //-------------------------源小花 start ------------------------------
    //协议列表获取
    fun yxhProtocolList(
        success: () -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "源小花协议获取"
        errorHash["request"] = "/kkh/protocol/list"

        launchGo({

            val result = retrofit.yxhProtocolList().await()
            if (result.errcode == 200) {
                success.invoke()
                defUI.toastEvent.postValue("协议获取成功")
            } else {
                defUI.toastEvent.postValue(result.msg)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.errcode, Gson().toJson(errorHash))
            }

        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    fun yxhAddressIp(
        success: (String) -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "源小花协议获取"
        errorHash["request"] = "/kkh/tools/location/ip"

        launchGo({

            val result = retrofit.yxhAddressIp().await()
            if (result.errcode == 200) {
                success.invoke(result.data.city.toString())
            } else {
                defUI.toastEvent.postValue(result.msg)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.errcode, Gson().toJson(errorHash))
            }

        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }


    fun yxhIdent(
        card: String,
        name: String,
        success: (YXHResultBean<YXHIdentData>?) -> Unit
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "源小花二要素验证"
        errorHash["request"] = "/kkh/oauth/ident/query"
        errorHash["param"] = name

        launchGo({

            val map: HashMap<String, String?> = hashMapOf(
                // "encryptionMobile" to SHA256.encryptMD5(mobile),
                "username" to name,
                "identNumber" to card,
            )
            val hashMapStr = Gson().toJson(map)
            val requestBody = hashMapStr.toRequestBody("application/json".toMediaType())
            val result = retrofit.yxhIdentQueay(requestBody).await()
            success.invoke(result)

            if (result.errcode != 200) {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.errcode, Gson().toJson(errorHash))
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, false)
    }

    fun yxhFormCommit(
        stayFieldJson: String,
        stayFieldOptionsJson: String,
        quota: String,
        city: String,
        success: () -> Unit
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "源小花表单提交信贷额度评估字段"
        errorHash["request"] = "/kkh/stay/form/commit"

        launchGo({

            val map: HashMap<String, String?> = hashMapOf(
                // "encryptionMobile" to SHA256.encryptMD5(mobile),
                "stayFieldJson" to stayFieldJson,
                "stayFieldOptionsJson" to stayFieldOptionsJson,
                "quota" to quota,
                "city" to city,
            )
            val hashMapStr = Gson().toJson(map)

            val requestBody = hashMapStr.toRequestBody("application/json".toMediaType())

            val result = retrofit.yxhFormCommit(requestBody).await()
            if (result.errcode == 200) {
                success.invoke()
            } else {
                Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.errcode, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    fun yxhStayMatch(
        success: (MatchData) -> Unit
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "源小花匹配推荐机构或下游产品"
        errorHash["request"] = "/kkh/stay/match"

        launchGo({

            val map: HashMap<String, String?> = hashMapOf(

            )
            val hashMapStr = Gson().toJson(map)

            val requestBody = hashMapStr.toRequestBody("application/json".toMediaType())

            val result = retrofit.yxhStayMatch(requestBody).await()
            if (result.errcode == 200) {
                success.invoke(result.data)
            } else {
                Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.errcode, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    fun yxhOrgansApply(
        planOrgansGoodsId: String, // 机构产品id 匹配机构返回的 planMatchGoodsId  示例 :1
        planMatchToken: String,  //申请凭证 匹配接口返回的 planMatchToken
        quota: String,  //申请额度 单位元 如果没有则传0
        term: String,  //使用期限 单位：月 如果没有则传0
        success: (OrgansApplyData) -> Unit
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "源小花申请自营机构产品"
        errorHash["request"] = "/kkh/stay/match/organs/apply"

        launchGo({

            val map: HashMap<String, String?> = hashMapOf(
                // "encryptionMobile" to SHA256.encryptMD5(mobile),
                "planOrgansGoodsId" to planOrgansGoodsId,
                "planMatchToken" to planMatchToken,
                "quota" to quota,
                "term" to term,
            )
            val hashMapStr = Gson().toJson(map)

            val requestBody = hashMapStr.toRequestBody("application/json".toMediaType())

            val result = retrofit.yxhOrgansApply(requestBody).await()
            if (result.errcode == 200) {
                success.invoke(result.data)
            } else {
                Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.errcode, Gson().toJson(errorHash))
            }
        }, {}, {}, true)
    }


    //申请下游同业平台产品
    fun yxhPlatformApply(
        applyFlowProviderAccountNoJson: String, // ["20232015","accountNo2","accountNo3", ...] 匹配返回的 platformAccountNo,组成的json字符串格式
        planMatchToken: String,  //申请凭证 匹配接口返回的 planMatchToken
        success: (OrgansApplyData) -> Unit
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "源小花下游同业平台产品"
        errorHash["request"] = "/kkh/stay/match/platform/apply"

        launchGo({

            val map: HashMap<String, String?> = hashMapOf(
                "applyFlowProviderAccountNoJson" to applyFlowProviderAccountNoJson,
                "planMatchToken" to planMatchToken
            )
            val hashMapStr = Gson().toJson(map)

            val requestBody = hashMapStr.toRequestBody("application/json".toMediaType())

            val result = retrofit.yxhPlatformApply(requestBody).await()
            if (result.errcode == 200) {
                success.invoke(result.data)
            } else {
                Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.errcode, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    //----------------------------期贷接口 START--------------------------------------

    fun qiDaiGetAgreement(phone: String, code: String, success: (token: String) -> Unit) {

        launchGo({
            val hashmap = hashMapOf(
                "phone" to phone,
                "code" to code
            )
            val bizData = SHA256.AESEncrypt(Constants.QIDAI_PUBLICK_KEY, Gson().toJson(hashmap))
            LogUtils.e("期贷接口加密后: $hashmap")
            val body = hashMapOf(
                "bizData" to bizData,
                "channelCode" to Constants.QIDAI_CHANNELCODE
            )
            LogUtils.e("期贷接口加密后: $body")

            val result = retrofit.qiDaiGetAgreement().await()
            if (result.code == 200) {
                success.invoke(result.data.toString())
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

    //表单填写
    fun qiDaiMatchCheck(
        datas: QiDaiSaveData,
        success: (productInfo: ResultBean<QiDaiProductObject>?) -> Unit
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "期贷提交表单"
        errorHash["request"] = "v2/platform/outChannelApi/matchCheck"
        errorHash["param"] = Gson().toJson(datas)

        launchGo({
            val bizData = SHA256.AESEncrypt(Constants.QIDAI_PUBLICK_KEY, Gson().toJson(datas))
            LogUtils.e("期贷接口加密后: ${Gson().toJson(datas)}")
            val body = hashMapOf(
                "bizData" to bizData,
                "channelCode" to Constants.QIDAI_CHANNELCODE
            )
            LogUtils.e("期贷接口加密后: $body")

            val result = retrofit.qiDaiMatchCheck(body).await()
            success.invoke(result)

            if (result.code != 200) {
                errorHash["result"] = Gson().toJson(result.msg)
                reportAbnormal(result.code, Gson().toJson(errorHash))
            }
            judgeCode(result)
        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    fun qiDaiMatchRegister(
        orgId: String,  //多个机构id（10,20,30,40）
        mobile: String, //用户手机号
        success: (ResultBean<Any>) -> Unit
    ) {

        launchGo({
            val hashmap = hashMapOf(
                "orgId" to orgId,
                "mobile" to mobile
            )

            val bizData = SHA256.AESEncrypt(Constants.QIDAI_PUBLICK_KEY, Gson().toJson(hashmap))
            LogUtils.e("期贷接口加密前: ${hashmap}")
            val body = hashMapOf(
                "bizData" to bizData,
                "channelCode" to Constants.QIDAI_CHANNELCODE
            )
            LogUtils.e("期贷接口加密后: $body")

            val result = retrofit.qiDaiMatchRegister(body).await()
            success.invoke(result)
        })
    }

    /**
     * 查询是否完成表单
     */
    fun qiDaiQueryFormStatus(mobile: String, success: (Int) -> Unit) {

        launchGo({
            val hashmap = hashMapOf(
                "mobile" to mobile
            )
            val bizData = SHA256.AESEncrypt(Constants.QIDAI_PUBLICK_KEY, Gson().toJson(hashmap))
            LogUtils.e("期贷接口加密前: ${Gson().toJson(hashmap)}")
            val body = hashMapOf(
                "bizData" to bizData,
                "channelCode" to Constants.QIDAI_CHANNELCODE
            )
            LogUtils.e("期贷接口加密后: $body")

            val result = retrofit.qiDaiQueryFormStatus(body).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

    /**
     * 查询可申请产品
     */
    fun qiDaiQueryPlatform(mobile: String, success: (ResultBean<QiDaiProductObject>?) -> Unit) {

        launchGo({
            val hashmap = hashMapOf(
                "mobile" to mobile
            )
            val bizData = SHA256.AESEncrypt(Constants.QIDAI_PUBLICK_KEY, Gson().toJson(hashmap))
            LogUtils.e("期贷接口加密前: ${Gson().toJson(hashmap)}")
            val body = hashMapOf(
                "bizData" to bizData,
                "channelCode" to Constants.QIDAI_CHANNELCODE
            )
            LogUtils.e("期贷接口加密后: $body")

            val result = retrofit.qiDaiQueryPlatform(body).await()
            success.invoke(result)

            judgeCode(result)
        }, {
            success.invoke(null)
        })
    }


    //获取贷超详情 Url
    fun viewLoanNew(loan_id: String, success: (String) -> Unit) {
        launchGo({
            val partner_id = MmkvUtil.getInstance().decodeInt("partner_id").toString()
            val mobile = MmkvUtil.getInstance().decodeString("loginphone")
            val result = retrofit.viewLoanNew(loan_id, partner_id, mobile).await()
            if (result.code == 0) {
                success.invoke(result.data.url.toString())
            } else {
                defUI.toastEvent.postValue(result.msg)
            }

            judgeCode(result)
        })
    }


    /**
     * 贷款逾期处理上报, 实际是用的 loanView接口
     */
    fun daikuanYuqiPush(
        id: String,
        partner_id: String,
        mobile: String,
        success: (String) -> Unit
    ) {
        launchGo({
            val result = retrofit.viewLoanNew(id, partner_id, mobile).await()
            if (result.code == 0) {
                success.invoke(result.data.url.toString())
            }
            judgeCode(result)
        }, {}, {}, false)
    }

    /**
     * 节点数据上报：
     * 1 登陆注册、
     * 2 点击进入表单按钮、
     * 3 填写部分/全部表单数据、
     * 4 点击提交表单按钮、
     * 5 合作方响应表单成功
     */
    fun reportPointRequest(at: Int) {
        launchGo({
            val hashMap = HashMap<String, Any?>()
            hashMap["partner_id"] = MmkvUtil.getInstance().decodeInt("partner_id").toString()
            hashMap["mobile"] = MmkvUtil.getInstance().decodeString("loginphone")
            hashMap["at"] = at
            val result = retrofit.reportPointRequest(hashMap).await()
            judgeCode(result)
        }, {}, {}, false)
    }

    fun oppoIntercept(success: (ResultBean<Any>) -> Unit) {
        launchGo({
            val hashMap = HashMap<String, Any?>()
            hashMap["mobile"] = MmkvUtil.getInstance().decodeString("loginphone")
            val result = retrofit.oppoApplyIntercept(hashMap).await()
            if (result.code == 0) {
                success.invoke(result)
            } else {
//                success.invoke(result)
                defUI.toastEvent.postValue(result.msg)
            }
            judgeCode(result)
        }, {}, {}, true)
    }

    //----------------------------微融宝 START--------------------------------------

    //微融宝撞库
    fun wrbBeForeMatch(
        data: WrbSaveData,
        success: (ResultBean<WrbFormResultBean>?) -> Unit
    ) {

        launchGo({
            val dataString = Gson().toJson(data)
            LogUtils.e("微融宝预进件表单数据: $dataString")

            val jiamiData = SHA256.AESEncrypt(Constants.WEIRONGBAO_APPKEY, dataString)
            val hashMpa = hashMapOf(
                "channel_code" to Constants.WEIRONGBAO_CHANNWL_CODE,
                "data" to jiamiData
            )
            val result = retrofit.wrbBeForeMatch(hashMpa).await()
            success.invoke(result)
            //全流程上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
            if (result.code == 200) {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_WRB,
                        1,
                        result.data.price.toString(),
                        result.msg
                    )
                )
                completeFlowReport(1)
            } else {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_WRB,
                        0,
                        "0",
                        result.msg
                    )
                )
                completeFlowReport(1)
            }

        }, {
            success.invoke(null)
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                MatchResData(
                    Constants.SHRIMP_WRB,
                    0,
                    "0",
                    it.message.toString()
                )
            )
            completeFlowReport(1)

        }, {}, false)
    }


    //微融宝推送
    fun wrbApplyForm(
        agreeProtocol: String,
        name: String,
        orderID: String,
        data: WrbSaveData,
        success: (ResultBean<Any>?) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "微融宝-进件"
        errorHash["request"] = "api/process/jj/${Constants.WEIRONGBAO_CHANNWL_CODE}"
        errorHash["param"] = Gson().toJson(data)

        launchGo({
            data.agreeProtocol = agreeProtocol
            data.phoneMd5 = null
            data.name = name
            data.phone = MmkvUtil.getInstance().decodeString("loginphone") ?: ""
            data.orderId = orderID
            val dataString = Gson().toJson(data)
            LogUtils.e("微融宝进件表单数据: $dataString")

            val jiamiData = SHA256.AESEncrypt(Constants.WEIRONGBAO_APPKEY, dataString)
            val hashMpa = hashMapOf(
                "channel_code" to Constants.WEIRONGBAO_CHANNWL_CODE,
                "data" to jiamiData
            )
            val result = retrofit.wrbApplyForm(hashMpa).await()
            success.invoke(result)
            //全流程上报和异常上报
            if (result.code == 200) {
                //全流程进件上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
                completeFlowReport(2, 1, Constants.SHRIMP_WRB)
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
                completeFlowReport(2, -1, Constants.SHRIMP_WRB)
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, Constants.SHRIMP_WRB)
        }, {}, true)
    }


    //----------------------------吉贷 START--------------------------------------


    fun jiDaiSaveUserInfo(data: JiDaiUserInfo, success: (ResultBean<Any>) -> Unit) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉贷-上传表单"
        errorHash["request"] = "jidaiapi/user/updateUserInfo"
        errorHash["param"] = Gson().toJson(data)

        launchGo({


//            val hashmap = hashMapOf(
//                "name" to Constants.JIDAI_APPID,
//                "channelId" to Constants.JIDAI_CHANNELID,
//                "channelCode" to Constants.JIDAI_CHANNELCODE,
//                "phone" to phone,
//                "code" to code,
//            )
            val hashMapStr = Gson().toJson(data)
            val requestBody = hashMapStr.toRequestBody("application/json".toMediaType())

            LogUtils.e("吉贷保存用户信息: $hashMapStr")

            val baseUrl =
                if (MmkvUtil.getInstance().decodeInt("partner_id") == Constants.PARTNER_JIDAI) {
                    "jiDaiBaseUrl"
                } else {
                    "yueXiangBaseUrl"
                }

            val result = retrofit.jiDaiSaveUserInfo(baseUrl, requestBody).await()
            success.invoke(result)

            if (result.code != 200) {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
            }

        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }


    fun jiDaiProductList(success: (ResultBean<List<JiDaiProductInfo>>) -> Unit) {
        val baseUrl =
            if (MmkvUtil.getInstance().decodeInt("partner_id") == Constants.PARTNER_JIDAI) {
                "jiDaiBaseUrl"
            } else {
                "yueXiangBaseUrl"
            }

        launchGo({
            val emptyBody = "{}".toRequestBody("application/json".toMediaTypeOrNull())

            val result = retrofit.jiDaiProductList(baseUrl, emptyBody).await()
            success.invoke(result)
        })
    }


    fun jiDaiSendProduct(apiTypeList: List<String?>, success: () -> Unit) {
        val baseUrl =
            if (MmkvUtil.getInstance().decodeInt("partner_id") == Constants.PARTNER_JIDAI) {
                "jiDaiBaseUrl"
            } else {
                "yueXiangBaseUrl"
            }

        launchGo({

            val hashMpa = hashMapOf(
                "apiTypeList" to apiTypeList,
            )
            val result = retrofit.jiDaiSendProduct(baseUrl, hashMpa).await()
            success.invoke()
        })
    }


    //----------------------------小福借款 START--------------------------------------

    //小福借款撞库
    fun xiaoFuJKMatch(
        data: XiaoFuUserData,
        success: (ResultBean<XiaoFuPResult>?) -> Unit
    ) {

        launchGo({
            val originData = Gson().toJson(data)
            LogUtils.e("小福撞库请求参数: $originData")
            val encrypt_data = XiaoFuAESUtils.aesEncrypt(
                Constants.XIAOFU_PUBLICK_KEY,
                Constants.XIAOFU_IV,
                originData
            )
            val time = (System.currentTimeMillis() / 1000)
            val trace_id = SHA256.generateUniqueUUID()

            val sign =
                XiaoFuAESUtils.md5("${Constants.XIAOFU_CHANNEL_NUM}${encrypt_data}${time}${trace_id}${Constants.XIAOFU_FACTOR}")

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "channel" to Constants.XIAOFU_CHANNEL_NUM,
                "encrypt_data" to encrypt_data,
                "timestamp" to time,
                "sign" to sign,
                "trace_id" to trace_id
            )

            val result = retrofit.xiaoFuJKMatch(hashMpa).await()
            success.invoke(result)

            //全流程上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
            if (result.code == 200) {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_XIAOFU,
                        1,
                        result.data.price.toString(),
                        result.msg
                    )
                )
                completeFlowReport(1)
            } else {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_XIAOFU,
                        0,
                        "0",
                        result.msg
                    )
                )
                completeFlowReport(1)
            }

        }, {
            success.invoke(null)
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                MatchResData(
                    Constants.SHRIMP_XIAOFU,
                    0,
                    "0",
                    it.message.toString()
                )
            )
            completeFlowReport(1)

        }, {}, false)
    }


    //小福推送
    fun xiaoFuApply(
        product_id: Int?,
        agreement_list: List<XiaoFuAgreement>?,
        data: XiaoFuUserData,
        success: (ResultBean<Any>?) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "小福借款-进件"
        errorHash["request"] = "open/support/apply"
        errorHash["param"] = Gson().toJson(data)

        launchGo({
            val originHash: HashMap<String, Any?> = hashMapOf(
                "phone" to data.phone,
                "name" to data.name,
                "age" to data.age,
                "city_name" to data.city_name,
                "city_code" to data.city_code,
                "gender" to data.gender,
                "quota" to data.quota,
                "job" to data.job,
                "house" to data.house,
                "car" to data.car,
                "social" to data.social,
                "fund" to data.fund,
                "insurance" to data.insurance,
                "zm" to data.zm,
                "credit" to data.credit,
                "baitiao_huabei" to data.baitiao_huabei,
                "ip" to data.ip,
                "os" to data.os,
                "product_id" to product_id,
                "agreements" to agreement_list,
            )
            LogUtils.e("小福进件请求参数: ${Gson().toJson(originHash)}")

            //进件使用

            val encrypt_data = XiaoFuAESUtils.aesEncrypt(
                Constants.XIAOFU_PUBLICK_KEY,
                Constants.XIAOFU_IV,
                Gson().toJson(originHash)
            )
            val time = (System.currentTimeMillis() / 1000)
            val trace_id = SHA256.generateUniqueUUID()

            val sign =
                XiaoFuAESUtils.md5("${Constants.XIAOFU_CHANNEL_NUM}${encrypt_data}${time}${trace_id}${Constants.XIAOFU_FACTOR}")

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "channel" to Constants.XIAOFU_CHANNEL_NUM,
                "encrypt_data" to encrypt_data,
                "timestamp" to time,
                "sign" to sign,
                "trace_id" to trace_id
            )

            val result = retrofit.xiaoFuApply(hashMpa).await()

            success.invoke(result)

            //全流程上报和异常上报
            if (result.code == 200) {
                //全流程进件上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
                completeFlowReport(2, 1, Constants.SHRIMP_XIAOFU)
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
                completeFlowReport(2, -1, Constants.SHRIMP_XIAOFU)
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, Constants.SHRIMP_XIAOFU)
        }, {}, false)
    }


    //----------------------------龙炎 START--------------------------------------

    //龙炎撞库
    fun longYanMatch(
        data: LongYanSaveData,
        success: (ResultBean<LongYanFormResultBean>?) -> Unit
    ) {

        launchGo({
            val dataString = Gson().toJson(data)
            LogUtils.e("龙炎撞库数据: $dataString")
            val jiamiData = SHA256.AESEncrypt(Constants.LONGYAN_APPKEY, dataString)
            val hashMpa = hashMapOf(
                "data" to jiamiData
            )
            val result = retrofit.longYanMatch(hashMpa).await()
            success.invoke(result)

            //全流程上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
            if (result.code == 0) {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_LONGYAN,
                        1,
                        result.data.price.toString(),
                        result.msg
                    )
                )
                completeFlowReport(1)
            } else {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_LONGYAN,
                        0,
                        "0",
                        result.msg
                    )
                )
                completeFlowReport(1)
            }

        }, {
            success.invoke(null)
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                MatchResData(
                    Constants.SHRIMP_LONGYAN,
                    0,
                    "0",
                    it.message.toString()
                )
            )
            completeFlowReport(1)
        }, {}, false)
    }


    //龙炎推送
    fun longYanApplyForm(
        order_id: String?,
        phone: String?,
        name: String?,
        idno: String?,
        ip: String?,
        success: (ResultBean<Any>?) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "龙焱-推送"
        errorHash["request"] = "open/api/n_20116/push"

        launchGo({
            val originHashmap = hashMapOf(
                "order_id" to order_id,
                "phone" to phone,
                "name" to name,
                "idno" to idno,
                "ip" to ip,
            )
            val originString = Gson().toJson(originHashmap)
            errorHash["param"] = originString
            LogUtils.e("龙焱-推送进件表单数据: ${originString}")

            val jiamiData = SHA256.AESEncrypt(Constants.LONGYAN_APPKEY, originString)
            val hashMpa = hashMapOf(
                "data" to jiamiData
            )
            val result = retrofit.longYanFormPush(hashMpa).await()
            success.invoke(result)

            //全流程上报和异常上报
            if (result.code == 0) {
                //全流程进件上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
                completeFlowReport(2, 1, Constants.SHRIMP_LONGYAN)
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
                completeFlowReport(2, -1, Constants.SHRIMP_LONGYAN)
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, Constants.SHRIMP_LONGYAN)

        }, {}, true)
    }


    //----------------------------微银信用 START--------------------------------------


    fun weiyinSubmitMatch(
        data: WeiYinSaveData,
        success: (ResultBean<WeiYinProductInfo>?) -> Unit
    ) {

        launchGo({
            val dataString = Gson().toJson(data)
            LogUtils.e("微银信用撞库数据: $dataString")
            val jiamiData = SHA256.AESEncrypt(Constants.WEIYIN_APPKEY, dataString)
            val hashMpa = hashMapOf(
                "orgId" to Constants.WEIYIN_ORGID,
                "data" to jiamiData
            )
            val result = retrofit.weiYinXyMatch(hashMpa).await()
            success.invoke(result)

            //全流程上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
            if (result.code == 0) {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_WEIYIN,
                        1,
                        result.data.price.toString(),
                        result.msg
                    )
                )
                completeFlowReport(1)
            } else {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_WEIYIN,
                        0,
                        "0",
                        result.msg
                    )
                )
                completeFlowReport(1)
            }

        }, {
            success.invoke(null)
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                MatchResData(
                    Constants.SHRIMP_WEIYIN,
                    0,
                    "0",
                    it.message.toString()
                )
            )
            completeFlowReport(1)

        }, {}, false)
    }


    //微银--推送进件
    fun weiYinXyApplyPush(
        data: WeiYinSaveData,
        success: (ResultBean<Any>?) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "微银--推送进件"
        errorHash["request"] = "yxt-api/api/apply"

        launchGo({

            val originString = Gson().toJson(data)
            errorHash["param"] = originString
            LogUtils.e("微银--推送进件表单数据: ${originString}")

            val jiamiData = SHA256.AESEncrypt(Constants.WEIYIN_APPKEY, originString)
            val hashMpa = hashMapOf(
                "orgId" to Constants.WEIYIN_ORGID,
                "data" to jiamiData
            )
            val result = retrofit.weiYinXyApplyPush(hashMpa).await()
            success.invoke(result)

            //全流程上报和异常上报
            if (result.code == 0) {
                //全流程进件上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
                completeFlowReport(2, 1, Constants.SHRIMP_WEIYIN)
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
                completeFlowReport(2, -1, Constants.SHRIMP_WEIYIN)
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, Constants.SHRIMP_WEIYIN)

        }, {}, true)
    }


    //----------------------------吉意花MD5 START--------------------------------------

    //吉意花MD5撞库
    fun jiYiHuaMD5Match(
        data: JiYiHuaSaveData,
        success: (ResultBean<JiYiHuaResult>?) -> Unit
    ) {

        launchGo({
            val originData = Gson().toJson(data)
            LogUtils.e("吉意花MD5请求参数: $originData")
            val encrypt_data = JYHAESUtils.encryptByAES(originData, Constants.JIYIHUA_MD5_APPKEY)
            val trace_id = SHA256.generateUniqueUUID()

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "channelCode" to Constants.JIYIHUA_MD5_CHANNEL,
                "content" to encrypt_data,
                "serialNo" to trace_id
            )

            val result = retrofit.jiYiHuaMD5Match(hashMpa).await()
            success.invoke(result)

            //全流程上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
            if (result.code == 200) {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_JIYIHUA_MD5,
                        1,
                        result.data.discountPrice.toString(),
                        result.msg
                    )
                )
                completeFlowReport(1)
            } else {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_JIYIHUA_MD5,
                        0,
                        "0",
                        result.msg
                    )
                )
                completeFlowReport(1)

            }

        }, {
            success.invoke(null)

            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                MatchResData(
                    Constants.SHRIMP_JIYIHUA_MD5,
                    0,
                    "0",
                    it.message.toString()
                )
            )
            completeFlowReport(1)

        }, {}, false)
    }


    //吉意花推送  phone 手机号(明文)   applyNo 撞库返回的订单号
    fun jiYiHuaMD5Apply(
        applyNo: String,
        phone: String,
        protocolList: List<JiYiHuaAgreement>?,
        data: JiYiHuaSaveData,
        success: (ResultBean<Any>?) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉意花MD5-进件"
        errorHash["request"] = "jyh/open/v1/apply"
        errorHash["param"] = Gson().toJson(data)

        launchGo({

            val originHash: HashMap<String, Any?> = hashMapOf(
                "phone" to phone,
                "applyNo" to applyNo,
                "userName" to data.userName,
                "sex" to data.sex,
                "age" to data.age,
                "ip" to data.ip,
                "loanMoney" to data.loanMoney,
                "cityCode" to data.cityCode,
                "cityName" to data.cityName,
                "job" to data.job,
                "monthlyIncome" to data.monthlyIncome,
                "busLicense" to data.busLicense,
                "has" to data.has,
                "fund" to data.fund,
                "house" to data.house,
                "car" to data.car,
                "policy" to data.policy,
                "zhiMa" to data.zhiMa,
                "education" to data.education,
                "platform" to data.platform,
                "protocolList" to protocolList,
                "protocolList.protocolName" to (protocolList?.get(0)?.protocolName ?: ""),
                "protocolList.protocolUrl" to (protocolList?.get(0)?.protocolUrl ?: ""),

                )
            LogUtils.e("吉意花MD5进件请求参数: ${Gson().toJson(originHash)}")

            val encrypt_data =
                JYHAESUtils.encryptByAES(Gson().toJson(originHash), Constants.JIYIHUA_MD5_APPKEY)
            val trace_id = SHA256.generateUniqueUUID()

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "channelCode" to Constants.JIYIHUA_MD5_CHANNEL,
                "content" to encrypt_data,
                "serialNo" to trace_id
            )
            val result = retrofit.jiYiHuaMD5Apply(hashMpa).await()

            success.invoke(result)

            //全流程上报和异常上报
            if (result.code == 200) {
                //全流程进件上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
                completeFlowReport(2, 1, Constants.SHRIMP_JIYIHUA_MD5)
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
                completeFlowReport(2, -1, Constants.SHRIMP_JIYIHUA_MD5)
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, Constants.SHRIMP_JIYIHUA_MD5)
        }, {}, false)
    }

    //----------------------------吉意花掩码 START--------------------------------------

    //吉意花掩码撞库
    fun jiYiHuaMaskMatch(
        data: JiYiHuaSaveData,
        success: (ResultBean<JiYiHuaResult>?) -> Unit
    ) {

        launchGo({
            val originData = Gson().toJson(data)
            LogUtils.e("吉意花掩码请求参数: $originData")
            val encrypt_data = JYHAESUtils.encryptByAES(originData, Constants.JIYIHUA_MASK_APPKEY)
            val trace_id = SHA256.generateUniqueUUID()

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "channelCode" to Constants.JIYIHUA_MASK_CHANNEL,
                "content" to encrypt_data,
                "serialNo" to trace_id
            )

            val result = retrofit.jiYiHuaMaskMatch(hashMpa).await()
            success.invoke(result)

            //全流程上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
            if (result.code == 200) {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_JIYIHUA_MASK,
                        1,
                        result.data.discountPrice.toString(),
                        result.msg
                    )
                )
                completeFlowReport(1)
            } else {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_JIYIHUA_MASK,
                        0,
                        "0",
                        result.msg
                    )
                )
                completeFlowReport(1)
            }

        }, {
            success.invoke(null)
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                MatchResData(
                    Constants.SHRIMP_JIYIHUA_MASK,
                    0,
                    "0",
                    it.message.toString()
                )
            )
            completeFlowReport(1)
        }, {}, false)
    }


    //吉意花掩码推送  phone 手机号(明文)   applyNo 撞库返回的订单号
    fun jiYiHuaMaskApply(
        applyNo: String,
        phone: String,
        protocolUrl: String,
        data: JiYiHuaSaveData,
        success: (ResultBean<Any>?) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉意花掩码-进件"
        errorHash["request"] = "jyh/open/v1/apply"
        errorHash["param"] = Gson().toJson(data)

        launchGo({

            val originHash: HashMap<String, Any?> = hashMapOf(
                "phone" to phone,
                "applyNo" to applyNo,
                "userName" to data.userName,
                "sex" to data.sex,
                "age" to data.age,
                "ip" to data.ip,
                "loanMoney" to data.loanMoney,
                "cityCode" to data.cityCode,
                "cityName" to data.cityName,
                "job" to data.job,
                "monthlyIncome" to data.monthlyIncome,
                "busLicense" to data.busLicense,
                "has" to data.has,
                "fund" to data.fund,
                "house" to data.house,
                "car" to data.car,
                "policy" to data.policy,
                "zhiMa" to data.zhiMa,
                "education" to data.education,
                "platform" to data.platform,
                "protocolUrl" to protocolUrl
            )
            LogUtils.e("吉意花掩码进件请求参数: ${Gson().toJson(originHash)}")

            val encrypt_data =
                JYHAESUtils.encryptByAES(Gson().toJson(originHash), Constants.JIYIHUA_MASK_APPKEY)
            val trace_id = SHA256.generateUniqueUUID()

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "channelCode" to Constants.JIYIHUA_MASK_CHANNEL,
                "content" to encrypt_data,
                "serialNo" to trace_id
            )
            val result = retrofit.jiYiHuaMaskApply(hashMpa).await()

            success.invoke(result)

            //全流程上报和异常上报
            if (result.code == 200) {
                //全流程进件上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
                completeFlowReport(2, 1, Constants.SHRIMP_JIYIHUA_MASK)
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
                completeFlowReport(2, -1, Constants.SHRIMP_JIYIHUA_MASK)
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, Constants.SHRIMP_JIYIHUA_MASK)

        }, {}, false)
    }


    //----------------------------八戒掩码 全流程 START--------------------------------------

    fun bajieMaskMatch(
        data: BaJieSaveData,
        success: (ResultBean<List<BaJieProductResult>>?) -> Unit
    ) {

        launchGo({
            val originData = Gson().toJson(data)
            LogUtils.e("八戒掩码撞库请求参数: $originData")

            val iv = BaJieAESUtils.getIv()

            val encrypt_data = BaJieAESUtils.encryptCbc(originData, Constants.BAJIE_APPKEY, iv)

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "data" to encrypt_data,
                "iv" to iv
            )

            val result = retrofit.bajieMaskMatch(hashMpa).await()
            success.invoke(result)

            //全流程上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
            if (result.code == 200) {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_BAJIE,
                        1,
                        result.data[0].price.toString(),
                        result.msg
                    )
                )
                completeFlowReport(1)
            } else {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_BAJIE,
                        0,
                        "0",
                        result.msg
                    )
                )
                completeFlowReport(1)
            }

        }, {
            success.invoke(null)
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                MatchResData(
                    Constants.SHRIMP_BAJIE,
                    0,
                    "0",
                    it.message.toString()
                )
            )
            completeFlowReport(1)
        }, {}, false)
    }

    fun bajieMaskApply(
        serialNo: String?,
        data: BaJieSaveData,
        success: (ResultBean<Any>?) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "八戒掩码-进件"
        errorHash["request"] = " prod-api/app/openapi/full/push/v2"
        errorHash["param"] = Gson().toJson(data)


        launchGo({
            val originData = Gson().toJson(data)
            LogUtils.e("八戒掩码进件请求参数: $originData")

            val iv = BaJieAESUtils.getIv()

            val encrypt_data = BaJieAESUtils.encryptCbc(originData, Constants.BAJIE_APPKEY, iv)

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "data" to encrypt_data,
                "iv" to iv,
                "serialNo" to serialNo
            )

            val result = retrofit.bajieMaskApply(hashMpa).await()
            success.invoke(result)

            //全流程上报和异常上报
            if (result.code == 200) {
                //全流程进件上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
                completeFlowReport(2, 1, Constants.SHRIMP_BAJIE)
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
                completeFlowReport(2, -1, Constants.SHRIMP_BAJIE)
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, Constants.SHRIMP_BAJIE)
        }, {}, false)
    }


    //---------------------------- suspeed START 八戒掩码 吉意花掩码 吉意花MD5 微银 龙炎 小福借款 微融宝 智享贷 挂起请求--------------------------------------

    suspend fun bajieMaskMatchSuspend(
        data: BaJieSaveData,
    ): BaJieProductResult? {

        return try {
            val originData = Gson().toJson(data)
            LogUtils.e("八戒掩码撞库请求参数: $originData")

            val iv = BaJieAESUtils.getIv()

            val encrypt_data = BaJieAESUtils.encryptCbc(originData, Constants.BAJIE_APPKEY, iv)

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "data" to encrypt_data,
                "iv" to iv
            )

            val result = retrofit.bajieMaskMatch(hashMpa).await()

            if (result.code == 200) {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_BAJIE, 1, result.data[0].price.toString(), result.msg
                    )
                )
                result.data[0]
            } else {
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(
                    MatchResData(
                        Constants.SHRIMP_BAJIE,
                        0,
                        "0",
                        result.msg
                    )
                )
                null
            }
        } catch (e: Exception) {
            val matchResData = MatchResData(Constants.SHRIMP_BAJIE, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }


    suspend fun jiYiHuaMaskMatchSuspend(
        data: JiYiHuaSaveData
    ): JiYiHuaResult? {

        return try {
            val originData = Gson().toJson(data)
            LogUtils.e("吉意花掩码请求参数: $originData")
            val encrypt_data = JYHAESUtils.encryptByAES(originData, Constants.JIYIHUA_MASK_APPKEY)
            val trace_id = SHA256.generateUniqueUUID()

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "channelCode" to Constants.JIYIHUA_MASK_CHANNEL,
                "content" to encrypt_data,
                "serialNo" to trace_id
            )

            val result = retrofit.jiYiHuaMaskMatch(hashMpa).await()


            if (result.code == 200) {

                val matchResData = MatchResData(
                    Constants.SHRIMP_JIYIHUA_MASK,
                    1,
                    result.data.discountPrice.toString(),
                    result.msg
                )
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)

                result.data
            } else {

                val matchResData = MatchResData(Constants.SHRIMP_JIYIHUA_MASK, 0, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                null
            }
        } catch (e: Exception) {
            val matchResData =
                MatchResData(Constants.SHRIMP_JIYIHUA_MASK, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }

    suspend fun jiYiHuaMD5MatchSuspend(
        data: JiYiHuaSaveData
    ): JiYiHuaResult? {

        return try {
            val originData = Gson().toJson(data)
            LogUtils.e("吉意花请求参数: $originData")
            val encrypt_data = JYHAESUtils.encryptByAES(originData, Constants.JIYIHUA_MD5_APPKEY)
            val trace_id = SHA256.generateUniqueUUID()

            val hashMpa: HashMap<String, Any?> = hashMapOf(
                "channelCode" to Constants.JIYIHUA_MD5_CHANNEL,
                "content" to encrypt_data,
                "serialNo" to trace_id
            )

            val result = retrofit.jiYiHuaMD5Match(hashMpa).await()

            if (result.code == 200) {
                val matchResData = MatchResData(
                    Constants.SHRIMP_JIYIHUA_MD5,
                    1,
                    result.data.discountPrice.toString(),
                    result.msg
                )
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)

                result.data
            } else {
                val matchResData = MatchResData(Constants.SHRIMP_JIYIHUA_MD5, 0, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                null
            }
        } catch (e: Exception) {
            val matchResData =
                MatchResData(Constants.SHRIMP_JIYIHUA_MD5, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }


    suspend fun weiYinXyMatchSuspend(
        data: WeiYinSaveData
    ): WeiYinProductInfo? {

        return try {

            val dataString = Gson().toJson(data)
            LogUtils.e("微银信用撞库数据: $dataString")
            val jiamiData = SHA256.AESEncrypt(Constants.WEIYIN_APPKEY, dataString)
            val hashMpa = hashMapOf(
                "orgId" to Constants.WEIYIN_ORGID,
                "data" to jiamiData
            )
            val result = retrofit.weiYinXyMatch(hashMpa).await()

            if (result.code == 0) {
                val matchResData = MatchResData(
                    Constants.SHRIMP_WEIYIN,
                    1,
                    result.data.price.toString(),
                    result.msg
                )
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)

                result.data
            } else {
                val matchResData = MatchResData(Constants.SHRIMP_WEIYIN, 0, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                null
            }
        } catch (e: Exception) {
            val matchResData = MatchResData(Constants.SHRIMP_WEIYIN, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }

    suspend fun longYanMatchSuspend(
        data: LongYanSaveData
    ): LongYanFormResultBean? {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "龙焱分期-撞库"
        errorHash["request"] = "open/api/n_20116/check"
        errorHash["param"] = Gson().toJson(data)

        return try {
            val dataString = Gson().toJson(data)
            LogUtils.e("龙炎撞库数据: $dataString")
            val jiamiData = SHA256.AESEncrypt(Constants.LONGYAN_APPKEY, dataString)
            val hashMpa = hashMapOf(
                "data" to jiamiData
            )
            // 直接 suspend 等待 Retrofit 结果
            val result = retrofit.longYanMatch(hashMpa).await()

            errorHash["result"] = Gson().toJson(result)
            reportAbnormal(result.code, Gson().toJson(errorHash))

            if (result.code == 200) {
                val matchResData = MatchResData(
                    Constants.SHRIMP_LONGYAN,
                    1,
                    result.data.price.toString(),
                    result.msg
                )
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)

                result.data
            } else {
                val matchResData = MatchResData(Constants.SHRIMP_LONGYAN, 0, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                null
            }

        } catch (e: Exception) {
            val matchResData = MatchResData(Constants.SHRIMP_LONGYAN, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }

    suspend fun xiaoFuJKMatchSuspend(
        data: XiaoFuUserData
    ): XiaoFuProduct? {

        return try {

            val originData = Gson().toJson(data)
            LogUtils.e("小福撞库请求参数: $originData")

            val encryptData = XiaoFuAESUtils.aesEncrypt(
                Constants.XIAOFU_PUBLICK_KEY,
                Constants.XIAOFU_IV,
                originData
            )
            val time = System.currentTimeMillis() / 1000
            val sign = XiaoFuAESUtils.md5(
                "${Constants.XIAOFU_CHANNEL_NUM}${encryptData}${time}${Constants.XIAOFU_IV}${Constants.XIAOFU_FACTOR}"
            )

            val hashMap = hashMapOf<String, Any?>(
                "channel" to Constants.XIAOFU_CHANNEL_NUM,
                "encrypt_data" to encryptData,
                "timestamp" to time,
                "sign" to sign,
                "trace_id" to SHA256.generateUniqueUUID()
            )

            // 直接 suspend 等待 Retrofit 结果
            val result = retrofit.xiaoFuJKMatch(hashMap).await()

            if (result.code == 200) {
                if (!result.data.products.isNullOrEmpty()) {
                    val matchResData = MatchResData(
                        Constants.SHRIMP_XIAOFU,
                        1,
                        result.data?.products?.get(0)?.channel_settlement_price.toString(),
                        result.msg
                    )
                    CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)

                    result.data.products!![0]
                } else {
                    val matchResData = MatchResData(Constants.SHRIMP_XIAOFU, 0, "0", result.msg)
                    CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                    null
                }
            } else {
                val matchResData = MatchResData(Constants.SHRIMP_XIAOFU, 0, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                null
            }

        } catch (e: Exception) {
            val matchResData = MatchResData(Constants.SHRIMP_XIAOFU, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }

    suspend fun wrbBeForeMatchSuspend(
        data: WrbSaveData
    ): WrbFormResultBean? {

        return try {

            val dataString = Gson().toJson(data)
            LogUtils.e("微融宝撞库-数据: $dataString")

            val jiamiData = SHA256.AESEncrypt(Constants.WEIRONGBAO_APPKEY, dataString)

            val hashMap = hashMapOf(
                "channel_code" to Constants.WEIRONGBAO_CHANNWL_CODE,
                "data" to jiamiData
            )

            // suspend 等待 Retrofit 的结果
            val result = retrofit.wrbBeForeMatch(hashMap).await()

            if (result.code == 200) {
                val matchResData =
                    MatchResData(Constants.SHRIMP_WRB, 1, result.data.price.toString(), result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                result.data
            } else {
                val matchResData = MatchResData(Constants.SHRIMP_WRB, 0, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                null
            }

        } catch (e: Exception) {
            val matchResData = MatchResData(Constants.SHRIMP_WRB, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }

    suspend fun zxdNewMatchSuspend(
        orderID: String,
        data: ZxdAPISaveData
    ): ZxdNewResultProduceBean? {

        return try {

            val dataString = Gson().toJson(data)
            LogUtils.e("智享贷撞库数据: $dataString")

            val jiamiData = SHA256.AESEncrypt(Constants.ZXD_PUBLICK_KEY, dataString)

            val hashMap = hashMapOf(
                "order_id" to orderID,
                "channel_code" to Constants.ZXD_CHANNELCODE,
                "data" to jiamiData
            )

            // suspend 等待 Retrofit 返回
            val result = retrofit.zxdNewMatch(hashMap).await()

            if (result.code == 200) {
                val matchResData =
                    MatchResData(Constants.SHRIMP_ZXD, 1, result.data.price.toString(), result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)

                result.data

            } else {
                val matchResData = MatchResData(Constants.SHRIMP_ZXD, 0, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                null
            }


        } catch (e: Exception) {
            val matchResData = MatchResData(Constants.SHRIMP_ZXD, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }

    /***********小鱼全流程 上报**************/

    fun completeFlowReport(
        type: Int,   //业务类型：1撞库结果，2进件结果
        push_res: Int? = -1, //进件结果
        org_id: Int = 0 //合作机构ID

    ) {
        launchGo({
            val hashMap = HashMap<String, Any?>()

            hashMap["mobile"] = MmkvUtil.getInstance().decodeString("loginphone")
            hashMap["partner_id"] = MmkvUtil.getInstance().decodeInt("partner_id").toString()
            hashMap["type"] = type
            if (type == 1) { //撞库结果上报
                val jsonStr = Gson().toJson(CallbackManager.getAppStateManager()?.getOrgMatchRes())
                hashMap["org_match_res"] = jsonStr
            } else if (type == 2) {//进件结果上班
                hashMap["org_id"] = org_id
                hashMap["org_push_res"] = push_res
            }
            val result = retrofit.completeFlowReport(hashMap).await()
        })
    }

    /**
     * 吉用的撞库上报
     */
    fun completeFlowReportJiYong(
        resData: ArrayList<MatchResData>
    ) {
        launchGo({
            val hashMap = HashMap<String, Any?>()
            hashMap["mobile"] = MmkvUtil.getInstance().decodeString("loginphone")
            hashMap["partner_id"] = MmkvUtil.getInstance().decodeInt("partner_id").toString()
            hashMap["type"] = 1  //业务类型：1撞库结果，2进件结果
            hashMap["org_match_res"] = Gson().toJson(resData)
            val result = retrofit.completeFlowReport(hashMap).await()
        })
    }

    /******************************** 闪贷喵 START******************************************/

    fun shanDaiMiaoMatch(
        data: ShanDaiMiaoSaveData,
        success: (ResultBean<ShanDaiMiaoProductResult>) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "闪贷喵-提交数据"
        errorHash["request"] = "api/full/loan/testLuck"
        val dataString = Gson().toJson(data)
        errorHash["param"] = dataString
        launchGo({
            LogUtils.d("闪贷喵撞库:---$dataString")
            val jiamiData = SHA256.AESEncrypt(Constants.SHANDAIMIAO_APPKEY, dataString)

            val hashMap = hashMapOf(
                "orgId" to Constants.SHANDAIMIAO_ORGID,
                "data" to jiamiData
            )
            val result = retrofit.shanDaiMiaoMatch(hashMap).await()
            success.invoke(result)

            if (result.code != 200 || result.data.order_no.isNullOrEmpty()) {
//                Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    fun shanDaiMiaoPush(
        searchId: String?, //通知进件时的 检索Id
        orderOn: String?, //预撞库时返回的order_no
        phone: String?, //手机号
        name: String?, //用户姓名（如果撞库没传姓名，进件务必传值姓名）
        success: (ResultBean<ShanDaiMiaoPushResult>) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "闪贷喵-提交数据"
        errorHash["request"] = "api/full/loan/testLuck"
        errorHash["param"] = "$searchId/$orderOn/$phone/$name"
        launchGo({


            val orgindata = hashMapOf(
                "searchId" to searchId,
                "orderOn" to orderOn,
                "phone" to phone,
                "name" to name
            )
            LogUtils.d("闪贷喵push:---${orgindata}")

            val result = retrofit.shanDaiMiaoPush(orgindata).await()
            success.invoke(result)

            if (result.code != 200 || result.data.code != 200) {
//                Toast.makeText(BaseApp.context, result.msg, Toast.LENGTH_LONG).show()
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }


    //旧版--弃用
    //本部用户信息上报
    fun benbuReportUserData(
        realname: String?,
        id_card_no: String?,
        province_name: String?,
        city_name: String?,
        ip: String?
    ) {
        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["realname"] = realname
            hashMap["mobile"] = MmkvUtil.getInstance().decodeString("loginphone")
            hashMap["id_card_no"] = id_card_no
            hashMap["province_name"] = province_name
            hashMap["city_name"] = city_name

            // 偶数是女性，奇数是男性
            //sex	Int	性别: 1【女】；2【男】
            val sexgender = getAgeAndGender(id_card_no ?: "")
            if (sexgender.second == 1) {
                hashMap["sex"] = "2"
            } else {
                hashMap["sex"] = "1"
            }
            hashMap["age"] = sexgender.first.toString()
            hashMap["ip"] = ip

//            val result = retrofit.benbuReportUserData(hashMap).await()

        }, {}, {}, false)
    }


    //----------------------------二项目装 START--------------------------------------

    //全流程--进件成功上报
    fun qlcPushReport(data: ApiOriginData) {
        val jsons = Gson().toJson(data)
        val requestBody: RequestBody =
            jsons.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        launchGo({
            val result = retrofit.qlcPushReport(requestBody).await()
            if (result.code == 200) {

            } else {
//                defUI.toastEvent.postValue(result.msg)
            }
            judgeCode(result)
        })
    }


    suspend fun fishWyLyMatch(
        shrimpCHANNEL: Int,
        data: FishMatchSaveData
    ): FishMatchResult? {

        return try {

            val jsons = Gson().toJson(data)
            val requestBody: RequestBody =
                jsons.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            LogUtils.e("小鱼全流程撞库数据: $jsons")

            // suspend 等待 Retrofit 返回
            val result = retrofit.fishWyLyMatch(requestBody).await()

            if (result.data.code == 1) {
                val matchResData =
                    MatchResData(shrimpCHANNEL, 1, result.data.price.toString(), result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)

                result.data

            } else {
                val matchResData = MatchResData(shrimpCHANNEL, 0, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                null
            }

        } catch (e: Exception) {
            val matchResData = MatchResData(shrimpCHANNEL, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }

    fun fishWyLyPush(
        shrimpCHANNEL: Int,
        data: FishMatchSaveData,
        success: (ResultBean<FishMatchPushResult>?) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "小鱼-进件"
        errorHash["request"] = "prod-api/app/openapi/full/push/v2"
        errorHash["param"] = Gson().toJson(data)


        launchGo({
            val jsons = Gson().toJson(data)
            val requestBody: RequestBody =
                jsons.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            LogUtils.e("小鱼进件请求参数: $jsons")


            val result = retrofit.fishWyLyPush(requestBody).await()
            success.invoke(result)

            //全流程上报和异常上报
            if (result.code == 0) {
                if (!result.data.order_sn.isNullOrEmpty()) {
                    //全流程进件上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
                    completeFlowReport(2, 1, shrimpCHANNEL)
                } else {
                    completeFlowReport(2, -1, shrimpCHANNEL)
                }

            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
                completeFlowReport(2, -1, shrimpCHANNEL)
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, shrimpCHANNEL)
        }, {}, false)
    }


    //----------------------------吉用钱包全流程 START--------------------------------------


    /**
     * 吉用钱包同时撞库 挂起方法
     */
    suspend fun jyqbQlcMatchSuspend(
        jyqBqlcUserData: JYQBqlcUserData
    ): JiyongOrderData? {

        return try {

            val dataString = Gson().toJson(jyqBqlcUserData)
            LogUtils.e("吉用钱包全流程-撞库数据: $dataString")

            val channel = Constants.JYQB_CHANNELCODE
            val timestamp = System.currentTimeMillis().toString() //精确到毫秒 13位
            val reqNo = JYQBDESUtils.getReqNo()
            val data =
                JYQBDESUtils.encrypt(dataString, Constants.JYQB_PULICK_KEY, Constants.JYQB_IV)
            val signature =
                SHA256.encryptMD5(channel + data + timestamp + reqNo + Constants.JYQB_PULICK_KEY)


            val bodyHash = HashMap<String, String?>()
            bodyHash["channel"] = Constants.JYQB_CHANNELCODE
            bodyHash["reqNo"] = reqNo
            bodyHash["signature"] = signature
            bodyHash["timestamp"] = timestamp
            bodyHash["data"] = data

            // suspend 等待 Retrofit 返回
            val result = retrofit.jyqbQlcMatch(bodyHash).await()

            if (result.code == 200 && result.data.status == 1) {  //1=成功，0=失败
                val matchResData =
                    MatchResData(Constants.SHRIMP_JIYONGQIANBAO, 1, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                result.data

            } else {
                val matchResData = MatchResData(Constants.SHRIMP_JIYONGQIANBAO, 0, "0", result.msg)
                CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
                null
            }


        } catch (e: Exception) {
            val matchResData =
                MatchResData(Constants.SHRIMP_JIYONGQIANBAO, 0, "0", e.message.toString())
            CallbackManager.getAppStateManager()?.getOrgMatchRes()?.add(matchResData)
            null
        }
    }


    //吉用钱包掩码撞库
    fun jyqbQlcMatch(
        data: JYQBqlcUserData,
        price: Double?,
        success: (ResultBean<JiyongOrderData>?) -> Unit
    ) {

        val orgMatchRes: ArrayList<MatchResData> = ArrayList()

        launchGo({
            val dataString = Gson().toJson(data)
            LogUtils.e("吉用钱包全流程-撞库数据: $dataString")

            val channel = Constants.JYQB_CHANNELCODE
            val timestamp = System.currentTimeMillis().toString() //精确到毫秒 13位
            val reqNo = JYQBDESUtils.getReqNo()
            val data =
                JYQBDESUtils.encrypt(dataString, Constants.JYQB_PULICK_KEY, Constants.JYQB_IV)
            val signature =
                SHA256.encryptMD5(channel + data + timestamp + reqNo + Constants.JYQB_PULICK_KEY)

            val bodyHash = HashMap<String, String?>()
            bodyHash["channel"] = Constants.JYQB_CHANNELCODE
            bodyHash["reqNo"] = reqNo
            bodyHash["signature"] = signature
            bodyHash["timestamp"] = timestamp
            bodyHash["data"] = data

            val result = retrofit.jyqbQlcMatch(bodyHash).await()
            success.invoke(result)


            //全流程上报 type=1撞库结果，2进件结果  org_id=机构ID org_res=结果 0失败 1成功 org_match_res=结果
            if (result.code == 200 && result.data.status == 1) {  //1=成功，0=失败
                orgMatchRes.add(
                    MatchResData(
                        Constants.SHRIMP_JIYONGQIANBAO,
                        1,
                        price.toString(),
                        result.msg
                    )
                )
                completeFlowReportJiYong(orgMatchRes)

            } else {
                orgMatchRes.add(
                    MatchResData(
                        Constants.SHRIMP_JIYONGQIANBAO,
                        0,
                        price.toString(),
                        result.msg
                    )
                )
                completeFlowReportJiYong(orgMatchRes)
            }

        }, {
            success.invoke(null)
            orgMatchRes.add(
                MatchResData(
                    Constants.SHRIMP_JIYONGQIANBAO,
                    0,
                    "0",
                    it.message.toString()
                )
            )
            completeFlowReportJiYong(orgMatchRes)
        }, {}, false)
    }

    fun jyqbQlcApply(
        data: JYQBqlcUserData,
        success: (ResultBean<JYQBPushResult>?) -> Unit
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉用钱包全流程--推送进件"
        errorHash["request"] = "channel/apply/thirdV2/submit"

        launchGo({
            val dataString = Gson().toJson(data)
            errorHash["param"] = dataString
            LogUtils.e("吉用钱包全流程--推送进件表单数据: ${dataString}")

            val channel = Constants.JYQB_CHANNELCODE
            val timestamp = System.currentTimeMillis().toString() //精确到毫秒 13位
            val reqNo = JYQBDESUtils.getReqNo()
            val data =
                JYQBDESUtils.encrypt(dataString, Constants.JYQB_PULICK_KEY, Constants.JYQB_IV)
            val signature =
                SHA256.encryptMD5(channel + data + timestamp + reqNo + Constants.JYQB_PULICK_KEY)

            val bodyHash = HashMap<String, String?>()
            bodyHash["channel"] = Constants.JYQB_CHANNELCODE
            bodyHash["timestamp"] = timestamp
            bodyHash["reqNo"] = reqNo
            bodyHash["signature"] = signature
            bodyHash["data"] = data


            val result = retrofit.jyqbQlcApply(bodyHash).await()
            success.invoke(result)

            //全流程上报和异常上报
            if (result.code == 200 && result.data.status == 1) {
                completeFlowReport(2, 1, Constants.SHRIMP_JIYONGQIANBAO)
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
                completeFlowReport(2, -1, Constants.SHRIMP_JIYONGQIANBAO)
            }

        }, {
            success.invoke(null)
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
            completeFlowReport(2, -1, Constants.SHRIMP_JIYONGQIANBAO)

        }, {}, true)
    }


}