package com.yundou.loans.widget

//import com.luck.picture.lib.listener.OnImageCompleteCallback
//import com.luck.picture.lib.tools.MediaUtils
//import com.luck.picture.lib.widget.longimage.ImageSource
//import com.luck.picture.lib.widget.longimage.ImageViewState
//import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.luck.picture.lib.engine.ImageEngine


class GlideEngine private constructor() : ImageEngine {
    /**
     * 加载图片
     *
     * @param context   上下文
     * @param url       资源url
     * @param imageView 图片承载控件
     */
    override fun loadImage(@NonNull context: Context, @NonNull url: String, @NonNull imageView: ImageView) {
        if (!assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    override fun loadImage(
        context: Context,
        imageView: ImageView,
        url: String,
        maxWidth: Int,
        maxHeight: Int
    ) {
        Glide.with(context)
            .asBitmap()
            .override(maxWidth, maxHeight)
            .load(url)
            .into(imageView)
    }

//    /**
//     * 加载指定url并返回bitmap
//     *
//     * @param context 上下文
//     * @param url     资源url
//     * @param call    回调接口
//     */
//    override fun loadImageBitmap(@NonNull context: Context, @NonNull url: String, maxWidth: Int, maxHeight: Int, call: OnCallbackListener<Bitmap>) {
//        if (!assertValidRequest(context)) {
//            return
//        }
//        Glide.with(context)
//            .asBitmap()
//            .override(maxWidth, maxHeight)
//            .load(url)
//            .into(object : CustomTarget<Bitmap>() {
//
//                override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
//                    call.onCall(null)
//                }
//
//                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    call.onCall(resource)
//                }
//            })
//    }


    /**
     * 加载相册目录封面
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAlbumCover(@NonNull context: Context, @NonNull url: String, @NonNull imageView: ImageView) {
        if (!assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .centerCrop()
            .sizeMultiplier(0.5f)
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, resource)
                    circularBitmapDrawable.cornerRadius = 8F
                    imageView.setImageDrawable(circularBitmapDrawable)
                }
            })
    }


    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(@NonNull context: Context, @NonNull url: String, @NonNull imageView: ImageView) {
        if (!assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .into(imageView)
    }


    override fun pauseRequests(context: Context?) {
        Glide.with(context!!).pauseRequests()
    }

    override fun resumeRequests(context: Context?) {
        Glide.with(context!!).resumeRequests()
    }


    fun assertValidRequest(context: Context?): Boolean {
        if (context is Activity) {
            return !isDestroy(context)
        } else if (context is ContextWrapper) {
            val contextWrapper = context
            if (contextWrapper.baseContext is Activity) {
                val activity = contextWrapper.baseContext as Activity
                return !isDestroy(activity)
            }
        }
        return true
    }

    private fun isDestroy(activity: Activity?): Boolean {
        return if (activity == null) {
            true
        } else activity.isFinishing || activity.isDestroyed
    }


    companion object {

         var instance: GlideEngine? = null

        fun createGlideEngine(): GlideEngine? {
            if (null == instance) {
                synchronized(GlideEngine::class.java) {
                    if (null == instance) {
                        instance = GlideEngine()
                    }
                }
            }
            return instance
        }
    }
}