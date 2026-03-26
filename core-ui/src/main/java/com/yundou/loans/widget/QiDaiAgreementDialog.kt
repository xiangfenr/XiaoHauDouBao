package com.yundou.loans.widget

import android.app.Activity
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.core.BottomPopupView
import com.yundou.loans.coreui.R
import com.yundou.loans.adapter.QiDaiAgreementAdapter
import com.yundou.loans.coreui.databinding.QidaiAgreementDialogBinding
import com.yundou.loans.entity.QiDaiProductInfo


/**
 * @Author: fenr
 * 时间: 2025/4/2
 * 类名: ACTIVITY
 * 简述: 期贷-协议弹窗
 *
 */
class QiDaiAgreementDialog(private var context: Activity) : BottomPopupView(context) {

    private lateinit var mBinding: QidaiAgreementDialogBinding
    private var productInfo: QiDaiProductInfo? = null

    private val adapter by lazy { QiDaiAgreementAdapter() }

    override fun getImplLayoutId(): Int {
        return R.layout.qidai_agreement_dialog
    }

    override fun onCreate() {
        super.onCreate()

        mBinding = DataBindingUtil.bind(popupImplView)!!

        mBinding.xieyRecyclerview.layoutManager = LinearLayoutManager(context)
        mBinding.xieyRecyclerview.adapter = adapter


        productInfo?.let { it ->
//            Glide.with(this)
//                .load(it.productLogo)
//                .placeholder(R.mipmap.qidai_logo) // 加载中的占位图
//                .error(R.mipmap.qidai_logo) // 加载错误时的图片
//                .into(mBinding.imgICon)
//            mBinding.jigouname.text = it.companyName
//            mBinding.jigougongsiname.text = it.companyAddress

            adapter.setList( it.agreement)
        }

        mBinding.yijianCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                adapter.setAllCheckedTrue()
            } else {
                adapter.setAllCheckedFalse()
            }
        }

        mBinding.agreementbtn.setOnClickListener {
            if (mBinding.yijianCheckbox.isChecked){
                xieyiClick?.agreementClick()
            }else{
                Toast.makeText(context,"请先阅读查看协议",Toast.LENGTH_LONG).show()
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

    fun setAgreementtData(data: QiDaiProductInfo) {
        this.productInfo = data

    }

    override fun onDismiss() {
        context.finish()
        super.onDismiss()
    }


    private var xieyiClick: IXieyiDialogClick? = null

    fun setXieyiDialogClick(click: IXieyiDialogClick?) {
        this.xieyiClick = click
    }

    interface IXieyiDialogClick {
        fun agreementClick()

    }

}