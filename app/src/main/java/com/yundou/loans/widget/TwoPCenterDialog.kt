package com.yundou.loans.widget

import android.content.Context
import android.widget.ProgressBar
import com.lxj.xpopup.core.CenterPopupView
import com.yundou.loans.R

/**
 * @Author: fenr
 * 时间: 2025/4/15
 * 类名: ACTIVITY
 * 简述: 二项目弹窗
 */
class TwoPCenterDialog(context: Context) : CenterPopupView(context) {
    private var progressBar: ProgressBar? = null

    override fun getImplLayoutId(): Int {
        return R.layout.two_center_dialog
    }


    override fun onCreate() {
        super.onCreate()

        progressBar = findViewById(R.id.progressBar)
    }

    fun setProGress(progress: Int) {
        progressBar?.progress = progress
    }


//        @Override
    //        protected int getMaxHeight() {
    //            return 200;
    //        }
    //
    //        @Override
    //        protected int getMaxWidth() {
    //            return 1000;
    //        }
}