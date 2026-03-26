package com.yundou.loans.ui.loan

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContracts
import com.bigkoo.pickerview.view.OptionsPickerView
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.JiybangFormTwoLayoutBinding
import com.yundou.loans.entity.JIYBangSaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat

/**
 * 吉用帮 表单2
 */
class JiYBangFormTwoActivity : CommonActivity<UserViewModel, JiybangFormTwoLayoutBinding>() {

    private var saveData = JIYBangSaveData()
    private var pvOptions: OptionsPickerView<String>? = null


    override fun getLayoutId(): Int {
        return R.layout.jiybang_form_two_layout
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

        mBinding.gongjijinGroup.setButtons(listOf("无", "有")) { index, label ->
            saveData.accumulationFund = label
            hideKeyboard()
        }
        mBinding.shebaoGroup.setButtons(listOf("无", "有")) { index, label ->
            saveData.socialInsurance = label
            hideKeyboard()
        }

        mBinding.shangyeGroup.setButtons(listOf("无", "有")) { index, label ->
            saveData.businessInsurance = index.toString()
            hideKeyboard()
        }

        mBinding.qiyezhuGroup.setButtons(
            listOf(
                "是",
                "否"
            )
        ) { index, label ->
            saveData.businessOwners = label
            hideKeyboard()
        }



        mBinding.zhimascoreGroup.setButtons(
            listOf(
                "0-599",
                "600-649",
                "650-699",
                "700-950"
            )
        ) { index, label ->
            saveData.zmScore = label
            hideKeyboard()
        }

        mBinding.huabeiGroup.setButtons(
            listOf(
                "无",
                "5000以内",
                "5000以上",
            )
        ) { index, label ->
            saveData.spendBaiLimit = label
            hideKeyboard()
        }
        mBinding.baitiaoGroup.setButtons(
            listOf(
                "无",
                "5000以内",
                "5000以上",
            )
        ) { index, label ->
            saveData.jdbtLimit = label
            hideKeyboard()
        }


        mBinding.carGroup.setButtons(listOf("无", "有")) { index, label ->
            saveData.carProperty = label
            hideKeyboard()
        }

        mBinding.hourseGroup.setButtons(listOf("无房产", "有房产")) { index, label ->
            saveData.houseProperty = label
            hideKeyboard()
        }


    }


    private fun initClickListenr() {


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {

            saveData.let {

                if (TextUtils.isEmpty(it.accumulationFund.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择公积金")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.socialInsurance)) {
                    viewModel.defUI.toastEvent.postValue("请选择社保")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.businessInsurance)) {
                    viewModel.defUI.toastEvent.postValue("请选择商业保险")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.businessOwners)) {
                    viewModel.defUI.toastEvent.postValue("请选择是否是企业主")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.zmScore)) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.spendBaiLimit)) {
                    viewModel.defUI.toastEvent.postValue("请选择花呗借呗额度")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.jdbtLimit)) {
                    viewModel.defUI.toastEvent.postValue("请选择京东白条")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.carProperty)) {
                    viewModel.defUI.toastEvent.postValue("请选择车产")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.houseProperty)) {
                    viewModel.defUI.toastEvent.postValue("请选择房产")
                    return@clickNoRepeat
                }

                val intent = Intent(this, JiYBangFormThreeActivity::class.java)
                intent.putExtra("choicedata", saveData)
                someActivityResultLauncher.launch(intent)

            }
        }


    }

    private val someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }


}