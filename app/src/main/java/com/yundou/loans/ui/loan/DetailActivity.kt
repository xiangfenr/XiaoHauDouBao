package com.yundou.loans.ui.loan

import android.content.Intent
import android.view.Gravity
import android.widget.TextView
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.DetailActivityLayoutBinding


import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.RepayDateUtils
import com.yundou.loans.widget.CommonDialog
import com.yundou.loans.widget.clickNoRepeat

class DetailActivity : CommonActivity<UserViewModel, DetailActivityLayoutBinding>() {


    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "详情"
    }


    override fun getLayoutId(): Int {
        return R.layout.detail_activity_layout
    }

    override fun init() {

        mBinding.tvli.clickNoRepeat {
            startActivity(Intent(this, DetailItemActivity::class.java))
        }

        mBinding.tvDetail.clickNoRepeat {
            val codeDialog = CommonDialog.Builder(this)
                .setContentView(R.layout.dialog_detail)
                .setCancelable(true)
                .setGravity(Gravity.BOTTOM)
                .setCanceledOnTouchOutside(true)
                .setFullWidth()
                .create()
            codeDialog?.findViewById<TextView>(R.id.tv_diss)?.clickNoRepeat { codeDialog?.dismiss() }
            codeDialog?.findViewById<TextView>(R.id.tv_ok)?.clickNoRepeat {
                codeDialog.dismiss()
            }

            //获取当前时间
            val firstLaunchDate = RepayDateUtils.getFirstLaunchDate(this)
            val dateAfter25Days = RepayDateUtils.getDateAfter25Days(firstLaunchDate)
            codeDialog?.findViewById<TextView>(R.id.jiekuanTv)?.text =
                "$firstLaunchDate 借30000元 还剩1期"
            codeDialog?.findViewById<TextView>(R.id.huankuanTv)?.text = "$dateAfter25Days"

            codeDialog?.show()
        }
    }
}