package com.yundou.loans.ui.loan

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.lxj.xpopup.XPopup
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.JiybangFormOneLayoutBinding
import com.yundou.loans.entity.JIYBangSaveData
import com.yundou.loans.entity.ProvinceBean
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import com.yundou.loans.utils.Utils
import com.yundou.loans.utils.loadRegionsFromAssets
import com.yundou.loans.widget.FormStayDialog
import com.yundou.loans.widget.clickNoRepeat

/**
 * 吉用帮 表单1
 */
class JIYBangFormOneActivity : CommonActivity<UserViewModel, JiybangFormOneLayoutBinding>() {

    private val saveData = JIYBangSaveData()
    private var pvOptions: OptionsPickerView<String>? = null

    //城市列表
    private var provinList: List<String> = ArrayList<String>()
    private var citylist: List<List<String>> = ArrayList<ArrayList<String>>()
    private var arearList: List<List<List<String?>?>?> = ArrayList<ArrayList<ArrayList<String>>>()


    override fun getLayoutId(): Int {
        return R.layout.jiybang_form_one_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {
        saveData.realPhone = MmkvUtil.getInstance().decodeString("loginphone")
        mBinding.sexGroup.setButtons(listOf("男", "女")) { index, label ->
            saveData.sex = (index + 1).toString()
            hideKeyboard()
        }

        initClickListenr()
        initAddressDialog()

    }

    private fun initAddressDialog() {

        //关闭输入框
        val view = this.currentFocus
        // 获取输入法管理器
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏输入法
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

        //展示省份和城市
        val originList: List<ProvinceBean> = loadRegionsFromAssets(this)
        provinList = originList.map { it.name!! }

        citylist = originList.map { region ->
            region.city?.map { city -> city.name!! } ?: listOf()
        }


        arearList = originList.map { region ->
            region.city?.map { city ->
                city.area?.map { area ->
                    area.name
                }
            }
        }

        pvOptions = OptionsPickerBuilder(this) { position1, position2, position3, _ ->

            val location = "${provinList.getOrNull(position1)},${
                citylist.getOrNull(position1)?.getOrNull(position2)
            },${arearList.getOrNull(position1)?.getOrNull(position2)?.getOrNull(position3)}"

            mBinding.address.text = location

            saveData.location = location

        }.build()
        pvOptions?.setTitleText("地址选择")
        pvOptions?.setPicker(provinList, citylist, arearList)

    }


    private fun initClickListenr() {
        val phone = MmkvUtil.getInstance().decodeString("loginphone") ?: ""


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {

            saveData.let {
                //模拟地址
                //it.cityOfWork = "林芝"
                //it.cityOfWorkCode = "540400"
                it.realName = mBinding.tvZhongValue.text.trim().toString()
                it.idCardNo = mBinding.tvIdcard.text.trim().toString()
                //it.age = mBinding.editAge.text.trim().toString()

                //从身份证号中识别出年龄和性别
                it.age = getAgeAndGender(it.idCardNo!!).first.toString()
                it.sex = getAgeAndGender(it.idCardNo!!).second.toString()


                if (TextUtils.isEmpty(it.realName)) {
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

                if (TextUtils.isEmpty(it.location)) {
                    viewModel.defUI.toastEvent.postValue("请选择工作城市")
                    return@clickNoRepeat
                }


                val intent = Intent(this, JiYBangFormTwoActivity::class.java)
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