package com.yundou.loans.ui.loan

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.bigkoo.pickerview.view.OptionsPickerView
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.TwopChoiceTwoLayoutBinding
import com.yundou.loans.entity.TwoPFormData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat


/**
 * 二项目 表单
 */
class TwoProTwoFormActivity : CommonActivity<UserViewModel, TwopChoiceTwoLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private var choiceData = TwoPFormData()


    override fun getLayoutId(): Int {
        return R.layout.twop_choice_two_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {
        choiceData = intent.getSerializableExtra("choicedata") as TwoPFormData


        initview()
        initClickListenr()

    }

    private fun initview() {


        mBinding.creditCardGroup.setButtons(listOf("无", "有")) { index, label ->
            choiceData.credit_card = (index + 1)
            hideKeyboard()
        }

        mBinding.creditGroup.setButtons(listOf("信用良好", "当前逾期")) { index, label ->
            choiceData.credit = (index + 1)
            hideKeyboard()
        }

        mBinding.professionGroup.setButtons(
            listOf(
                "上班族",
                "私营企业主",
                "自由职业",
                "公务员/国企"
            )
        ) { index, label ->
            choiceData.profession = (index + 1)
            hideKeyboard()
        }

        mBinding.sesameSeedGroup.setButtons(
            listOf(
                "600 分以下",
                "600-650分",
                "650-700分",
                "700分以上"
            )
        ) { index, label ->
            choiceData.sesame_seed = (index + 1)
            hideKeyboard()
        }


        mBinding.fundGroup.setButtons(
            listOf(
                "无公积金",
                "有公积金"
            )
        ) { index, label ->
            choiceData.fund = (index + 1)
            hideKeyboard()
        }


        mBinding.socialInsuranceGroup.setButtons(
            listOf(
                "无社保",
                "有社保"
            )
        ) { index, label ->
            choiceData.social_insurance = (index + 1)
            hideKeyboard()
        }

        mBinding.businessInsuranceGroup.setButtons(
            listOf(
                "无商业保单",
                "有商业保单"
            )
        ) { index, label ->
            choiceData.business_insurance = (index + 1)
            hideKeyboard()
        }


        mBinding.housePropertyGroup.setButtons(
            listOf(
                "无房产",
                "有房产"
            )
        ) { index, label ->
            choiceData.house_property = (index + 1)
            hideKeyboard()
        }

        mBinding.carPropertyGroup.setButtons(
            listOf(
                "无车产",
                "有车产"
            )
        ) { index, label ->
            choiceData.car_property = (index + 1)
            hideKeyboard()
        }


    }


    private fun initClickListenr() {


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {


            choiceData.let {

                if (choiceData.credit_card == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择是否有信用卡")
                    return@clickNoRepeat
                }
                if (choiceData.credit == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择信用情况")
                    return@clickNoRepeat
                }
                if (choiceData.profession == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择职业")
                    return@clickNoRepeat
                }
                if (choiceData.sesame_seed == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }
                if (choiceData.fund == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择公积金")
                    return@clickNoRepeat
                }
                if (choiceData.social_insurance == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择社保")
                    return@clickNoRepeat
                }
                if (choiceData.business_insurance == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择商业保险")
                    return@clickNoRepeat
                }
                if (choiceData.car_property == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择车产")
                    return@clickNoRepeat
                }
                if (choiceData.house_property == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择房产")
                    return@clickNoRepeat
                }


            }
            val intent = Intent(this, TwoProThreeFormActivity::class.java)
            intent.putExtra("choicedata", choiceData)
            finishActivityResultLauncher.launch(intent)


        }


    }

    private val finishActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }


}