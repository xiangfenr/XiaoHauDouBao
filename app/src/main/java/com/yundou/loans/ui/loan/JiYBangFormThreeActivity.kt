package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.JiybangFormThreeLayoutBinding
import com.yundou.loans.entity.JIYBangSaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat

/**
 * 吉用帮 表单 3
 */
class JiYBangFormThreeActivity : CommonActivity<UserViewModel, JiybangFormThreeLayoutBinding>() {

    private var saveData = JIYBangSaveData()

    override fun getLayoutId(): Int {
        return R.layout.jiybang_form_three_layout
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
        saveData = intent.getSerializableExtra("choicedata") as JIYBangSaveData

        mBinding.yuqiGroup.setButtons(
            listOf(
                "无逾期记录",
                "有逾期记录"
            )
        ) { index, label ->
            saveData.creditSituation = label
            hideKeyboard()
        }

        mBinding.qixianGroup.setButtons(
            listOf(
                "3-6月",
                "6-12月",
                "12-24月",
                "大于24月"
            )
        ) { index, label ->
            saveData.loanLongTime = label
            hideKeyboard()
        }

        mBinding.yueshouruGroup.setButtons(
            listOf(
                "1万以下",
                "1万-3万",
                "3万以上"
            )
        ) { index, label ->
            saveData.monthlyIncome = label
            hideKeyboard()
        }

        mBinding.eduGroup.setButtons(
            listOf(
                "5万以下",
                "5-10万",
                "10-15万",
                "20万以上"
            )
        ) { index, label ->
            saveData.loanLimit = label
            hideKeyboard()
        }


    }



    private fun initClickListenr() {



        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {

            saveData.let {
                //  it.loanAmount = mBinding.password.text.trim().toString()
                if (TextUtils.isEmpty(it.creditSituation)) {
                    viewModel.defUI.toastEvent.postValue("请选择逾期情况")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.loanLongTime)) {
                    viewModel.defUI.toastEvent.postValue("请选择借款期限")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.monthlyIncome)) {
                    viewModel.defUI.toastEvent.postValue("请选择月收入")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.loanLimit.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择贷款额度")
                    return@clickNoRepeat
                }


                viewModel.jiYBangApplyPost(it) {

                      viewModel.reportPointRequest(5)
                    MyApplication.isForm = true
                    val intent = Intent(this, WmSuccessActivity::class.java)
                    startActivity(intent)
                    setResult(RESULT_OK)
                    finish()
                }

            }
        }
    }


}