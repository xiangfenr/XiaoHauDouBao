package com.yundou.loans.ui.mine

import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.JkGuideLayoutBinding
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat

class JkGuideActivity : CommonActivity<UserViewModel, JkGuideLayoutBinding>() {



    override fun getLayoutId(): Int {
        return R.layout.jk_guide_layout
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