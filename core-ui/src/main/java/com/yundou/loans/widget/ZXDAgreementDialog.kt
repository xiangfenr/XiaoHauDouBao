package com.yundou.loans.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.yundou.loans.coreui.R
import com.yundou.loans.adapter.ZXDAgreementAdapter
import com.yundou.loans.coreui.databinding.ZxdAgreementDialogBinding
import com.yundou.loans.entity.ZxdNewResultProduceBean
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil


/**
 * @Author: fenr
 * 时间: 2025/4/2
 * 类名: ACTIVITY
 * 简述: 智享贷-协议弹窗
 *
 */
class ZXDAgreementDialog(private var context: Context) : BottomPopupView(context) {

    private lateinit var mBinding: ZxdAgreementDialogBinding
    private var agreementData: ZxdNewResultProduceBean? = null

    private var timer: CountDownTimer? = null

    override fun getImplLayoutId(): Int {
        return R.layout.zxd_agreement_dialog
    }

    override fun onCreate() {
        super.onCreate()


        mBinding = DataBindingUtil.bind(popupImplView)!!

//        mBinding.xieyRecyclerview.layoutManager = LinearLayoutManager(context)
//        mBinding.xieyRecyclerview.adapter = adapter
        val webSettings = mBinding.xieyiWebview.getSettings()


        // 启用JavaScript支持
        webSettings.javaScriptEnabled = true
        // 启用DOM存储API支持
        webSettings.domStorageEnabled = true
        // 启用数据库存储API支持
        webSettings.databaseEnabled = true
        // 设置Web视口的宽度适应屏幕
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        // 把文字整体放大，比如 150%
        webSettings.textZoom = 190
        webSettings.defaultFontSize = 18     // 默认是 16
        webSettings.minimumFontSize = 12     // 默认是 8

        mBinding.xieyiWebview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val js = "document.getElementsByTagName('body')[0].style.fontSize='22px';" +
                        "document.body.style.paddingBottom='80px';"

                mBinding.xieyiWebview.evaluateJavascript(js, null)
            }
        }

        agreementData?.let {
            Glide.with(this)
                .load(it.logoUrl)
                .placeholder(R.mipmap.app_logo) // 加载中的占位图
                .error(R.mipmap.app_logo) // 加载错误时的图片
                .into(mBinding.imgICon)
            mBinding.jigouname.text = it.productName
            mBinding.jigougongsiname.text = it.productOrgName
            mBinding.xieyiWebview.loadUrl(it.agreementList[0].protocolUrl ?: "")
        }


        mBinding.agreementbtn.setOnClickListener {
            timer?.cancel()
            xieyiClick?.agreementClick()
        }


    }

    fun setAgreementtData(data: ZxdNewResultProduceBean) {
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

    override fun onShow() {

        //是否点击协议后在进件---1是，2否
        val zxd_click_deal = MmkvUtil.getInstance().decodeInt(Constants.ZXD_CLICKDEAL)
        if (zxd_click_deal == 2) {
            countDownTimer()
        }
        super.onShow()
    }

    /*
   *倒计时开始¬
   */
    private fun countDownTimer() {
        var num = 10
        timer = object : CountDownTimer((num + 1) * 1000L, 1000L) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if (num == 0) {
                    num = 0
                } else {
                    num--
                }
                mBinding.agreementbtn.text = "同意协议 激活额度 ($num)"
            }

            override fun onFinish() {
                xieyiClick?.agreementClick()
                mBinding.agreementbtn.text = "同意协议 激活额度"
            }
        }
        timer?.start()
    }

    override fun onDismiss() {
        timer?.cancel()
        super.onDismiss()

    }

}