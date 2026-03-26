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
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.TwopChoiceOneLayoutBinding
import com.yundou.loans.entity.MoLiProvince
import com.yundou.loans.entity.TwoPFormData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.*
import com.yundou.loans.utils.TwoProUtils.getAgeAndGender
import com.yundou.loans.widget.ShowIdInfoDialog
import com.yundou.loans.widget.clickNoRepeat


/**
 * 二项目 表单
 */
class TwoProOneFormActivity : CommonActivity<UserViewModel, TwopChoiceOneLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private val choiceData = TwoPFormData()

    private var provinList: List<String> = ArrayList<String>()
    private var citylist: List<List<String>> = ArrayList<ArrayList<String>>()
    private var arearList: List<List<List<String>>> = ArrayList<ArrayList<ArrayList<String>>>()
    var provinceName: String = ""
    var cityName: String = ""
    val ip = IpUtils.getDeviceIp(this)


    override fun getLayoutId(): Int {
        return R.layout.twop_choice_one_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {

        choiceData.mobile = MmkvUtil.getInstance().decodeString("loginphone")
        if (BaseApp.context.storeid == Constants.CHANNEL_HUAWEI) {
            showIdInfoDialog()
        }
        initview()
        initAddressDialog()
        initClickListenr()

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
    private fun initview() {


    }

    private fun initAddressDialog() {

        //关闭输入框
        val view = this.currentFocus
        // 获取输入法管理器
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        // 隐藏输入法
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

        //展示省份和城市
        val dataJson = JsonUtils.readJsonFromAssets(this, "twopregion.json")
        val originProvinceList: List<MoLiProvince> = getTwoPRegion(this)
        provinList = originProvinceList.map { it.name }

        citylist = originProvinceList.map { region ->
            region.child?.map { city -> city.name } ?: listOf()
        }

        arearList = originProvinceList.map { region ->
            region.child.map { city ->
                city.child.map { area ->
                    area.name
                }
            }
        }

        pvOptions = OptionsPickerBuilder(this) { position1, position2, position3, _ ->
            mBinding.address.text =
                provinList.getOrNull(position1) +
                        "-" + citylist.getOrNull(position1)?.getOrNull(position2) +
                        "-" + arearList.getOrNull(position1)?.getOrNull(position2)
                    ?.getOrNull(position3)

            choiceData.province = provinList.getOrNull(position1)
            choiceData.city = citylist.getOrNull(position1)?.getOrNull(position2) + "市"
            choiceData.district =
                arearList.getOrNull(position1)?.getOrNull(position2)?.getOrNull(position3)

            //省市区Code
//            choiceData.provinceCode = originProvinceList.getOrNull(position1)!!.code
//            choiceData.cityCode =
//                originProvinceList.getOrNull(position1)!!.child.getOrNull(position2)!!.code
//            choiceData.districtCode =
//                originProvinceList.getOrNull(position1)!!.child.getOrNull(position2)!!.child.getOrNull(
//                    position3
//                )!!.code


            // choiceData.current_district_id = originProvinceList[position1].child[position2].child[position3].code.toString()
            provinceName = provinList.getOrNull(position1)!!
            cityName = citylist.getOrNull(position1)?.getOrNull(position2)?:""
            //上报用户数据
            if (!TextUtils.isEmpty(mBinding.tvZhongValue.text.trim().toString())
                && !TextUtils.isEmpty(mBinding.tvIdcard.text.trim().toString())
            ) {
                viewModel.benbuReportUserData(
                    mBinding.tvZhongValue.text.trim().toString(),
                    mBinding.tvIdcard.text.trim().toString(),
                    provinceName,
                    cityName,
                    ip
                )
            }
        }.build()
        pvOptions?.setTitleText("地址选择")
        pvOptions?.setPicker(provinList, citylist, arearList)

    }

    private fun initClickListenr() {


        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {


            choiceData.let {
                it.name = mBinding.tvZhongValue.text.trim().toString()
                it.id_number = mBinding.tvIdcard.text.trim().toString()
                val age = getAgeAndGender(mBinding.tvIdcard.text.trim().toString()).first
                MmkvUtil.getInstance().encode(Constants.IDCARD_AGE,age)

                if (TextUtils.isEmpty(choiceData.name)) {
                    viewModel.defUI.toastEvent.postValue("请输入真实姓名")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.id_number)) {
                    viewModel.defUI.toastEvent.postValue("请输入身份证号码")
                    return@clickNoRepeat
                }
                if (!Utils.isIDCardValid(choiceData.id_number)) {
                    viewModel.defUI.toastEvent.postValue("请输入正确身份证号码")
                    return@clickNoRepeat
                }


                if (TextUtils.isEmpty(choiceData.province) || TextUtils.equals(
                        choiceData.city,
                        "请选择地址"
                    )
                ) {
                    viewModel.defUI.toastEvent.postValue("请选择省份和城市")
                    return@clickNoRepeat
                }
                // 上报用户数据
                viewModel.benbuReportUserData(
                    it.name,
                    it.id_number,
                    provinceName,
                    cityName,
                    ip
                )

            }

            val intent = Intent(this, TwoProTwoFormActivity::class.java)
            intent.putExtra("choicedata", choiceData)
            finishActivityResultLauncher.launch(intent)
        }


        mBinding.addressRela.clickNoRepeat {
            hideKeyboard()
            pvOptions?.show()
        }

    }



    private val finishActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

}