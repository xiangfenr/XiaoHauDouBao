package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.bigkoo.pickerview.view.OptionsPickerView
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.AmoliTwoFormLayoutBinding
import com.yundou.loans.entity.MoLiFormData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.MoLiXyDialog
import com.yundou.loans.widget.clickNoRepeat


/**
 * 魔力表单2
 */
class MoLiTwoFormActivity : CommonActivity<UserViewModel, AmoliTwoFormLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private var choiceData = MoLiFormData()

    private var moliDialog: MoLiXyDialog? = null


    override fun getLayoutId(): Int {
        return R.layout.amoli_two_form_layout
    }

    override fun isShowActionBar(): Boolean {
        return false
    }

    override fun setTitle(): CharSequence {
        return ""
    }

    override fun init() {

        choiceData = intent.getSerializableExtra("choicedata") as MoLiFormData

        initview()
        initClickListenr()

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


    private fun initClickListenr() {
        mBinding.back.clickNoRepeat { finish() }
        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {
            //点击提交表单 按钮上报
            viewModel.reportPointRequest(4)

            choiceData.let {

                if (TextUtils.isEmpty(choiceData.zhima_score)) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.other_assets)) {
                    viewModel.defUI.toastEvent.postValue("请选择资质情况")
                    return@clickNoRepeat
                }
            }

            //提交表单
            viewModel.moliSubmitForm(choiceData) {

                if (null != it.match_info.product_info && !it.match_info.agreement_list.isNullOrEmpty()) {
                    val submitBean = it
                    //获取协议
                    viewModel.protocolGet(
                        it.match_info.agreement_list[0].code,
                        it.form_id,
                        it.match_info.product_id.toString()
                    ) {
                        moliDialog = MoLiXyDialog(this, it.content)
                        moliDialog?.show()
                        moliDialog?.setXieyiDialogClick(object : MoLiXyDialog.IXieyiDialogClick {
                            override fun agreementClick() {
                                //同意激活 - 推送
                                viewModel.moliConfirm(submitBean.match_info.step_id.toString()) {
                                    shujuMaidian()
                                    MyApplication.isForm = true
                                    startActivity(
                                        Intent(
                                            this@MoLiTwoFormActivity,
                                            WmSuccessActivity::class.java
                                        )
                                    )
                                    setResult(RESULT_OK)
                                    finish()
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


    }

    //提交资料数据埋点
    private fun shujuMaidian() {
          viewModel.reportPointRequest(5)
    }


}