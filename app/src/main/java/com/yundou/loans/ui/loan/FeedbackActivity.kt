package com.yundou.loans.ui.loan

import android.text.TextUtils
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.FeedBackLayoutBinding

import com.yundou.loans.model.LoginViewModel
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.widget.clickNoRepeat


class FeedbackActivity : CommonActivity<LoginViewModel, FeedBackLayoutBinding>() {

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "意见反馈"
    }

    override fun getLayoutId(): Int {
        return R.layout.feed_back_layout
    }


    override fun init() {

        val partner_id = MmkvUtil.getInstance().decodeInt("partner_id")


        mBinding.txtFeedbackSubmit.clickNoRepeat {
            if (!TextUtils.isEmpty(mBinding.edtFeedbackContent.text.toString())) {
                if (partner_id==1) {
                    viewModel.feedback(mBinding.edtFeedbackContent.text.toString()) {
                        finish()
                    }
                } else {
                    viewModel.wMfeedback(mBinding.edtFeedbackContent.text.toString()) {
                        finish()
                    }
                }
            } else {
                viewModel.defUI.toastEvent.postValue("请输入投诉内容")
            }
        }
    }
}