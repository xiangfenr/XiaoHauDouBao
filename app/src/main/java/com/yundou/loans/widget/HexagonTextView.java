package com.yundou.loans.widget;/*
 *@author jh
 *create at $
 *description:
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class HexagonTextView extends View {
    private Paint hexagonPaint;
    private TextPaint textPaint;
    private String text = "审核速度";
    private float textSize = 50f;

    public HexagonTextView(Context context) {
        super(context);
        init();
    }

    public HexagonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HexagonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        hexagonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hexagonPaint.setColor(Color.parseColor("#c3c3f5")); // 设置六边形颜色，可修改
        hexagonPaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK); // 设置文字颜色，可修改
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2;

        Path hexagonPath = new Path();
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI * i / 6;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            if (i == 0) {
                hexagonPath.moveTo((float) x, (float) y);
            } else {
                hexagonPath.lineTo((float) x, (float) y);
            }
        }
        hexagonPath.close();

        canvas.drawPath(hexagonPath, hexagonPaint);

        // 计算文字的绘制位置，使其在六边形中间显示
        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.descent() - textPaint.ascent();
        float x = (getWidth() - textWidth) / 2;
        float y = (getHeight() - textHeight) / 2 - textPaint.ascent();

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
