package com.yundou.loans.model

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.BaseViewModel
import com.yundou.loans.base.OrgMatchResStore
import com.yundou.loans.callback.CallbackManager
import com.yundou.loans.entity.*
import com.yundou.loans.http.ResultBean
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.utils.*
import com.yundou.loans.widget.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.text.toDouble
import kotlin.toString

/**
 * @Author: fenr
 * 时间: 2025/11/10
 * 类名: ACTIVITY
 * 简述:
 *
 */
class QuanLiuChengFormViewModel : BaseViewModel() {

    val viewModel = UserViewModel()
    var OVERTIME: Long = ((CallbackManager.getAppStateManager()?.getTimeoutSecond() ?: 10) * 1000).toLong()
    val JIYIHUA_MD5 = 0
    val JIYIHUA_MASK = 1


    fun allMatchRequest(
        context: Activity,
        originData: ApiOriginData,
        zxd_orderId: String,
        onFail: (String) -> Unit
    ) {

        launchGo({
            val codeList = CallbackManager.getAppStateManager()?.getShrimpChannelConcurrency()

            if (codeList.isNullOrEmpty()) {
                onFail("")
                return@launchGo
            }
            OrgMatchResStore.orgMatchRes.clear()

            val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
            val zxdData = FormDataConversionUtils.matchZXDData(originData)
            val wrbData = FormDataConversionUtils.matchToWeiRongBaoData(originData)
            val xfjkData = FormDataConversionUtils.matchToXiaoFuData(originData)
//            val longyanData = FormDataConversionUtils.matchToLongYanData(originData)
//            val weiYinData = FormDataConversionUtils.matchToWeiYinData(originData)

            val baJieData = FormDataConversionUtils.matchToBaJieData(originData)
            val fishData = FormDataConversionUtils.matchToFishMatchData(originData)
            val jyhMD5Data =
                FormDataConversionUtils.matchToJiYiHuaData(originData, Constants.SHRIMP_JIYIHUA_MD5)
            val jyhMaskData = FormDataConversionUtils.matchToJiYiHuaData(
                originData,
                Constants.SHRIMP_JIYIHUA_MASK
            )

            val taskMap: Map<Int, suspend () -> Any?> = mapOf(
                Constants.SHRIMP_ZXD to {
                    withTimeoutOrNull(OVERTIME) {
                        viewModel.zxdNewMatchSuspend(
                            zxd_orderId,
                            zxdData
                        )
                    }
                },
                Constants.SHRIMP_WRB to {
                    withTimeoutOrNull(OVERTIME) {
                        viewModel.wrbBeForeMatchSuspend(
                            wrbData
                        )
                    }
                },
                Constants.SHRIMP_XIAOFU to {
                    withTimeoutOrNull(OVERTIME) {
                        viewModel.xiaoFuJKMatchSuspend(
                            xfjkData
                        )
                    }
                },
                Constants.SHRIMP_LONGYAN to {
                    fishData.channel_id = Constants.SHRIMP_LONGYAN
                    withTimeoutOrNull(OVERTIME) {
                        viewModel.fishWyLyMatch(
                            Constants.SHRIMP_LONGYAN,
                            fishData
                        )
                    }
                },
                Constants.SHRIMP_WEIYIN to {
                    fishData.channel_id = Constants.SHRIMP_WEIYIN
                    withTimeoutOrNull(OVERTIME) {
                        viewModel.fishWyLyMatch(
                            Constants.SHRIMP_WEIYIN,
                            fishData
                        )
                    }
                },
                Constants.SHRIMP_JIYIHUA_MD5 to {

                    withTimeoutOrNull(OVERTIME) {
                        viewModel.jiYiHuaMD5MatchSuspend(
                            jyhMD5Data
                        )
                    }
                },
                Constants.SHRIMP_JIYIHUA_MASK to {

                    withTimeoutOrNull(OVERTIME) {
                        viewModel.jiYiHuaMaskMatchSuspend(
                            jyhMaskData
                        )
                    }
                },
                Constants.SHRIMP_BAJIE to {
                    withTimeoutOrNull(OVERTIME) {
                        viewModel.bajieMaskMatchSuspend(
                            baJieData
                        )
                    }
                },
//                Constants.SHRIMP_JIYONGQIANBAO to {
//                    withTimeoutOrNull(OVERTIME) {
//                        viewModel.jyqbQlcMatchSuspend(jiYongQBData)
//                    }
//                }
            )
            LogUtils.d("合并请求任务列表: ${taskMap.toString()}")


            coroutineScope {
                val deferredList = codeList.mapNotNull { code ->
                    taskMap[code]?.let { task ->
                        async { code to task() }
                    }
                }

                // 等待所有请求结束
                val results: List<Pair<Int, Any?>> = deferredList.awaitAll()
                LogUtils.d("合并result: ${Gson().toJson(results)}")
                loadingPopup.dismiss()


                val maxFirst: Int? = findMaxPriceFirst(results)
                Log.d("AAAAA", "allMatchRequest: index -- > " + maxFirst)


                //上报撞库结果
                val errorHash = HashMap<String, String>()
                errorHash["name"] = "全流程-撞库所有结果合并上报"
                errorHash["request"] = ""
                errorHash["param"] = Gson().toJson(originData)
                errorHash["result"] = Gson().toJson(results)
                viewModel.reportAbnormal(0, Gson().toJson(errorHash))


                viewModel.completeFlowReport(1) //上报撞库结果给本部


                if (null != maxFirst) {

                    when (maxFirst) {

                        Constants.SHRIMP_ZXD -> {
                            val zxdFormResultBean: ZxdNewResultProduceBean =
                                results.first { it.first == Constants.SHRIMP_ZXD }.second as ZxdNewResultProduceBean
                            zxdPushDialogShow(
                                context,
                                zxd_orderId,
                                originData,
                                zxdData,
                                zxdFormResultBean
                            ) {
                                onFail("")
                            }
                        }

                        Constants.SHRIMP_WRB -> {
                            val wrbResultBean: WrbFormResultBean =
                                results.first { it.first == Constants.SHRIMP_WRB }.second as WrbFormResultBean
                            weiRongBaoPushDialogShow(context, originData, wrbData, wrbResultBean) {
                                onFail("")
                            }
                        }


                        Constants.SHRIMP_XIAOFU -> {
                            val xiaofuResultBean: XiaoFuProduct =
                                results.first { it.first == Constants.SHRIMP_XIAOFU }.second as XiaoFuProduct
                            xiaoFuJKPushDialogShow(
                                context,
                                originData,
                                xfjkData,
                                xiaofuResultBean
                            ) {
                                onFail("")
                            }
                        }


                        Constants.SHRIMP_LONGYAN -> {
                            val fishResult: FishMatchResult =
                                results.first { it.first == Constants.SHRIMP_LONGYAN }.second as FishMatchResult
                            fishMatchPushDialogShow(
                                context,
                                Constants.SHRIMP_LONGYAN,
                                originData,
                                fishData,
                                fishResult
                            ) {
                                onFail("")
                            }
                        }

                        Constants.SHRIMP_WEIYIN -> {
                            val fishResult: FishMatchResult =
                                results.first { it.first == Constants.SHRIMP_WEIYIN }.second as FishMatchResult
                            fishMatchPushDialogShow(
                                context,
                                Constants.SHRIMP_WEIYIN,
                                originData,
                                fishData,
                                fishResult
                            ) {
                                onFail("")
                            }
                        }


                        Constants.SHRIMP_JIYIHUA_MD5 -> {
                            val jiYiHuaResult: JiYiHuaResult =
                                results.first { it.first == Constants.SHRIMP_JIYIHUA_MD5 }.second as JiYiHuaResult
                            jiYiHuaPushDialogShow(
                                context,
                                originData,
                                jyhMD5Data,
                                jiYiHuaResult,
                                JIYIHUA_MD5
                            ) {
                                onFail("")
                            }
                        }


                        Constants.SHRIMP_JIYIHUA_MASK -> {
                            val jiYiHuaResult: JiYiHuaResult =
                                results.first { it.first == Constants.SHRIMP_JIYIHUA_MASK }.second as JiYiHuaResult
                            jiYiHuaPushDialogShow(
                                context,
                                originData,
                                jyhMaskData,
                                jiYiHuaResult,
                                JIYIHUA_MASK
                            ) {
                                onFail("")
                            }
                        }

                        Constants.SHRIMP_BAJIE -> {
                            val bajieResult: BaJieProductResult =
                                results.first { it.first == Constants.SHRIMP_BAJIE }.second as BaJieProductResult
                            val phoneMd5 = SHA256.encryptMD5(originData.phone ?: "")

                            if (!bajieResult.hitPhoneList!!.contains(phoneMd5)) {
                                baJiePushDialogShow(context, originData, baJieData, bajieResult) {
                                    onFail("")
                                }
                            } else {
                                onFail("")
                            }
                        }
//                        Constants.SHRIMP_JIYONGQIANBAO -> {
//                            val resultData: JiyongOrderData =
//                                results.first { it.first == Constants.SHRIMP_JIYONGQIANBAO }.second as JiyongOrderData
//                            jYQBPushDialogShow(context,originData, jiYongQBData,resultData){
//                                onFail("")
//                            }
//                        }


                    }
                } else {
                    onFail("")
                }
            }

        }, {}, {}, false)
    }

