package com.yundou.loans.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView

/**
 * @Author: fenr
 * 时间: 2025/2/7
 * 类名: ACTIVITY
 * 简述:
 *
 */
class TagsLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    // 标签点击回调接口
    interface OnTagClickListener {
        fun onTagClick(position: Int, text: String)
    }

    private var horizontalSpacing = 8.dpToPx()  // 水平间距
    private var verticalSpacing = 4.dpToPx()     // 垂直间距
   // private var tagBackgroundRes = R.drawable.tag_background // 标签背景
    private var tagTextColor = Color.BLUE       // 标签文字颜色
    private var tagPadding = 4.dpToPx()          // 标签内边距

    var onTagClickListener: OnTagClickListener? = null

    // 测量子View并确定容器尺寸
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0
        var currentWidth = 0
        var currentHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            if (currentWidth + child.measuredWidth > width) {
                height += currentHeight + verticalSpacing
                currentWidth = 0
                currentHeight = 0
            }

            currentWidth += child.measuredWidth + horizontalSpacing
            currentHeight = maxOf(currentHeight, child.measuredHeight)
        }

        height += currentHeight
        setMeasuredDimension(width, height)
    }

    // 布局子View位置
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        var currentTop = 0
        var currentLeft = 0
        var maxHeightInRow = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (currentLeft + child.measuredWidth > width) {
                currentTop += maxHeightInRow + verticalSpacing
                currentLeft = 0
                maxHeightInRow = 0
            }

            child.layout(
                currentLeft,
                currentTop,
                currentLeft + child.measuredWidth,
                currentTop + child.measuredHeight
            )

            currentLeft += child.measuredWidth + horizontalSpacing
            maxHeightInRow = maxOf(maxHeightInRow, child.measuredHeight)
        }
    }

    // 设置标签数据
    fun setTags(tags: List<String>) {
        removeAllViews()
        tags.forEachIndexed { index, text ->
            addView(createTagView(text, index))
        }
    }

    // 创建单个标签View
    private fun createTagView(text: String, position: Int): TextView {
        return TextView(context).apply {
            this.text = text
            //标签背景
            //setBackgroundResource(tagBackgroundRes)
            setTextColor(tagTextColor)
            setPadding(tagPadding, tagPadding/2, tagPadding, tagPadding/2)
            setOnClickListener {
                onTagClickListener?.onTagClick(position, text)
            }
        }
    }

    // 扩展函数：dp转px
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
