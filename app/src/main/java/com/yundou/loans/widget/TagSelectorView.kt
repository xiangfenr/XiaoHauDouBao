package com.yundou.loans.widget


import android.animation.ValueAnimator
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.yundou.loans.R

/**
 * @Author: fenr
 * 时间: 2025/9/17
 * 类名: ACTIVITY
 * 简述: 折叠组件
 *
 */
class TagSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val titleTv: TextView
    private val selectedTv: TextView
    private val imgArrow: ImageView
    private val flexbox: SingleSelectButtonGroup
    private var isExpanded = true
    private var columnCount=2

    // 声明一个可空的函数类型
    private var onTagSelected: ((text: String, index: Int) -> Unit)? = null

    // 设置回调方法
    fun setOnTagSelectedListener(listener: (text: String, index: Int) -> Unit) {
        onTagSelected = listener
    }

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_tag_selector, this, true)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SingleSelectButtonGroup)
        columnCount = typedArray.getInt(R.styleable.SingleSelectButtonGroup_columnCount, 2) // 默认2列

        titleTv = findViewById(R.id.tvTitle)
        selectedTv = findViewById(R.id.tvSelected)
        imgArrow = findViewById(R.id.imgArrow)
        flexbox = findViewById(R.id.buttonGroun)

        // 点击第一行展开/收起
        findViewById<View>(R.id.topRow).setOnClickListener {
            if (!TextUtils.isEmpty(selectedTv.text)){
                toggleExpand()
            }

        }
    }

    fun setTitle(title: String) {
        titleTv.text = title
    }

    fun setTags(tags: List<String>) {
        flexbox.setButtons(tags ) { index, label ->
            selectedTv.text = label
            toggleExpand()
            onTagSelected?.invoke(label, index)
        }
        flexbox.setColumnCountDynamic(columnCount)
    }

    private fun toggleExpand() {
//        if (isExpanded) {
//            collapse()
//        } else {
//            expand()
//        }
        isExpanded = !isExpanded
//        imgArrow.rotation = if (isExpanded) 0f else 180f
        imgArrow.animate()
            .rotation(if (isExpanded) 0f else 180f)
            .setDuration(300) // 动画持续时间，单位毫秒
            .setInterpolator(AccelerateDecelerateInterpolator()) // 加速减速插值器，更自然
            .start()
    }

    private fun expand() {
        flexbox.visibility = VISIBLE

        val animator = ValueAnimator.ofInt(0, flexbox.measuredHeight)
        animator.addUpdateListener { valueAnimator ->
            flexbox.layoutParams.height = valueAnimator.animatedValue as Int
            flexbox.requestLayout()
        }
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun collapse() {
        flexbox.visibility = GONE
        val initialHeight = flexbox.measuredHeight
        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener { valueAnimator ->
            flexbox.layoutParams.height = valueAnimator.animatedValue as Int
            flexbox.requestLayout()
        }
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }
}