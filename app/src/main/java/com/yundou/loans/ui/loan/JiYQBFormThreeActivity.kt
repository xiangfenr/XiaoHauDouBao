package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.JiyongFormThreeLayoutBinding
import com.yundou.loans.entity.JIYongSaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.LogUtils
import com.yundou.loans.widget.clickNoRepeat

/**
 * 吉用钱包 表单 3
 */
class JiYQBFormThreeActivity : CommonActivity<UserViewModel, JiyongFormThreeLayoutBinding>() {

    private var saveData = JIYongSaveData()

    override fun getLayoutId(): Int {
        return R.layout.jiyong_form_three_layout
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
        saveData = intent.getSerializableExtra("choicedata") as JIYongSaveData

        mBinding.qixianGroup.setButtons(
            listOf(
                "3-3期",
                "6-6期",
                "9-9期",
                "12-12期",
                "24-24期",
                "36-36期"
            )
        ) { index, label ->
            saveData.period = (index + 1).toString()
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

        mBinding.tvYinsiZcxy.clickNoRepeat {
            val encodedName = saveData.name!!
            val name = nameEncrypt(encodedName)
            val idCardNo = idEncrypt(saveData.sfz!!)
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
                //  it.loanAmount = mBinding.password.text.trim().toString()

                if (TextUtils.isEmpty(it.period)) {
                    viewModel.defUI.toastEvent.postValue("请选择借款期限")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.purpose)) {
                    viewModel.defUI.toastEvent.postValue("请选择借款用途")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.loanAmount.toString())) {
                    viewModel.defUI.toastEvent.postValue("请输入贷款额度")
                    return@clickNoRepeat
                }
                if (!mBinding.srcheckBox.isChecked) {
                    viewModel.defUI.toastEvent.postValue("请阅读并同意《用户隐私协议》和《用户授权协议》")
                    return@clickNoRepeat
                }

                viewModel.jiYongApplyPost(it) { orderData ->

                      viewModel.reportPointRequest(5)
                    MyApplication.isForm = true
                    val intent = Intent(this, WmSuccessActivity::class.java)
                    intent.putExtra("orderData", orderData)
                    startActivity(intent)
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }


}