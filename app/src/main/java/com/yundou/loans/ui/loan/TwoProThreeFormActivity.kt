package com.yundou.loans.ui.loan

import android.content.Intent
import android.widget.Toast
import com.bigkoo.pickerview.view.OptionsPickerView
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.TwopChoiceThreeLayoutBinding
import com.yundou.loans.entity.TwoPFormData
import com.yundou.loans.entity.TwoResultData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.widget.TwoPCenterDialog
import com.yundou.loans.widget.clickNoRepeat
import kotlinx.coroutines.delay


/**
 * 二项目 表单
 */
class TwoProThreeFormActivity : CommonActivity<UserViewModel, TwopChoiceThreeLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private var choiceData = TwoPFormData()


    override fun getLayoutId(): Int {
        return R.layout.twop_choice_three_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {

        initview()
        initClickListenr()
    }

    private fun initview() {
        choiceData = intent.getSerializableExtra("choicedata") as TwoPFormData

        mBinding.salaryGroup.setButtons(
            listOf(
                "银行卡",
                "现金",
                "其他"
            )
        ) { index, label ->
            choiceData.salary = (index + 1)
            hideKeyboard()
        }

        mBinding.monthlyIncomeGroup.setButtons(
            listOf(
                "5000以下",
                "5000以上"
            )
        ) { index, label ->
            choiceData.monthly_income = (index + 1)
            hideKeyboard()
        }

        mBinding.applyLimitGroup.setButtons(
            listOf(
                "1万-5万",
                "5万-10万",
                "10万-15万",
                "15万-20万"
            )
        ) { index, label ->
            choiceData.apply_limit = (index + 1)
            hideKeyboard()
        }

    }


    private fun initClickListenr() {


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {
            viewModel.reportPointRequest(4)


            choiceData.let {

                if (choiceData.salary == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择工资发放形式")
                    return@clickNoRepeat
                }
                if (choiceData.monthly_income == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择月收入")
                    return@clickNoRepeat
                }
                if (choiceData.apply_limit == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择申请额度")
                    return@clickNoRepeat
                }

            }

            //给本部提交数据
            viewModel.twoPrFormSubmit(choiceData) {
                viewModel.reportPointRequest(5)
                MyApplication.isForm =  true
                val resultBean: TwoResultData? = it

                val dataId = resultBean?.data_id?.toString() ?: run {
                    Toast.makeText(this@TwoProThreeFormActivity,"提交失败，请稍后再试",Toast.LENGTH_LONG).show()
                    return@twoPrFormSubmit
                }

                val centerDialog =
                    TwoPCenterDialog(this)

                viewModel.launchUI {
                    val loading = XPopup.Builder(this@TwoProThreeFormActivity).asLoading()
                    loading.setTitle("匹配中...")
                    loading.show()

                    //等待4秒再获取结果
                    delay(2000)

                    viewModel.twoPGetResult(dataId) { resultData ->

                        when (resultData.code) {
                            1 -> {   //code=1 稍后重试
                                //五秒调用一次
                                viewModel.startRequesting(dataId) //开启结果查询

                                XPopup.Builder(this@TwoProThreeFormActivity)
                                    .dismissOnBackPressed(false)
                                    .asCustom(centerDialog)
                                    .show()
                            }

                            2 -> {  //code=2 暂无匹配机构
                                XPopup.Builder(this@TwoProThreeFormActivity).asConfirm(
                                    "", "暂无匹配机构"
                                ) {
                                    setResult(RESULT_OK)
                                    finish()
                                }.show()

                            }

                            3 -> {  // code=3 匹配到机构，展示url
                                val intent =
                                    Intent(
                                        this@TwoProThreeFormActivity,
                                        CommonWebViewActivity::class.java
                                    )
                                intent.putExtra("webUrl", resultData.channel_url)
                                intent.putExtra("isClose",true)
                                startActivity(intent)
                                setResult(RESULT_OK)
                                finish()
                            }
                        }

                        loading.dismiss()

                    }


                    viewModel.progress.observe(this@TwoProThreeFormActivity) { count ->
                        centerDialog.setProGress(count)
                    }

                    viewModel.resultUrl.observe(this@TwoProThreeFormActivity) { url ->
                        if (url != null) {
                            centerDialog.dismiss()
                            val intent =
                                Intent(
                                    this@TwoProThreeFormActivity,
                                    CommonWebViewActivity::class.java
                                )
                            intent.putExtra("webUrl", url)
                            intent.putExtra("isClose",true)
                            startActivity(intent)
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            centerDialog.dismiss()
                            XPopup.Builder(this@TwoProThreeFormActivity)
                                .asConfirm(
                                    "", "非常抱歉, 匹配数据失败"
                                ) {
                                    setResult(RESULT_OK)
                                    finish()
                                }.show()
                        }
                    }
                }

            }
        }
    }


}