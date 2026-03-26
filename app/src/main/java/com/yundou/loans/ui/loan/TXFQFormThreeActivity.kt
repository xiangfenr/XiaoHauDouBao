package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils

import com.lxj.xpopup.XPopup

import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.TxfqFormThreeLayoutBinding
import com.yundou.loans.entity.TxfqSaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.TXFQAgreementDialog
import com.yundou.loans.widget.clickNoRepeat

/**
 * 天下分期 表单 3
 */
class TXFQFormThreeActivity : CommonActivity<UserViewModel, TxfqFormThreeLayoutBinding>() {

    private var saveData = TxfqSaveData()
    private val agreementDialog by lazy { TXFQAgreementDialog(this) }

    override fun getLayoutId(): Int {
        return R.layout.txfq_form_three_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {
        initData()
        initClickListenr()
    }

    private fun initData() {
        saveData = intent.getSerializableExtra("choicedata") as TxfqSaveData

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
        mBinding.yongtuGroup.setButtons(
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
            saveData.loanPurpose = label
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


    private fun initClickListenr() {


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {
            //点击提交表单 按钮上报
            viewModel.reportPointRequest(4)

            saveData.let {


                if (TextUtils.isEmpty(it.loanPeriod)) {
                    viewModel.defUI.toastEvent.postValue("请选择借款期限")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.loanPurpose)) {
                    viewModel.defUI.toastEvent.postValue("请选择借款用途")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.loanAmount)) {
                    viewModel.defUI.toastEvent.postValue("请选择贷款额度")
                    return@clickNoRepeat
                }


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
    }


    private fun goSuccessActivity() {
        startActivity(
            Intent(
                this,
                WmSuccessActivity::class.java
            )
        )
        setResult(RESULT_OK)
        finish()
    }
}


