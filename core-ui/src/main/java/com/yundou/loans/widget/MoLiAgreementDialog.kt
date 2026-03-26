package com.yundou.loans.widget

import android.app.Activity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.lxj.xpopup.core.BottomPopupView
import com.yundou.loans.coreui.R
import com.yundou.loans.coreui.databinding.MoliXieyiDialogBinding
import com.yundou.loans.entity.MoliProductInfo


/**
 * @Author: fenr
 * 时间: 2025/4/2
 * 类名: ACTIVITY
 * 简述: 魔力-协议弹窗
 *
 */
class MoLiAgreementDialog(private var context: Activity) : BottomPopupView(context) {

    private lateinit var mBinding: MoliXieyiDialogBinding

    private var xieyiContent: String = ""

    private var moliProduct: MoliProductInfo? = null

    override fun getImplLayoutId(): Int {
        return R.layout.moli_xieyi_dialog
    }

    override fun onCreate() {
        super.onCreate()


        mBinding = DataBindingUtil.bind(popupImplView)!!

        moliProduct.let {
            Glide.with(this)
                .load(it?.logo)
                .placeholder(R.mipmap.app_logo) // 加载中的占位图
                .error(R.mipmap.app_logo) // 加载错误时的图片
                .into(mBinding.imgICon)

            mBinding.jigouname.text = it?.name
            mBinding.jigougongsiname.text = it?.operating_entity
        }

        //webview加载行内样式的文本
        mBinding.xieyiWebview.settings.javaScriptEnabled = true
        mBinding.xieyiWebview.loadDataWithBaseURL(null, xieyiContent, "text/html", "UTF-8", null)


        mBinding.agreementbtn.setOnClickListener {

            xieyiClick?.agreementClick()

        }

    }

    fun setAgreementtData(dxieyiContentta: String, infoData: MoliProductInfo) {
        this.xieyiContent = dxieyiContentta
        this.moliProduct = infoData

    }

    private var xieyiClick: IXieyiDialogClick? = null

    fun setXieyiDialogClick(click: IXieyiDialogClick?) {
        this.xieyiClick = click
    }

    interface IXieyiDialogClick {
        fun agreementClick()

    }


}