package com.yundou.loans.ui.loan

import android.content.Intent
import android.text.TextUtils
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.yundou.loans.MyApplication
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.KydFormLayoutBinding
import com.yundou.loans.entity.FormGroup
import com.yundou.loans.entity.GroupBean
import com.yundou.loans.entity.KydSubmitData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.widget.KydPipeiDialog
import com.yundou.loans.widget.clickNoRepeat


class KydFormActivity : CommonActivity<UserViewModel, KydFormLayoutBinding>() {

    private var pvOptions: OptionsPickerView<String>? = null

    private val choiceData = KydSubmitData()

    private var provinList: List<String> = ArrayList<String>()
    private var citylist: List<List<String?>> = ArrayList()

    private var formList: List<FormGroup> = ArrayList()

    private var xieyiContent:String =""
    private var kydDialog: KydPipeiDialog?=null



    override fun getLayoutId(): Int {
        return R.layout.kyd_form_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请额度"
    }

    override fun init() {

        // choiceData.mobile=MmkvUtil.getInstance().decodeString("loginphone")

        initClickListenr()


        viewModel.kydGetFormData {
            formList = it.formConfigInfoList
            initData()
        }

        //获取城市数据
        viewModel.kydGetCity {
            provinList = it.map { it.provinceName!! }

            citylist = it.map { region ->
                region.cityList?.map { city -> city.cityName } ?: listOf()
            }
            initAddressDialog()
        }


    }

    private fun initData() {

        //职业
        val itemlist1 = formList[0].groupFromItem[0].itemList[0]
        mBinding.kydIssuetv1.text = formList[0].groupFromItem[0].groupName
        val groupList1 = itemlist1.selectList.map {
            it.label
        }
        mBinding.kydGroup1.setButtons(groupList1) { index, label ->
            choiceData.occupation = itemlist1.selectList[index].id
            hideKeyboard()

           // choiceData.currentPage=1
            //viewModel.kydSubmitForm(choiceData){}
        }

        //社保
        mBinding.kydIssuetv2.text = formList[1].groupFromItem[0].groupName
        val groupList2 = formList[1].groupFromItem[0].itemList[0].selectList.map {
            it.label
        }
        mBinding.kydGroup2.setButtons(groupList2) { index, label ->
            choiceData.socialSecurity =
                formList[1].groupFromItem[0].itemList[0].selectList[index].id
            hideKeyboard()

           // choiceData.currentPage=2
          //  viewModel.kydSubmitForm(choiceData){}
        }

        //公积金
        mBinding.kydIssuetv3.text = formList[2].groupFromItem[0].groupName
        val groupList3 = formList[2].groupFromItem[0].itemList[0].selectList.map {
            it.label
        }
        mBinding.kydGroup3.setButtons(groupList3) { index, label ->
            choiceData.accumulation =
                formList[2].groupFromItem[0].itemList[0].selectList[index].id
            hideKeyboard()

           // choiceData.currentPage=3
           // viewModel.kydSubmitForm(choiceData){}
        }

        //车辆
        mBinding.kydIssuetv4.text = formList[3].groupFromItem[0].groupName
        val groupList4 = formList[3].groupFromItem[0].itemList[0].selectList.map {
            it.label
        }
        mBinding.kydGroup4.setButtons(groupList4) { index, label ->
            choiceData.car =
                formList[3].groupFromItem[0].itemList[0].selectList[index].id
            hideKeyboard()
          //  choiceData.currentPage=4
          //  viewModel.kydSubmitForm(choiceData){}
        }

        //房
        mBinding.kydIssuetv5.text = formList[4].groupFromItem[0].groupName
        val groupList5 = formList[4].groupFromItem[0].itemList[0].selectList.map {
            it.label
        }
        mBinding.kydGroup5.setButtons(groupList5) { index, label ->
            choiceData.house =
                formList[4].groupFromItem[0].itemList[0].selectList[index].id
            hideKeyboard()

           // choiceData.currentPage=5
           // viewModel.kydSubmitForm(choiceData){}
        }

        //芝麻分
        mBinding.kydIssuetv6.text = formList[5].groupFromItem[0].groupName
        val groupList6 = formList[5].groupFromItem[0].itemList[0].selectList.map {
            it.label
        }
        mBinding.kydGroup6.setButtons(groupList6) { index, label ->
            choiceData.sesameSeed =
                formList[5].groupFromItem[0].itemList[0].selectList[index].id
            hideKeyboard()

          //  choiceData.currentPage=6
          //  viewModel.kydSubmitForm(choiceData){}
        }


        //基本信息展示
        val jibenGroupList: List<GroupBean> = formList[6].groupFromItem
        //基本信息
        mBinding.kydIssuetv7Userinfo.text = jibenGroupList[0].groupName
        mBinding.kydIssuetv7Dis.text = jibenGroupList[0].groupTips
        //居住
        mBinding.kydIssuetv7Address.text = jibenGroupList[1].groupName
        mBinding.kydIssuetv7Addressdis.text = jibenGroupList[1].groupTips
        //显示默认城市
        mBinding.address.text = jibenGroupList[1].itemList[0].value
        choiceData.workCity = jibenGroupList[1].itemList[0].value

        //性别
        mBinding.tvSex.text = formList[6].groupFromItem[0].itemList[2].label
        val groupList7 = formList[6].groupFromItem[0].itemList[2].selectList.map {
            it.label
        }
        mBinding.kydGroup7.setButtons(groupList7) { index, label ->
            choiceData.sex =
                formList[6].groupFromItem[0].itemList[2].selectList[index].id
            hideKeyboard()
        }

        //逾期情况
        mBinding.kydIssuetv7Xinyong.text = jibenGroupList[2].groupName
        val groupList8 = jibenGroupList[2].itemList[0].selectList.map {
            it.label
        }
        mBinding.kydGroup7xinyong.setButtons(groupList8) { index, label ->
            choiceData.overdueSituation =
                jibenGroupList[2].itemList[0].selectList[index].id
            hideKeyboard()
        }
    }


