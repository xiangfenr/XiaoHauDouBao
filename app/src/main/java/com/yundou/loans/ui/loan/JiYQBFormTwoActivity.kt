package com.yundou.loans.ui.loan

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContracts
import com.bigkoo.pickerview.view.OptionsPickerView
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.JiyongFormTwoLayoutBinding
import com.yundou.loans.entity.JIYongSaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat

/**
 * 吉用钱包 表单2
 */
class JiYQBFormTwoActivity : CommonActivity<UserViewModel, JiyongFormTwoLayoutBinding>() {

    private var saveData = JIYongSaveData()
    private var pvOptions: OptionsPickerView<String>? = null


    override fun getLayoutId(): Int {
        return R.layout.jiyong_form_two_layout
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


        mBinding.zhiyeGroup.setButtons(
            listOf(
                "无",
                "有"
            )
        ) { index, label ->
            saveData.owners = (index).toString()
            hideKeyboard()
        }

        mBinding.zhimascoreGroup.setButtons(
            listOf(
                "700以上",
                "650-700",
                "600-650"
            )
        ) { index, label ->
            saveData.sesame = (index + 1).toString()
            hideKeyboard()
        }

        mBinding.gongjijinGroup.setButtons(listOf("无公积金", "有公积金")) { index, label ->
            saveData.fund = index.toString()
            hideKeyboard()
        }
        mBinding.shebaoGroup.setButtons(listOf("无社保", "有社保")) { index, label ->
            saveData.salary = index.toString()
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

        mBinding.shangyeGroup.setButtons(listOf("无商业保单", "有商业保单")) { index, label ->
            saveData.insurance = index.toString()
            hideKeyboard()
        }

    }


    private fun initClickListenr() {


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {

            saveData.let {

                if (TextUtils.isEmpty(it.fund.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择公积金")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.salary)) {
                    viewModel.defUI.toastEvent.postValue("请选择社保")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.insurance)) {
                    viewModel.defUI.toastEvent.postValue("请选择商业保险")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.owners)) {
                    viewModel.defUI.toastEvent.postValue("请选择是否是企业主")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.sesame)) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
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

                val intent = Intent(this, JiYQBFormThreeActivity::class.java)
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