    /**
     * 找出价格最高的
     */
    fun findMaxPriceFirst(results: List<Pair<Int, Any?>>): Int? {
        return results
            .mapNotNull { (first, second) ->
                val price: Double? = when (second) {
                    // 根据对象类型判断
                    is ZxdNewResultProduceBean -> second.price?.toDouble()
                    is WrbFormResultBean -> second.price?.toDouble()
                    is XiaoFuProduct -> second.channel_settlement_price.toDouble()
                    is LongYanFormResultBean -> second.price?.toDouble()
                    is WeiYinProductInfo -> second.price?.toDoubleOrNull()
                    is JiYiHuaResult -> second.discountPrice.toDouble()
                    is BaJieProductResult -> second.price?.toDouble()
                    is FishMatchResult -> second.price?.toDouble()
                    is JiyongOrderData ->  CallbackManager.getAppStateManager()?.getJYQBPrice()
                    else -> null
                }
                price?.let { first to it }
            }
            .maxByOrNull { it.second }
            ?.first
    }


    private fun baJiePushDialogShow(
        context: Activity,
        originData: ApiOriginData,
        saveData: BaJieSaveData,
        productResult: BaJieProductResult,
        onFail: (String) -> Unit
    ) {

        productResult.let { it ->

            saveData.phone = originData.phone
            saveData.name = originData.real_name
            saveData.idCard = originData.id_card

            // -- 替换个人信息授权书--
            val encodedName = Utils.maskName(saveData.name)
            val encodedIDCard = Utils.hidePhoneNumber(saveData.idCard)
            val encodedPhone = Utils.hidePhoneNumber(saveData.phone)

            it.authorizationAgreement?.forEach { protocol ->
                if (protocol.name!!.contains("个人信息共享")) {
                    protocol.url =
                        "${protocol.url}?name=$encodedName&idno=$encodedIDCard&cPhone=$encodedPhone"
                }
            }

            // -- --
            val agreementDialog = BaJieAgreementDialog(context)
            agreementDialog.setAgreementtData(it)
            val popup = XPopup.Builder(context)
                .hasShadowBg(true)
                .moveUpToKeyboard(false)
                .isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .enableDrag(false)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(agreementDialog)
                .show()
            agreementDialog.setXieyiDialogClick(object :
                BaJieAgreementDialog.IXieyiDialogClick {
                override fun agreementClick() {
                    val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
                    viewModel.bajieMaskApply(it.serialNo, saveData) { it ->
                        loadingPopup.dismiss()
                        if (it?.code == 200) {
                            //---进件成功---上报本部服务器---
                            originData.partner_id =
                                BaseApp.context.storeid.toInt()
                            originData.price = productResult.price.toString().toDouble()
                            originData.channel_id = Constants.SHRIMP_BAJIE.toString()
                            viewModel.qlcPushReport(originData)

                            goSuccessActivity(context)
                        } else {
                            popup.dismiss()
                            //进件失败--走其它的合作方
                            onFail(it?.msg ?: "")
                        }
                    }
                }
            })
        }

    }


