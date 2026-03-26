package com.yundou.loans.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


@Suppress("UNREACHABLE_CODE")
class GlideUtils {
    companion object {
        fun getInstance() = SingleHolder.instance
    }

    /**
     * 通过内部静态类保证线程双重校验的安全性
     **/
    object SingleHolder {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GlideUtils()
        }
    }

    fun assertValidRequest(context: Context?): Boolean {
        if (context is Activity) {
            return !isDestroy(context)
        } else if (context is ContextWrapper) {
            if (context.baseContext is Activity) {
                val activity = context.baseContext as Activity
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


    /**
     * 普通加载 这种方式会与的Adapter绑定
     *
     * @param context    上下文  传递 helper.context
     * @param url         地址
     * @param imageView   加载的view
     * @param placeholder 占位的图片
     * @param errorImg    占位的图片
     */
    fun showImageViewAdapter(
        context: Context, url: String, imageView: ImageView, placeholder: Int, errorImg: Int,
    ) {
        if (!assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .load(url)
            .placeholder(placeholder)
            .error(errorImg)
            .fitCenter()
            .apply(
                RequestOptions().format(DecodeFormat.PREFER_RGB_565).dontAnimate()
                    .priority(com.bumptech.glide.Priority.LOW)
                    .diskCacheStrategy(DiskCacheStrategy.DATA).skipMemoryCache(true).fitCenter()
            )
            .into(imageView)
    }


    /**
     * 加载二进制流
     * @param context    上下文  传递 helper.context
     * @param url         地址
     * @param imageView   加载的view
     */
    fun showImageBase64(
        context: Context,
        byteStr: String,
        imageView: ImageView,
        placeholder: Int?,
    ) {
        if (!assertValidRequest(context)) {
            return
        }
        val bytes = Base64.decode(byteStr, Base64.DEFAULT)
        if (placeholder == null) {
            Glide.with(context).load(bytes).into(imageView)
        } else {
            placeholder.let {
                Glide.with(context).load(bytes).placeholder(it).error(it).into(imageView)
            }
        }

    }


    //加载动态图
    fun showImageGif(context: Context?, url: String, imageView: ImageView) {
        if (!assertValidRequest(context)) {
            return
        }
        val options = RequestOptions()
        Glide.with(context!!).asGif().load(url).apply(options)
            .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView)
    }

    /**
     * 加载地址 [url] 图片返回 Bitmap
     */
    fun load(context: Context, url: String?, success: (Bitmap) -> Unit, err: (String) -> Unit) {
        try {
            if (!assertValidRequest(context)) {
                return
            }
            Glide.with(context) // context，可添加到参数中
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        // 成功返回 Bitmap
                        success.invoke(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        err.invoke("图片加载失败")
                    }
                })
        } catch (e: IllegalArgumentException) {
//                imageView.setImageResource(errorImg)
            err.invoke("图片加载失败")
        }

    }

}
