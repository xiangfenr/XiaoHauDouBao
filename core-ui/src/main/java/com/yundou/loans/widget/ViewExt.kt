package com.yundou.loans.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat

/**
 * 设置view显示
 */
fun View.visible() {
    visibility = View.VISIBLE
}


/**
 * 设置view占位隐藏
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * 根据条件设置view显示隐藏 为true 显示，为false 隐藏
 */
fun View.visibleOrGone(flag: Boolean) {
    visibility = if (flag) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/**
 * 根据条件设置view显示隐藏 为true 显示，为false 隐藏
 */
fun View.visibleOrInvisible(flag: Boolean) {
    visibility = if (flag) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

/**
 * 设置view隐藏
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * 将view转为bitmap
 */
@Deprecated("use View.drawToBitmap()")
fun View.toBitmap(scale: Float = 1f, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    if (this is ImageView) {
        if (drawable is BitmapDrawable) return (drawable as BitmapDrawable).bitmap
    }
    this.clearFocus()
    val bitmap = createBitmapSafely(
        (width * scale).toInt(),
        (height * scale).toInt(),
        config,
        1
    )
    if (bitmap != null) {
        Canvas().run {
            setBitmap(bitmap)
            save()
            drawColor(Color.WHITE)
            scale(scale, scale)
            this@toBitmap.draw(this)
            restore()
            setBitmap(null)
        }
    }
    return bitmap
}

fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(
                width,
                height,
                config,
                retryCount - 1
            )
        }
        return null
    }
}


/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 * @param interval 时间间隔 默认0.5秒
 * @param action 执行方法
 */
var lastClickTime = 0L
fun View.clickNoRepeat(interval: Long = 500, action: (view: View) -> Unit) {
    setOnClickListener {
        it.requestFocus()
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action(it)
    }
}
fun View.clickNoRepeat2(interval: Long = 0, action: (view: View) -> Unit) {
    setOnClickListener {
        it.requestFocus()
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = currentTime
        action(it)
    }
}

fun View.click(interval: Long = 500, action: (view: View) -> Unit) {
    clickNoRepeat(interval, action)
}

fun Any?.notNull(notNullAction: (value: Any) -> Unit, nullAction1: () -> Unit) {
    if (this != null) {
        notNullAction.invoke(this)
    } else {
        nullAction1.invoke()
    }
}

fun String.getSpanClick(
    clickStr: String,
    textColor: String?,
    click: () -> Unit,
): SpannableString {
    val spannableString = SpannableString(this)
    val start = this.indexOf(clickStr)
    val end = start + clickStr.length
    //这一行是实现局部点击效果，实现Clickable（自定义的继承ClickableSpan implements OnClickListener）
    spannableString.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            click.invoke()
        }

    },
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    textColor?.let {
        //这一行是设置文字颜色的
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor(textColor)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    //这一行主要是用来消除点击文字下划线的
    spannableString.setSpan(NoUnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannableString
}

//设置点击效果。 使用此方法一定要设置富文本点击事件
fun Spanned.getSpanClick(
    clickStr: String,
    textColor: String?,
    boolean: Boolean=false,
    click: () -> Unit,

): SpannableString {
    val spannableString = SpannableString(this)
    val start = this.indexOf(clickStr)
    if(start==-1) return spannableString
    if(start<0|| clickStr.isEmpty()||start==clickStr.length) return spannableString
    val end = start + clickStr.length
    //这一行是实现局部点击效果，实现Clickable（自定义的继承ClickableSpan implements OnClickListener）
    //这一行是设置文字颜色的
    spannableString.setSpan(ForegroundColorSpan(Color.parseColor(textColor)),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            click.invoke()
        }
    },
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    //这一行主要是用来消除点击文字下划线的
    if(!boolean)
        spannableString.setSpan(NoUnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


    return spannableString
}

//去除下划线
class NoUnderlineSpan : UnderlineSpan() {
    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }
}
/**
 * 设置背景
 * @receiver View
 * @param context Context
 * @param id Int
 */
fun View.background(context: Context, id:Int) {
   background=ContextCompat.getDrawable(context,id)
}
