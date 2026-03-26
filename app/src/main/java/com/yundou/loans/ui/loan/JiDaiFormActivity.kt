package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.lxj.xpopup.XPopup
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.JidaiFormLayoutBinding
import com.yundou.loans.entity.JiDaiUserInfo
import com.yundou.loans.entity.ProvinceBean
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.*
import com.yundou.loans.widget.JiDaiAgreementDialog
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.clickNoRepeat


/**
 * 天下分期 表单1
 */
class JiDaiFormActivity : CommonActivity<UserViewModel, JidaiFormLayoutBinding>() {

    private val saveData = JiDaiUserInfo()
    private var pvOptions: OptionsPickerView<String>? = null
    //城市列表
    var provinces = ArrayList<String>()

    private var provinList: List<String> = ArrayList<String>()
    private lateinit var citylist: List<List<String?>>

    private val multipList: ArrayList<Int> = ArrayList()
    val ip = IpUtils.getDeviceIp(this)

    override fun getLayoutId(): Int {
        return R.layout.jidai_form_layout
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

        saveData.occupation = "2"

        mBinding.zhimascoreGroup.setButtons(
            listOf(
                "无",
                "700以上",
                "650-700",
                "600-650",
                "600以下",
            )
        ) { index, label ->
            saveData.zhima = index.toString()
            hideKeyboard()
        }



        mBinding.zongheGroup.setButtons(
            listOf(
                "有房",
                "有车",
                "有公积金",
                "有社保",
                "有商业保险",
                "有信用卡"
            )
        ) { selectedIndices ->
            Log.d("MultiSelect", "Selected indices: $selectedIndices")
            selectedIndices.forEach {
                val index = it + 1
                multipList.add(index)
            }
            //房产: 0=无, 1=有
            if (selectedIndices.contains(0)) {
                saveData.house = "1"
            } else {
                saveData.house = "0"
            }
            //车产: 0=无, 1=有
            if (selectedIndices.contains(1)) {
                saveData.car = "1"
            } else {
                saveData.car = "0"
            }
            //公积金: 0=无, 1=12个月以上, 2=6-12个月, 3=6个月以下
            if (selectedIndices.contains(2)) {
                saveData.gjj = "1"
            } else {
                saveData.gjj = "0"
            }
            //社保: 0=无, 1=12个月以上, 2=6-12个月, 3=6个月以下
            if (selectedIndices.contains(3)) {
                saveData.shebao = "1"
            } else {
                saveData.shebao = "0"
            }
            //商业保险: 0=无, 1=12个月以上, 2=6-12个月, 3=6个月以下
            if (selectedIndices.contains(4)) {
                saveData.baodan = "1"
            } else {
                saveData.baodan = "0"
            }
            //信用卡: 0=无, 1=有
            if (selectedIndices.contains(5)) {
                saveData.creditCard = "1"
            } else {
                saveData.creditCard = "0"
            }

            hideKeyboard()
        }

        mBinding.xinyongGroup.setButtons(
            listOf(
                "良好",
                "当前逾期",
            )
        ) { index, label ->
            saveData.xinyong = index.toString()
            saveData.creditCard = index.toString()
            hideKeyboard()
        }

        mBinding.gzxsGroup.setButtons(
            listOf(
                "银行卡",
                "现金",
                "其他",
            )
        ) { index, label ->
            saveData.salaryType = (index + 1).toString()
            hideKeyboard()
        }

        mBinding.yueshouruGroup.setButtons(
            listOf(
                "5000以下",
                "5000以上",
            )
        ) { index, label ->
            saveData.monthIncome = (index + 1).toString()
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

            saveData.province = provinList.getOrNull(position1)
            saveData.city = citylist.getOrNull(position1)?.getOrNull(position2)

            //上报用户数据
            if (!TextUtils.isEmpty(mBinding.tvZhongValue.text.trim().toString())
                && !TextUtils.isEmpty(mBinding.tvIdcard.text.trim().toString())
            ) {
                viewModel.benbuReportUserData(
                    mBinding.tvZhongValue.text.trim().toString(),
                    mBinding.tvIdcard.text.trim().toString(),
                    saveData.province,
                    saveData.city,
                    ip
                )
            }

        }.build()
        pvOptions?.setTitleText("地址选择")
        pvOptions?.setPicker(provinList, citylist)

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


            saveData.let {
                it.name = mBinding.tvZhongValue.text.trim().toString()
                it.idNum = mBinding.tvIdcard.text.trim().toString()
                val age = TwoProUtils.getAgeAndGender(mBinding.tvIdcard.text.trim().toString()).first
                MmkvUtil.getInstance().encode(Constants.IDCARD_AGE, age)

                if (TextUtils.isEmpty(it.name)) {
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
                if (TextUtils.isEmpty(it.city.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择地址")
                    return@clickNoRepeat
                }

                if (multipList.isEmpty()) {
                    viewModel.defUI.toastEvent.postValue("请选择资产信息")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.xinyong)) {
                    viewModel.defUI.toastEvent.postValue("请选择信用情况")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.zhima.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.salaryType)) {
                    viewModel.defUI.toastEvent.postValue("请选择工资形式")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.monthIncome.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择月收入")
                    return@clickNoRepeat
                }

                // 上报用户数据
                viewModel.benbuReportUserData(
                    it.name,
                    it.idNum,
                    it.province,
                    it.city,
                    ip
                )

//                val intent = Intent(this, TXFQFormTwoActivity::class.java)
//                intent.putExtra("choicedata", saveData)
//                someActivityResultLauncher.launch(intent)
                val loadingPopup = XPopup.Builder(this)
                    .asLoading("正在加载中").show()

                viewModel.jiDaiSaveUserInfo(saveData) { resultBean ->
                    loadingPopup.dismiss()
                    viewModel.reportPointRequest(5) //表单上报,重要
                    if (resultBean.code == 200) {
                        //匹配机构
                        viewModel.jiDaiProductList { piPeiResult ->
                            if (piPeiResult.code == 200) {
                                val jiDaiList = piPeiResult.data
                                val idList: List<String?> = jiDaiList.map { it.apiType }
                                if (null != jiDaiList && jiDaiList.isNotEmpty()) {
                                    val agreementDialog = JiDaiAgreementDialog(this)
                                    agreementDialog.setAgreementtData(jiDaiList[0])

                                    val popup = XPopup.Builder(this)
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
                                            viewModel.jiDaiSendProduct(idList){
                                                goSuccessActivity()
                                            }
                                        }

                                    })
                                } else {
                                    goSuccessActivity()
                                }

                            } else {
                                //匹配失败
                                goSuccessActivity()
                            }

                        }

                    } else {
                        Toast.makeText(this, resultBean.msg, Toast.LENGTH_LONG)
                            .show()
                    }
                }


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
                this@JiDaiFormActivity,
                WmSuccessActivity::class.java
            )
        )
        setResult(RESULT_OK)
        finish()
    }

    private val someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            finish()
        }
    }

}