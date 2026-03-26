package com.yundou.loans.ui.loan

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.YuanxiaohuaFormTwoLayoutBinding
import com.yundou.loans.entity.YXHuaSaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.YuanXiaoHuaAgreementDialog
import com.yundou.loans.widget.clickNoRepeat
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * 源小花 表单2
 */
class YuanXiaoHuaFormTwoActivity : CommonActivity<UserViewModel, YuanxiaohuaFormTwoLayoutBinding>() {

    private var saveData = YXHuaSaveData()
    private val agreementDialog by lazy { YuanXiaoHuaAgreementDialog(this) }
    val chooseContentMap =
        HashMap<String, String>()   //芝麻分id=1  房id=3  车id=4  公积金id=5  社保id=19  保险保单id=6  职业身份id=21
    val chooseIdMap = HashMap<String, String>()


    override fun getLayoutId(): Int {
        return R.layout.yuanxiaohua_form_two_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {
        initData()
        initClickListenr()
    }

    private fun initData() {
        saveData = intent.getSerializableExtra("choicedata") as YXHuaSaveData


        //
        mBinding.zhimaGroup.setButtons( //芝麻分
            listOf(
                "600分以下",
                "600-650分",
                "650-700分",
                "700分以上"
            )
        ) { index, label ->
            chooseContentMap.put("1", label)
            when (index) {
                0 -> {
                    chooseIdMap.put("1", "68")
                    saveData.zhima.id = "68"
                }

                1 -> {
                    chooseIdMap.put("1", "69")
                    saveData.zhima.id = "69"

                }

                2 -> {
                    chooseIdMap.put("1", "70")
                    saveData.zhima.id = "70"

                }

                3 -> {
                    chooseIdMap.put("1", "71")
                    saveData.zhima.id = "71"

                }
            }
            hideKeyboard()
        }
        mBinding.hourseGroup.setButtons(
            listOf(
                "无房",
                "有房可抵押",
                "有房不抵押"
            )
        ) { index, label ->
            chooseContentMap.put("3", label)
            when (index) {
                0 -> {
                    chooseIdMap.put("3", "140")
                    saveData.house.id = "140"
                }

                1 -> {
                    chooseIdMap.put("3", "138")
                    saveData.house.id = "138"

                }

                2 -> {
                    chooseIdMap.put("3", "139")
                    saveData.house.id = "139"

                }
            }
            hideKeyboard()
        }

        mBinding.carGroup.setButtons(listOf("无车", "有车可抵押", "有车不抵押")) { index, label ->
            chooseContentMap.put("4", label)
            when (index) {
                0 -> {
                    chooseIdMap.put("4", "142")
                    saveData.car.id = "142"
                }

                1 -> {
                    chooseIdMap.put("4", "141")
                    saveData.car.id = "141"

                }

                2 -> {
                    chooseIdMap.put("4", "143")
                    saveData.car.id = "143"

                }
            }
            saveData.car.id = index.toString()
            hideKeyboard()
        }


        mBinding.gongjijinGroup.setButtons(
            listOf(
                "无公积金",
                "缴纳半年以上",
                "缴纳半年以下"
            )
        ) { index, label ->
            chooseContentMap.put("5", label)
            saveData.reservedFunds.id = index.toString()
            when (index) {
                0 -> {
                    chooseIdMap.put("5", "149")
                    saveData.reservedFunds.id = "149"
                }

                1 -> {
                    chooseIdMap.put("5", "147")
                    saveData.reservedFunds.id = "147"

                }

                2 -> {
                    chooseIdMap.put("5", "148")
                    saveData.reservedFunds.id = "148"

                }
            }
            hideKeyboard()
        }
        mBinding.shebaoGroup.setButtons(
            listOf(
                "无社保",
                "缴纳半年以上",
                "缴纳半年以下"
            )
        ) { index, label ->
            chooseContentMap.put("19", label)
            when (index) {
                0 -> {
                    chooseIdMap.put("19", "163")
                    saveData.socialSecurity.id = "163"
                }

                1 -> {
                    chooseIdMap.put("19", "164")
                    saveData.socialSecurity.id = "164"

                }

                2 -> {
                    chooseIdMap.put("19", "164")
                    saveData.socialSecurity.id = "164"

                }
            }
            hideKeyboard()
        }
        mBinding.baodanGroup.setButtons(
            listOf(
                "无保单",
                "缴纳一年以上",
                "缴纳一年以下"
            )
        ) { index, label ->
            chooseContentMap.put("6", label)
            when (index) {
                0 -> {
                    chooseIdMap.put("6", "146")
                    saveData.insurance.id = "146"
                }

                1 -> {
                    chooseIdMap.put("6", "144")
                    saveData.insurance.id = "144"

                }

                2 -> {
                    chooseIdMap.put("6", "145")
                    saveData.insurance.id = "145"

                }
            }
            hideKeyboard()
        }
        mBinding.zhiyeGroup.setButtons(
            listOf(
                "上班族",
                "企业主",
                "个体户",
                "自由职业",
            )
        ) { index, label ->
            chooseContentMap.put("21", label)

            when (index) {
                0 -> {
                    chooseIdMap.put("21", "170")
                    saveData.profession.id = "170"
                }

                1 -> {
                    chooseIdMap.put("21", "171")
                    saveData.profession.id = "171"
                }

                2 -> {
                    chooseIdMap.put("21", "172")
                    saveData.profession.id = "172"
                }

                3 -> {
                    chooseIdMap.put("21", "173")
                    saveData.profession.id = "173"
                }
            }
            hideKeyboard()
        }

        //收入情况
        mBinding.yueshouruGroup.setButtons(listOf("现金收入", "打卡工资")) { index, label ->
            chooseContentMap.put("23", label)
            when (index) {
                0 -> {
                    chooseIdMap.put("23", "179")
                    saveData.revenue.id = "179"
                }

                1 -> {
                    chooseIdMap.put("23", "180")
                    saveData.revenue.id = "180"
                }
            }
            hideKeyboard()
        }

    }

    fun loadJSONFromAsset(context: Context, filename: String): String? {
        val json: String
        json = try {
            val `is`: InputStream = context.getAssets().open(filename)
            val reader = InputStreamReader(`is`, StandardCharsets.UTF_8)
            val buffer = CharArray(`is`.available())
            reader.read(buffer)
            reader.close()
            String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return json
    }

    fun <T> parseJsonWithGson(json: String?, type: Class<T>?): T {
        val gson = Gson()
        return gson.fromJson(json, type)
    }

    private fun initClickListenr() {


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {
            //点击按钮上报
            viewModel.reportPointRequest(4)

            saveData.let {

                if (TextUtils.isEmpty(it.reservedFunds.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择公积金")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.socialSecurity.id)) {
                    viewModel.defUI.toastEvent.postValue("请选择社保")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.profession.id)) {
                    viewModel.defUI.toastEvent.postValue("请选择职业")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.car.id)) {
                    viewModel.defUI.toastEvent.postValue("请选择车产")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.house.id)) {
                    viewModel.defUI.toastEvent.postValue("请选择房产")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.revenue.id)) {
                    viewModel.defUI.toastEvent.postValue("请选择月收入")
                    return@clickNoRepeat
                }

                if (!TextUtils.isEmpty(mBinding.password.text.trim().toString())) {
                    it.loanAmount = mBinding.password.text.trim().toString().toInt()
                } else {
                    viewModel.defUI.toastEvent.postValue("请输入贷款额度")
                    return@clickNoRepeat
                }
                Log.d(
                    "AAAA",
                    "initClickListenr: 选择的数据文本 -- > " + Gson().toJson(chooseContentMap)
                )
                Log.d("AAAA", "initClickListenr: 选择的数据id -- > " + Gson().toJson(chooseIdMap))

                viewModel.yxhFormCommit(
                    Gson().toJson(chooseContentMap),
                    Gson().toJson(chooseIdMap),
                    mBinding.password.text.trim().toString(),
                    saveData.city?:""
                ) {
                    viewModel.yxhStayMatch { resultData ->
                        Log.d(
                            "AAAAA",
                            "onCreate: 协议条目数据 -- > " + JSON.toJSONString(resultData)
                        )
                        Log.d(
                            "AAAAA",
                            "onCreate: 协议条目数据 2222 -- > " + JSON.toJSONString(resultData.planMatchPlatforms)
                        )

                        //合作方逻辑
                        // 1.如果 planMatchOrgans 不为空，优先展示 planMatchOrgans 中的第一个产品
                        //2.如果 planMatchOrgans为空，则展示planMatchPlatforms中patformType=5或者patformType=1的，第一个产品-----新增判断planMatchPlatforms.resultList这个对像是是否有产品可申请
                        //3.planMatchOrgans && planMatchPlatforms 都为空的情况下，那就是没有匹配到产品

                        val organs = resultData.planMatchOrgans
                        val organsProtocols = organs?.planMatchProtocol

                        if (!organsProtocols.isNullOrEmpty()) {
                            agreementDialog.setAgreementtData(resultData)
                            XPopup.Builder(this)
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
                                    val organs = resultData.planMatchOrgans
                                    viewModel.yxhOrgansApply(
                                        organs?.planMatchGoodsId.toString(),
                                        resultData.planMatchToken.toString(),
                                        mBinding.password.text.trim().toString(),
                                        "0"
                                    ) { applyResult ->
                                        startActivity(
                                            Intent(
                                                this@YuanXiaoHuaFormTwoActivity,
                                                WmSuccessActivity::class.java
                                            )
                                        )
                                        setResult(RESULT_OK)
                                        finish()
                                    }
                                }

                            })
                        } else {
                            val platforms = resultData.planMatchPlatforms
                            if (platforms != null && platforms.resultList.isNotEmpty()) {
                                if (platforms.platformType == 5 || platforms.platformType == 1) {
                                agreementDialog.setAgreementtData(resultData)
                                XPopup.Builder(this)
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
                                            resultData.planMatchToken.toString()) { applyResult ->
                                            startActivity( Intent( this@YuanXiaoHuaFormTwoActivity,  WmSuccessActivity::class.java ) )
                                            setResult(RESULT_OK)
                                            finish()
                                        }
                                    }
                                })
                                } else {
                                    startActivity( Intent( this@YuanXiaoHuaFormTwoActivity,  WmSuccessActivity::class.java ) )
                                    setResult(RESULT_OK)
                                    finish()
                                }
                            } else {
                            startActivity( Intent( this@YuanXiaoHuaFormTwoActivity,  WmSuccessActivity::class.java ) )
                            setResult(RESULT_OK)
                            finish()
                            }
                        }


                    }
                    shujuMaidian()
                    MyApplication.isForm = true
                }

            }
        }

    }

    //提交资料数据埋点
    private fun shujuMaidian() {
        //关闭输入框
        val view = this.currentFocus
        // 获取输入法管理器
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏输入法
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
          viewModel.reportPointRequest(5)
    }

}