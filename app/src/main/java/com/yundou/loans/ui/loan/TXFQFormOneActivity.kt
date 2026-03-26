package com.yundou.loans.ui.loan

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication

import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.TxfqFormOneLayoutBinding
import com.yundou.loans.entity.Region
import com.yundou.loans.entity.TxfqSaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.*
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.TXFQAgreementDialog
import com.yundou.loans.widget.clickNoRepeat


/**
 * 天下分期 表单1
 */
class TXFQFormOneActivity : CommonActivity<UserViewModel, TxfqFormOneLayoutBinding>() {

    private val saveData = TxfqSaveData()
    private var pvOptions: OptionsPickerView<Region>? = null

    //城市列表
    private val provinceList = mutableListOf<Region>() // 一级：省份名
    private val cityList = mutableListOf<List<Region>>() // 二级：每个省份对应的城市列表
    var provinceName: String = ""
    var cityName: String = ""
    val ip = IpUtils.getDeviceIp(this)


    override fun getLayoutId(): Int {
        return R.layout.txfq_form_one_layout
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
        initData()
        initClickListenr()
        initAddressDialog()

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
    private fun initData() {


        mBinding.zhimascoreGroup.setButtons(
            listOf(
                "700以上",
                "650-700",
                "600-650",
                "600以下",
            )
        ) { index, label ->
            saveData.credit = label
            hideKeyboard()
        }

//        mBinding.yuqiGroup.setButtons(
//            listOf(
//                "征信良好",
//                "无人行征信",
//                "600-650",
//                "600以下",
//            )
//        ) { index, label ->
//            saveData.credit = label
//            hideKeyboard()
//        }


        mBinding.zongheGroup.setButtons(
            listOf(
                "有房",
                "有车",
                "有公积金",
                "有社保",
                "有商业保险",
                "企业主"
            )
        ) { selectedIndices ->
            Log.d("MultiSelect", "Selected indices: $selectedIndices")
            val list: ArrayList<Int> = ArrayList()
            selectedIndices.forEach {
                val index = it + 1
                list.add(index)
            }
            saveData.assets = list
            hideKeyboard()
        }

        mBinding.qixianGroup.setButtons(
            listOf(
                "3期",
                "6期",
                "12期",
                "24期"
            )
        ) { index, label ->
            when (index) {
                0 -> saveData.loanPeriod = 3.toString()
                1 -> saveData.loanPeriod = 6.toString()
                2 -> saveData.loanPeriod = 12.toString()
                3 -> saveData.loanPeriod = 24.toString()
            }

            hideKeyboard()
        }

        mBinding.eduGroup.setButtons(
            listOf(
                "5万",
                "10万",
                "15万",
                "20万"
            )
        ) { index, label ->

            when (index) {
                0 -> {
                    saveData.loanAmount = "50000"
                }

                1 -> {
                    saveData.loanAmount = "100000"
                }

                2 -> {
                    saveData.loanAmount = "200000"
                }

                3 -> {
                    saveData.loanAmount = "300000"
                }
            }
            hideKeyboard()
        }

    }

    private fun initAddressDialog() {

        //关闭输入框
        val view = this.currentFocus
        // 获取输入法管理器
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏输入法
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

        //展示省份和城市
        viewModel.txfqGetCity(0) {
            provinceList.clear()
            it.forEach { province ->
                provinceList.add(Region(province.id, province.localName, province.code))
                cityList.add(emptyList())
            }
            showPicker()
            it.forEachIndexed { index, txfqCityBean ->
                loadCityData(txfqCityBean.id, index)
            }
        }


    }

    private fun loadCityData(provinceID: Int, provinceIndex: Int) {
        viewModel.txfqGetCity(provinceID) {
            val cityNames: List<Region> = it.map { Region(it.id, it.localName, it.code) }

            cityList[provinceIndex] = cityNames
            pvOptions?.setPicker(provinceList, cityList)
        }

    }

    private fun showPicker() {
        pvOptions = OptionsPickerBuilder(this) { opt1, opt2, _, _ ->
            val selectedProvince = provinceList[opt1]
            val selectedCity = cityList[opt1][opt2]

            // 获取 ID
            val provinceId = selectedProvince.id
            val cityId = selectedCity.id

            mBinding.address.text = selectedProvince.name + "/" + selectedCity.name
            saveData.cityId = cityId
            provinceName =  selectedProvince.name
            cityName = selectedCity.name
            //上报用户数据
            if (!TextUtils.isEmpty(mBinding.tvZhongValue.text.trim().toString())
                && !TextUtils.isEmpty(mBinding.tvIdcard.text.trim().toString())
            ) {
                viewModel.benbuReportUserData(
                    mBinding.tvZhongValue.text.trim().toString(),
                    mBinding.tvIdcard.text.trim().toString(),
                    provinceName,
                    cityName,
                    ip
                )
            }
        }.setTitleText("选择城市")
            .build<Region>()
        pvOptions?.setPicker(provinceList, cityList)
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


            //点击提交表单 按钮上报
            viewModel.reportPointRequest(4)

            val list2 = arrayOf(
                "资金周转",
                "日常消费",
                "房屋装修",
                "医疗贷款",
                "旅游贷款",
                "买车贷款",
                "其它"
            )
            saveData.loanPurpose = list2.random()


            saveData.let {
                it.realName = SHA256.RSAEncrypt(
                    Constants.TXFQ_PUBLICKEY,
                    mBinding.tvZhongValue.text.trim().toString()
                )
                it.idCard = SHA256.RSAEncrypt(
                    Constants.TXFQ_PUBLICKEY,
                    mBinding.tvIdcard.text.trim().toString()
                )
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
                if (TextUtils.isEmpty(it.credit.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }
                if (it.assets.isEmpty()) {
                    viewModel.defUI.toastEvent.postValue("请选择资产信息")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.loanPeriod)) {
                    viewModel.defUI.toastEvent.postValue("请选择借款期限")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.loanAmount)) {
                    viewModel.defUI.toastEvent.postValue("请选择贷款额度")
                    return@clickNoRepeat
                }


                if (TextUtils.isEmpty(it.cityId.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择工作城市")
                    return@clickNoRepeat
                }
                // 上报用户数据
                viewModel.benbuReportUserData(
                    it.realName,
                    it.idCard,
                    provinceName,
                    cityName,
                    ip
                )

//                val intent = Intent(this, TXFQFormTwoActivity::class.java)
//                intent.putExtra("choicedata", saveData)
//                someActivityResultLauncher.launch(intent)
                viewModel.txfqApplySubmit(it, success = { orderData ->
                    viewModel.reportPointRequest(5)
                    MyApplication.isForm = true
                    if (orderData.hasPushSuccess == 1) {
                        if (orderData.agreements.defaultAgreements.isNotEmpty()) {
                            val agreementDialog = TXFQAgreementDialog(this)
                            agreementDialog.setAgreementtData(orderData)
                            val popup = XPopup.Builder(this)
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
                                                goSuccessActivity()
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
                                                goSuccessActivity()
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
                            goSuccessActivity()
                        }
                    } else {
                        goSuccessActivity()
                    }
                }, onFail = {

                })


            }
        }


        mBinding.addressRela.clickNoRepeat {
            hideKeyboard()
            pvOptions?.show()
        }
    }

    private fun goSuccessActivity() {
        startActivity(
            Intent(
                this@TXFQFormOneActivity,
                WmSuccessActivity::class.java
            )
        )
        setResult(RESULT_OK)
        finish()
    }

    private val someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

}