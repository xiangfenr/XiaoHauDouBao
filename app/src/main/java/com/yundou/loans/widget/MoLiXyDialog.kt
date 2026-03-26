package com.yundou.loans.widget

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.yundou.loans.coreui.databinding.MoliXieyiDialogBinding

/**
 * @Author: fenr
 * 时间: 2025/1/8
 * 类名: ACTIVITY
 * 简述: 魔力28  匹配机构的弹窗
 *
 */
class MoLiXyDialog(
    private var mContext: Context,
    private var xieyiContent:String
) : XsBaseBottomDialog<MoliXieyiDialogBinding>(mContext) {

    override var isHideable: Boolean = false

    override fun inflateBinding(): MoliXieyiDialogBinding {
        return MoliXieyiDialogBinding.inflate(layoutInflater)
    }

    override fun initData() {
        //webview加载行内样式的文本
        binding.xieyiWebview.settings.javaScriptEnabled = true
       binding.xieyiWebview.loadDataWithBaseURL(null, xieyiContent, "text/html", "UTF-8", null)

    }

    override fun initLiveData() {
    }

    override fun initListener() {

        binding.agreementbtn.setOnClickListener {
            //我在本群昵称
            xieyiClick?.agreementClick()
        }

    }

    override fun initAfterView() {

    }


    private var xieyiClick: IXieyiDialogClick? = null

    fun setXieyiDialogClick(click: IXieyiDialogClick?) {
        this.xieyiClick = click
    }

    interface IXieyiDialogClick {
        fun agreementClick()

    }


}