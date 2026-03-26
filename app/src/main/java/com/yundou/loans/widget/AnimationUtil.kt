package com.yundou.loans.widget

import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation

object AnimationUtil {
    @JvmStatic
    fun slideToDown(view: View, listener: AnimationEndListener?) {
        val slide: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 1.0f
        )
        slide.duration = 200
        slide.isFillEnabled = true
        slide.fillAfter = true
        view.startAnimation(slide)
        slide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                listener?.onFinish()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    @JvmStatic
    fun slideToUp(view: View) {
        val slide: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        )
        slide.duration = 200
        slide.isFillEnabled = true
        slide.fillAfter = true
        view.startAnimation(slide)
    }

    interface AnimationEndListener {
        fun onFinish()
    }
}