    //吉意花借款进件
    private fun jiYiHuaPushDialogShow(
        context: Activity,
        originData: ApiOriginData,
        jiYiHuaSaveData: JiYiHuaSaveData,
        productResult: JiYiHuaResult,
        isMD5Mask: Int, //0=MD5 1=掩码
        onFail: (String) -> Unit
    ) {

        val onApplyResult: (ResultBean<Any>?) -> Unit = { pushResult ->
            if (pushResult?.code == 200) {
                //---进件成功---上报本部服务器---
                originData.partner_id = BaseApp.context.storeid.toInt()
                originData.price = productResult.discountPrice
                if (isMD5Mask == JIYIHUA_MASK) {
                    originData.channel_id = Constants.SHRIMP_JIYIHUA_MASK.toString()
                } else {
                    originData.channel_id = Constants.SHRIMP_JIYIHUA_MD5.toString()
                }
                viewModel.qlcPushReport(originData)

                goSuccessActivity(context)
            } else {
                onFail(pushResult?.msg ?: "")
            }
        }

        productResult.let {
            //显示Dialog
            val agreementDialog = JiYiHuaAgreementDialog(context)
            var protocolUrl = ""
            if (isMD5Mask == JIYIHUA_MASK) {
                val encodedName = Utils.maskName(originData.real_name)
                val encodedPhone = Utils.hidePhoneNumber(originData.phone)
                protocolUrl =
                    "https://www.ruihe.info/jiyihua_auth_agreement.html?name=$encodedName&mobile=$encodedPhone&date=${RepayDateUtils.getCurrentFormatDate()}"
                it.protocolList?.get(0)?.protocolUrl = protocolUrl
            }
            agreementDialog.setAgreementtData(productResult)

            val popup = XPopup.Builder(context)
                .hasShadowBg(true)
                .moveUpToKeyboard(false)
                .isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .enableDrag(false)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(agreementDialog)
                .show()

            agreementDialog.setXieyiDialogClick(object :
                JiYiHuaAgreementDialog.IXieyiDialogClick {
                override fun agreementClick() {
                    //同意后,申请进件
                    jiYiHuaSaveData.userName = originData.real_name

                    if (isMD5Mask == JIYIHUA_MD5) {
                        viewModel.jiYiHuaMD5Apply(
                            productResult.applyNo.toString(),
                            originData.phone!!,
                            productResult.protocolList,
                            jiYiHuaSaveData,
                            onApplyResult
                        )
                    } else if (isMD5Mask == JIYIHUA_MASK) {
                        viewModel.jiYiHuaMaskApply(
                            productResult.applyNo.toString(),
                            originData.phone!!,
                            protocolUrl,
                            jiYiHuaSaveData,
                            onApplyResult
                        )
                    }
                }
            })

        }
    }

    /**
     * 吉用钱包撞库
     */
    fun jYQBMatchSubmit(
        context: Activity,
        originData: ApiOriginData,
        onFail: (String) -> Unit
    ) {
        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
        val jyqbData = FormDataConversionUtils.matchToJYQBQlcData(originData)

        viewModel.jyqbQlcMatch(jyqbData, CallbackManager.getAppStateManager()?.getJYQBPrice()) { resultBean ->
            loadingPopup.dismiss()
            if (resultBean?.code == 200 && resultBean.data.status == 1) {
                jYQBPushDialogShow(context, originData, jyqbData, resultBean.data) {
                    onFail("")
                }
            } else {
                onFail("")
            }
        }
    }

    private fun jYQBPushDialogShow(
        context: Activity,
        originData: ApiOriginData,
        jyqbData: JYQBqlcUserData,
        resultData: JiyongOrderData,
        onFail: (String) -> Unit
    ) {
        resultData.let {
            jyqbData.md5Mobile = null

            jyqbData.orderNo = resultData.orderNo
            jyqbData.idCard = originData.id_card
            jyqbData.realName = originData.real_name
            jyqbData.mobile = originData.phone

            val agreementDialog = JYQBAgreementDialog(context)
            agreementDialog.setAgreementtData(resultData)
            val popup = XPopup.Builder(context)
                .hasShadowBg(true)
                .moveUpToKeyboard(false)
                .isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .enableDrag(false)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(agreementDialog)
                .show()

            agreementDialog.setXieyiDialogClick(object :
                JYQBAgreementDialog.IXieyiDialogClick {
                override fun agreementClick() {
                    viewModel.jyqbQlcApply(jyqbData) { pushResult ->
                        if (pushResult?.code == 200 && pushResult.data.status == 1) {
                            //---进件成功---上报本部服务器---
                            originData.partner_id =
                                BaseApp.context.storeid.toInt()
                            originData.price =  CallbackManager.getAppStateManager()?.getJYQBPrice()
                            originData.channel_id = Constants.SHRIMP_JIYONGQIANBAO.toString()
                            viewModel.qlcPushReport(originData)

                            goSuccessActivity(context)
                        } else {
                            popup.dismiss()
                            //进件失败--走其它的合作方
                            onFail(pushResult?.msg ?: "")
                        }
                    }
                }
            })
        }

    }


    //    走的小鱼的撞库(微银和龙焱)
    private fun fishMatchPushDialogShow(
        context: Activity,
        channel_code: Int,
        originData: ApiOriginData,
        fishSaveData: FishMatchSaveData,
        resultBean: FishMatchResult,
        onFail: (String) -> Unit
    ) {
        resultBean.let {
            // -- 替换个人信息授权书--
            val encodedName = Utils.maskName(originData.real_name)
            val encodedPhone = Utils.hidePhoneNumber(originData.phone)
            fishSaveData.channel_id = channel_code
            fishSaveData.channel_match_no = resultBean.channel_match_no ?: ""
            fishSaveData.auth_url =
                "https://www.ruihe.info/weiyin_auth_agreement.html?name=$encodedName&mobile=$encodedPhone&productname=${resultBean.product_name + "," + resultBean.company_name}&date=${RepayDateUtils.getCurrentFormatDate()}"
            resultBean.protocol_list?.forEachIndexed { index, protocol ->
                if (protocol.name.contains("个人信息授权")) {
                    protocol.url = fishSaveData.auth_url!!
                }
            }

            // -- --
            val agreementDialog = FishMatchAgreementDialog(context)
            agreementDialog.setAgreementtData(resultBean)
            val popup = XPopup.Builder(context)
                .hasShadowBg(true)
                .moveUpToKeyboard(false)
                .isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .enableDrag(false)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(agreementDialog)
                .show()

            agreementDialog.setXieyiDialogClick(object :
                FishMatchAgreementDialog.IXieyiDialogClick {
                override fun agreementClick() {
                    viewModel.fishWyLyPush(channel_code, fishSaveData) { it ->
                        if (it?.code == 0 && !it.data?.order_sn.isNullOrEmpty()) {
                            //---进件成功---上报本部服务器---
                            originData.partner_id =
                                BaseApp.context.storeid.toInt()
                            originData.price = resultBean.price.toString().toDouble()
                            originData.channel_id = channel_code.toString()
                            viewModel.qlcPushReport(originData)

                            goSuccessActivity(context)
                        } else {
                            popup.dismiss()
                            //进件失败--走其它的合作方
                            onFail(it?.msg ?: "")
                        }
                    }
                }
            })
        }
    }





