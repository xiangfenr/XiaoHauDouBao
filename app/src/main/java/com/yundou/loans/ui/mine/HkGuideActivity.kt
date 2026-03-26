package com.yundou.loans.ui.mine

import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.HkGuideLayoutBinding
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat

class HkGuideActivity : CommonActivity<UserViewModel, HkGuideLayoutBinding>() {



    override fun getLayoutId(): Int {
        return R.layout.hk_guide_layout
    }

    override fun isShowActionBar(): Boolean {
        return false
    }

    override fun setTitle(): CharSequence {
        return ""
    }

    override fun init() {
        initview()
    }


    private fun initview() {
        mBinding.back.clickNoRepeat { finish() }

    }



}