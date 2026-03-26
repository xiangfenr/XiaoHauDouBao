package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.AmoliOneFormLayoutBinding
import com.yundou.loans.entity.MoLiFormData
import com.yundou.loans.entity.MoLiProvince
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.IpUtils
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import com.yundou.loans.utils.Utils
import com.yundou.loans.widget.MoLiAgreementDialog
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.clickNoRepeat


/**
 * 魔力 表单1
 */
class MoLiOneFormActivity : CommonActivity<UserViewModel, AmoliOneFormLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private val choiceData = MoLiFormData()

    private var originProvinceList: List<MoLiProvince> = ArrayList()

    private var provinList: List<String> = ArrayList<String>()
    private var citylist: List<List<String>> = ArrayList<ArrayList<String>>()
    private var arearList: List<List<List<String>>> = ArrayList<ArrayList<ArrayList<String>>>()
    private var hasReportedZhongValue = false

    var provinceName: String = ""
    var cityName: String = ""
    val ip = IpUtils.getDeviceIp(this)
    // 1. 定义全局变量，标记用户是否主动选择过第一行数据
    private var isUserActivelySelected = false
    override fun getLayoutId(): Int {
        return R.layout.amoli_one_form_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {

        // choiceData.mobile=MmkvUtil.getInstance().decodeString("loginphone")
        if (BaseApp.context.storeid == Constants.CHANNEL_HUAWEI) {
            showIdInfoDialog()
        }
        initview()
        initAddressDialog()
        initClickListenr()

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

    private fun initview() {

        mBinding.sesameScoreGroup.setButtons(
            listOf(
                "700分以上",
                "650-700分",
                "600-650分"
            )
        ) { index, label ->
            when (index) {
                0 -> {
                    choiceData.zhima_score = "506"
                }

                1 -> {
                    choiceData.zhima_score = "505"
                }

                2 -> {
                    choiceData.zhima_score = "504"
                }
            }
            hideKeyboard()
        }

        mBinding.occupationGroup.setButtons(
            listOf(
                "有京东白条",
                "有社保",
                "有公积金",
                "有车",
                "有房",
                "有营业执照",
                "有信用卡",
                "有商业保单"
            )
        ) { selectedIndices ->
            Log.d("MultiSelect", "Selected indices: $selectedIndices")
            val list: ArrayList<Int> = ArrayList()
            selectedIndices.forEach {
                val index = it + 1
                if (index == 8) {
                    list.add(index + 1)
                } else {
                    list.add(index)
                }
            }
            val params = list.joinToString(separator = ",")
            choiceData.other_assets = params
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

        viewModel.moliTreeV1() {
            //展示省份和城市
            originProvinceList = it
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

            pvOptions = OptionsPickerBuilder(this) { position1, position2, position3, _ ->


                // 核心判断逻辑：
                // - 未主动选择 + 选中第一行 → 提示
                // - 主动选择（无论是否第一行）→ 不提示，直接赋值
                if (!isUserActivelySelected && position1 == 0 && position2 == 0 && position3 == 0) {
                    Toast.makeText(this,"请选择您当前实际工作所在的城市。系统将根据该信息匹配本地合作资金方",
                        Toast.LENGTH_LONG).show()
                    return@OptionsPickerBuilder
                }
                // 标记：用户已主动选择（无论选的是哪一行）
                isUserActivelySelected = true

                mBinding.address.text =
                    provinList.getOrNull(position1) + "-" + citylist.getOrNull(position1)
                        ?.getOrNull(position2) + "-" + arearList.getOrNull(position1)
                        ?.getOrNull(position2)?.getOrNull(position3)

                choiceData.current_district_id =
                    originProvinceList[position1].child[position2].child[position3].code.toString()

                provinceName = provinList.getOrNull(position1)!!
                cityName = citylist.getOrNull(position1)?.getOrNull(position2) ?: ""

                // 标记：用户已主动选择（无论选的是哪一行）
                isUserActivelySelected = true

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

            }.setOptionsSelectChangeListener { options1, options2, options3 ->
                // 滑动监听：只要用户滑动过，就标记为“主动操作”（可选增强逻辑）
                // 补充：即使用户滑动后又滑回第一行，也视为主动选择
                if (options1 != 0 || options2 != 0 || options3 != 0) {
                    isUserActivelySelected = true
                }
            }
                .build()
            pvOptions?.setTitleText("地址选择")
            pvOptions?.setPicker(provinList, citylist, arearList)
        }
        // 3. 关键：如果用户之前主动选择过第一行，打开时默认显示第一行且不提示
        if (isUserActivelySelected) {
            pvOptions?.setSelectOptions(0, 0, 0) // 主动选择过第一行，打开时默认选中
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isUserActivelySelected = false
    }

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

            choiceData.let {
                it.realname = mBinding.tvZhongValue.text.trim().toString()
                it.id_card_no = mBinding.tvIdcard.text.trim().toString()
                val age = getAgeAndGender(mBinding.tvIdcard.text.trim().toString()).first
                MmkvUtil.getInstance().encode(Constants.IDCARD_AGE, age)


                if (TextUtils.isEmpty(choiceData.realname)) {
                    viewModel.defUI.toastEvent.postValue("请输入真实姓名")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.id_card_no)) {
                    viewModel.defUI.toastEvent.postValue("请输入身份证号码")
                    return@clickNoRepeat
                }
                if (!Utils.isIDCardValid(choiceData.id_card_no)) {
                    viewModel.defUI.toastEvent.postValue("请输入正确身份证号码")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.current_district_id)) {
                    viewModel.defUI.toastEvent.postValue("请选择城市和区")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.zhima_score)) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.other_assets)) {
                    viewModel.defUI.toastEvent.postValue("请选择资质情况")
                    return@clickNoRepeat
                }

                // 上报用户数据
                viewModel.benbuReportUserData(
                    it.realname,
                    it.id_card_no,
                    provinceName,
                    cityName,
                    ip
                )
            }

            //提交表单
            viewModel.moliSubmitForm(choiceData) { result ->
                MyApplication.isForm = true
                shujuMaidian()


                if (null != result.match_info.product_info && !result.match_info.agreement_list.isNullOrEmpty()) {
                    //获取协议
                    viewModel.protocolGet(
                        result.match_info.agreement_list[0].code,
                        result.form_id,
                        result.match_info.product_id.toString()
                    ) { protocol ->

                        //匹配机构
                        val agreementDialog = MoLiAgreementDialog(this)
                        agreementDialog.setAgreementtData(protocol.content,result.match_info.product_info)

                        XPopup.Builder(this)
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
                                    shujuMaidian()
                                    if (it.match_info.skip_type == 3) {
                                        val intent = Intent(
                                            this@MoLiOneFormActivity,
                                            CommonWebViewActivity::class.java
                                        )
                                        intent.putExtra("webUrl", it.match_info.redirect_url)
                                        startActivity(intent)
                                        setResult(RESULT_OK)
                                        finish()

                                    } else {

                                        startActivity(
                                            Intent(
                                                this@MoLiOneFormActivity,
                                                WmSuccessActivity::class.java
                                            )
                                        )
                                        setResult(RESULT_OK)
                                        finish()
                                    }
                                }
                            }
                        })

                    }
                } else {
                    shujuMaidian()
                    MyApplication.isForm = true
                    startActivity(Intent(this, WmSuccessActivity::class.java))
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }



        mBinding.addressRela.clickNoRepeat {
            hideKeyboard()
            pvOptions?.show()
        }

    }

    //提交资料数据埋点
    private fun shujuMaidian() {
        viewModel.reportPointRequest(5)
    }



}