    //小福借款进件
    private fun xiaoFuJKPushDialogShow(
        context: Activity,
        originData: ApiOriginData,
        xfjkData: XiaoFuUserData,
        productResult: XiaoFuProduct,
        onFail: (String) -> Unit
    ) {
        productResult.let {
            //显示Dialog
            val agreementDialog = XiaoFuJKAgreementDialog(context)
            agreementDialog.setAgreementtData(productResult)

            val popup = XPopup.Builder(context)
                .hasShadowBg(true)
                .moveUpToKeyboard(false)
                .isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .enableDrag(false)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(agreementDialog)
                .show()
            agreementDialog.setXieyiDialogClick(object :
                XiaoFuJKAgreementDialog.IXieyiDialogClick {
                override fun agreementClick() {
                    //同意后,申请进件
                    xfjkData.phone = originData.phone
                    xfjkData.name = originData.real_name
                    viewModel.xiaoFuApply(
                        productResult.id,
                        productResult.agreement_list,
                        xfjkData
                    ) { pushResult ->
                        if (null != pushResult && pushResult.code == 200) {
                            //---进件成功---上报本部服务器---
                            originData.partner_id =
                                BaseApp.context.storeid.toInt()
                            originData.price = productResult.channel_settlement_price
                            originData.channel_id = Constants.SHRIMP_XIAOFU.toString()
                            viewModel.qlcPushReport(originData)

                            goSuccessActivity(context)
                        } else {
                            onFail(pushResult?.msg ?: "")
                        }
                    }
                }
            })

        }
    }


    //微融宝进件接口
    private fun weiRongBaoPushDialogShow(
        context: Activity,
        originData: ApiOriginData,
        wrbData: WrbSaveData,
        wrbResultBean: WrbFormResultBean,
        onFail: (String) -> Unit
    ) {
        wrbResultBean.let { it ->
            val agreementDialog = WrbAgreementDialog(context)

            val agreeProtocol =
                "https://www.ruihe.info/wrb_auth_agreement.html?name=${originData.real_name}&mobile=${originData.phone}&date=${DateUtil.getCurrentDateOne()}"
            it.agreements[2].agreementUrl = agreeProtocol
            agreementDialog.setAgreementtData(it)


            val popup = XPopup.Builder(context)
                .hasShadowBg(true)
                .moveUpToKeyboard(false)
                .isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .enableDrag(false)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(agreementDialog)
                .show()
            agreementDialog.setXieyiDialogClick(object :
                WrbAgreementDialog.IXieyiDialogClick {
                override fun agreementClick() {
                    viewModel.wrbApplyForm(
                        agreeProtocol,
                        originData.real_name!!,
                        it.orderId ?: "",
                        wrbData
                    ) { it ->
                        if (it?.code == 200) {
                            //---进件成功---上报本部服务器---
                            originData.partner_id =
                                BaseApp.context.storeid.toInt()
                            originData.price = wrbResultBean.price
                            originData.channel_id = Constants.SHRIMP_WRB.toString()
                            viewModel.qlcPushReport(originData)

                            goSuccessActivity(context)
                        } else {
                            popup.dismiss()
                            //进件失败--走其它的合作方
                            onFail(it?.msg ?: "")
                        }
                    }

                }
            })
        }

    }


    /**
     * 智享贷进件接口
     */
    private fun zxdPushDialogShow(
        context: Activity,
        zxd_orderId: String,
        originData: ApiOriginData,
        zxdData: ZxdAPISaveData,
        zxdFormResultBean: ZxdNewResultProduceBean,
        onFail: (String) -> Unit
    ) {
        zxdFormResultBean.let { it ->
            zxdData.phone = originData.phone
            zxdData.id_card = originData.id_card
            zxdData.real_name = originData.real_name

            val agreementDialog = ZXDAgreementDialog(context)
            agreementDialog.setAgreementtData(it)
            val popup = XPopup.Builder(context)
                .hasShadowBg(true)
                .moveUpToKeyboard(false)
                .isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .enableDrag(false)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(agreementDialog)
                .show()

            agreementDialog.setXieyiDialogClick(object :
                ZXDAgreementDialog.IXieyiDialogClick {
                override fun agreementClick() {
                    viewModel.zxdNewApply(zxd_orderId, zxdData) { it ->
                        if (it?.code == 200) {
                            //---进件成功---上报本部服务器---
                            originData.partner_id =
                                BaseApp.context.storeid.toInt()
                            originData.price = zxdFormResultBean.price
                            originData.channel_id = Constants.SHRIMP_ZXD.toString()
                            viewModel.qlcPushReport(originData)

                            goSuccessActivity(context)
                        } else {
                            popup.dismiss()
                            //进件失败--走其它的合作方
                            onFail("")
                        }
                    }

                }
            })
        }
    }


    /**
     * 天下分期表单
     */
    fun txfqFormSubmit(context: Activity, txfq_city_id: Int, data: ApiOriginData) {
        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()

        val txfqData = FormDataConversionUtils.matchToTXFQData(data)
        txfqData.cityId = txfq_city_id
        LogUtils.e("天下分期表单数据: ${Gson().toJson(txfqData)}")

        viewModel.txfqApplySubmit(txfqData, success = { orderData ->
            CallbackManager.getAppStateManager()?.setFormSubmitted(true)
            viewModel.reportPointRequest(5)
            if (orderData.hasPushSuccess == 1) {
                if (orderData.agreements.defaultAgreements.isNotEmpty()) {
                    val agreementDialog = TXFQAgreementDialog(context)
                    agreementDialog.setAgreementtData(orderData)

                    val popup = XPopup.Builder(context)
                        .hasShadowBg(true)
                        .moveUpToKeyboard(false)
                        .isViewMode(true)
                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .enableDrag(false)
                        .dismissOnTouchOutside(false)
                        .asCustom(agreementDialog)
                        .show()

                    agreementDialog.setXieyiDialogClick(object :
                        TXFQAgreementDialog.IXieyiDialogClick {
                        override fun agreementClick() {
                            if (orderData.apiReqType == 1) {
                                viewModel.txfqPushApply(
                                    orderData.applyId ?: "",
                                    orderData.productId ?: ""
                                ) { resultData ->
                                    if (resultData.code == 200) {
                                        goSuccessActivity(context)
                                    } else {
                                        if (popup.isShow) {
                                            popup.dismiss()
                                        }
                                    }
                                }
                            } else {
                                viewModel.txfqPushApply2JQ8(
                                    orderData.jqbApplyId ?: "",
                                    orderData.jqbProductId ?: ""
                                ) { resultData ->
                                    if (resultData.code == 200) {
                                        goSuccessActivity(context)
                                    } else {
                                        if (popup.isShow) {
                                            popup.dismiss()
                                        }
                                    }
                                }
                            }
                        }
                    })
                } else {
                    goSuccessActivity(context)
                }
            } else {
                goSuccessActivity(context)
            }
            loadingPopup.dismiss()
        }, onFail = {
            loadingPopup.dismiss()
        })
    }

