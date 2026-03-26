package com.yundou.loans.widget;/*
 *@author jh
 *create at $
 *description:
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
public class SquareTextView extends View {
    private Paint textPaint;
    private String text="申请简单";
    public SquareTextView(Context context) {
        super(context);
        init();
    }
    public SquareTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SquareTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50f);
        text = "申请简单";
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 确保是正方形
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        // 绘制文字
        float textX = (width - textPaint.measureText(text)) / 2;
        float textY = (height - (textPaint.descent() + textPaint.ascent())) / 2;
        canvas.drawText(text, textX, textY, textPaint);
    }
    public void setText(String text) {
        this.text = text;
        invalidate();
    }
}
