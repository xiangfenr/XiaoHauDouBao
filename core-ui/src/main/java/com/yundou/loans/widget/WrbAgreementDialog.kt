package com.yundou.loans.widget

import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.yundou.loans.adapter.WrbAgreementAdapter
import com.yundou.loans.coreui.R
import com.yundou.loans.coreui.databinding.WrbAgreementDialogBinding
import com.yundou.loans.entity.WrbFormResultBean


/**
 * @Author: fenr
 * 时间: 2025/11/10
 * 类名: ACTIVITY
 * 简述: 微融宝-协议弹窗
 *
 */
class WrbAgreementDialog(private var context: Context) : BottomPopupView(context) {

    private lateinit var mBinding: WrbAgreementDialogBinding
    private var agreementData: WrbFormResultBean? = null

    private var timer: CountDownTimer? = null
    private val adapter by lazy { WrbAgreementAdapter() }

    override fun getImplLayoutId(): Int {
        return R.layout.wrb_agreement_dialog
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
            mBinding.jigouname.text = it.productName
            mBinding.jigougongsiname.text = it.companyName
            adapter.setList(it.agreements)
        }

        mBinding.agreementbtn.setOnClickListener {
            if (adapter.areaAllChecked()) {
                xieyiClick?.agreementClick()
            } else {
                Toast.makeText(context, "请阅读并同意相关协议", Toast.LENGTH_LONG)
                    .show()
            }
        }


    }

    fun setAgreementtData(data: WrbFormResultBean) {
        this.agreementData = data
    }

    private var xieyiClick: IXieyiDialogClick? = null

    fun setXieyiDialogClick(click: IXieyiDialogClick?) {
        this.xieyiClick = click
    }

    interface IXieyiDialogClick {
        fun agreementClick()

    }

    override fun onBackPressed(): Boolean {
        val popup = XPopup.Builder(context)
            .asConfirm(
                "",
                "您需同意服务协议及相关条款后，方可继续产品申请流程。",
                "取消",
                "同意协议",
                {
                    xieyiClick?.agreementClick()
                },
                {
                    dismiss()
                },
                false
            )

        // 修改按钮颜色
        popup.confirmTextView.setTextColor(Color.WHITE)
        popup.confirmTextView.setBackgroundResource(R.drawable.button_confirme)

        popup.cancelTextView.setTextColor(Color.GRAY)
        popup.cancelTextView.setBackgroundResource(R.drawable.button_cancel)

        popup.show()

        return super.onBackPressed()

    }


    override fun onDismiss() {
        timer?.cancel()
        super.onDismiss()

    }

}