    /**
     * 魔力的表单提交
     */
    fun moliFormSubmit(context: Activity, current_district_id: String, data: ApiOriginData) {
        val loadingPopup = XPopup.Builder(context)
            .asLoading("正在加载中").show()

        val moliData = FormDataConversionUtils.matchToMoLiData(data)
        moliData.current_district_id = current_district_id
        LogUtils.e("魔力表单数据: ${Gson().toJson(moliData)}")
        //提交表单
        viewModel.moliSubmitForm(moliData) { result ->
            CallbackManager.getAppStateManager()?.setFormSubmitted(true)

            loadingPopup.dismiss()
            if (null != result.match_info.product_info && !result.match_info.agreement_list.isNullOrEmpty()) {
                viewModel.reportPointRequest(5)

                //获取协议
                viewModel.protocolGet(
                    result.match_info.agreement_list[0].code,
                    result.form_id,
                    result.match_info.product_id.toString()
                ) { protocol ->

                    //匹配机构
                    val agreementDialog = MoLiAgreementDialog(context)
                    agreementDialog.setAgreementtData(
                        protocol.content,
                        result.match_info.product_info
                    )

                    XPopup.Builder(context)
                        .hasShadowBg(true)
                        .moveUpToKeyboard(false)
                        .isViewMode(true)
                        .isDestroyOnDismiss(true)
                        .enableDrag(false)
                        .dismissOnTouchOutside(false)
                        .asCustom(agreementDialog)
                        .show()

                    agreementDialog.setXieyiDialogClick(object :
                        MoLiAgreementDialog.IXieyiDialogClick {
                        override fun agreementClick() {
                            //同意激活 - 推送
                            viewModel.moliConfirm(result.match_info.step_id.toString()) {
                                if (it.match_info.skip_type == 3) {
                                    val intent = Intent(context, CommonWebViewActivity::class.java)
                                    intent.putExtra("webUrl", it.match_info.redirect_url)
                                    context.startActivity(intent)
                                    context.setResult(RESULT_OK)
                                    context.finish()

                                } else {
                                    viewModel.reportPointRequest(5)
                                    CallbackManager.getAppStateManager()?.setFormSubmitted(true)
                                    goSuccessActivity(context)
                                }
                            }
                        }
                    })

                }
            } else {
                viewModel.reportPointRequest(5)
                CallbackManager.getAppStateManager()?.setFormSubmitted(true)
                goSuccessActivity(context)
            }
        }
    }

    /**
     * 笙融的表单提交
     */
    fun shengRongFormSubmit(context: Activity, data: ApiOriginData) {
        val srData = FormDataConversionUtils.matchToShengRongData(data)
        viewModel.getWmSubmit(srData) {
            CallbackManager.getAppStateManager()?.setFormSubmitted(true)
            viewModel.getWmCheckInto { srUserData ->
                //是否有勾选协议
                val products = srUserData.products
                if (!products.isNullOrEmpty()) {
                    val agreementDialog = SRAgreementDialog(context)
                    agreementDialog.setAgreementtData(products[0])

                    val popup = XPopup.Builder(context)
                        .hasShadowBg(true)
                        .moveUpToKeyboard(false)
                        .isViewMode(true)
                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .enableDrag(false)
                        .dismissOnTouchOutside(false)
                        .asCustom(agreementDialog)
                        .show()

                    agreementDialog.setXieyiDialogClick(object :
                        SRAgreementDialog.IXieyiDialogClick {
                        override fun agreementClick() {
                            viewModel.agreement(products[0].id.toString())
                            viewModel.wMapply(products[0].id.toString()) { resultData ->

                                if (resultData.code == 200) {
                                    goSuccessActivity(context)

                                } else {
                                    if (popup.isShow) {
                                        popup.dismiss()
                                    }
                                }
                            }
                        }

                    })
                } else {
                    goSuccessActivity(context)
                }
            }
            viewModel.reportPointRequest(5)
            CallbackManager.getAppStateManager()?.setFormSubmitted(true)
        }
    }


