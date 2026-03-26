package com.yundou.loans.widget

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.CenterPopupView
import com.yundou.loans.R
import com.yundou.loans.adapter.ZXDAgreementAdapter
import com.yundou.loans.databinding.FormStayDialogBinding
import com.yundou.loans.entity.ZxdNewResultProduceBean


/**
 * @Author: fenr
 * 时间: 2025/9/23
 * 类名: FormStayDialog
 * 简述: 表单挽留弹窗
 *
 */
class FormStayDialog(private var context: Context) : CenterPopupView(context) {

    private lateinit var mBinding: FormStayDialogBinding

    override fun getImplLayoutId(): Int {
        return R.layout.form_stay_dialog
    }

    override fun onCreate() {
        super.onCreate()

        mBinding = DataBindingUtil.bind(popupImplView)!!

        mBinding.agreementbtn.setOnClickListener {
            xieyiClick?.agreementClick(1)
        }
        mBinding.closeImg.setOnClickListener{
            xieyiClick?.agreementClick(0)
        }
    }


    private var xieyiClick: IXieyiDialogClick? = null

    fun setXieyiDialogClick(click: IXieyiDialogClick?) {
        this.xieyiClick = click
    }

    interface IXieyiDialogClick {
        fun agreementClick(type: Int)

    }


}