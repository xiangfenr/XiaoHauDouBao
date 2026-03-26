package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.YuanxiaohuaFormOneLayoutBinding
import com.yundou.loans.entity.YXHuaSaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.*
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import com.yundou.loans.widget.FormStayDialog
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.YuanXiaoHuaAgreementDialog
import com.yundou.loans.widget.clickNoRepeat


/**
 * 源小花 表单1
 */
class YuanXiaoHuaFormOneActivity : CommonActivity<UserViewModel, YuanxiaohuaFormOneLayoutBinding>() {
    private val saveData = YXHuaSaveData()

    private val agreementDialog by lazy { YuanXiaoHuaAgreementDialog(this) }

    val chooseContentMap =
        HashMap<String, String>()   //芝麻分id=1  房id=3  车id=4  公积金id=5  社保id=19  保险保单id=6  职业身份id=21
    val chooseIdMap = HashMap<String, String>()
    val multipList: ArrayList<Int> = ArrayList()

    val ip = IpUtils.getDeviceIp(this)

    override fun getLayoutId(): Int {
        return R.layout.yuanxiaohua_form_one_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {
        if (BaseApp.context.storeid == Constants.CHANNEL_HUAWEI) {
            showIdInfoDialog()
        }
        initView()
        initClickListenr()

        initAddress()
    }
    fun showIdInfoDialog(){
        val showIdInfoDialog = ShowIdInfoDialog(this)
        XPopup.Builder(this)
            .hasShadowBg(true)
            .moveUpToKeyboard(false)
            .isViewMode(true)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .asCustom(showIdInfoDialog)
            .show()

    }
    private fun initAddress() {
        viewModel.yxhAddressIp {
            mBinding.address.text =  it
            saveData.city = it
        }
    }

    private fun initView() {

        //
        mBinding.zhimaGroup.setButtons( //芝麻分
            listOf(
                "600以下",
                "600-650",
                "650-700",
                "700以上"
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

        mBinding.zongheGroup.setButtons(
            listOf(
                "有公积金",
                "有房",
                "有车",
                "有社保"
            )
        ) { selectedIndices ->
            Log.d("MultiSelect", "Selected indices: $selectedIndices")
            selectedIndices.forEach {
                val index = it + 1
                multipList.add(index)
            }
            if (selectedIndices.contains(0)){
                saveData.reservedFunds.id = 1.toString()
                chooseContentMap["5"] = "缴纳半年以上"
                chooseIdMap["5"] = "147"
                saveData.reservedFunds.id = "147"
            }else{
                saveData.reservedFunds.id = 0.toString()
                chooseContentMap["5"] = "无公积金"
                chooseIdMap["5"] = "149"
                saveData.reservedFunds.id = "149"
            }

            if (selectedIndices.contains(1)){
                chooseContentMap.put("3", "有房可抵押")
                chooseIdMap.put("3", "138")
                saveData.house.id = "138"
            }else{
                chooseContentMap.put("3", "无房")
                chooseIdMap.put("3", "140")
                saveData.house.id = "140"
            }

            if (selectedIndices.contains(2)){
                chooseContentMap.put("4", "有车可抵押")
                chooseIdMap.put("4", "141")
                saveData.car.id = "141"
            }else{
                chooseContentMap.put("4", "无车")
                chooseIdMap.put("4", "142")
                saveData.car.id = "142"
            }

            if (selectedIndices.contains(3)){
                chooseContentMap.put("19", "缴纳半年以上")
                chooseIdMap.put("19", "164")
                saveData.socialSecurity.id = "164"
            }else{
                chooseContentMap.put("19", "无社保")
                chooseIdMap.put("19", "163")
                saveData.socialSecurity.id = "163"
            }

            chooseContentMap.put("6", "缴纳一年以上")
            chooseIdMap.put("6", "144")
            saveData.insurance.id = "144"

            hideKeyboard()
        }



    }


    private var hasReportedZhongValue = false

    private fun initClickListenr() {

        mBinding.tvZhongValue.doAfterTextChanged { text ->
            if (!hasReportedZhongValue && !text.isNullOrEmpty()) {
                hasReportedZhongValue = true
                viewModel.reportPointRequest(3)
            }
        }

        val phone = MmkvUtil.getInstance().decodeString("loginphone") ?: ""
        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {
            viewModel.reportPointRequest(4)

            saveData.let { it ->
                it.realName = mBinding.tvZhongValue.text.trim().toString()
                it.idCard = mBinding.tvIdcard.text.trim().toString()

                val age = getAgeAndGender(mBinding.tvIdcard.text.trim().toString()).first
                MmkvUtil.getInstance().encode(Constants.IDCARD_AGE,age)

                if (TextUtils.isEmpty(it.realName)) {
                    viewModel.defUI.toastEvent.postValue("请输入真实姓名")
                    return@clickNoRepeat
                }
                if (!NameValidator.isValidName(mBinding.tvZhongValue.text.trim().toString())) {
                    viewModel.defUI.toastEvent.postValue("请输入正确姓名")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(mBinding.tvIdcard.text.trim().toString())) {
                    viewModel.defUI.toastEvent.postValue("请输入身份证号码")
                    return@clickNoRepeat
                }
                if (!Utils.isIDCardValid(mBinding.tvIdcard.text.trim().toString())) {
                    viewModel.defUI.toastEvent.postValue("请输入正确身份证号码")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.zhima.id)) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }

                if ( multipList.isEmpty()){
                    viewModel.defUI.toastEvent.postValue("请至少选择一项资产信息")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.revenue.id)) {
                    viewModel.defUI.toastEvent.postValue("请选择月收入")
                    return@clickNoRepeat
                }

                if (!TextUtils.isEmpty(mBinding.eduEdit.text.trim().toString())) {
                    it.loanAmount = mBinding.eduEdit.text.trim().toString().toInt()
                } else {
                    viewModel.defUI.toastEvent.postValue("请输入贷款额度")
                    return@clickNoRepeat
                }


//                if (TextUtils.isEmpty(it.cityId.toString())) {
//                    viewModel.defUI.toastEvent.postValue("请选择工作城市")
//                    return@clickNoRepeat
//                }
                // 上报用户数据
                viewModel.benbuReportUserData(
                    it.realName,
                    it.idCard,
                    "",
                    it.city,
                    ip
                )
                viewModel.yxhIdent(
                    mBinding.tvIdcard.text.trim().toString(),
                    mBinding.tvZhongValue.text.trim().toString()
                ) { result->
                    if (result?.data?.result == 1) {  //1.认证成功 0.认证失败
//                        val intent = Intent(this, YuanXiaoHuaFormTwoActivity::class.java)
//                        intent.putExtra("choicedata", saveData)
//                        someActivityResultLauncher.launch(intent)

                        //提交表单
                        submitForm()
                    } else {
                        viewModel.defUI.toastEvent.postValue("身份证与姓名不匹配, 请核对")
                    }
                }
            }
        }
    }