    /**
     * 源小花表单提交
     */
    fun yxhFormSubmit(
        zhongValue: String,
        idCard: String,
        loan_amount: String,
        yxhCity: String,
        context: Activity, data: ApiOriginData
    ) {
        val loadingPopup = XPopup.Builder(context)
            .asLoading("正在加载中").show()

        val yxhResultBundle = FormDataConversionUtils.matchToYXHData(data)

        viewModel.yxhIdent(
            zhongValue,
            idCard
        ) { result ->

            CallbackManager.getAppStateManager()?.setFormSubmitted(true)
            if (null != result) {
                if (result.errcode == 200) {
                    if (result.data.result == 1) {  //1.认证成功 0.认证失败
                        viewModel.yxhFormCommit(
                            Gson().toJson(yxhResultBundle.chooseContentMap),
                            Gson().toJson(yxhResultBundle.chooseIdMap),
                            loan_amount,
                            yxhCity
                        ) {

                            val loadingPopup = XPopup.Builder(context)
                                .asLoading("正在加载中").show()
                            viewModel.yxhStayMatch { resultData ->
                                Log.d(
                                    "AAAAA",
                                    "onCreate: 协议条目数据 -- > " + JSON.toJSONString(resultData)
                                )
                                Log.d(
                                    "AAAAA",
                                    "onCreate: 协议条目数据 2222 -- > " + JSON.toJSONString(
                                        resultData.planMatchPlatforms
                                    )
                                )

                                //合作方逻辑
                                // 1.如果 planMatchOrgans 不为空，优先展示 planMatchOrgans 中的第一个产品
                                //2.如果 planMatchOrgans为空，则展示planMatchPlatforms中patformType=5或者patformType=1的，第一个产品-----新增判断planMatchPlatforms.resultList这个对像是是否有产品可申请
                                //3.planMatchOrgans && planMatchPlatforms 都为空的情况下，那就是没有匹配到产品

                                val organs = resultData.planMatchOrgans
                                val platforms = resultData.planMatchPlatforms
                                val organsProtocols = organs?.planMatchProtocol

                                if (!organsProtocols.isNullOrEmpty()) {
                                    val agreementDialog = YuanXiaoHuaAgreementDialog(context)
                                    agreementDialog.setAgreementtData(resultData)
                                    XPopup.Builder(context)
                                        .hasShadowBg(true)
                                        .moveUpToKeyboard(false)
                                        .isViewMode(true)
                                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                                        .enableDrag(false)
                                        .dismissOnTouchOutside(false)
                                        .asCustom(agreementDialog)
                                        .show()

                                    agreementDialog.setXieyiDialogClick(object :
                                        YuanXiaoHuaAgreementDialog.IXieyiDialogClick {
                                        override fun agreementClick() {
                                            viewModel.yxhOrgansApply(
                                                organs?.planMatchGoodsId.toString(),
                                                resultData.planMatchToken.toString(),
                                                loan_amount,
                                                "0"
                                            ) { applyResult ->
                                                goSuccessActivity(context)
                                            }
                                        }

                                    })
                                } else if (platforms != null && platforms.resultList.isNotEmpty()) {
                                    if (platforms.platformType == 5 || platforms.platformType == 1) {
                                        val agreementDialog = YuanXiaoHuaAgreementDialog(context)
                                        agreementDialog.setAgreementtData(resultData)
                                        XPopup.Builder(context)
                                            .hasShadowBg(true)
                                            .moveUpToKeyboard(false)
                                            .isViewMode(true)
                                            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                                            .enableDrag(false)
                                            .dismissOnTouchOutside(false)
                                            .asCustom(agreementDialog)
                                            .show()

                                        val list: List<String> =
                                            platforms.resultList.map { it.platformAccountNo.toString() }

                                        agreementDialog.setXieyiDialogClick(object :
                                            YuanXiaoHuaAgreementDialog.IXieyiDialogClick {
                                            override fun agreementClick() {
                                                viewModel.yxhPlatformApply(
                                                    Gson().toJson(list),
                                                    resultData.planMatchToken.toString()
                                                ) { applyResult ->
                                                    goSuccessActivity(context)
                                                }
                                            }
                                        })
                                    } else {
                                        goSuccessActivity(context)
                                    }
                                } else {
                                    goSuccessActivity(context)
                                }
                                loadingPopup.dismiss()
                            }
                            viewModel.reportPointRequest(5)
                            CallbackManager.getAppStateManager()?.setFormSubmitted(true)

                        }
                    } else {
                        Toast.makeText(
                            context,
                            "身份证号码与姓名不匹配，请核对后重新输入。",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                } else {
                    goSuccessActivity(context)
                }
            } else {
                goSuccessActivity(context)
            }
            loadingPopup.dismiss()
        }

    }

    /**
     * 期贷表单提交
     */
    fun qiDaiFormSubmit(context: Activity, data: ApiOriginData) {
        val loadingPopup = XPopup.Builder(context)
            .asLoading("正在加载中").show()
        val qiDaiSaveData = FormDataConversionUtils.matchToQiDaiData(data)

        viewModel.qiDaiMatchCheck(qiDaiSaveData) {
            CallbackManager.getAppStateManager()?.setFormSubmitted(true)
            if (null != it?.data) {
                if (it.code == 200) {
                    viewModel.reportPointRequest(5) //上报很重要
                    if (it.data.productList.isNotEmpty()) {
                        val agreementDialog = QiDaiAgreementDialog(context)
                        agreementDialog.setAgreementtData(it.data.productList[0])

                        val popup = XPopup.Builder(context)
                            .hasShadowBg(true)
                            .moveUpToKeyboard(false)
                            .isViewMode(true)
                            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                            .enableDrag(false)
                            .dismissOnTouchOutside(false)
                            .asCustom(agreementDialog)
                            .show()

                        agreementDialog.setXieyiDialogClick(object :
                            QiDaiAgreementDialog.IXieyiDialogClick {
                            override fun agreementClick() {

                                val idString = it.data.productList.joinToString(
                                    separator = ",",
                                    transform = { it -> it.orgId.toString() })
                                viewModel.qiDaiMatchRegister(
                                    idString, data.phone ?: ""
                                ) { resultData ->
                                    if (resultData.code == 200) {
                                        goSuccessActivity(context)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            resultData.message,
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                        if (popup.isShow) {
                                            popup.dismiss()
                                        }
                                    }
                                }
                            }
                        })
                    }
                } else {
                    goSuccessActivity(context)
                    context.finish()
                }
            } else {
                goSuccessActivity(context)
                context.finish()
            }
            loadingPopup.dismiss()
        }
    }

    /**
     * 吉用钱包表单
     */
    fun jyqbFormSubmit(
        context: Activity,
        data: ApiOriginData,
        zhongValue: String,
        idCard: String,
    ) {
        val jyqbData = FormDataConversionUtils.matchToJiYongQianBaoData(data)

        viewModel.jiYongCheckInfo(
            zhongValue,
            idCard,
            data.phone ?: ""
        ) {
            viewModel.jiYongApplyPost(jyqbData) { orderData ->
                viewModel.reportPointRequest(5)
                CallbackManager.getAppStateManager()?.setFormSubmitted(true)
                CallbackManager.getNavigationCallback()?.navigateToSuccessWithOrder(orderData)
            }
        }
    }


    /**
     * 吉贷表单
     */
    fun jiDaiFormSubmit(context: Activity, data: ApiOriginData) {
        val loadingPopup = XPopup.Builder(context)
            .asLoading("正在加载中").show()
        val jiDaiData = FormDataConversionUtils.matchToJiDaiData(data)
        viewModel.jiDaiSaveUserInfo(jiDaiData) { resultBean ->
            loadingPopup.dismiss()
            CallbackManager.getAppStateManager()?.setFormSubmitted(true)
            if (resultBean.code == 200) {
                viewModel.reportPointRequest(5) //表单上报,重要

                //匹配机构
                viewModel.jiDaiProductList { piPeiResult ->
                    if (piPeiResult.code == 200) {
                        val jiDaiList = piPeiResult.data
                        val idList: List<String?> = jiDaiList.map { it.apiType }
                        if (null != jiDaiList && jiDaiList.isNotEmpty()) {
                            val agreementDialog = JiDaiAgreementDialog(context)
                            agreementDialog.setAgreementtData(jiDaiList[0])

                            val popup = XPopup.Builder(context)
                                .hasShadowBg(true)
                                .moveUpToKeyboard(false)
                                .isViewMode(true)
                                .isDestroyOnDismiss(true)
                                .enableDrag(false)
                                .dismissOnTouchOutside(false)
                                .asCustom(agreementDialog)
                                .show()

                            agreementDialog.setXieyiDialogClick(object :
                                JiDaiAgreementDialog.IXieyiDialogClick {
                                override fun agreementClick() {
                                    viewModel.jiDaiSendProduct(idList) {
                                        goSuccessActivity(context)
                                    }
                                }

                            })
                        } else {
                            goSuccessActivity(context)
                        }

                    } else {
                        //匹配失败
                        goSuccessActivity(context)
                    }

                }

            } else {
                goSuccessActivity(context)
            }
        }

    }

    /**
     * 闪贷喵
     */
    fun shanDaiMiaoFormSubmit(context: Activity, originData: ApiOriginData) {
        val loadingPopup = XPopup.Builder(context)
            .asLoading("正在加载中").show()

        val shandDaiMiaoData = FormDataConversionUtils.matchToShanDaiMiaoData(originData)

        viewModel.shanDaiMiaoMatch(shandDaiMiaoData) { resultBean ->
            loadingPopup.dismiss()

            //请求成功上报本部
            viewModel.reportPointRequest(5) //表单上报,重要

            if (resultBean.code == 200) {
                //这些判断继续走-- 这步结算
                // 判断撞库结果请判断order_no是否为null，不要判断code和msg

                val phoneMd5 = SHA256.encryptMD5(originData.phone ?: "")

                if (!resultBean.data.md5List.isNullOrEmpty()) {
                    if (resultBean.data.md5List!!.contains(phoneMd5)) {
                        goSuccessActivity(context)
                        return@shanDaiMiaoMatch
                    }
                }

                //匹配机构
                val agreementDialog = ShanDaiMiaoAgreementDialog(context)
                agreementDialog.setAgreementtData(resultBean.data)

                XPopup.Builder(context)
                    .hasShadowBg(true)
                    .moveUpToKeyboard(false)
                    .isViewMode(true)
                    .isDestroyOnDismiss(true)
                    .enableDrag(false)
                    .dismissOnTouchOutside(false)
                    .asCustom(agreementDialog)
                    .show()

                agreementDialog.setXieyiDialogClick(object :
                    ShanDaiMiaoAgreementDialog.IXieyiDialogClick {
                    override fun agreementClick() {
                        viewModel.shanDaiMiaoPush(
                            resultBean.data.searchId, resultBean.data.order_no, originData.phone,
                            originData.real_name
                        ) {
                            goSuccessActivity(context)
                        }
                    }
                })
            } else {
                goSuccessActivity(context)
            }
        }

    }


    fun goSuccessActivity(context: Activity) {
        CallbackManager.getAppStateManager()?.setFormSubmitted(true)
        CallbackManager.getNavigationCallback()?.navigateToSuccess()
    }

    //---------------------------全流程撞库,单个调用 注释--------------------------------------

    /**
     * 小福表单
     */
//    fun xiaoFuJKSubmit(
//        context: Activity,
//        originData: ApiOriginData,
//        onFail: (String) -> Unit
//    ) {
//        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
//        val xfjkData = FormDataConversionUtils.matchToXiaoFuData(originData)
//
//        viewModel.xiaoFuJKMatch(xfjkData) { resultBean ->
//            loadingPopup.dismiss()
//            if (null != resultBean && resultBean.code == 200 && !resultBean.data.products.isNullOrEmpty()) {
//                xiaoFuJKPushDialogShow(
//                    context, originData, xfjkData,
//                    resultBean.data.products!![0]
//                ) {
//                    onFail("")
//                }
//            } else {
//                onFail("")
//            }
//        }
//    }


    /**
     * 八戒掩码
     */
//    fun baJieMaskSubmit(
//        context: Activity,
//        originData: ApiOriginData,
//        onFail: (String) -> Unit
//    ) {
//        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
//        val baJieData = FormDataConversionUtils.matchToBaJieData(originData)
//        viewModel.bajieMaskMatch(baJieData) { resultBean ->
//            loadingPopup.dismiss()
//            if (null != resultBean && resultBean.code == 200 && !resultBean.data.isNullOrEmpty()) {
//                val phoneMd5 = SHA256.encryptMD5(originData.phone ?: "")
//                if (!resultBean.data[0].hitPhoneList!!.contains(phoneMd5)) {
//                    baJiePushDialogShow(context, originData, baJieData, resultBean.data[0]) {
//                        onFail("")
//                    }
//                } else {
//                    onFail("")
//                }
//            } else {
//                onFail("")
//            }
//        }
//    }
//
//    /**
//     * 吉意花掩码
//     */
//    fun jiYiHuaMaskSubmit(
//        context: Activity,
//        originData: ApiOriginData,
//        onFail: (String) -> Unit
//    ) {
//        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
//        val jyhData = FormDataConversionUtils.matchToJiYiHuaData(originData)
//        jyhData.phoneMd5 = null
//        jyhData.phoneMask = originData.phone?.take(8)
//        viewModel.jiYiHuaMaskMatch(jyhData) { resultBean ->
//            loadingPopup.dismiss()
//            if (null != resultBean && resultBean.code == 200 && !resultBean.data.protocolList.isNullOrEmpty()) {
//                jiYiHuaPushDialogShow(context, originData, jyhData, resultBean.data, JIYIHUA_MASK) {
//                    onFail("")
//                }
//            } else {
//                onFail("")
//            }
//        }
//
//    }
//
//
//    /**
//     * 吉意花MD5
//     */
//    fun jiYiHuaMD5Submit(
//        context: Activity,
//        originData: ApiOriginData,
//        onFail: (String) -> Unit
//    ) {
//        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
//        val huaData = FormDataConversionUtils.matchToJiYiHuaData(originData)
//
//        viewModel.jiYiHuaMD5Match(huaData) { resultBean ->
//            loadingPopup.dismiss()
//            if (null != resultBean && resultBean.code == 200 && !resultBean.data.protocolList.isNullOrEmpty()) {
//                jiYiHuaPushDialogShow(context, originData, huaData, resultBean.data, JIYIHUA_MD5) {
//                    onFail("")
//                }
//            } else {
//                onFail("")
//            }
//        }
//    }
//
//    /**
//     * 智享贷表单
//     */
//    fun zhiXiangDaiSubmit(
//        context: Activity,
//        zxd_orderId: String,
//        originData: ApiOriginData,
//        onFail: (String) -> Unit
//    ) {
//        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
//        val zxdData = FormDataConversionUtils.matchZXDData(originData)
//        //先撞智享贷
//        viewModel.zxdNewMatch(zxd_orderId, zxdData) { resultBean ->
//            loadingPopup.dismiss()
//            if (null != resultBean && resultBean.code == 200) {  //智享贷撞库成功
//                zxdPushDialogShow(context, zxd_orderId, originData, resultBean.data) {
//                    onFail("")
//                }
//            } else { //智享贷撞库失败--去撞魔力或者天下分期
//                onFail("")
//            }
//        }
//    }
//
//
//
//
//    /**
//     * 微融宝表单
//     */
//    fun weiRongBaoSubmit(
//        context: Activity,
//        originData: ApiOriginData,
//        onFail: (String) -> Unit
//    ) {
//        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
//        val wrbData = FormDataConversionUtils.matchToWeiRongBaoData(originData)
//
//        //撞微融宝
//        viewModel.wrbBeForeMatch(wrbData) { resultBean ->
//            loadingPopup.dismiss()
//            if (null != resultBean && resultBean.code == 200) {
//                weiRongBaoPushDialogShow(context, originData, wrbData, resultBean.data) {
//                    onFail("")
//                }
//            } else {
//                onFail("")
//            }
//        }
//    }
//
//    /**
//     * 龙炎表单
//     */
//    fun longYanSubmit(
//        context: Activity,
//        originData: ApiOriginData,
//        onFail: (String) -> Unit
//    ) {
//        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
//        val longyanData = FormDataConversionUtils.matchToLongYanData(originData)
//
//        viewModel.longYanMatch(longyanData) { resultBean ->
//            loadingPopup.dismiss()
//            if (null != resultBean && resultBean.code == 0) {
//                longYanPushDialogShow(context, originData, longyanData, resultBean.data) {
//                    onFail("")
//                }
//            } else {
//                onFail("")
//            }
//        }
//    }
//    /**
//     * 微银表单
//     */
//    fun weiYinSubmit(
//        context: Activity,
//        originData: ApiOriginData,
//        onFail: (String) -> Unit
//    ) {
//        val loadingPopup = XPopup.Builder(context).asLoading("正在加载中").show()
//        val weiYinData = FormDataConversionUtils.matchToWeiYinData(originData)
//
//        viewModel.weiyinSubmitMatch(weiYinData) { resultBean ->
//            loadingPopup.dismiss()
//            if (null != resultBean && resultBean.code == 0) {
//                weiYinPushDialogShow(context, originData, weiYinData, resultBean.data) {
//                    onFail("")
//                }
//            } else {
//                onFail("")
//            }
//        }
//    }

    //    微银借款推送进件
//    private fun weiYinPushDialogShow(
//        context: Activity,
//        originData: ApiOriginData,
//        weiYinData: WeiYinSaveData,
//        resultBean: WeiYinProductInfo,
//        onFail: (String) -> Unit
//    ) {
//        resultBean.let { it ->
//            // -- 替换个人信息授权书--
//            val encodedName = Utils.maskName(weiYinData.name)
//            val encodedPhone = Utils.hidePhoneNumber(weiYinData.phone)
//            weiYinData.agreeProtocol =
//                "https://www.ruihe.info/weiyin_auth_agreement.html?name=$encodedName&mobile=$encodedPhone&productname=${resultBean.productName + "," + resultBean.companyName}&date=${DateUtils.getCurrentFormatDate()}"
//            it.protocolList?.forEach { protocol ->
//                if (protocol.protocolName.equals("个人信息授权书")) {
//                    protocol.protocolValue = weiYinData.agreeProtocol
//                }
//            }
//            // -- --
//            val agreementDialog = WeiYinAgreementDialog(context)
//            agreementDialog.setAgreementtData(it)
//            val popup = XPopup.Builder(context)
//                .hasShadowBg(true)
//                .moveUpToKeyboard(false)
//                .isViewMode(true)
//                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                .enableDrag(false)
//                .dismissOnTouchOutside(false)
//                .dismissOnBackPressed(false)
//                .asCustom(agreementDialog)
//                .show()
//            agreementDialog.setXieyiDialogClick(object :
//                WeiYinAgreementDialog.IXieyiDialogClick {
//                override fun agreementClick() {
//                    viewModel.weiYinXyApplyPush(weiYinData) { it ->
//                        if (it?.code == 0) {
//                            //---进件成功---上报本部服务器---
//                            originData.partner_id =
//                                BaseApp.context.storeid.toInt()
//                            originData.price = resultBean.price.toString().toDouble()
//                            originData.channel_id = Constants.SHRIMP_WEIYIN.toString()
//                            viewModel.qlcPushReport(originData)
//
//                            goSuccessActivity(context)
//                        } else {
//                            popup.dismiss()
//                            //进件失败--走其它的合作方
//                            onFail(it?.msg ?: "")
//                        }
//                    }
//                }
//            })
//        }
//    }
//
//    //龙炎借款推送进件
//    private fun longYanPushDialogShow(
//        context: Activity,
//        originData: ApiOriginData,
//        longyanData: LongYanSaveData,
//        resultBean: LongYanFormResultBean,
//        onFail: (String) -> Unit
//    ) {
//        resultBean.let { it ->
//            val agreementDialog = LongYanFQAgreementDialog(context)
//            agreementDialog.setAgreementtData(it)
//
//            val popup = XPopup.Builder(context)
//                .hasShadowBg(true)
//                .moveUpToKeyboard(false)
//                .isViewMode(true)
//                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                .enableDrag(false)
//                .dismissOnTouchOutside(false)
//                .dismissOnBackPressed(false)
//                .asCustom(agreementDialog)
//                .show()
//            agreementDialog.setXieyiDialogClick(object :
//                LongYanFQAgreementDialog.IXieyiDialogClick {
//                override fun agreementClick() {
//                    viewModel.longYanApplyForm(
//                        resultBean.order_id,
//                        originData.phone,
//                        originData.real_name,
//                        originData.id_card,
//                        originData.ip
//                    ) { it ->
//                        if (it?.code == 0) {
//                            //---进件成功---上报本部服务器---
//                            originData.partner_id =
//                                BaseApp.context.storeid.toInt()
//                            originData.price = resultBean.price
//                            originData.channel_id = Constants.SHRIMP_LONGYAN.toString()
//                            viewModel.qlcPushReport(originData)
//
//                            goSuccessActivity(context)
//                        } else {
//                            popup.dismiss()
//                            //进件失败--走其它的合作方
//                            onFail(it?.msg ?: "")
//                        }
//                    }
//
//                }
//            })
//        }
//    }


}