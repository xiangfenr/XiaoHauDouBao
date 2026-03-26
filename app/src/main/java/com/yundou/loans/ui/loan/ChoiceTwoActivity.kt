package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import com.bigkoo.pickerview.view.OptionsPickerView
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.ChoiceTwoLayoutBinding
import com.yundou.loans.entity.ChoiceData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.widget.clickNoRepeat

/**
 * 本部表单  2
 */
class ChoiceTwoActivity : CommonActivity<UserViewModel, ChoiceTwoLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private var choiceData: ChoiceData = ChoiceData()

    var provinces = ArrayList<String>()

    var arealist = ArrayList<ArrayList<String>?>()

    override fun getLayoutId(): Int {
        return R.layout.choice_two_layout
    }

    override fun isShowActionBar(): Boolean {
        return false
    }

    override fun setTitle(): CharSequence {
        return ""
    }

    override fun init() {
        choiceData = intent.getSerializableExtra("choicedata") as ChoiceData
        choiceData.mobile = MmkvUtil.getInstance().decodeString("loginphone")

        initData()
        initClickListenr()
    }


    private fun initData() {
        choiceData.mobile = MmkvUtil.getInstance().decodeString("loginphone")

        mBinding.zhiyeGroup.setButtons(
            listOf(
                "上班族",
                "私营企业",
                "自由职业",
                "公务员"
            )
        ) { index, label ->
            choiceData.professional_identity = (index + 1).toString()
            hideKeyboard()
        }

        mBinding.gongjijinGroup.setButtons(listOf("无公积金", "有公积金")) { index, label ->
            choiceData.fund = (index + 1).toString()
            hideKeyboard()
        }
        mBinding.shebaoGroup.setButtons(listOf("无社保", "有社保")) { index, label ->
            choiceData.social_insurance = (index + 1).toString()
            hideKeyboard()
        }


        mBinding.shangyebaodanGroup.setButtons(listOf("无商业保单", "有商业保单")) { index, label ->
            choiceData.business_insurance = (index + 1).toString()
            hideKeyboard()
        }


        mBinding.carGroup.setButtons(listOf("无车产", "有车产")) { index, label ->
            choiceData.car_property = (index + 1).toString()
            hideKeyboard()
        }
        mBinding.hourseGroup.setButtons(listOf("无房产", "有房产")) { index, label ->
            choiceData.house_property = (index + 1).toString()
            hideKeyboard()
        }


        mBinding.gongzifafangGroup.setButtons(listOf("银行卡", "现金")) { index, label ->
            choiceData.salary = (index + 1).toString()
            hideKeyboard()
        }
        mBinding.yueshouruGroup.setButtons(listOf("5000以下", "5000以上")) { index, label ->
            choiceData.monthly_income = (index + 1).toString()
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
            choiceData.apply_limit = (index + 1).toString()
            hideKeyboard()
        }


    }


    private fun initClickListenr() {

        mBinding.back.clickNoRepeat { finish() }
        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {

            viewModel.reportPointRequest(4)  //本部点击提交表单按钮-上报

            choiceData.let {

                if (TextUtils.isEmpty(choiceData.professional_identity)) {
                    viewModel.defUI.toastEvent.postValue("请选择职业身份")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.fund)) {
                    viewModel.defUI.toastEvent.postValue("请选择公积金")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.social_insurance)) {
                    viewModel.defUI.toastEvent.postValue("请选择社保")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.business_insurance)) {
                    viewModel.defUI.toastEvent.postValue("请选择商保")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.car_property)) {
                    viewModel.defUI.toastEvent.postValue("请选择车产")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.house_property)) {
                    viewModel.defUI.toastEvent.postValue("请选择房产")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.salary)) {
                    viewModel.defUI.toastEvent.postValue("请选择工资发放形式")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.monthly_income)) {
                    viewModel.defUI.toastEvent.postValue("请选择月收入")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.apply_limit)) {
                    viewModel.defUI.toastEvent.postValue("请选择申请额度")
                    return@clickNoRepeat
                }

            }

            MyApplication.isForm = true
            MmkvUtil.getInstance().encode( choiceData.mobile+ Constants.IS_EDIT_FORM, true)
            startActivity(Intent(this, WmSuccessActivity::class.java))
            setResult(RESULT_OK)
            finish()
        }

    }


}