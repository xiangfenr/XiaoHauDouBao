package com.yundou.loans.widget

import android.content.Context
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.yundou.loans.coreui.R
import com.yundou.loans.adapter.XiaoFuJKAgreementAdapter
import com.yundou.loans.coreui.databinding.AsrAgreementDialogBinding
import com.yundou.loans.entity.XiaoFuProduct


/**
 * @Author: fenr
 * 时间: 2025/12/3
 * 类名: ACTIVITY
 * 简述: 小福借款 - Agreement Dialog
 *
 */
class XiaoFuJKAgreementDialog(private var context: Context) : BottomPopupView(context) {

    private lateinit var mBinding: AsrAgreementDialogBinding
    private var agreementData: XiaoFuProduct? = null

    private val adapter by lazy { XiaoFuJKAgreementAdapter() }

    override fun getImplLayoutId(): Int {
        return R.layout.asr_agreement_dialog
    }

    override fun onCreate() {
        super.onCreate()

        mBinding = DataBindingUtil.bind(popupImplView)!!

        mBinding.xieyRecyclerview.layoutManager = LinearLayoutManager(context)
        mBinding.xieyRecyclerview.adapter = adapter


        agreementData?.let {
            Glide.with(this)
                .load(it.logo)
                .placeholder(R.mipmap.app_logo) // 加载中的占位图
                .error(R.mipmap.app_logo) // 加载错误时的图片
                .into(mBinding.imgICon)

            mBinding.jigouname.text = it.name
            mBinding.jigougongsiname.text = it.platform

            adapter.setList(it.agreement_list)
        }


        mBinding.agreementbtn.setOnClickListener {
            if (adapter.areaAllChecked()) {
                xieyiClick?.agreementClick()
            } else {
                Toast.makeText(context, "请阅读并同意相关协议", Toast.LENGTH_LONG)
                    .show()
            }
        }



        mBinding.closeImg.clickNoRepeat {
            XPopup.Builder(context).asConfirm(
                "确定要关闭吗? ", "您只需要点击下方的同意协议按钮, 就能完成您的贷款申请!"
            ) {
                dismiss()
            }.show()
        }

    }

    fun setAgreementtData(data: XiaoFuProduct) {
        this.agreementData = data

    }

    private var xieyiClick: IXieyiDialogClick? = null

    fun setXieyiDialogClick(click: IXieyiDialogClick?) {
        this.xieyiClick = click
    }

    interface IXieyiDialogClick {
        fun agreementClick()

    }

}