package com.yundou.loans.ui.loan

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.ChoiceOneLayoutBinding
import com.yundou.loans.entity.AddressArrayData
import com.yundou.loans.entity.ChoiceData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.IpUtils
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import com.yundou.loans.utils.Utils
import com.yundou.loans.widget.FormStayDialog
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.clickNoRepeat

class ChoiceOneActivity : CommonActivity<UserViewModel, ChoiceOneLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private val choiceData = ChoiceData()

    var provinces = ArrayList<String>()

    var arealist = ArrayList<ArrayList<String>?>()
    var provinceName: String = ""
    val ip = IpUtils.getDeviceIp(this)
    override fun getLayoutId(): Int {
        return R.layout.choice_one_layout
    }

    override fun isShowActionBar(): Boolean {
        return false
    }

    override fun setTitle(): CharSequence {
        return ""
    }

    override fun init() {

        choiceData.mobile = MmkvUtil.getInstance().decodeString("loginphone")
        if (BaseApp.context.storeid == Constants.CHANNEL_HUAWEI) {
            showIdInfoDialog()
        }
        initview()
        initData()
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
    private fun initData() {
        choiceData.mobile = MmkvUtil.getInstance().decodeString("loginphone")


        mBinding.yuqiGroup.setButtons(listOf("信用良好", "当前逾期")) { index, label ->
            choiceData.credit = (index + 1).toString()
            hideKeyboard()
        }

        mBinding.xinyongqkGroup.setButtons(listOf("无", "有")) { index, label ->
            choiceData.credit_card = (index + 1).toString()
            hideKeyboard()
        }

        mBinding.zhimaGroup.setButtons(
            listOf(
                "600以下",
                "600~650",
                "650~700",
                "700以上"
            )
        ) { index, label ->
            choiceData.sesame_seed = (index + 1).toString()
            hideKeyboard()
        }

    }

    private fun initview() {
        mBinding.back.clickNoRepeat { finish() }
        //监听当身份证号码18位时，进行二要素校验
        mBinding.tvIdcard.addTextChangedListener(object : TextWatcher {
            private val maxLength = 10 // 设置最大字符数为10
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // 文本改变之前的回调，这里不需要处理
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 文本改变时的回调
                val partner_id = MmkvUtil.getInstance().decodeInt("partner_id")
                if (partner_id == 1) {
                    if (s.length == 18) {
                        mBinding.tvIdcard?.text.let { idcard ->
                            mBinding.tvZhongValue?.text.let { name ->
                                if (name?.isNotEmpty() == true) {
                                    viewModel.twoElements(idcard.toString(), name.toString()) {

                                    }
                                }

                            }
                        }

                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                // 文本改变之后的回调，这里不需要处理
            }
        })

        mBinding.tvZhongValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                    if (mBinding.tvIdcard.text.length == 18 && mBinding.tvZhongValue.text.length >= 2) {
                        viewModel.twoElements(
                            mBinding.tvIdcard.text.toString(),
                            mBinding.tvZhongValue.text.toString()
                        ) { }
                    }
            }

        })
    }

    private fun initAddressDialog() {

        //关闭输入框
        val view = this.currentFocus
        // 获取输入法管理器
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏输入法
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

        val dataJson =
            com.yundou.loans.utils.JsonUtils.readJsonFromAssets(this, "region.json")
        val cityData: AddressArrayData? =
            Gson().fromJson(dataJson.toString(), AddressArrayData::class.java)
        cityData?.array?.forEachIndexed { index, addressListData ->
            addressListData.city?.forEachIndexed { j, item ->
                provinces.add(item.name.toString())
                arealist.add(item.area)
            }
        }

        pvOptions = OptionsPickerBuilder(this) { position1, position2, _, _ ->
            mBinding.address.text =
                provinces[position1] + "-" + (arealist[position1]?.get(position2) ?: "")

            provinceName = provinces[position1]

            //上报用户数据
            if (!TextUtils.isEmpty(mBinding.tvZhongValue.text.trim().toString())
                && !TextUtils.isEmpty(mBinding.tvIdcard.text.trim().toString())
            ) {
                viewModel.benbuReportUserData(
                    mBinding.tvZhongValue.text.trim().toString(),
                    mBinding.tvIdcard.text.trim().toString(),
                    provinceName,
                    arealist[position1]?.get(position2) ?: "",
                    ip
                )
            }
        }.build()
        pvOptions?.setTitleText("地址选择")
        pvOptions?.setPicker(provinces, arealist as List<MutableList<String>>?)

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

            choiceData.let {
                it.real_name = mBinding.tvZhongValue.text.trim().toString()
                it.id_number = mBinding.tvIdcard.text.trim().toString()
                it.work_city = mBinding.address.text.toString()
                val age = getAgeAndGender(mBinding.tvIdcard.text.trim().toString()).first
                MmkvUtil.getInstance().encode(Constants.IDCARD_AGE, age)

                if (TextUtils.isEmpty(choiceData.real_name)) {
                    viewModel.defUI.toastEvent.postValue("请输入真实姓名")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.id_number)) {
                    viewModel.defUI.toastEvent.postValue("请输入身份证号码")
                    return@clickNoRepeat
                }
                if (!Utils.isIDCardValid(choiceData.id_number)) {
                    viewModel.defUI.toastEvent.postValue("请输入正确身份证号码")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.mobile)) {
                    viewModel.defUI.toastEvent.postValue("请输入手机号码")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.work_city) || TextUtils.equals(
                        choiceData.work_city,
                        "请选择城市"
                    )
                ) {
                    viewModel.defUI.toastEvent.postValue("请选择城市")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.credit)) {
                    viewModel.defUI.toastEvent.postValue("请选择信用情况")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.credit_card)) {
                    viewModel.defUI.toastEvent.postValue("请选择信用卡")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.sesame_seed)) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻分")
                    return@clickNoRepeat
                }

                // 上报用户数据
                viewModel.benbuReportUserData(
                    it.real_name,
                    it.id_number,
                    provinceName,
                    it.work_city,
                    ip
                )
            }

            if (viewModel.twoElementCode.value != "1") {
                viewModel.defUI.toastEvent.postValue("身份证号码与姓名不匹配")
                return@clickNoRepeat
            }

            val intent = Intent(this, ChoiceTwoActivity::class.java)
            intent.putExtra("choicedata", choiceData)
            someActivityResultLauncher.launch(intent)

        }


        mBinding.address.clickNoRepeat {
            //关闭输入框
            val view = this.currentFocus
            // 获取输入法管理器
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            // 隐藏输入法
            imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
            pvOptions?.show()
        }
        mBinding.addressRela.clickNoRepeat {
            hideKeyboard()
            pvOptions?.show()
        }
    }
//    private var canFinished = false
//    override fun onBackPressed() {
//        if (canFinished){
//            finish()
//        }
//
//        val dialog = FormStayDialog(this)
//        val popup = XPopup.Builder(this)
//            .hasShadowBg(true)
//            .moveUpToKeyboard(false)
//            .isViewMode(true)
//            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//            .enableDrag(false)
//            .dismissOnTouchOutside(false)
//            .asCustom(dialog)
//            .show()
//        dialog.setXieyiDialogClick(object : FormStayDialog.IXieyiDialogClick {
//            override fun agreementClick(type: Int) {
//                if (type == 0) {
//                    canFinished =true
//                }
//                popup.dismiss()
//            }
//
//        })
////        super.onBackPressed()
//    }

    private val someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

}