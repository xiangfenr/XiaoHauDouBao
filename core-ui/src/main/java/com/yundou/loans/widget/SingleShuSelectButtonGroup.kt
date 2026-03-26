package com.yundou.loans.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.yundou.loans.coreui.R

class SingleShuSelectButtonGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private var selectedIndex: Int = -1

    init {
        // 读取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SingleSelectButtonGroup)
        columnCount = typedArray.getInt(R.styleable.SingleSelectButtonGroup_columnCount, 2) // 默认2列
        typedArray.recycle()
    }

    fun setButtons(labels: List<String?>, onButtonSelected: ((index: Int, label: String) -> Unit)? = null) {
        removeAllViews()
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
     * 更新选中状态
     */
    private fun updateSelection(index: Int) {
        if (index == selectedIndex) return
        selectedIndex = index

        for (i in 0 until childCount) {
            val child = getChildAt(i) as TextView
            if (i == index) {
                val drawable = ContextCompat.getDrawable(context, R.drawable.login)
                child.background = drawable
                child.setTextColor(Color.BLACK)
            } else {
                val drawable = ContextCompat.getDrawable(context, R.drawable.button_clicke_red_bg)
                child.background = drawable
                val colorStateList =
                    ContextCompat.getColorStateList(context, R.color.button_text_color)
                child.setTextColor(colorStateList)
            }
        }
    }

    /**
     * 创建按钮
     */
    private fun createButton(label: String, index: Int): TextView {
        return TextView(context).apply {
            text = label
            textSize = 14f
            height = 80
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
            maxLines = 1
            val drawable = ContextCompat.getDrawable(context, R.drawable.button_clicke_red_bg)
            background = drawable
            val colorStateList = ContextCompat.getColorStateList(context, R.color.button_text_color)
            setTextColor(colorStateList)

            layoutParams = LayoutParams().apply {
                width = 0
                height = LayoutParams.WRAP_CONTENT
                columnSpec = spec(UNDEFINED, 1f)
                rowSpec = spec(UNDEFINED)
                setMargins(10, 16, 10, 16)
            }
        }
    }
}
