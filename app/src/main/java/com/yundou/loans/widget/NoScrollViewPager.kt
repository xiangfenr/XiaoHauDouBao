package com.yundou.loans.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @Author: fenr
 * 时间: 2025/11/7
 * 类名: ACTIVITY
 * 简述: 禁止ViewPager左右滑动
 *
 */
class NoScrollViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewPager(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent?) = false
    override fun onTouchEvent(ev: MotionEvent?) = false
}