package com.yundou.loans.ui.loan

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.AsrChoiceOneLayoutBinding
import com.yundou.loans.entity.AddressData
import com.yundou.loans.entity.SaveData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.IpUtils
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import com.yundou.loans.utils.Utils
import com.yundou.loans.widget.FormStayDialog
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.clickNoRepeat


class SRChoiceOneActivity : CommonActivity<UserViewModel, AsrChoiceOneLayoutBinding>() {

    private val saveData = SaveData()
    private var pvOptions: OptionsPickerView<String>? = null

    //城市列表
    var provinces = ArrayList<String>()
    var arealist = ArrayList<ArrayList<String>>()
    var realList: MutableList<AddressData> = mutableListOf()
    var provinceName: String = ""
    val ip = IpUtils.getDeviceIp(this)

    override fun getLayoutId(): Int {
        return R.layout.asr_choice_one_layout
    }

    override fun isShowActionBar(): Boolean {
        return false
    }

    override fun setTitle(): CharSequence {
        return ""
    }

    override fun init() {
        if (BaseApp.context.storeid == Constants.CHANNEL_HUAWEI) {
            showIdInfoDialog()
        }
        initClickListenr()
        initAddressDialog()
        initData()
    }
    fun showIdInfoDialog(){
        val showIdInfoDialog = ShowIdInfoDialog(this)
        XPopup.Builder(this)
            .hasShadowBg(true)
            .moveUpToKeyboard(false)
            .isViewMode(true)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .asCustom(showIdInfoDialog)
            .show()

    }
    private fun initData() {
        mBinding.zhimaGroup.setButtons(
            listOf(
                "550分以下",
                "550分-600分",
                "600分-650分",
                "650分-700分",
                "700分以上"
            )
        ) { index, label ->
            saveData.zhima = (index + 1).toString()
            hideKeyboard()
        }

        mBinding.xinyongqkGroup.setButtons(listOf("无信用卡", "有信用卡")) { index, label ->
            saveData.creditCard = (index).toString()
            hideKeyboard()
        }

        mBinding.shangbaoGroup.setButtons(listOf("无商业保单", "有商业保单")) { index, label ->
            saveData.insurance = index.toString()
            hideKeyboard()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun initAddressDialog() {

        viewModel.tree {
            provinces = ArrayList()
            realList = it.data
            it.data.forEachIndexed { index, addressData ->
                provinces.add(addressData.title.toString())
                val aresitemList = ArrayList<String>()
                addressData.children?.forEach { area ->
                    aresitemList.add(area.title.toString())
                }
                arealist.add(index, aresitemList)
            }
            pvOptions?.setPicker(provinces, arealist as List<MutableList<String>>?)
        }

        pvOptions = OptionsPickerBuilder(this) { position1, position2, _, _ ->
            mBinding.address.text = provinces[position1] + "-" + arealist[position1][position2]

            saveData.cityOfWork = arealist[position1][position2]
            saveData.cityOfWorkCode = realList[position1].children?.get(position2)?.id
            Log.d("地址选择-----", Gson().toJson(saveData))
            provinceName = provinces[position1].toString()

            //上报用户数据
            if (!TextUtils.isEmpty(mBinding.tvZhongValue.text.trim().toString())
                && !TextUtils.isEmpty(mBinding.tvIdcard.text.trim().toString())
            ) {
                viewModel.benbuReportUserData(
                    mBinding.tvZhongValue.text.trim().toString(),
                    mBinding.tvIdcard.text.trim().toString(),
                    provinceName,
                    arealist[position1]?.get(position2) ?: "",
                    ip
                )
            }

        }.build()
        pvOptions?.setTitleText("地址选择")

    }


    private var hasReportedZhongValue = false

    private fun initClickListenr() {

        mBinding.tvZhongValue.doAfterTextChanged { text ->
            if (!hasReportedZhongValue && !text.isNullOrEmpty()) {
                hasReportedZhongValue = true
                viewModel.reportPointRequest(3)
            }
        }

        mBinding.back.clickNoRepeat { finish() }

        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {
            saveData.let {
                it.name = mBinding.tvZhongValue.text.trim().toString()
                it.idCardNo = mBinding.tvIdcard.text.trim().toString()
                val age = getAgeAndGender(mBinding.tvIdcard.text.trim().toString()).first
                MmkvUtil.getInstance().encode(Constants.IDCARD_AGE,age)

                if (TextUtils.isEmpty(it.name)) {
                    viewModel.defUI.toastEvent.postValue("请输入真实姓名")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.idCardNo)) {
                    viewModel.defUI.toastEvent.postValue("请输入身份证号码")
                    return@clickNoRepeat
                }
                if (!Utils.isIDCardValid(it.idCardNo)) {
                    viewModel.defUI.toastEvent.postValue("请输入正确身份证号码")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.zhima)) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(it.creditCard)) {
                    viewModel.defUI.toastEvent.postValue("请选择信用卡")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(it.insurance)) {
                    viewModel.defUI.toastEvent.postValue("请选择商业保险")
                    return@clickNoRepeat
                }

                viewModel.benbuReportUserData(it.name,it.idCardNo,provinceName,it.cityOfWork,ip)

                val intent = Intent(this, SRChoiceTwoActivity::class.java)
                intent.putExtra("choicedata", saveData)
                someActivityResultLauncher.launch(intent)
            }
        }


        mBinding.addressRela.clickNoRepeat {
            hideKeyboard()
            pvOptions?.show()
        }
    }

    private var canFinished = false
    override fun onBackPressed() {
        if (canFinished){
            finish()
        }

        val dialog = FormStayDialog(this)
        val popup = XPopup.Builder(this)
            .hasShadowBg(true)
            .moveUpToKeyboard(false)
            .isViewMode(true)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .asCustom(dialog)
            .show()
        if (dialog.isShow){
            canFinished =true
        }
        dialog.setXieyiDialogClick(object : FormStayDialog.IXieyiDialogClick {
            override fun agreementClick(type: Int) {
                if (type == 0) {
                    canFinished =true
                }
                popup.dismiss()
            }

        })
//        super.onBackPressed()
    }


    private val someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

}