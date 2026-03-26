package com.yundou.loans.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.yundou.loans.coreui.R

/**
 * @Author: fenr
 * 时间: 2025/1/14
 * 类名: ACTIVITY
 * 简述:  多选控件
 *
 */class MultiSelectButtonGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private val selectedIndices = mutableSetOf<Int>() // 存储选中的索引

    init {
        // 读取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SingleSelectButtonGroup)
        columnCount = typedArray.getInt(R.styleable.SingleSelectButtonGroup_columnCount, 2) // 默认2列
        typedArray.recycle()
    }

    /**
     * 设置按钮
     * @param labels 按钮的文字列表
     * @param onSelectionChanged 按钮选中状态改变的回调
     */
    fun setButtons(labels: List<String>, onSelectionChanged: ((selectedIndices: Set<Int>) -> Unit)? = null) {
        removeAllViews() // 清除已有按钮
        labels.forEachIndexed { index, label ->
            val button = createButton(label, index)
            button.setOnClickListener {
                toggleSelection(index)
                onSelectionChanged?.invoke(selectedIndices)
            }
            addView(button)
        }
    }

    /**
     * 切换选中状态
     * @param index 被点击按钮的索引
     */
    private fun toggleSelection(index: Int) {
        if (selectedIndices.contains(index)) {
            selectedIndices.remove(index) // 如果已经选中，则取消选中
        } else {
            selectedIndices.add(index) // 如果未选中，则选中
        }

        updateButtonStates()
    }

    /**
     * 更新所有按钮的状态
     */
    private fun updateButtonStates() {
        for (i in 0 until childCount) {
            val child = getChildAt(i) as TextView
            if (selectedIndices.contains(i)) {
                val drawable = ContextCompat.getDrawable(context, R.drawable.login)
                child.background = drawable
                child.setTextColor(Color.BLACK)

                val drawable_up = ContextCompat.getDrawable(context, R.mipmap.up_white)
                child.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,        // start (对应 left，在 RTL 布局中会自动适配)
                    null,        // top
                    drawable_up,    // end (对应 right)
                    null         // bottom
                )
            } else {
                val drawable = ContextCompat.getDrawable(context, R.drawable.button_clicke_red_bg)
                child.background = drawable
                val colorStateList =
                    ContextCompat.getColorStateList(context, R.color.button_text_color)
                child.setTextColor(colorStateList)

                val drawable_up = ContextCompat.getDrawable(context, R.mipmap.shangshen)
                child.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,        // start (对应 left，在 RTL 布局中会自动适配)
                    null,        // top
                    drawable_up,    // end (对应 right)
                    null         // bottom
                )
            }
        }
    }

    /**
     * 创建按钮
     * @param label 按钮文字
     * @param index 按钮索引
     */
    private fun createButton(label: String, index: Int): TextView {
        return TextView(context).apply {
            text = label
            textSize = 14f
            height = 80
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            val drawable = ContextCompat.getDrawable(context, R.drawable.button_clicke_red_bg)
            background = drawable
            val colorStateList = ContextCompat.getColorStateList(context, R.color.button_text_color)
            setTextColor(colorStateList)
            val drawable_up = ContextCompat.getDrawable(context, R.mipmap.shangshen)
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,        // start (对应 left，在 RTL 布局中会自动适配)
                null,        // top
                drawable_up,    // end (对应 right)
                null         // bottom
            )
            setCompoundDrawablePadding((5)) // 需要将 dp 转为 px


            layoutParams = LayoutParams().apply {
                width = 0
                height = LayoutParams.WRAP_CONTENT
                columnSpec = spec(UNDEFINED, 1f) // 每列等分
                rowSpec = spec(UNDEFINED)
                setMargins(10, 16, 10, 16)
            }
        }
    }
}
