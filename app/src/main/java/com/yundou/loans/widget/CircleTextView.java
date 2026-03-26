package com.yundou.loans.widget;/*
 *@author jh
 *create at $
 *description:
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class CircleTextView extends View {
    private Paint circlePaint;
    private TextPaint textPaint;
    private String text = "放款快捷";
    private float textSize = 50f;

    public CircleTextView(Context context) {
        super(context);
        init();
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.parseColor("#fbeec2")); // 设置圆形颜色，可修改
        circlePaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK); // 设置文字颜色，可修改
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        // 确保视图是一个圆形，取宽高的最小值作为直径
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int radius = width / 2;
        // 绘制圆形
        canvas.drawCircle(width / 2, width / 2, radius, circlePaint);
        // 计算文字的宽度和高度
        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.descent() - textPaint.ascent();
        // 计算文字的绘制位置，使其在圆形中间显示
        float x = (width - textWidth) / 2;
        float y = (width - textHeight) / 2 - textPaint.ascent();
        canvas.drawText(text, x, y, textPaint);
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        invalidate();
    }
}

