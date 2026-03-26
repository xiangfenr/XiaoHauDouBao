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
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.JiyongFormOneLayoutBinding
import com.yundou.loans.entity.JIYongSaveData
import com.yundou.loans.entity.ProvinceBean
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.utils.*
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import com.yundou.loans.widget.FormStayDialog
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.clickNoRepeat

/**
 * 吉用钱包 表单1
 */
class JIYQBFormOneActivity : CommonActivity<UserViewModel, JiyongFormOneLayoutBinding>() {

    private val saveData = JIYongSaveData()
    private var pvOptions: OptionsPickerView<String>? = null

    //城市列表
    private var provinList: List<String> = ArrayList<String>()
    private lateinit var citylist: List<List<String?>>

    private var hasReportedZhongValue = false
    private val multipList: ArrayList<Int> = ArrayList()
    var provinceName: String = ""
    var cityName: String = ""
    val ip = IpUtils.getDeviceIp(this)

    override fun getLayoutId(): Int {
        return R.layout.jiyong_form_one_layout
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

    private fun initData() {


        mBinding.zhimascoreGroup.setButtons(
            listOf(
                "700以上",
                "650-700",
                "600-650"
            )
        ) { index, label ->
            saveData.sesame = (index + 1).toString()
            hideKeyboard()
        }


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
            selectedIndices.forEach {
                val index = it + 1
                multipList.add(index)
            }
            if (selectedIndices.contains(0)) {
                saveData.house = 1.toString()
            } else {
                saveData.house = 0.toString()
            }
            if (selectedIndices.contains(1)) {
                saveData.car = 1.toString()
            } else {
                saveData.car = 0.toString()
            }
            if (selectedIndices.contains(2)) {
                saveData.fund = 1.toString()
            } else {
                saveData.fund = 0.toString()
            }
            if (selectedIndices.contains(3)) {
                saveData.salary = 1.toString()
            } else {
                saveData.salary = 0.toString()
            }
            if (selectedIndices.contains(4)) {
                saveData.insurance = 1.toString()
            } else {
                saveData.insurance = 0.toString()
            }
            if (selectedIndices.contains(5)) {
                saveData.owners = 1.toString()
            } else {
                saveData.owners = 0.toString()
            }
            hideKeyboard()
        }

        mBinding.qixianGroup.setButtons(
            listOf(
                "3期",
                "6期",
                "9期",
                "12期",
                "24期",
                "36期"
            )
        ) { index, label ->
            saveData.period = (index + 1).toString()
            hideKeyboard()
        }

        mBinding.useGroup.setButtons(
            listOf(
                "资金周转",
                "日常消费",
                "房屋装修",
                "医疗贷款",
                "旅游贷款",
                "买车贷款",
                "其它"
            )
        ) { index, label ->
            saveData.purpose = (index + 1).toString()
            hideKeyboard()
        }

        mBinding.eduGroup.setButtons(
            listOf(
                "1万-5万",
                "5万-10万",
                "10万-15万",
                "15万-20万"
            )
        ) { index, label ->

            when (index) {
                0 -> {
                    saveData.loanAmount = "20000"
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


            saveData.city = citylist.getOrNull(position1)?.getOrNull(position2)
            saveData.cityCode =
                originList.getOrNull(position1)?.city?.getOrNull(position2)?.code.toString()
            provinceName = provinList.getOrNull(position1) ?: ""
            cityName = citylist.getOrNull(position1)?.getOrNull(position2) ?: ""

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


        }.build()
        pvOptions?.setTitleText("地址选择")
        pvOptions?.setPicker(provinList, citylist)

    }


    private fun initClickListenr() {
        //  val phone ="13205818771"
        val phone = MmkvUtil.getInstance().decodeString("loginphone") ?: ""
        saveData.mobile = phone
        mBinding.tvZhongValue.doAfterTextChanged { text ->
            if (!hasReportedZhongValue && !text.isNullOrEmpty()) {
                hasReportedZhongValue = true
                viewModel.reportPointRequest(3)
            }
        }

        mBinding.tvYinsiZcxy.clickNoRepeat {

            val encodedName = mBinding.tvZhongValue.text.trim().toString()
            val sfz = mBinding.tvIdcard.text.trim().toString()

            val name = nameEncrypt(encodedName)
            val idCardNo = idEncrypt(sfz)
            val intent = Intent(this, CommonWebViewActivity::class.java)
            intent.putExtra(
                "webUrl",
                "${Constants.JIYONG_PROTOCOL1}?name=$name&sfz=$idCardNo"
            )
            startActivity(intent)
            LogUtils.e("姓名和身份证  $name----$idCardNo")

        }

        mBinding.tvShouquanYszc.clickNoRepeat {
            val intent = Intent(this, CommonWebViewActivity::class.java)
            intent.putExtra(
                "webUrl",
                "${Constants.JIYONG_PROTOCOL2}"
            )
            startActivity(intent)
        }

        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {
            viewModel.reportPointRequest(4)
            saveData.let {
                //模拟地址
                //it.cityOfWork = "林芝"
                //it.cityOfWorkCode = "540400"
                it.name = mBinding.tvZhongValue.text.trim().toString()
                it.sfz = mBinding.tvIdcard.text.trim().toString()
                //it.age = mBinding.editAge.text.trim().toString()

                //从身份证号中识别出年龄和性别
                it.age = getAgeAndGender(it.sfz!!).first.toString()
                it.sex = getAgeAndGender(it.sfz!!).second.toString()

                val age = getAgeAndGender(mBinding.tvIdcard.text.trim().toString()).first
                MmkvUtil.getInstance().encode(Constants.IDCARD_AGE, age)

                if (TextUtils.isEmpty(it.name)) {
                    viewModel.defUI.toastEvent.postValue("请输入真实姓名")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.sfz)) {
                    viewModel.defUI.toastEvent.postValue("请输入身份证号码")
                    return@clickNoRepeat
                }
                if (!Utils.isIDCardValid(it.sfz)) {
                    viewModel.defUI.toastEvent.postValue("请输入正确身份证号码")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.sesame)) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }

                if (multipList.isEmpty()) {
                    viewModel.defUI.toastEvent.postValue("请至少选择一项资产信息")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.period)) {
                    viewModel.defUI.toastEvent.postValue("请选择借款期限")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.purpose)) {
                    viewModel.defUI.toastEvent.postValue("请选择借款用途")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.loanAmount)) {
                    viewModel.defUI.toastEvent.postValue("请选择贷款额度")
                    return@clickNoRepeat
                }


                if (TextUtils.isEmpty(it.city)) {
                    viewModel.defUI.toastEvent.postValue("请选择工作城市")
                    return@clickNoRepeat
                }

                if (!mBinding.srcheckBox.isChecked) {
                    viewModel.defUI.toastEvent.postValue("请阅读并同意《个人信息使用授权书》和《免责声明条款》")
                    return@clickNoRepeat
                }
                // 上报用户数据
                viewModel.benbuReportUserData(
                    it.name,
                    it.sfz,
                    provinceName,
                    cityName,
                    ip
                )

                LogUtils.e("吉用钱包--${Gson().toJson(saveData)}")

                viewModel.jiYongCheckInfo(
                    mBinding.tvZhongValue.text.toString(),
                    mBinding.tvIdcard.text.toString(),
                    phone
                ) {
                    viewModel.jiYongApplyPost(saveData) { orderData ->

                        viewModel.reportPointRequest(5)
                        MyApplication.isForm = true
                        val intent = Intent(this, WmSuccessActivity::class.java)
                        intent.putExtra("orderData", orderData)
                        startActivity(intent)
                        setResult(RESULT_OK)
                        finish()
                    }

//                    val intent = Intent(this, JiYQBFormTwoActivity::class.java)
//                    intent.putExtra("choicedata", saveData)
//                    someActivityResultLauncher.launch(intent)
                }
            }
        }


        mBinding.addressRela.clickNoRepeat {
            hideKeyboard()
            pvOptions?.show()
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

    private val someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    /*
    * 身份证前三后四脱敏
    */
    fun idEncrypt(id: String): String {
        if (id.isBlank() || id.length < 8) {
            return id
        }
        return id.replace(Regex("(?<=\\w{3})\\w(?=\\w{4})"), "*")
    }

    fun nameEncrypt(name: String): String {
        if (name.isEmpty() || name.length == 1) {
            // 如果名字为空或者只有一个字符，直接返回原样
            return name
        }
        // 假设第一个字符是姓，后面的字符都是名
        val xing = name[0]
        val min = "*".repeat(name.length - 1)
        return "$xing$min"
    }


}