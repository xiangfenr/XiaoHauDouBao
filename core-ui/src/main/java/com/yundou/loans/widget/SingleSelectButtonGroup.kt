package com.yundou.loans.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.yundou.loans.coreui.R

class SingleSelectButtonGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private var selectedIndex: Int = -1

    init {
        columnCount = 2 // 每行显示两个按钮
    }

    /**
     * 设置按钮
     * @param labels 按钮的文字列表
     * @param onButtonSelected 按钮选中事件回调
     */
    fun setButtons(
        labels: List<String?>,
        onButtonSelected: ((index: Int, label: String) -> Unit)? = null
    ) {
        removeAllViews() // 清除已有按钮
        labels.forEachIndexed { index, label ->
            val button = createButton(label!!, index)
            button.setOnClickListener {
                updateSelection(index)
                onButtonSelected?.invoke(index, label)
            }
            addView(button)
        }
    }

    /**
     * 动态设置列数
     */
    fun setColumnCountDynamic(count: Int) {
        if (count > 0) {
            columnCount = count
            requestLayout()
        }
    }

    /**
     * 更新选中状态
     * @param index 被选中按钮的索引
     */
    private fun updateSelection(index: Int) {
        if (index == selectedIndex) return // 如果点击的是当前选中的按钮，则不更新
        selectedIndex = index

        for (i in 0 until childCount) {
            val child = getChildAt(i) as TextView
            if (i == index) {
                val drawable = ContextCompat.getDrawable(context, R.drawable.login)
                child.background = drawable // 高亮选中
                child.setTextColor(ContextCompat.getColor(context,R.color.main_color))
            } else {
                val drawable = ContextCompat.getDrawable(context, R.drawable.button_clicke_red_bg)
                child.background = drawable // 未选中置灰
                val colorStateList =
                    ContextCompat.getColorStateList(context, R.color.button_text_color)
                child.setTextColor(colorStateList)
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
            setPadding(32, 16, 32, 16)
            maxLines = 1
            val drawable = ContextCompat.getDrawable(context, R.drawable.button_clicke_red_bg)
            background = drawable // 未选中置灰
            val colorStateList = ContextCompat.getColorStateList(context, R.color.button_text_color)
            setTextColor(colorStateList)

            layoutParams = LayoutParams().apply {
                width = 0
                height = LayoutParams.WRAP_CONTENT
                columnSpec = spec(UNDEFINED, 1f) // 每列等分
                rowSpec = spec(UNDEFINED)
                setMargins(16, 16, 16, 16)
            }
        }
    }
}
