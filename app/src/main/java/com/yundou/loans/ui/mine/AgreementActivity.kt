package com.yundou.loans.ui.mine

import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.AgreementLayoutBinding

import com.yundou.loans.model.UserViewModel

class AgreementActivity : CommonActivity<UserViewModel, AgreementLayoutBinding>() {

    override fun setTitle(): CharSequence {
        return "注册协议"
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.agreement_layout
    }

    override fun init() {
        //拍拍

    }
}