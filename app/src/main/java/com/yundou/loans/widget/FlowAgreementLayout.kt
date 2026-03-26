package com.yundou.loans.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.max

/**
 * @Author: fenr
 * 时间: 2025/9/25
 * 类名: ACTIVITY
 * 简述:
 *
 */
class FlowAgreementLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    // 默认间距（可改成从 attrs 读取）
    private val horizontalSpacing = dpToPx(8)
    private val verticalSpacing = dpToPx(6)

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density + 0.5f).toInt()

    // 用来在 layout 时复用上次 measure 计算出的每一行信息
    private data class Line(val startIndex: Int, val endIndex: Int, val width: Int, val height: Int)
    private val lines = ArrayList<Line>()

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return if (p is MarginLayoutParams) MarginLayoutParams(p) else MarginLayoutParams(p.width, p.height)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        lines.clear()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val maxLineWidth = if (widthMode == MeasureSpec.UNSPECIFIED) Int.MAX_VALUE
        else widthSize - paddingLeft - paddingRight

        var curLineStart = -1
        var curLineWidth = 0
        var curLineHeight = 0

        var maxUsedLineWidth = 0

        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue

            val lp = ensureMarginLayoutParams(child)

            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)

            val childW = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childH = child.measuredHeight + lp.topMargin + lp.bottomMargin

            if (curLineStart == -1) {
                // 新起一行
                curLineStart = i
                curLineWidth = childW
                curLineHeight = childH
            } else {
                val need = if (curLineWidth == 0) childW else curLineWidth + horizontalSpacing + childW
                if (need > maxLineWidth) {
                    // 当前行结束，保存
                    lines.add(Line(curLineStart, i - 1, curLineWidth, curLineHeight))
                    maxUsedLineWidth = max(maxUsedLineWidth, curLineWidth)
                    // 新起一行
                    curLineStart = i
                    curLineWidth = childW
                    curLineHeight = childH
                } else {
                    // 放到当前行
                    if (curLineWidth != 0) curLineWidth += horizontalSpacing
                    curLineWidth += childW
                    curLineHeight = max(curLineHeight, childH)
                }
            }
        }

        // 保存最后一行
        if (curLineStart != -1) {
            lines.add(Line(curLineStart, childCount - 1, curLineWidth, curLineHeight))
            maxUsedLineWidth = max(maxUsedLineWidth, curLineWidth)
        }

        // 计算总内容高度（所有行高 + 行间距）
        var contentHeight = 0
        if (lines.isNotEmpty()) {
            for (j in 0 until lines.size) {
                contentHeight += lines[j].height
            }
            contentHeight += verticalSpacing * (lines.size - 1)
        }

        val finalWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            else -> paddingLeft + paddingRight + maxUsedLineWidth
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val finalHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            else -> paddingTop + contentHeight + paddingBottom
        }

        setMeasuredDimension(
            resolveSize(finalWidth, widthMeasureSpec),
            resolveSize(finalHeight, heightMeasureSpec)
        )
    }

    // 确保 child.layoutParams 是 MarginLayoutParams，并返回它
    private fun ensureMarginLayoutParams(child: View): MarginLayoutParams {
        val rawLp = child.layoutParams
        return if (rawLp is MarginLayoutParams) {
            rawLp
        } else {
            val newLp = MarginLayoutParams(rawLp?.width ?: LayoutParams.WRAP_CONTENT, rawLp?.height ?: LayoutParams.WRAP_CONTENT)
            child.layoutParams = newLp
            newLp
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (lines.isEmpty()) return

        var y = paddingTop

        for ((lineIndex, line) in lines.withIndex()) {
            var x = paddingLeft
            var childPlacedCount = 0
            // 注意：line.startIndex..line.endIndex 是 child 的索引（包含 GONE 的可能性），
            // 我们只 layout 可见的 child（ensureMarginLayoutParams 已在 measure 中执行）
            for (idx in line.startIndex..line.endIndex) {
                val child = getChildAt(idx) ?: continue
                if (child.visibility == View.GONE) continue

                val lp = child.layoutParams as MarginLayoutParams
                val left = x + lp.leftMargin
                val top = y + lp.topMargin
                val right = left + child.measuredWidth
                val bottom = top + child.measuredHeight

                child.layout(left, top, right, bottom)

                x = x + lp.leftMargin + child.measuredWidth + lp.rightMargin + horizontalSpacing
                childPlacedCount++
            }
            // 行高已经包含了子 view 的 top/bottom margin（在 measure 里计算的）
            y += line.height
            if (lineIndex < lines.size - 1) {
                y += verticalSpacing
            }
        }
    }
}