    private fun submitForm(){
        viewModel.yxhFormCommit(
            Gson().toJson(chooseContentMap),
            Gson().toJson(chooseIdMap),
            mBinding.eduEdit.text.trim().toString(),
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
                val platforms = resultData.planMatchPlatforms

                if (!organsProtocols.isNullOrEmpty()) {
                    val organsNonNull = organs ?: return@yxhStayMatch
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
                            viewModel.yxhOrgansApply(
                                organsNonNull.planMatchGoodsId.toString(),
                                resultData.planMatchToken.toString(),
                                mBinding.eduEdit.text.trim().toString(),
                                "0"
                            ) { applyResult ->
                                startActivity(
                                    Intent(
                                        this@YuanXiaoHuaFormOneActivity,
                                        WmSuccessActivity::class.java
                                    )
                                )
                                setResult(RESULT_OK)
                                finish()
                            }
                        }

                    })
                } else if (platforms != null && platforms.resultList.isNotEmpty()) {
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
                                    startActivity( Intent( this@YuanXiaoHuaFormOneActivity,  WmSuccessActivity::class.java ) )
                                    setResult(RESULT_OK)
                                    finish()
                                }
                            }
                        })
                    } else {
                        startActivity( Intent( this@YuanXiaoHuaFormOneActivity,  WmSuccessActivity::class.java ) )
                        setResult(RESULT_OK)
                        finish()
                    }
                } else {
                    startActivity( Intent( this@YuanXiaoHuaFormOneActivity,  WmSuccessActivity::class.java ) )
                    setResult(RESULT_OK)
                    finish()
                }


            }
            viewModel.reportPointRequest(5)
            MyApplication.isForm = true
        }
    }


    private val someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            finish()
        }
    }


    private var canFinished = false
    override fun onBackPressed() {
        if (canFinished) {
            finish()
        }


        val dialog = FormStayDialog(this)
        val popup = XPopup.Builder(this)
            .hasShadowBg(true)
            .moveUpToKeyboard(false)
            .isViewMode(true)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .asCustom(dialog)
            .show()
        if (dialog.isShow){
            canFinished =true
        }
        dialog.setXieyiDialogClick(object : FormStayDialog.IXieyiDialogClick {
            override fun agreementClick(type: Int) {
                if (type == 0) {
                    canFinished = true
                }
                popup.dismiss()
            }

        })
//        super.onBackPressed()
    }


}