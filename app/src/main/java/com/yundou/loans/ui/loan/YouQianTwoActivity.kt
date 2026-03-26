package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSON
import com.lxj.xpopup.XPopup
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.YouqianTwoLayoutBinding
import com.yundou.loans.entity.YqChoiceData
import com.yundou.loans.entity.YqqbProductData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.widget.YQQBAgreementDialog
import com.yundou.loans.widget.clickNoRepeat

/**
 * 本部表单  2
 */
class YouQianTwoActivity : CommonActivity<UserViewModel, YouqianTwoLayoutBinding>() {


    private var choiceData: YqChoiceData = YqChoiceData()
    private var serialNo = ""
    private var submit1 = 0

    override fun getLayoutId(): Int {
        return R.layout.youqian_two_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {
        choiceData = intent.getSerializableExtra("choicedata") as YqChoiceData
        choiceData.mobile = MmkvUtil.getInstance().decodeString("loginphone")

        initData()
        initClickListenr()
    }


    private fun initData() {
        choiceData.mobile = MmkvUtil.getInstance().decodeString("loginphone")

        val jobList = listOf("10", "20", "30")
        mBinding.zhiyeGroup.setButtons(
            listOf(
                "上班族",
                "自由职业者",
                "企业主"
            ),
        ) { index, label ->
            choiceData.job = jobList[index]

            Log.d("AAAA", "initData: choiceData.job -- > " + choiceData.job)
            Log.d("AAAA", "initData: jobList[index] -- > " + jobList[index])
            hideKeyboard()
        }

        mBinding.gongjijinGroup.setButtons(
            listOf(
                "无",
                "六个月以下",
                "六个月以上"
            )
        ) { index, label ->
            choiceData.housingFund = (index + 1).toString()
            hideKeyboard()
        }
        mBinding.shebaoGroup.setButtons(listOf("无", "六个月以下", "六个月以上")) { index, label ->
            choiceData.socialSecurity = (index + 1).toString()
            hideKeyboard()
        }


        mBinding.shangyebaodanGroup.setButtons(
            listOf(
                "无保单",
                "缴纳不足1年",
                "缴纳1年以上"
            )
        ) { index, label ->
            choiceData.insurance = (index + 1).toString()
            hideKeyboard()
        }


        mBinding.carGroup.setButtons(listOf("无车产", "有车产", "有且接受抵押")) { index, label ->
            choiceData.carProperty = (index + 1).toString()
            hideKeyboard()
        }
        mBinding.hourseGroup.setButtons(
            listOf(
                "无房产",
                "有房产",
                "有且接受抵押"
            )
        ) { index, label ->
            choiceData.houseProperty = (index + 1).toString()
            hideKeyboard()
        }

        val payList = listOf("1", "2", "3")
        mBinding.gongzifafangGroup.setButtons(
            listOf(
                "银行转账",
                "银行代发",
                "现金"
            )
        ) { index, label ->
            choiceData.payoffForm = payList[index]
            hideKeyboard()
        }
        val monthList = listOf("1", "2", "3")
        mBinding.yueshouruGroup.setButtons(
            listOf(
                "5000以下",
                "5000~1万",
                "1万以上"
            )
        ) { index, label ->
            choiceData.monthlyProfit = monthList[index]
            hideKeyboard()
        }
        val eduList = listOf("1", "3", "5")
        mBinding.eduGroup.setButtons(
            listOf(
                "0-5万",
                "5-20万",
                "20万以上"
            )
        ) { index, label ->
            choiceData.applyLimit = eduList[index]
            hideKeyboard()
        }

        val workTimeList = listOf("10", "20", "30")
        mBinding.worktimeGroup.setButtons(
            listOf(
                "6个月以下",
                "6-12个月",
                "12个月以上"
            )
        ) { index, label ->
            choiceData.workDuration = workTimeList[index]
            hideKeyboard()
        }


        val assetsList = listOf(10, 20, 30, 40, 50)
        mBinding.assetGroup.setButtons(
            listOf(
                "有房",
                "有车",
                "有保单",
                "有公积金",
                "有社保"
            )
        ) { selectedIndices ->
            Log.d("MultiSelect", "Selected indices: $selectedIndices")
            val list: ArrayList<Int> = ArrayList()
            selectedIndices.forEach {
                val selectedContent = assetsList[it] //选中的数据value (多选)
                list.add(selectedContent)  //暂存
                list.distinct()
            }
            choiceData.assetSituation = list
            hideKeyboard()
        }


    }


    private fun initClickListenr() {


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {

            Log.d("AAAA", "initClickListenr: choiceData -- > " + JSON.toJSONString(choiceData))

            choiceData.let {

                choiceData.adChannelCode = Constants.YQQB_CHANNELCODE

                if (TextUtils.isEmpty(choiceData.job)) {
                    viewModel.defUI.toastEvent.postValue("请选择职业身份")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.housingFund)) {
                    viewModel.defUI.toastEvent.postValue("请选择公积金")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.socialSecurity)) {
                    viewModel.defUI.toastEvent.postValue("请选择社保")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.insurance)) {
                    viewModel.defUI.toastEvent.postValue("请选择商业保单")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.carProperty)) {
                    viewModel.defUI.toastEvent.postValue("请选择车产")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.houseProperty)) {
                    viewModel.defUI.toastEvent.postValue("请选择房产")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.payoffForm)) {
                    viewModel.defUI.toastEvent.postValue("请选择工资发放形式")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.monthlyProfit)) {
                    viewModel.defUI.toastEvent.postValue("请选择月收入")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.applyLimit)) {
                    viewModel.defUI.toastEvent.postValue("请选择申请额度")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.workDuration)) {
                    viewModel.defUI.toastEvent.postValue("请选择工作年限")
                    return@clickNoRepeat
                }
                if (null == choiceData.assetSituation || choiceData.assetSituation!!.isEmpty()) {
                    viewModel.defUI.toastEvent.postValue("请选择资产情况")
                    return@clickNoRepeat
                }
            }
            Log.d("AAAA", "initClickListenr: choiceData 111-- > " + JSON.toJSONString(choiceData))

            viewModel.yqqbSubmit(choiceData) { productData ->

                if (null != productData) {
                    submit1 = 1
                    serialNo = productData.serialNo ?: ""
                    agreeDialogShow(productData)
                } else {
                    successFun()
                }
            }
        }
    }

    private fun agreeDialogShow(productData: YqqbProductData) {
        val agreementDialog by lazy { YQQBAgreementDialog(this) }

        agreementDialog.setAgreementtData(productData)

        XPopup.Builder(this)
            .hasShadowBg(true)
            .moveUpToKeyboard(false)
            .isViewMode(true)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .asCustom(agreementDialog)
            .show()

        agreementDialog.setXieyiDialogClick(object :
            YQQBAgreementDialog.IXieyiDialogClick {
            override fun agreementClick() {
                viewModel.yqqbAuth(productData.id.toString(), serialNo) { authproduct ->
                    if (authproduct.applyStatus == 4 && submit1 == 1) {
                        authproduct.productNextMatchDTO?.let {
                            //匹配失败--需要重新展示协议
                            agreeDialogShow(authproduct.productNextMatchDTO!!)
                            submit1 = 2
                        }

                    } else {
                        successFun()
                    }
                }
            }
        })
    }

    private fun successFun() {
        MyApplication.isForm = true
        startActivity(Intent(this, WmSuccessActivity::class.java))
        setResult(RESULT_OK)
        finish()
    }


}