    private fun initAddressDialog() {
        pvOptions = OptionsPickerBuilder(this) { position1, position2, _, _ ->
            val cityname = provinList.getOrNull(position1) + "/" + citylist.getOrNull(position1)
                ?.getOrNull(position2)
            mBinding.address.text = cityname
            choiceData.workCity = cityname
        }.build()
        pvOptions?.setTitleText("地址选择")
        pvOptions?.setPicker(provinList, citylist)

    }

    private fun initClickListenr() {

        //提交
        mBinding.txtFeedbackSubmit.clickNoRepeat {
            choiceData.let {
                it.userName = mBinding.tvZhongValue.text.trim().toString()
                it.age = mBinding.tvAgeValue.text.trim().toString()
                // it.sex = mBinding.loanAmountEdit.text.trim().toString()

                if (choiceData.occupation == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择职业")
                    return@clickNoRepeat
                }
                if (choiceData.socialSecurity == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择社保缴纳时间")
                    return@clickNoRepeat
                }
                if (choiceData.accumulation == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择公积金")
                    return@clickNoRepeat
                }

                if (choiceData.car == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择车产")
                    return@clickNoRepeat
                }
                if (choiceData.house == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择房产")
                    return@clickNoRepeat
                }
                if (choiceData.sesameSeed == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择芝麻信用分")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.userName)) {
                    viewModel.defUI.toastEvent.postValue("请输入真实姓名")
                    return@clickNoRepeat
                }
                if (TextUtils.isEmpty(choiceData.age)) {
                    viewModel.defUI.toastEvent.postValue("请输入年龄")
                    return@clickNoRepeat
                }
                if (choiceData.sex == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择性别")
                    return@clickNoRepeat
                }

                if (choiceData.overdueSituation == -1) {
                    viewModel.defUI.toastEvent.postValue("请选择逾期情况")
                    return@clickNoRepeat
                }

                if (TextUtils.isEmpty(choiceData.workCity)) {
                    viewModel.defUI.toastEvent.postValue("请选择省份和城市")
                    return@clickNoRepeat
                }
                choiceData.currentPage=6
                viewModel.kydSubmitForm(choiceData){
                    //保存信息成功, 匹配机构
                    viewModel.matchingInstitutions(){
                        if(it.matchingInstitutionsList.isNullOrEmpty()){
                            shujuMaidian()
                            MyApplication.isForm = true
                            MmkvUtil.getInstance().encode(Constants.IS_EDIT_FORM,true)
                            val intent = Intent(this@KydFormActivity, CommonWebViewActivity::class.java)
                            intent.putExtra("webUrl", it.jumpH5Url)
                            startActivity(intent)
                            finish()
                        }else{
                            val pipeiID= it.matchingInstitutionsList[0].id
                            val pipeibean =  it.matchingInstitutionsList[0]
                            //匹配机构成功-去获取协议内容
                            viewModel.userInformationAuthorizationLetter(){
                                xieyiContent=it.content
                                kydDialog = KydPipeiDialog(this, xieyiContent,pipeibean)
                                kydDialog?.show()
                                kydDialog?.setXieyiDialogClick(object : KydPipeiDialog.IXieyiDialogClick{
                                    override fun agreementClick() {
                                        //同意激活- 推送进件儿
                                        viewModel.authorizationApply(pipeiID){
                                            shujuMaidian()
                                            MyApplication.isForm = true
                                            val intent = Intent(this@KydFormActivity, CommonWebViewActivity::class.java)
                                            intent.putExtra("webUrl", it.jumpH5Url)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                })
                            }
                        }

                    }
                }
            }
        }

        mBinding.addressRela.setOnClickListener {
            pvOptions?.show()
            hideKeyboard()
        }

    }

    //提交资料数据埋点
    private fun shujuMaidian() {
          viewModel.reportPointRequest(5)
    }


}