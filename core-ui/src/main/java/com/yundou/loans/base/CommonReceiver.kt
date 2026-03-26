package com.yundou.loans.base

import android.app.VoiceInteractor
import android.view.View
import androidx.appcompat.app.AppCompatActivity

open class CommonReceiver : AppCompatActivity(), IBaseAction{
    override fun showSnackbar(msg: String?, type: VoiceInteractor.Prompt?) {
    }

    override fun confrimSnackBar(msg: String?, clickListener: View.OnClickListener?) {
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }


    /**
     * 此方法用于判断是否使用公共模板
     *  设置为false 需要开发人员自定义 标题等组件
     *  默认为true
     */
    open fun isShowActionBar(): Boolean {
        return false
    }

}