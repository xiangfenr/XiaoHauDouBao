package com.yundou.loans.ui.loan

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import com.alibaba.fastjson.JSON
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.lxj.xpopup.XPopup
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.YouqianOneLayoutBinding
import com.yundou.loans.entity.ProvinceBean
import com.yundou.loans.entity.YqChoiceData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.TwoProUtils
import com.yundou.loans.utils.Utils
import com.yundou.loans.utils.loadRegionsFromAssets
import com.yundou.loans.widget.FormStayDialog
import com.yundou.loans.widget.clickNoRepeat

class YouQianOneActivity : CommonActivity<UserViewModel, YouqianOneLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private val choiceData = YqChoiceData()

    //城市列表
    private var provinList: List<String> = ArrayList<String>()
    private lateinit var citylist: List<List<String?>>

    override fun getLayoutId(): Int {
        return R.layout.youqian_one_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    val zhengxinvalue = listOf("10","11","20","21","30","31") //征信情况对应的value值  todo

    override fun init() {

        choiceData.mobile = MmkvUtil.getInstance().decodeString("loginphone")

        initview()
        initData()
        initAddressDialog()
        initClickListenr()

    }

    private fun initData() {
        choiceData.mobile = MmkvUtil.getInstance().decodeString("loginphone")


        mBinding.yuqiGroup.setButtons(listOf("无逾期", "有逾期")) { index, label ->
            choiceData.creditInvestigation = (index + 1).toString()
            hideKeyboard()
        }

        mBinding.zhengxinGroup.setButtons(listOf("花呗无逾期", "花呗有逾期","白条无逾期","白条有逾期","信用卡无逾期","信用卡有逾期")) { index, label ->
            val array:ArrayList<String> = arrayListOf(zhengxinvalue[index].toString())
            choiceData.credit =  array
            hideKeyboard()
        }

        mBinding.zhimaGroup.setButtons(
            listOf(
                "无芝麻分",
                "650分以下",
                "650~700",
                "700以上"
            )
        ) { index, label ->
            choiceData.sesameCredit = (index + 1).toString()
            hideKeyboard()
        }

    }

    private fun initview() {
        //监听当身份证号码18位时，进行二要素校验
        mBinding.tvIdcard.addTextChangedListener(object : TextWatcher {
            private val maxLength = 10 // 设置最大字符数为10
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // 文本改变之前的回调，这里不需要处理
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 文本改变时的回调
//                val partner_id = MmkvUtil.getInstance().decodeInt("partner_id")
//                if (partner_id == 1) {
//                    if (s.length == 18) {
//                        mBinding.tvIdcard?.text.let { idcard ->
//                            mBinding.tvZhongValue?.text.let { name ->
//                                if (name?.isNotEmpty() == true) {
//                                    viewModel.twoElements(idcard.toString(), name.toString()) {
//
//                                    }
//                                }
//
//                            }
//                        }
//
//                    }
//                }
            }

            override fun afterTextChanged(s: Editable) {
                // 文本改变之后的回调，这里不需要处理
            }
        })

        mBinding.tvZhongValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
//                val partner_id = MmkvUtil.getInstance().decodeInt("partner_id")
//                if (partner_id == 1) {
//                    if (mBinding.tvIdcard.text.length == 18 && mBinding.tvZhongValue.text.length >= 2) {
//                        viewModel.twoElements(
//                            mBinding.tvIdcard.text.toString(),
//                            mBinding.tvZhongValue.text.toString()
//                        ) { }
//                    }
//                }


            }

        })
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
            region.city?.map { city -> city.name } ?: listOf()
        }

        pvOptions = OptionsPickerBuilder(this) { position1, position2, _, _ ->
            mBinding.address.text =
                provinList.getOrNull(position1) + "-" + citylist.getOrNull(position1)
                    ?.getOrNull(position2)


            choiceData.workCity = citylist.getOrNull(position1)?.getOrNull(position2)
            choiceData.workCityCode =
                originList.getOrNull(position1)?.city?.getOrNull(position2)?.code.toString()


        }.build()
        pvOptions?.setTitleText("地址选择")
        pvOptions?.setPicker(provinList, citylist)

    }

    private fun initClickListenr() {

        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {


            choiceData.let {
                it.realName = mBinding.tvZhongValue.text.trim().toString()
                it.identity = mBinding.tvIdcard.text.trim().toString()
                it.age = TwoProUtils.getAgeAndGender(it.identity?:"").first
                it.gender = TwoProUtils.getAgeAndGender(it.identity?:"").second


                if (TextUtils.isEmpty(choiceData.realName)) {
                    viewModel.defUI.toastEvent.postValue("请输入真实姓名")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.identity)) {
                    viewModel.defUI.toastEvent.postValue("请输入身份证号码")
                    return@clickNoRepeat
                }
                if (!Utils.isIDCardValid(choiceData.identity)) {
                    viewModel.defUI.toastEvent.postValue("请输入正确身份证号码")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.mobile)) {
                    viewModel.defUI.toastEvent.postValue("请输入手机号码")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.workCity) || TextUtils.equals(
                        choiceData.workCity,
                        "请选择城市"
                    )
                ) {
                    viewModel.defUI.toastEvent.postValue("请选择城市")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.creditInvestigation)) {
                    viewModel.defUI.toastEvent.postValue("请选择逾期情况")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.credit?.get(0))) {
                    viewModel.defUI.toastEvent.postValue("请选择信用情况")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.sesameCredit)) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻分")
                    return@clickNoRepeat
                }

            }
//            if (viewModel.twoElementCode.value != "1") {
//                viewModel.defUI.toastEvent.postValue("身份证号码与姓名不匹配")
//                return@clickNoRepeat
//            }


            Log.d("AAAAA", "initClickListenr: choiceData -- > " + JSON.toJSONString(choiceData))
            val intent = Intent(this, YouQianTwoActivity::class.java)
            intent.putExtra("choicedata", choiceData)
            someActivityResultLauncher.launch(intent)

        }


        mBinding.address.clickNoRepeat {
            //关闭输入框
            val view = this.currentFocus
            // 获取输入法管理器
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            // 隐藏输入法
            imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
            pvOptions?.show()
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