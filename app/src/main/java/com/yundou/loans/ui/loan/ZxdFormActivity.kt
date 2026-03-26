package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.ZxdChoiceLayoutBinding
import com.yundou.loans.entity.ProvinceBean
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat

import com.yundou.loans.entity.ZxdFormData
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.utils.*
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.ZxdXieyiDialog

/**
 * 智享贷表单
 */
class ZxdFormActivity : CommonActivity<UserViewModel, ZxdChoiceLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private val choiceData = ZxdFormData()

    private var provinList: List<String> = ArrayList<String>()

    private lateinit var citylist: List<List<String?>>
    private var hasReportedZhongValue = false
    private val multipList: ArrayList<Int> = ArrayList()
    val ip = IpUtils.getDeviceIp(this)

    override fun getLayoutId(): Int {
        return R.layout.zxd_choice_layout
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
        initView()
        initAddressDialog()
        initClickListenr()

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
            .dismissOnBackPressed(false)
            .asCustom(showIdInfoDialog)
            .show()

    }
    private fun initView() {

        mBinding.zongheGroup.setButtons(
            listOf(
                "有房",
                "有车",
                "有公积金",
                "有社保",
                "有花呗",
                "事业单位",
            )
        ) { selectedIndices ->
            Log.d("MultiSelect", "Selected indices: $selectedIndices")
            selectedIndices.forEach {
                val index = it + 1
                multipList.add(index)
            }

            //house_property 房产 0无 2 有房
            if (selectedIndices.contains(0)) {
                choiceData.house_property = 2
            } else {
                choiceData.house_property = 0
            }
            if (selectedIndices.contains(1)) {
                choiceData.car_property = 2
            } else {
                choiceData.car_property = 0
            }
            if (selectedIndices.contains(2)) {
                choiceData.accumulation_fund = 2
            } else {
                choiceData.accumulation_fund = 0
            }
            if (selectedIndices.contains(3)) {
                choiceData.social_security = 2
            } else {
                choiceData.social_security = 0
            }
            if (selectedIndices.contains(4)) {
                choiceData.huabei = 2
            } else {
                choiceData.huabei = 0
            }
            //occupation 职业：1上班族 5事业单位   （不选传1）
            if (selectedIndices.contains(5)) {
                choiceData.occupation = 5
            } else {
                choiceData.occupation = 1
            }


            hideKeyboard()
        }

        mBinding.zhimascoreGroup.setButtons(
            listOf(
                "700以上",
                "650-699",
                "600-649",
                "600以下",
            )
        ) { index, label ->
            choiceData.sesame_score = index
            hideKeyboard()
        }

        mBinding.qixianGroup.setButtons(
            listOf(
                "3个月",
                "6个月",
                "12个月",
                "24个月"
            )
        ) { index, label ->
            when (index) {
                0 -> choiceData.loan_time = 1
                1 -> choiceData.loan_time = 2
                2 -> choiceData.loan_time = 3
                3 -> choiceData.loan_time = 4
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
                    choiceData.loan_amount = 50000.toString()
                }

                1 -> {
                    choiceData.loan_amount = 100000.toString()
                }

                2 -> {
                    choiceData.loan_amount = 150000.toString()
                }

                3 -> {
                    choiceData.loan_amount = 200000.toString()
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
        val dataJson = JsonUtils.readJsonFromAssets(this, "province.json")
        val originList: List<ProvinceBean> = loadRegionsFromAssets(this)
        provinList = originList.map { it.name!! }

        citylist = originList.map { region ->
            region.city?.map { city -> city.name } ?: listOf()
        }

        pvOptions = OptionsPickerBuilder(this) { position1, position2, _, _ ->
            mBinding.address.text =
                provinList.getOrNull(position1) + "-" + citylist.getOrNull(position1)
                    ?.getOrNull(position2)

            choiceData.work_province_name = provinList.getOrNull(position1)
            choiceData.work_city_name = citylist.getOrNull(position1)?.getOrNull(position2)

            choiceData.work_province = originList.getOrNull(position1)?.code.toString()
            choiceData.work_city =
                originList.getOrNull(position1)?.city?.getOrNull(position2)?.code.toString()
            //上报用户数据
            if (!TextUtils.isEmpty(mBinding.tvZhongValue.text.trim().toString())
                && !TextUtils.isEmpty(mBinding.tvIdcard.text.trim().toString())
            ) {
                viewModel.benbuReportUserData(
                    mBinding.tvZhongValue.text.trim().toString(),
                    mBinding.tvIdcard.text.trim().toString(),
                    choiceData.work_province_name,
                    choiceData.work_city_name,
                    ip
                )
            }

        }.build()
        pvOptions?.setTitleText("地址选择")
        pvOptions?.setPicker(provinList, citylist)

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
//            viewModel.reportPointRequest(4)
            choiceData.let {
                it.mobile_system = "Android"
                it.real_name = mBinding.tvZhongValue.text.trim().toString()
                it.id_card = mBinding.tvIdcard.text.trim().toString()
//                it.loan_amount= mBinding.loanAmountEdit.text.trim().toString()
                val age = getAgeAndGender(mBinding.tvIdcard.text.trim().toString()).first
                MmkvUtil.getInstance().encode(Constants.IDCARD_AGE,age)

                if (TextUtils.isEmpty(choiceData.real_name)) {
                    viewModel.defUI.toastEvent.postValue("请输入真实姓名")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.id_card)) {
                    viewModel.defUI.toastEvent.postValue("请输入身份证号码")
                    return@clickNoRepeat
                }
                if (!Utils.isIDCardValid(choiceData.id_card)) {
                    viewModel.defUI.toastEvent.postValue("请输入正确身份证号码")
                    return@clickNoRepeat
                }

                if (choiceData.sesame_score == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }

                if (multipList.isEmpty()) {
                    viewModel.defUI.toastEvent.postValue("请至少选择一项资产信息")
                    return@clickNoRepeat
                }

                if (choiceData.loan_time == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择借款期限")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.loan_amount)) {
                    viewModel.defUI.toastEvent.postValue("请选择贷款额度")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.work_province_name) || TextUtils.equals(
                        choiceData.work_city_name,
                        "请选择城市"
                    )
                ) {
                    viewModel.defUI.toastEvent.postValue("请选择贷款城市")
                    return@clickNoRepeat
                }
                // 上报用户数据
                viewModel.benbuReportUserData(
                    it.real_name,
                    it.id_card,
                    it.work_province_name,
                    it.work_city,
                    ip
                )
            }

            LogUtils.e("智享贷全接口参数: ${Gson().toJson(choiceData)}")


            viewModel.zxdSubmitForm(choiceData) { formResutBean ->
                if (formResutBean.url.isNullOrEmpty()) {
                    val zxdDialog = ZxdXieyiDialog(this, formResutBean)
                    zxdDialog.show()
                    zxdDialog.setXieyiDialogClick(object : ZxdXieyiDialog.IXieyiDialogClick {
                        override fun agreementClick() {
                            successActivity()
                        }
                    })
                } else {
                    val intent = Intent(this, CommonWebViewActivity::class.java)
                    intent.putExtra("webUrl", formResutBean.url)
                    startActivity(intent)
                    finish()
                }

            }

        }

        mBinding.addressRela.clickNoRepeat {
            hideKeyboard()
            pvOptions?.show()
        }

    }

    private fun successActivity() {
        shujuMaidian()
        MyApplication.isForm = true
        startActivity(
            Intent(
                this,
                WmSuccessActivity::class.java
            )
        )
        finish()
    }

    //提交资料数据埋点
    private fun shujuMaidian() {
        viewModel.reportPointRequest(5)
    }



}