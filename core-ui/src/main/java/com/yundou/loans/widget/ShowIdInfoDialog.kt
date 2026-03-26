package com.yundou.loans.widget

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.yundou.loans.coreui.R
import com.yundou.loans.coreui.databinding.ShowIdInfoDialogBinding


/**
 * @Author: fenr
 * 时间: 2025/12/3
 * 类名: ACTIVITY
 * 简述: 身份信息填写提示弹窗
 *
 */
class ShowIdInfoDialog(context: Context) : CenterPopupView(context) {

    private var contentText: String = "默认文本内容"
    private var buttonText: String = "关闭"
    private var onCloseListener: (() -> Unit)? = null
    private lateinit var mBinding: ShowIdInfoDialogBinding

    override fun getImplLayoutId(): Int {
        return R.layout.show_id_info_dialog
    }

    override fun onCreate() {
        super.onCreate()
        mBinding = DataBindingUtil.bind(popupImplView)!!

        // 设置关闭按钮点击事件
        mBinding.tvAgree.setOnClickListener {
            dismiss() // 关闭弹窗
            onCloseListener?.invoke() // 触发回调
        }
    }
    /**
     * 设置弹窗内容
     */
    fun setContent(text: String): ShowIdInfoDialog {
        this.contentText = text
        return this // 支持链式调用
    }

    /**
     * 设置按钮文本
     */
    fun setButtonText(text: String): ShowIdInfoDialog {
        this.buttonText = text
        return this
    }

    /**
     * 设置关闭回调
     */
    fun setOnCloseListener(listener: () -> Unit): ShowIdInfoDialog {
        this.onCloseListener = listener
        return this
    }

    /**
     * 快速创建方法
     */
    companion object {
//           .asCustom(ShowIdInfoDialog(context).setContent(content))
        fun show(context: Context, content: String = "提示信息") {
            XPopup.Builder(context)
                .asCustom(ShowIdInfoDialog(context))
                .show()
        }
    }
}