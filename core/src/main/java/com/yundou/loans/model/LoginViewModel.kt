package com.yundou.loans.model


import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.BaseViewModel
import com.yundou.loans.base.Message
import com.yundou.loans.entity.*
import com.yundou.loans.http.EasyHttp
import com.yundou.loans.http.ResultBean
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.LogUtils
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.SHA256
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.code
import kotlin.toString

class LoginViewModel : BaseViewModel() {

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

//    var ipLocationLiveData = MutableLiveData<String?>()
//
//    fun getIpLocation(success: () -> Unit) {
//        launchGo({
//            val result = retrofit.getIpLocation().await()
//            if (result.error_code == 0) {
//                ipLocationLiveData.value = null
//                success.invoke()
//            } else if (result.error_code == -1) {
//                defUI.toastEvent.postValue(result.error_msg)
//                ipLocationLiveData.value = result.error_msg
//            }
//        }, {}, {}, false)
//    }

    //发送短信
    fun getSmsCode(
        phone: String,
        success: () -> Unit,
    ) {
        launchGo({
//            val event: String = if (text == "用户注册") {
//                "register"
//            } else {
//                "login"
//            }
//            val hashMap = HashMap<String, String?>()
//            hashMap["mobile"] = phone
//            //"register/login"
//            hashMap["event"] = event
            val result = retrofit.getMsgCode(phone).await()
            if (result.code == 0) {
                success.invoke()
                defUI.toastEvent.postValue("验证码发送成功")
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

//    fun logoffThree(mobile: String, success: () -> Unit) {
//        launchGo({
//            val hashMap = HashMap<String, String?>()
//            hashMap["mobile"] = mobile
//            hashMap["partner_id"] = MmkvUtil.getInstance().decodeInt("partner_id").toString()
//            val result = retrofit.logoffThree(hashMap).await()
//            if (result.error_code == 0) {
//                success.invoke()
//            } else {
//                defUI.toastEvent.postValue(result.error_msg)
//            }
//        })
//    }
//
//    fun checkOffIsLogoff(mobile: String, success: (bean: LogOff) -> Unit) {
//        launchGo({
//            val result = retrofit.checkOffIsLogoff(mobile).await()
//            if (result.error_code == 0) {
//                success.invoke(result.data)
//            }
//        }, {}, {}, false)
//    }

    //注册
    fun getRegister(
        phone: String,
        code: String,
        password: String,
        success: (String) -> Unit,
    ) {
        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["mobile"] = phone
            hashMap["code"] = code
            hashMap["password"] = password
            val result = retrofit.getRegister(hashMap).await()
            if (result.code == 0) {
                success.invoke(result.data.token.toString())
            } else {

                defUI.toastEvent.postValue(result.msg)
            }

            judgeCode(result)
        })
    }

    //登录模块
    fun getLogin(
        type: Int = 1,
        phone: String,
        code: String,
        password: String,
        success: (String) -> Unit,
    ) {
        launchGo({
            val hashMap = HashMap<String, String?>()
            //type //1验证码登录 2密码登录
            hashMap["mobile"] = phone
            if (type == 1) {
                hashMap["code"] = code
            } else {
                hashMap["password"] = password
            }
            hashMap["type"] = type.toString()
            val result = retrofit.getLogin(hashMap).await()
            if (result.code == 0) {
                success.invoke(result.data.token.toString())
            } else {
                defUI.toastEvent.postValue(result.msg)
            }

            judgeCode(result)
        })
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

    //个人信息
    fun getUser(success: (GetUserInfoData) -> Unit) {
        launchGo({
            val result = retrofit.userInfo().await()
            if (result.code == 0) {
                success.invoke(result.data)
            }

            judgeCode(result)
        })
    }

    //反馈
    fun feedback(content: String, success: () -> Unit) {
        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["content"] = content
            hashMap["mobile"] =  MmkvUtil.getInstance().decodeString("loginphone")
            hashMap["partner_id"] = MmkvUtil.getInstance().decodeInt("partner_id").toString()
            val result = retrofit.feedback(hashMap).await()
            defUI.toastEvent.postValue(result.msg)
            success.invoke()

            judgeCode(result)
        })
    }



    //退出登录
//    fun logoOut(success: () -> Unit) {
//        launchGo({
//            val result = retrofit.logoOut().await()
//            if (result.error_code == 0) {
//                success.invoke()
//            }
//            judgeCode(result)
//        })
//    }

    //注销账号
    fun logoff(success: () -> Unit) {
        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["mobile"] =  MmkvUtil.getInstance().decodeString("loginphone")
            hashMap["partner_id"] =  MmkvUtil.getInstance().decodeInt("partner_id").toString()
            val result = retrofit.logoff(hashMap).await()
            if (result.code == 0) {
                success.invoke()
            }
            judgeCode(result)
        })
    }


    //---------------笙融-微秒用 ---------接口------------------

    //获取登录短信验证码
    fun getWmCode(phone: String, success: () -> Unit) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "笙融获取验证码"
        errorHash["request"] = "login/captcha"
        errorHash["param"] = phone

        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["phone"] = phone
            val result = retrofit.captchaAsync(hashMap).await()
            if (result.code == 200) {
                success.invoke()
                defUI.toastEvent.postValue("验证码发送成功")
            } else {
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash), phone)

                defUI.toastEvent.postValue(result.message)
            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash), phone)

        })
    }


    //笙融 - 验证码登录
    fun getWmLogin(phone: String, captcha: String, success: (String) -> Unit) {

        val hashMap = HashMap<String, String?>()
        hashMap["phone"] = phone
        hashMap["captcha"] = captcha

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "笙融登录"
        errorHash["request"] = "login/sms"
        errorHash["param"] = Gson().toJson(hashMap)

        launchGo({
            val result = retrofit.captchaLoginAsync(hashMap).await()
            if (result.code == 200) {
                success.invoke(result.data.token.toString())
            } else {
                defUI.toastEvent.postValue(result.message)

                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash), phone)

            }
            judgeCode(result)
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash), phone)
        })
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


    //--------------------智享贷 接口--------------------

    fun zxdGetCode(phone: String, success: (String) -> Unit) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "智享贷获取验证码"
        errorHash["request"] = "api/v1/get_sms_code"
        errorHash["param"] = phone

        launchGo({
            val map = mapOf("phone_number" to phone)
            val jsons = Gson().toJson(map)
            val body =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)

            val result = retrofit.zxdGetCode(body).await()
            if (result.code == 200) {
                defUI.toastEvent.postValue("验证码发送成功")
                success.invoke("1")
            } else {
                defUI.toastEvent.postValue(result.msg)

                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    //智享贷 验证码登录
    fun zxdcodeLogin(
        phone: String,
        sms_code: String,
        channel_code: String,
        success: (String) -> Unit
    ) {
        val map = mapOf(
            "phone_number" to phone,
            "sms_code" to sms_code,
            "channel_code" to channel_code
        )

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "智享贷登录"
        errorHash["request"] = "api/v1/login"
        errorHash["param"] = Gson().toJson(map)

        launchGo({

            val jsons = Gson().toJson(map)
            val body =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)

            val result = retrofit.zxdcodeLogin(body).await()
            if (result.code == 200) {
                success.invoke(result.data?.token.toString())
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


    //--------------------快易贷 接口--------------------

    fun iconV2Get() {
        launchGo({
            val result = retrofit.iconV2Get("4f5z").await()
            //judgeCode(result)
        })
    }

    fun kydGetCode(phone: String, success: (String) -> Unit) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "快易贷获取验证码"
        errorHash["request"] = ""
        errorHash["param"] = phone

        launchGo({
            val map = mapOf("phone" to phone, "channelSign" to "4f5z")
            val jsons = Gson().toJson(map)
            val body =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)

            val result = retrofit.kydGetCode(body).await()
            if (result.code == 200) {
                defUI.toastEvent.postValue("验证码发送成功")
                success.invoke("1")
            } else {
                defUI.toastEvent.postValue(result.msg)

                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    fun kydcodeLogin(
        phone: String,
        sms_code: String,
        success: (String) -> Unit
    ) {
        val map = mapOf(
            "phone" to phone,
            "smsCode" to sms_code,
            "channelSign" to "4f5z"
        )

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "快易贷登录"
        errorHash["request"] = ""
        errorHash["param"] = Gson().toJson(map)

        launchGo({

            val jsons = Gson().toJson(map)
            val body =
                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)

            val result = retrofit.kydcodeLogin(body).await()
            if (result.code == 200) {
                success.invoke(result.data?.token.toString())
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

    fun moliGetCode(mobile: String, success: (String) -> Unit) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "魔力"
        errorHash["request"] = "hyj/code/loginCode.html"
        errorHash["param"] = mobile

        launchGo({
            val mobilebody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), mobile)
            val appKeybody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), BaseApp.context.mlAPPKEY)
            val params =
                "app_key=${BaseApp.context.mlAPPKEY}&mobile=${mobile}${BaseApp.context.mlAPPSECRET}"
            val md5Sign = SHA256.encryptMD5(params)
            val sign: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), md5Sign)

            val result = retrofit.moliGetCode(mobilebody, appKeybody, sign).await()
            if (result.status == 1) {
                defUI.toastEvent.postValue("验证码发送成功")
                success.invoke("1")
            } else {
                defUI.toastEvent.postValue(result.info)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    fun moliCodeLogin(mobile: String, sms_code: String, success: (String) -> Unit) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "魔力"
        errorHash["request"] = "hyj/user/codeLogin.html"
        errorHash["param"] = mobile + "/" + sms_code

        launchGo({
            val mobilebody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), mobile)
            val appKeybody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), BaseApp.context.mlAPPKEY)
            val smsCodeBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), sms_code)

            val params =
                "app_key=${BaseApp.context.mlAPPKEY}&mobile=${mobile}&sms_code=$sms_code${BaseApp.context.mlAPPSECRET}"
            val md5Sign = SHA256.encryptMD5(params)
            val signBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), md5Sign)

            val result =
                retrofit.moliCodeLogin(mobilebody, appKeybody, smsCodeBody, signBody).await()
            if (result.status == 1) {
                success.invoke(result.data?.access_token.toString())
            } else {
                defUI.toastEvent.postValue(result.info)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        })
    }

    fun protocolRegisterGet(success: (MoliGetXieyi) -> Unit) {
        launchGo({
            val appKeybody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), BaseApp.context.mlAPPKEY)
            val codeBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), "register")

            val params =
                "app_key=${BaseApp.context.mlAPPKEY}&code=${"register"}${BaseApp.context.mlAPPSECRET}"
            val md5Sign = SHA256.encryptMD5(params)
            val signBody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), md5Sign)

            val result =
                retrofit.protocolRegisterGet(appKeybody, codeBody, signBody)
                    .await()
            if (result.status == 1) {
                success.invoke(result.data)
            } else {
                // defUI.toastEvent.postValue(result.info)
            }
        }, {}, {}, false)
    }

    //********************************二项目 接口***********************************

    //发送短信
    fun twopSendCode(
        mobile: String,
        success: () -> Unit,
    ) {
        launchGo({
            val time = (System.currentTimeMillis() / 1000).toString()
            val channel = BaseApp.context.storeid

            val pinJie =
                "appid=${BaseApp.context.app_id}&channel=$channel&mobile=$mobile&t=$time${Constants.TWOP_SIGB}"
            val signature = SHA256.encryptMD5(SHA256.encryptMD5(pinJie))

            val result = retrofit.twopSendCode(signature, mobile).await()
            if (result.code == 0) {
                success.invoke()
                defUI.toastEvent.postValue("验证码发送成功")
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        }, {}, {}, true)
    }

    //登录
    fun twopLogin(
        mobile: String,
        code: String,
        success: (String) -> Unit,
    ) {
        launchGo({
            val time = (System.currentTimeMillis() / 1000).toString()
            val channel = BaseApp.context.storeid

            val pinJie =
                "appid=${BaseApp.context.app_id}&channel=$channel&code=$code&mobile=$mobile&t=$time${Constants.TWOP_SIGB}"
            val signature = SHA256.encryptMD5(SHA256.encryptMD5(pinJie))

            val hashMap = HashMap<String, String?>()
            hashMap["mobile"] = mobile
            hashMap["code"] = code
            val result = retrofit.twopLogin(signature, hashMap).await()
            if (result.code == 0) {
                success.invoke(result.data.token.toString())
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }


    /****---------------------------吉用 接口-------------------------***/
    //发送短信
    fun jiYongSendCode(
        mobile: String,
        success: () -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉用发送短信"
        errorHash["request"] = "channel/feed/apply/sendCode"
        errorHash["param"] = mobile

        launchGo({
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val channel = Constants.JIYONG_CHANNEL
            val transSeq = SHA256.generateUniqueUUID()
            val hashMap = HashMap<String, String?>()
            hashMap["mobile"] = mobile
            val data = SHA256.RSAEncrypt(Constants.JIYONG_PUBLICKEY, Gson().toJson(hashMap))

            val signature = SHA256.encryptMD5(channel + data + timestamp + transSeq)

            val bodyHash = HashMap<String, String?>()
            bodyHash["channel"] = channel
            bodyHash["timestamp"] = timestamp
            bodyHash["transSeq"] = transSeq
            bodyHash["signature"] = signature
            bodyHash["data"] = data

            val result = retrofit.jiYongSendCode(bodyHash).await()
            if (result.code == 200) {
                success.invoke()
                defUI.toastEvent.postValue("验证码发送成功")
            } else {
                defUI.toastEvent.postValue(result.msg)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
            }

        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    //登录
    fun jiYongLoginPost(
        mobile: String,
        code: String,
        success: (String) -> Unit,
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉用登录"
        errorHash["request"] = "channel/feed/apply/login"
        errorHash["param"] = mobile + "/" + code

        launchGo({
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val channel = Constants.JIYONG_CHANNEL
            val transSeq = SHA256.generateUniqueUUID()
            val hashMap = HashMap<String, String?>()
            hashMap["mobile"] = mobile
            hashMap["code"] = code
            val data = SHA256.RSAEncrypt(Constants.JIYONG_PUBLICKEY, Gson().toJson(hashMap))
            val signature = SHA256.encryptMD5(channel + data + timestamp + transSeq)

            val bodyHash = HashMap<String, String?>()
            bodyHash["channel"] = channel
            bodyHash["timestamp"] = timestamp
            bodyHash["transSeq"] = transSeq
            bodyHash["signature"] = signature
            bodyHash["data"] = data

            val result = retrofit.jiYongLoginPost(bodyHash).await()
            if (result.code == 200) {
                success.invoke(result.data.token.toString())
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

    /****---------------------------阳薪花 接口-------------------------***/
    //发送短信
    fun yangXinHuaSendCode(
        mobile: String,
        success: () -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "阳薪花发送短信"
        errorHash["request"] = "login/getCode"
        errorHash["param"] = mobile

        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["phone"] = mobile
            val data = SHA256.AESEncrypt(Constants.YXH_AESKEY, Gson().toJson(hashMap))

            val bodyHash = HashMap<String, String?>()
            bodyHash["data"] = data

            val result = retrofit.yangXinHuaSendCode(bodyHash).await()
            if (result.code == 200) {
                success.invoke()
                defUI.toastEvent.postValue("验证码发送成功")
            } else {
                defUI.toastEvent.postValue(result.message)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    //登录
    fun yangXinHuaLogin(
        mobile: String,
        captcha: String,
        success: (String) -> Unit,
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "阳薪花登录"
        errorHash["request"] = "login/smsCode"
        errorHash["param"] = mobile

        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["phone"] = mobile
            hashMap["captcha"] = captcha
            val data = SHA256.AESEncrypt(Constants.YXH_AESKEY, Gson().toJson(hashMap))

            val bodyHash = HashMap<String, String?>()
            bodyHash["data"] = data

            val result = retrofit.yangXinHuaLogin(bodyHash).await()
            if (result.code == 200) {
                success.invoke(result.data.token.toString())
                MmkvUtil.getInstance().encode(Constants.YXH_USERID, result.data.userId)
            } else {
                defUI.toastEvent.postValue(result.message)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    /****---------------------------吉用帮 接口-------------------------***/


    //发送短信
    fun jiYongBangSendCode(
        mobile: String,
        success: () -> Unit,
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉用帮发送短信"
        errorHash["request"] = "api/v2/landingPages/send"
        errorHash["param"] = mobile

        launchGo({

            val result = retrofit.jiYongBangSendCode(mobile).await()
            if (result.code == 200) {
                success.invoke()
                defUI.toastEvent.postValue("验证码发送成功")
            } else {
                defUI.toastEvent.postValue(result.message)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }


    //登录
    fun jiYongBangLogin(
        mobile: String,
        captcha: String,
        success: (String) -> Unit,
    ) {
        val errorHash = HashMap<String, String>()
        errorHash["name"] = "吉用帮登录"
        errorHash["request"] = "api/v2/landingPages/login-check-sms"
        errorHash["param"] = mobile + "/" + captcha

        launchGo({
            val hashMap = HashMap<String, String?>()
            hashMap["phone"] = mobile
            hashMap["checkCode"] = captcha

            val result = retrofit.jiYongBangLogin(hashMap).await()
            if (result.code == 200) {
                success.invoke(result.data.token!!)
            } else {
                defUI.toastEvent.postValue(result.message)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.status, Gson().toJson(errorHash))
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }


    //-----------------------有钱钱包---------------------
    //提交
    fun yqqbSendCode(
        mobile: String,
        success: () -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "有钱发送短信"
        errorHash["request"] = "api/login/sendPhoneVerifyCode"
        errorHash["param"] = mobile

        launchGo({

            val map: HashMap<String, String> = hashMapOf(
                // "encryptionMobile" to SHA256.encryptMD5(mobile),
                "mobile" to mobile,
                "channelCode" to Constants.YQQB_CHANNELCODE,
                // "source" to "1",//1有钱来 2金信天
            )

            val result = retrofit.yqqbSendCode(map).await()
            Log.d("Login", "yqqbSendCode: result -- > " + JSON.toJSONString(result))
            if (result.code == 200) {
                success.invoke()
                defUI.toastEvent.postValue("验证码发送成功")
            } else {
                defUI.toastEvent.postValue(result.msg)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
            }

        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    fun yqqbLogin(
        mobile: String,
        code: String,
        success: (String) -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "有钱登录"
        errorHash["request"] = "api/login/login"
        errorHash["param"] = mobile


        launchGo({

            val map: HashMap<String, String> = hashMapOf(
                // "encryptionMobile" to SHA256.encryptMD5(mobile),
                "mobile" to mobile,
                "code" to code,
                "channelCode" to Constants.YQQB_CHANNELCODE,
                //  "source" to "1",//1有钱来 2金信天
            )
//            val jsons = Gson().toJson(map)
//            val body =
//                RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsons)

            val result = retrofit.yqqbLogin(map).await()
            Log.d("Login", "yqqbLogin: result -- > " + JSON.toJSONString(result))
            if (result.code == 200) {
                success.invoke(result.data.token)

            } else {
                defUI.toastEvent.postValue(result.msg)
                errorHash["result"] = Gson().toJson(result)
                reportAbnormal(result.code, Gson().toJson(errorHash))
            }

        }, {
            errorHash["result"] = Gson().toJson(it.message)
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash))
        }, {}, true)
    }

    //----------------------------天下分期 START--------------------------------------

    //发送验证码
    fun txfqSendCode(mobile: String, success: () -> Unit) {

        launchGo({
            val hashMpa: HashMap<String, Any> = hashMapOf(
                "mobile" to mobile,
                "scene" to "LOGIN",
                "channel" to Constants.TXFQ_CHANNEL,
                "tid" to 0
            )
            val hashMapStr = Gson().toJson(hashMpa)
            val jiami = SHA256.RSAEncrypt(Constants.TXFQ_PUBLICKEY, hashMapStr)
            LogUtils.e("天下分期加密===${jiami}")
            val requestBody = jiami.toRequestBody("application/json".toMediaType())

            val result = retrofit.txfqSendCode(requestBody).await()
            if (result.code == 200) {
                success.invoke()
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

    //登录
    fun txfqLogin(mobile: String, code: String, success: (token: String) -> Unit) {

        launchGo({
            val hashMpa: HashMap<String, Any> = hashMapOf(
                "mobile" to mobile,
                "code" to code,
                "channel" to Constants.TXFQ_CHANNEL,
                "tid" to 0
            )
            val hashMapStr = Gson().toJson(hashMpa)
            val jiami = SHA256.RSAEncrypt(Constants.TXFQ_PUBLICKEY, hashMapStr)
            LogUtils.e("天下分期登录传参===${hashMapStr}")
            LogUtils.e("天下分期登录加密===${jiami}")

            val requestBody = jiami.toRequestBody("application/json".toMediaType())

            val result = retrofit.txfqLogin(requestBody).await()
            if (result.code == 200) {
                success.invoke(result.data.token)
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

    //----------------------------天下分期 END--------------------------------------

    //-------------源小花 START -----------------------

    //发送验证码
    fun yxhSendCode(
        mobile: String,
        success: () -> Unit,
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "源小花发送短信"
        errorHash["request"] = "/kkh/sms/send"
        errorHash["param"] = mobile

        launchGo({

            val map: HashMap<String, String?> = hashMapOf(
                // "encryptionMobile" to SHA256.encryptMD5(mobile),
                "mobile" to mobile,
                "module" to "301",
            )
            val hashMapStr = Gson().toJson(map)
//            val jiami = SHA256.RSAEncrypt(Constants.TXFQ_PUBLICKEY, hashMapStr)
//            LogUtils.e("源小花加密===${jiami}")
            val requestBody = hashMapStr.toRequestBody("application/json".toMediaType())

            val result = retrofit.yxhSendCode(requestBody).await()
            if (result.errcode == 200) {
                success.invoke()
                defUI.toastEvent.postValue("验证码发送成功")
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

    fun yxhCodeLogin(
        mobile: String,
        code: String,
        success: (token: String) -> Unit
    ) {

        val errorHash = HashMap<String, String>()
        errorHash["name"] = "源小花验证码登录"
        errorHash["request"] = "/kkh/sms/send"
        errorHash["param"] = mobile

        launchGo({

            val map: HashMap<String, String?> = hashMapOf(
                // "encryptionMobile" to SHA256.encryptMD5(mobile),
                "mobile" to mobile,
                "code" to code,
            )
            val hashMapStr = Gson().toJson(map)
//            val jiami = SHA256.RSAEncrypt(Constants.TXFQ_PUBLICKEY, hashMapStr)
//            LogUtils.e("源小花加密===${jiami}")
            val requestBody = hashMapStr.toRequestBody("application/json".toMediaType())

            val result = retrofit.yxhCodeLogin(requestBody).await()
            if (result.errcode == 200) {
                success.invoke(result.data.token.toString())
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


    //----------------------------期贷接口 START--------------------------------------


    fun qiDaiSendCode(phone: String, success: (String) -> Unit) {

        launchGo({
            val hashmap = hashMapOf(
                "phone" to phone
            )
            val bizData = SHA256.AESEncrypt(Constants.QIDAI_PUBLICK_KEY, Gson().toJson(hashmap))
            LogUtils.e("期贷接口加密后: $hashmap")
            val body = hashMapOf(
                "bizData" to bizData,
                "channelCode" to Constants.QIDAI_CHANNELCODE
            )
            LogUtils.e("期贷接口加密后: $body")

            val result = retrofit.qiDaiSendCode(body).await()
            if (result.code == 200) {
                success.invoke(result.data)
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

    fun qiDaiLogin(phone: String, code: String, success: (token: String) -> Unit) {

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

            val result = retrofit.qiDaiLogin(body).await()
            if (result.code == 200) {
                success.invoke(result.data.toString())
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }

    //----------------------------getMobileInfo 历史账号 ----------------------------------

    fun getMobileInfo(mobile: String, success: (bean: LogOff) -> Unit) {
        launchGo({
            val result = retrofit.getMobileInfo(mobile).await()
            if (result.code == 0) {
                success.invoke(result.data)
            }
        }, {}, {}, true)
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
            success.invoke(result)
            judgeCode(result)
        }, {}, {}, true)
    }


    //----------------------------吉贷 START--------------------------------------

    fun jiDaiCodeGet(phone: String, success: () -> Unit) {

        launchGo({

            val hashmap = hashMapOf(
                "phone" to phone,
                "appId" to Constants.JIDAI_APPID
            )
            LogUtils.e("吉贷验证码: $hashmap")

            val baseUrl =
                if (MmkvUtil.getInstance().decodeInt("partner_id") == Constants.PARTNER_JIDAI) {
                    "jiDaiBaseUrl"
                } else {
                    "yueXiangBaseUrl"
                }

            val result = retrofit.jiDaiCodeGet(baseUrl, hashmap).await()
            if (result.code == 200) {
                success.invoke()
            } else
                defUI.toastEvent.postValue(result.msg)
        })
    }

    fun jiDaiLogin(phone: String, code: String, success: (String) -> Unit) {

        launchGo({

            val hashmap = hashMapOf(
                "appId" to Constants.JIDAI_APPID,
                "channelId" to Constants.JIDAI_CHANNELID,
                "channelCode" to Constants.JIDAI_CHANNELCODE,
                "phone" to phone,
                "code" to code,
            )
            LogUtils.e("吉贷登录: $hashmap")

            val baseUrl =
                if (MmkvUtil.getInstance().decodeInt("partner_id") == Constants.PARTNER_JIDAI) {
                    "jiDaiBaseUrl"
                } else {
                    "yueXiangBaseUrl"
                }

            val result = retrofit.jiDaiLogin(baseUrl, hashmap).await()
            if (result.code == 200) {
                success.invoke(result.data.token ?: "")
            } else
                defUI.toastEvent.postValue(result.msg)
        })
    }


    //----------------------------闪贷喵 START--------------------------------------

    fun shanDaiMiaoSendCode(phone: String, success: () -> Unit) {

        launchGo({

            val hashmap = hashMapOf(
                "phoneNumber" to phone
            )
            LogUtils.e("闪贷喵验证码: $hashmap")

            val result = retrofit.shanDaiMiaoSendCode(hashmap).await()
            if (result.code == 200) {
                success.invoke()
            } else
                defUI.toastEvent.postValue(result.msg)
        })
    }


    fun shanDaiMiaoLogin(phone: String, code: String, success: (String) -> Unit) {

        launchGo({

            val hashmap = hashMapOf(
                "phoneNumber" to phone,
                "code" to code,
            )
            LogUtils.e("闪贷喵登录: $hashmap")

            val result = retrofit.shanDaiMiaoLogin(hashmap).await()
            if (result.code == 200) {
                success.invoke(result.token ?: "")
            } else
                defUI.toastEvent.postValue(result.msg)
        })
    }


}