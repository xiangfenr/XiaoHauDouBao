package com.yundou.loans.widget

import android.content.Context
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.lxj.xpopup.core.BottomPopupView

import com.yundou.loans.coreui.R
import com.yundou.loans.adapter.TXFQAgreementAdapter
import com.yundou.loans.coreui.databinding.TxfqAgreementDialogBinding
import com.yundou.loans.entity.TxfqApplyData


/**
 * @Author: fenr
 * 时间: 2025/4/2
 * 类名: ACTIVITY
 * 简述: 天下分期协议弹窗
 *
 */
class TXFQAgreementDialog(private var context: Context) : BottomPopupView(context) {

    private lateinit var mBinding: TxfqAgreementDialogBinding
    private var agreementData: TxfqApplyData? = null

    private val adapter by lazy { TXFQAgreementAdapter() }

    override fun getImplLayoutId(): Int {
        return R.layout.txfq_agreement_dialog
    }

    override fun onCreate() {
        super.onCreate()

        mBinding = DataBindingUtil.bind(popupImplView)!!

        mBinding.xieyRecyclerview.layoutManager = LinearLayoutManager(context)
        mBinding.xieyRecyclerview.adapter = adapter


        agreementData?.let {
            adapter.setList(it.agreements?.defaultAgreements)
        }


        mBinding.agreementbtn.setOnClickListener {
            if (adapter.areaAllChecked()) {
                xieyiClick?.agreementClick()
            } else {
                Toast.makeText(context, "请阅读并同意相关协议", Toast.LENGTH_LONG)
                    .show()
            }
        }


//        mBinding.closeImg.clickNoRepeat {
//            XPopup.Builder(context).asConfirm(
//                "确定要关闭吗? ", "您只需要点击下方的同意协议按钮, 就能完成您的贷款申请!"
//            ) {
//                dismiss()
//            }.show()
//        }

    }

    fun setAgreementtData(data: TxfqApplyData) {
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