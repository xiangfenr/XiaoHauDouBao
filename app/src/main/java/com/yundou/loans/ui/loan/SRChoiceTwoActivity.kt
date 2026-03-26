package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.AsrChoiceTwoLayoutBinding
import com.yundou.loans.entity.SaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.widget.SRAgreementDialog
import com.yundou.loans.widget.clickNoRepeat
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 笙融表单 3
 */
class SRChoiceTwoActivity : CommonActivity<UserViewModel, AsrChoiceTwoLayoutBinding>() {

    private var saveData = SaveData()
    private val agreementDialog by lazy { SRAgreementDialog(this) }

    override fun getLayoutId(): Int {
        return R.layout.asr_choice_two_layout
    }

    override fun isShowActionBar(): Boolean {
        return false
    }

    override fun setTitle(): CharSequence {
        return ""
    }

    override fun init() {
        initData()
        initClickListenr()
    }

    private fun initData() {
        saveData = intent.getSerializableExtra("choicedata") as SaveData


        mBinding.gongjijinGroup.setButtons(listOf("无公积金", "有公积金")) { index, label ->
            saveData.reservedFunds = index
            hideKeyboard()
        }
        mBinding.shebaoGroup.setButtons(listOf("无社保", "有社保")) { index, label ->
            saveData.socialSecurity = index.toString()
            hideKeyboard()
        }

        mBinding.xueliGroup.setButtons(
            listOf(
                "高中/中专",
                "大专",
                "本科",
                "硕士"
            )
        ) { index, label ->
            saveData.education = (index).toString()
            hideKeyboard()
        }


        mBinding.zhiyeGroup.setButtons(
            listOf(
                "上班族",
                "个体户",
                "电商主",
                "自由职业",
                "企业主",
                "其他"
            )
        ) { index, label ->
            saveData.profession = (index).toString()
            hideKeyboard()
        }


        mBinding.carGroup.setButtons(listOf("无车产", "有车产")) { index, label ->
            saveData.car = index.toString()
            hideKeyboard()
        }

        mBinding.hourseGroup.setButtons(listOf("无房产", "有房产")) { index, label ->
            saveData.house = index.toString()
            hideKeyboard()
        }


        mBinding.qixianGroup.setButtons(
            listOf(
                "1个月",
                "3个月",
                "6个月",
                "9个月",
                "12个月",
                "24个月"
            )
        ) { index, label ->
            saveData.loanLimit = (index + 1).toString()
            hideKeyboard()
        }
        mBinding.yongtuGroup.setButtons(
            listOf(
                "购车贷款",
                "购房贷款",
                "装修贷款",
                "教育贷款",
                "消费贷款",
                "过桥贷款"
            )
        ) { index, label ->
            saveData.loanUse = (index + 1).toString()
            hideKeyboard()
        }
        mBinding.yueshouruGroup.setButtons(listOf("5000以下", "5000以上")) { index, label ->
            saveData.revenue = index
            hideKeyboard()
        }

    }


    private fun startXieyi(url: String) {
        val name = saveData.name
        val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString()) // 对参数值进行编码
        val idCardNo = saveData.idCardNo
        val appname = if (BaseApp.context.baseUrl.contains("chengshu")) {
            "南宁乘数小额贷"
        } else {
            "贵州贝花小贷"
        }

        val intent = Intent(this, CommonWebViewActivity::class.java)
        intent.putExtra(
            "webUrl",
            "${BaseApp.context.shengrongH5Url}${url}?name=$encodedName&idCardNo=$idCardNo&appName=$appname"
        )
        startActivity(intent)
    }

    private fun initClickListenr() {
        mBinding.back.clickNoRepeat { finish() }

        mBinding.tvYinsiZcxy.clickNoRepeat {
            // 隐私 #/app/privacy
            startXieyi("/#/app/privacy")
        }

        mBinding.tvShouquanYszc.clickNoRepeat {
            // #/app/authorize
            startXieyi("/#/app/authorize")
        }


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {
            //点击提交表单 按钮上报
            viewModel.reportPointRequest(4)

            saveData.let {

                if (TextUtils.isEmpty(it.reservedFunds.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择公积金")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.socialSecurity)) {
                    viewModel.defUI.toastEvent.postValue("请选择社保")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.profession)) {
                    viewModel.defUI.toastEvent.postValue("请选择职业")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.car)) {
                    viewModel.defUI.toastEvent.postValue("请选择车产")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.house)) {
                    viewModel.defUI.toastEvent.postValue("请选择房产")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.loanLimit)) {
                    viewModel.defUI.toastEvent.postValue("请选择贷款期限")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.loanUse)) {
                    viewModel.defUI.toastEvent.postValue("请选择贷款用途")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.revenue.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择月收入")
                    return@clickNoRepeat
                }

                if (!TextUtils.isEmpty(mBinding.password.text.trim().toString())) {
                    it.loanAmount = mBinding.password.text.trim().toString().toInt()
                }else{
                    viewModel.defUI.toastEvent.postValue("请输入贷款额度")
                    return@clickNoRepeat
                }

//                if (!mBinding.srcheckBox.isChecked) {
//                    viewModel.defUI.toastEvent.postValue("请阅读并同意《用户隐私协议》和《用户授权协议》")
//                    return@clickNoRepeat
//                }

                viewModel.getWmSubmit(it) {
                    viewModel.getWmCheckInto { srUserData ->
                        //是否有勾选协议
                        val products = srUserData.products
                        if (!products.isNullOrEmpty()) {
                            val firstProduct = products[0]

                            agreementDialog.setAgreementtData(firstProduct)

                            val popup =  XPopup.Builder(this)
                                .hasShadowBg(true)
                                .moveUpToKeyboard(false)
                                .isViewMode(true)
                                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                                .enableDrag(false)
                                .dismissOnTouchOutside(false)
                                .asCustom(agreementDialog)
                                .show()

                            agreementDialog.setXieyiDialogClick(object :
                                SRAgreementDialog.IXieyiDialogClick {
                                override fun agreementClick() {
                                    viewModel.agreement(firstProduct.id.toString())
                                    viewModel.wMapply(firstProduct.id.toString()) {resultData ->

                                        if (resultData.code == 200) {
                                            startActivity(
                                                Intent(
                                                    this@SRChoiceTwoActivity,
                                                    WmSuccessActivity::class.java
                                                )
                                            )
                                            setResult(RESULT_OK)
                                            finish()

                                        } else {
                                            if (popup.isShow) {
                                                popup.dismiss()
                                            }
                                        }
                                    }
                                }

                            })
                        } else {
                            startActivity(Intent(this, WmSuccessActivity::class.java))
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                    shujuMaidian()
                    MyApplication.isForm = true
                }
            }
        }

    }

    //提交资料数据埋点
    private fun shujuMaidian() {
        //关闭输入框
        val view = this.currentFocus
        // 获取输入法管理器
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏输入法
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
          viewModel.reportPointRequest(5)
    }

}