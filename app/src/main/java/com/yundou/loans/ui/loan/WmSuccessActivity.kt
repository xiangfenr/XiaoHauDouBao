package com.yundou.loans.ui.loan


import com.bumptech.glide.Glide
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.WmSuccessLayoutBinding
import com.yundou.loans.entity.JiyongOrderData
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.widget.clickNoRepeat
import com.yundou.loans.widget.gone
import com.yundou.loans.widget.visible


class WmSuccessActivity : CommonActivity<UserViewModel, WmSuccessLayoutBinding>() {


    override fun getLayoutId(): Int {
        return R.layout.wm_success_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "申请成功"
    }

    override fun init() {

        mBinding.back.clickNoRepeat {
            finish()
        }

        val orderData = intent.getSerializableExtra("orderData") as? JiyongOrderData
        if (null != orderData) {
            mBinding.productCardView.visible()

            if (null != orderData.logo && null != orderData.productName){
                Glide.with(this)
                    .load(orderData.logo)
                    .placeholder(R.mipmap.app_logo) // 加载中的占位图
                    .error(R.mipmap.app_logo) // 加载错误时的图片
                    .into(mBinding.ivCenterlistitemlistadapterIcon);

                mBinding.tvCenterlistitemlistadapterName1.text = orderData.productName
                mBinding.tvCenterlistitemlistadapterName2.text = orderData.companyName
            }

        } else {
            mBinding.productCardView.gone()
        }
    }


}