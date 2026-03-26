package com.yundou.loans.ui

import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.callback.CallbackFactoryManager
import com.yundou.loans.callback.CallbackManager
import com.yundou.loans.coreui.R
import com.yundou.loans.coreui.databinding.ZxdFormOneLayoutBinding
import com.yundou.loans.entity.ApiOriginData
import com.yundou.loans.entity.Region
import com.yundou.loans.model.QuanLiuChengFormViewModel
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.ChinaAdministrativeDivisions
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.IpUtils
import com.yundou.loans.utils.LogUtils
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.NameValidator
import com.yundou.loans.utils.SHA256
import com.yundou.loans.utils.TwoProUtils
import com.yundou.loans.utils.Utils
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.clickNoRepeat

/**
 *
 *  智享贷API全流程采量  表单1
 *  逻辑:
 *    不管合作方是谁, 先走智享贷的接口(注意异常404或500的情况), 再走合作方
 */

class QuanLiuChengFormLoanActivity : CommonActivity<UserViewModel, ZxdFormOneLayoutBinding>() {

    private val saveData = ApiOriginData()

    //天下分期--城市列表
    private val provinceList = mutableListOf<Region>() // 一级：省份名
    private val cityList = mutableListOf<List<Region>>() // 二级：每个省份对应的城市列表
    private var txfqpvOptions: OptionsPickerView<Region>? = null
    private var txfq_city_id = 0

    //魔力--城市列表
    private var molipvOptions: OptionsPickerView<String>? = null
    private var provinList: List<String> = kotlin.collections.ArrayList<String>()
    private var citylist: List<List<String>> = kotlin.collections.ArrayList<ArrayList<String>>()
    private var arearList: List<List<List<String>>> =
        kotlin.collections.ArrayList<ArrayList<ArrayList<String>>>()
    private var current_district_id: String = ""
    private var yxhCity = ""
    private val multipList: ArrayList<Int> = ArrayList()

    // 1. 定义全局变量，标记用户是否主动选择过第一行数据
    private var isUserActivelySelected = false

    //整合该页面的请求
    private val formViewModel by lazy { QuanLiuChengFormViewModel() }


    override fun getLayoutId(): Int {
        return R.layout.zxd_form_one_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }


    override fun init() {
        // 设置回调（使用工厂模式）
        val factory = CallbackFactoryManager.getFactory()
        if (factory != null) {
            CallbackManager.setNavigationCallback(factory.createNavigationCallback(this))
        }

        if (BaseApp.Companion.context.storeid == Constants.Companion.CHANNEL_HUAWEI) {
            showIdInfoDialog()
        }
        initView()
        initClickListenr()
        initAddressDialog()
        LogUtils.e(
            "获取codeList: ${
                CallbackManager.getAppStateManager()?.getShrimpChannelConcurrency()
            }"
        )
    }


    fun showIdInfoDialog() {
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

    private fun initView() {
        // 获取当前网络 IP（Wi-Fi/移动网络）
        val ip = IpUtils.getDeviceIp(this)
        saveData.ip = ip ?: "172.1.1.1"

        if (MmkvUtil.Companion.getInstance().decodeInt("partner_id") == Constants.Companion.PARTNER_YUANXIAOHUA) {
            viewModel.yxhAddressIp {
                yxhCity = it
            }
        }

        //教育, 婚姻情况 默认一个值
        saveData.education = 3
        saveData.marital_status = 2


        mBinding.zongheGroup.setButtons(
            listOf(
                "有房",
                "有车",
                "有公积金",
                "有社保",
                "有商业保险",
                "企业主",
                "营业执照",
                "花呗",
                "白条"
            )
        ) { selectedIndices ->
            Log.d("MultiSelect", "Selected indices: $selectedIndices")
            selectedIndices.forEach {
                val index = it + 1
                multipList.add(index)
            }

            if (selectedIndices.contains(0)) {
                saveData.house_property = 2
            } else {
                saveData.house_property = 0
            }
            if (selectedIndices.contains(1)) {
                saveData.car_property = 2
            } else {
                saveData.car_property = 0
            }
            if (selectedIndices.contains(2)) {
                saveData.accumulation_fund = 2
            } else {
                saveData.accumulation_fund = 0
            }
            if (selectedIndices.contains(3)) {
                saveData.social_security = 2
            } else {
                saveData.social_security = 0
            }
            if (selectedIndices.contains(4)) {
                saveData.personal_insurance = 2
            } else {
                saveData.personal_insurance = 0
            }
            //企业主
            if (selectedIndices.contains(5)) {
                saveData.qiYeZhu = 1
            } else {
                saveData.qiYeZhu = 0
            }

            if (selectedIndices.contains(6)) {
                saveData.business = 1
            } else {
                saveData.business = 0
            }

            if (selectedIndices.contains(7)) {
                saveData.huabei = 1
            } else {
                saveData.huabei = 0
            }
            if (selectedIndices.contains(8)) {
                saveData.baitiao = 1
            } else {
                saveData.baitiao = 0
            }


            //自定义企业主  0=无  1=企业主   /** 营业执照：0:无 1:有营业执照 */
            /** 职业：0无 1上班族(私企) 2企业主 3自由职业 4国企(公务员) 5事业单位 6其他 */

            if (selectedIndices.contains(5)) {
                saveData.occupation = 2
            } else if (selectedIndices.contains(6)) {
                saveData.occupation = 3
            } else {
                saveData.occupation = 1
            }


            hideKeyboard()
        }

        mBinding.xyqkGroup.setButtons(
            listOf(
                "当前无逾期",
                "当前有逾期"
            )
        ) { index, label ->
            saveData.credit = index + 1
            hideKeyboard()
        }

        mBinding.zhimascoreGroup.setButtons(
            listOf(
                "无",
                "700以上",
                "650-699",
                "600-649",
                "550-599",
                "550以下"
            )
        ) { index, label ->
            saveData.sesame_score = index
            hideKeyboard()
        }

        mBinding.eduGroup.setButtons(
            listOf(
                "1-5万",
                "5-10万",
                "10-15万",
                "15-20万"
            )
        ) { index, label ->
            when (index) {
                0 -> {
                    saveData.loan_amount = 50000
                }

                1 -> {
                    saveData.loan_amount = 100000
                }

                2 -> {
                    saveData.loan_amount = 150000
                }

                3 -> {
                    saveData.loan_amount = 200000
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

        val partner_id = MmkvUtil.Companion.getInstance().decodeInt("partner_id")
        if (partner_id == Constants.Companion.PARTNER_TXFQ || partner_id == Constants.Companion.PARTNER_QIDAI) {
            //展示省份和城市
            viewModel.txfqGetCity(0) {
                provinceList.clear()
                it.forEach { province ->
                    provinceList.add(Region(province.id, province.localName, province.code))
                    cityList.add(emptyList())
                }
                txfqpvOptions = OptionsPickerBuilder(this) { opt1, opt2, _, _ ->
                    val selectedProvince = provinceList[opt1]
                    val selectedCity = cityList[opt1][opt2]

                    // 获取 ID
                    val provinceId = selectedProvince.id
                    txfq_city_id = selectedCity.id

                    mBinding.address.text = selectedProvince.name + "/" + selectedCity.name
                    //智享贷的城市地址和code
                    saveData.city_code = selectedCity.code
                    saveData.city_name = selectedCity.name
                    saveData.province = selectedProvince.name

                    //上报用户数据
                    if (!TextUtils.isEmpty(mBinding.tvZhongValue.text.trim().toString())
                        && !TextUtils.isEmpty(mBinding.tvIdcard.text.trim().toString())
                    ) {
                        viewModel.benbuReportUserData(
                            mBinding.tvZhongValue.text.trim().toString(),
                            mBinding.tvIdcard.text.trim().toString(),
                            saveData.province,
                            saveData.city_name,
                            saveData.ip
                        )
                    }

                }.setTitleText("选择城市")
                    .build<Region>()
                txfqpvOptions?.setPicker(provinceList, cityList)

                it.forEachIndexed { index, txfqCityBean ->
                    loadCityData(txfqCityBean.id, index)
                }
            }
            mBinding.addressRela.clickNoRepeat {
                hideKeyboard()
                txfqpvOptions?.show()
            }
        } else {  //显示魔力城市地址
            viewModel.moliTreeV1() {
                //展示省份和城市
                val originProvinceList = it
                provinList = it.map { it.name }

                citylist = it.map { region ->
                    region.child?.map { city -> city.name } ?: listOf()
                }

                arearList = it.map { region ->
                    region.child.map { city ->
                        city.child.map { area ->
                            area.name
                        }
                    }
                }

                molipvOptions = OptionsPickerBuilder(this) { position1, position2, position3, _ ->

                    // 核心判断逻辑：
                    // - 未主动选择 + 选中第一行 → 提示
                    // - 主动选择（无论是否第一行）→ 不提示，直接赋值
                    if (!isUserActivelySelected && position1 == 0 && position2 == 0 && position3 == 0) {
                        Toast.makeText(
                            this,
                            "请选择您当前实际工作所在的城市。系统将根据该信息匹配本地合作资金方",
                            Toast.LENGTH_LONG
                        ).show()
                        return@OptionsPickerBuilder
                    }
                    // 标记：用户已主动选择（无论选的是哪一行）
                    isUserActivelySelected = true

                    mBinding.address.text =
                        provinList.getOrNull(position1) + "-" + citylist.getOrNull(position1)
                            ?.getOrNull(position2) + "-" + arearList.getOrNull(position1)
                            ?.getOrNull(position2)?.getOrNull(position3)

                    current_district_id =
                        originProvinceList[position1].child[position2].child[position3].code.toString()

                    //智享贷的城市地址和code
                    saveData.city_code =
                        originProvinceList[position1].child[position2].code.toString()
                    saveData.cityNoShi = originProvinceList[position1].child[position2].name
                    //处理城市名称 加市
                    val cityName =
                        ChinaAdministrativeDivisions.formatRegionName(originProvinceList[position1].child[position2].name)
                    saveData.city_name = cityName
                    saveData.province = provinList.getOrNull(position1)
                    LogUtils.e("城市名称: ${cityName}")

                    //上报用户数据
                    if (!TextUtils.isEmpty(mBinding.tvZhongValue.text.trim().toString())
                        && !TextUtils.isEmpty(mBinding.tvIdcard.text.trim().toString())
                    ) {
                        viewModel.benbuReportUserData(
                            mBinding.tvZhongValue.text.trim().toString(),
                            mBinding.tvIdcard.text.trim().toString(),
                            saveData.province,
                            saveData.city_name,
                            saveData.ip
                        )
                    }
                }.setOptionsSelectChangeListener { options1, options2, options3 ->
                    // 滑动监听：只要用户滑动过，就标记为“主动操作”（可选增强逻辑）
                    // 补充：即使用户滑动后又滑回第一行，也视为主动选择
                    if (options1 != 0 || options2 != 0 || options3 != 0) {
                        isUserActivelySelected = true
                    }
                }
                    .build()
                molipvOptions?.setTitleText("地址选择")
                molipvOptions?.setPicker(provinList, citylist, arearList)
            }
            // 3. 关键：如果用户之前主动选择过第一行，打开时默认显示第一行且不提示
            if (isUserActivelySelected) {
                molipvOptions?.setSelectOptions(0, 0, 0) // 主动选择过第一行，打开时默认选中
            }

            mBinding.addressRela.clickNoRepeat {
                hideKeyboard()
                molipvOptions?.show()
            }
        }


    }

    //天下分期的子级地址加载
    private fun loadCityData(provinceID: Int, provinceIndex: Int) {
        viewModel.txfqGetCity(provinceID) { it ->
            val cityNames: List<Region> = it.map { Region(it.id, it.localName, it.code) }

            cityList[provinceIndex] = cityNames
            txfqpvOptions?.setPicker(provinceList, cityList)
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

        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {

            viewModel.reportPointRequest(4)
            val phone = MmkvUtil.Companion.getInstance().decodeString("loginphone") ?: ""
            saveData.let { it ->
                it.phone = phone
                it.real_name = mBinding.tvZhongValue.text.trim().toString()
                it.id_card = mBinding.tvIdcard.text.trim().toString()
                val sexgender =
                    TwoProUtils.getAgeAndGender(mBinding.tvIdcard.text.trim().toString())
                it.age = sexgender.first
                it.sex = sexgender.second
                MmkvUtil.Companion.getInstance().encode(Constants.Companion.IDCARD_AGE, it.age)

                if (TextUtils.isEmpty(it.real_name)) {
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

                if (TextUtils.isEmpty(it.city_code)) {
                    viewModel.defUI.toastEvent.postValue("请选择工作城市")
                    return@clickNoRepeat
                }

                if (it.occupation == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择职业")
                    return@clickNoRepeat
                }

                if (it.social_security == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择社保")
                    return@clickNoRepeat
                }

                if (it.accumulation_fund == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择公积金")
                    return@clickNoRepeat
                }

                if (it.education == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择教育程度")
                    return@clickNoRepeat
                }

                if (it.marital_status == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择婚姻情况")
                    return@clickNoRepeat
                }

                if (it.personal_insurance == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择商业保险")
                    return@clickNoRepeat
                }

                if (it.car_property == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择车产")
                    return@clickNoRepeat
                }

                if (it.house_property == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择房产")
                    return@clickNoRepeat
                }

                if (it.business == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择营业执照")
                    return@clickNoRepeat
                }

                if (it.credit == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择信用情况")
                    return@clickNoRepeat
                }

                if (it.sesame_score == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻分")
                    return@clickNoRepeat
                }

                if (it.huabei == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择花呗")
                    return@clickNoRepeat
                }

                if (it.baitiao == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择白条")
                    return@clickNoRepeat
                }

                if (it.loan_amount == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择贷款额度")
                    return@clickNoRepeat
                }


                // 上报用户数据到本部
                viewModel.benbuReportUserData(
                    it.real_name,
                    it.id_card,
                    it.province,
                    it.city_name,
                    it.ip
                )


                val originData = it
                LogUtils.e("全流程表单数据-没有MD5: ${Gson().toJson(it)}")
                it.phone_md5 = SHA256.encryptMD5(phone)
                it.id_card_md5 = SHA256.encryptMD5(mBinding.tvIdcard.text.trim().toString())

                val time = (System.currentTimeMillis() / 1000).toString()
                val zxd_orderId = time + phone

                val partner_id = MmkvUtil.Companion.getInstance().decodeInt("partner_id")
                if (partner_id == Constants.Companion.PARTNER_QIDAI) {
                    goOtherForm(originData)
                    return@let
                }

                //同时撞库
                //全流程id包括吉用钱包--先走吉用钱包
                //CallbackManager.getAppStateManager()?.getShrimpChannelConcurrency()
                if (CallbackManager.getAppStateManager()?.getShrimpChannelConcurrency()?.contains(
                        Constants.Companion.SHRIMP_JIYONGQIANBAO) == true) {
                    formViewModel.jYQBMatchSubmit(this, originData) {
                        //吉用钱包撞库失败, 走 全流程的同时撞库
                        formViewModel.allMatchRequest(this, originData, zxd_orderId) {
                            // 走其余合作方
                            goOtherForm(originData)
                        }
                    }
                } else {
                    //同时撞库
                    formViewModel.allMatchRequest(this, originData, zxd_orderId) {
                        // 走其余合作方
                        goOtherForm(originData)
                    }
                }

                //********************测试代码**********

//                formViewModel.baJieMaskSubmit(this, originData) {
//
//                }

            }
        }
    }

    private fun goOtherForm(originData: ApiOriginData) {

        val partner_id = MmkvUtil.Companion.getInstance().decodeInt("partner_id")
        when (partner_id) {
            Constants.Companion.PARTNER_MOLI -> {
                formViewModel.moliFormSubmit(this, current_district_id, originData)
            }

            Constants.Companion.PARTNER_TXFQ -> {
                formViewModel.txfqFormSubmit(this, txfq_city_id, originData)
            }

            Constants.Companion.PARTNER_SHENGR -> {
                formViewModel.shengRongFormSubmit(this, originData)
            }

            Constants.Companion.PARTNER_YUANXIAOHUA -> {
                formViewModel.yxhFormSubmit(
                    mBinding.tvZhongValue.text.trim().toString(),
                    mBinding.tvIdcard.text.trim().toString(),
                    saveData.loan_amount.toString(),
                    yxhCity,
                    this,
                    originData
                )
            }

            Constants.Companion.PARTNER_QIDAI -> {
                formViewModel.qiDaiFormSubmit(this, originData)
            }

            Constants.Companion.PARTNER_JIYONGQB -> {
                formViewModel.jyqbFormSubmit(
                    this, originData,
                    mBinding.tvZhongValue.text.trim().toString(),
                    mBinding.tvIdcard.text.trim().toString()
                )
            }

            Constants.Companion.PARTNER_JIDAI -> {
                formViewModel.jiDaiFormSubmit(this, originData)
            }


            Constants.Companion.PARTNER_SHANDAIMIAO -> {
                formViewModel.shanDaiMiaoFormSubmit(this, originData)
            }
        }
    }


}