package com.yundou.loans.base

import android.app.VoiceInteractor
import android.view.View


/**
 *@ClassName IBaseAction
 *@Deseription iew 层基础接口行为类
 *@author：wangmingyu
 *@date：2020/8/1214:30
 */
interface IBaseAction {
    /**
     * 显示一个Snakbar
     *
     * @param msg
     * @param type
     */
    fun showSnackbar(msg: String?, type: VoiceInteractor.Prompt?)

    /**
     * 带按钮的实现
     *
     * @param msg           消息
     * @param clickListener 点击事件
     */
    fun confrimSnackBar(msg: String?, clickListener: View.OnClickListener?)


    /**
     * loading
     *
     */
    fun showLoading()


    /**
     * 销毁进度条
     */
    fun dismissLoading()


}