package com.yundou.loans.ui.loan

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts

import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.TxfqFormTwoLayoutBinding
import com.yundou.loans.entity.TxfqSaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat

/**
 * 天下分期 表单2
 */
class TXFQFormTwoActivity : CommonActivity<UserViewModel, TxfqFormTwoLayoutBinding>() {

    private var saveData = TxfqSaveData()


    override fun getLayoutId(): Int {
        return R.layout.txfq_form_two_layout
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



        mBinding.zhimascoreGroup.setButtons(
            listOf(
                "700以上",
                "650-700",
                "600-650",
                "600以下",
            )
        ) { index, label ->
            saveData.credit = label
            hideKeyboard()
        }

        mBinding.jdGroup.setButtons(listOf("无", "有")) { index, label ->
            saveData.jdIous = index
            hideKeyboard()
        }
        mBinding.mayihuabeiGroup.setButtons(listOf("无", "有")) { index, label ->
            saveData.antCreditPay = index
            hideKeyboard()
        }


        mBinding.zongheGroup.setButtons(
            listOf(
                "房产",
                "车产",
                "公积金",
                "社保",
                "商业保险",
                "企业主"
            )
        ) { selectedIndices ->
            Log.d("MultiSelect", "Selected indices: $selectedIndices")
            val list: ArrayList<Int> = ArrayList()
            selectedIndices.forEach {
                val index = it + 1
                list.add(index)
            }
            saveData.assets = list
            hideKeyboard()
        }


    }


    private fun initClickListenr() {


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {

            saveData.let {

                if (TextUtils.isEmpty(it.credit.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.jdIous.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择京东白条")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.antCreditPay.toString())) {
                    viewModel.defUI.toastEvent.postValue("请选择蚂蚁花呗")
                    return@clickNoRepeat
                }
                if (it.assets.isEmpty()) {
                    viewModel.defUI.toastEvent.postValue("请选择综合资产")
                    return@clickNoRepeat
                }


                val intent = Intent(this, TXFQFormThreeActivity::class.java)
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