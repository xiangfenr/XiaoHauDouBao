package com.yundou.loans.model


import com.google.gson.Gson
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.BaseViewModel
import com.yundou.loans.base.Message
import com.yundou.loans.entity.DaikuanUrlData
import com.yundou.loans.entity.LogOff
import com.yundou.loans.entity.MoliGetXieyi
import com.yundou.loans.http.EasyHttp
import com.yundou.loans.http.ResultBean
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.SHA256
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

/**
 * 当前版本仅保留本部服务与魔力合作方的登录请求，其余渠道接口已全部移除。
 */
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


    //--------------------本部 接口--------------------

    //发送短信（本部）
    fun getSmsCode(
        phone: String,
        success: () -> Unit,
    ) {
        launchGo({
            val result = retrofit.getMsgCode(phone).await()
            if (result.code == 0) {
                success.invoke()
                defUI.toastEvent.postValue("验证码发送成功")
            } else
                defUI.toastEvent.postValue(result.msg)

            judgeCode(result)
        })
    }


    //--------------------魔力 接口--------------------

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
                reportAbnormal(result.status, Gson().toJson(errorHash), mobile)
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            defUI.toastEvent.postValue("网络连接失败，请稍后重试")
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash), mobile)
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
                reportAbnormal(result.status, Gson().toJson(errorHash), mobile)
            }
        }, {
            errorHash["result"] = Gson().toJson(it.message)
            defUI.toastEvent.postValue("网络连接失败，请稍后重试")
            reportAbnormal(it.code!!.toInt(), Gson().toJson(errorHash), mobile)
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

}
