package com.yundou.loans.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.yundou.loans.coreui.R

class SingleHengSelectButtonGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private var selectedIndex: Int = -1

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SingleSelectButtonGroup, defStyleAttr, 0)
        columnCount = typedArray.getInt(R.styleable.SingleSelectButtonGroup_columnCount, 3)
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

    private fun updateSelection(index: Int) {
        if (index == selectedIndex) return
        selectedIndex = index

        for (i in 0 until childCount) {
            val child = getChildAt(i) as TextView
            if (i == index) {
                child.background = ContextCompat.getDrawable(context, R.drawable.login)
                child.setTextColor(ContextCompat.getColor(context, R.color.main_color))
            } else {
                child.background = ContextCompat.getDrawable(context, R.drawable.gray_button_bg_stroke)
                child.setTextColor(ContextCompat.getColor(context, R.color.color_txt2))
            }
        }
    }

    private fun createButton(label: String, index: Int): TextView {
        return TextView(context).apply {
            text = label
            textSize = 13f
            gravity = Gravity.CENTER
            minWidth = 180
            setPadding(22, 22, 22, 22)
            background = ContextCompat.getDrawable(context, R.drawable.gray_button_bg_stroke)
            setTextColor(ContextCompat.getColor(context, R.color.color_txt2))
            layoutParams = LayoutParams().apply {
                width = LayoutParams.WRAP_CONTENT
                height = LayoutParams.WRAP_CONTENT
              //  columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
              //  rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
                setMargins(16, 16, 16, 16)
            }
        }
    }
}
