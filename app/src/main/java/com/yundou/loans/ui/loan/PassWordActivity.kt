package com.yundou.loans.ui.loan


import android.text.TextUtils
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.databinding.PasswordItemLayoutBinding
import com.yundou.loans.widget.clickNoRepeat


class PassWordActivity : CommonActivity<UserViewModel, PasswordItemLayoutBinding>() {


    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "设置密码"
    }


    override fun getLayoutId(): Int {
        return R.layout.password_item_layout
    }

    override fun init() {
        //登录
        mBinding.setpwd.tvLoginLogin.clickNoRepeat {

            if (TextUtils.equals(mBinding.setpwd.etLoginPassword.text.toString().trim(),
                    mBinding.setpwd.etLoginPasswordAgain.text.toString().trim())
            ) {
                viewModel.setPassword(mBinding.setpwd.etLoginPasswordAgain.text.toString().trim()) {
                    viewModel.defUI.toastEvent.postValue("设置密码成功")
                    finish()
                }
            } else
                viewModel.defUI.toastEvent.postValue("密码不一致")
        }
    }
}