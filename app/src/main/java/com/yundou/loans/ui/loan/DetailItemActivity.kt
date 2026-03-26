package com.yundou.loans.ui.loan

import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.DetailItemLayoutBinding
import com.yundou.loans.model.UserViewModel

class DetailItemActivity : CommonActivity<UserViewModel, DetailItemLayoutBinding>() {

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请结果"
    }


    override fun getLayoutId(): Int {
        return R.layout.detail_item_layout
    }

    override fun init() {


    }


}