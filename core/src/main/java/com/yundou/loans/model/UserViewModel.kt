package com.yundou.loans.model


import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.BaseViewModel
import com.yundou.loans.base.Message
import com.yundou.loans.entity.ChannerList
import com.yundou.loans.entity.DaikuanUrlData
import com.yundou.loans.entity.MoLiFormData
import com.yundou.loans.entity.MoLiFormSubmit
import com.yundou.loans.entity.MoLiProvince
import com.yundou.loans.entity.MoliGetXieyi
import com.yundou.loans.http.EasyHttp
import com.yundou.loans.http.ResultBean
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.SHA256
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

/**
 * 当前版本仅保留本部服务与魔力合作方的请求，其余渠道接口已全部移除。
 */
class UserViewModel : BaseViewModel() {

    companion object {
        private const val KEY_MOLI_TREE_V1_CACHE = "KEY_MOLI_TREE_V1_CACHE"
    }

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

    //设置密码
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

    //--------------魔力--------------

    fun moliTreeV1(success: (List<MoLiProvince>) -> Unit) {
        val cacheJson = MmkvUtil.getInstance().decodeString(KEY_MOLI_TREE_V1_CACHE)
        if (!cacheJson.isNullOrEmpty()) {
            try {
                val cacheType = object : TypeToken<List<MoLiProvince>>() {}.type
                val cacheData: List<MoLiProvince> = Gson().fromJson(cacheJson, cacheType)
                if (cacheData.isNotEmpty()) {
                    success.invoke(cacheData)
                    return
                }
            } catch (_: Exception) {
                MmkvUtil.getInstance().removeKey(KEY_MOLI_TREE_V1_CACHE)
            }
        }

        launchGo({
            val appKeybody: RequestBody =
                RequestBody.create("text/plain".toMediaTypeOrNull(), BaseApp.context.mlAPPKEY)
            val params =
                "app_key=${BaseApp.context.mlAPPKEY}${BaseApp.context.mlAPPSECRET}"
            val md5Sign = SHA256.encryptMD5(params)
            val sign: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), md5Sign)
            val result = retrofit.moliTreeV1(appKeybody, sign).await()
            if (result.status == 1) {
                MmkvUtil.getInstance().encode(KEY_MOLI_TREE_V1_CACHE, Gson().toJson(result.data))
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

    //贷超-详情
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
                defUI.toastEvent.postValue(result.msg)
            }
            judgeCode(result)
        }, {}, {}, true)
    }

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

}
