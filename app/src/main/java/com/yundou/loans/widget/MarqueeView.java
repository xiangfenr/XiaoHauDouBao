package com.yundou.loans.widget;/*
 *@author jh
 *create at $
 *description:
 */

import android.content.Context;
import android.util.AttributeSet;

public class MarqueeView extends androidx.appcompat.widget.AppCompatTextView {
    //实现TextView的三个构造函数
    public MarqueeView( Context context ) {
        super( context );
    }

    public MarqueeView( Context context,  AttributeSet attrs ) {
        super( context, attrs );
    }

    public MarqueeView( Context context,  AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
    }
    //重写isFocused()方法
    @Override
    public boolean isFocused() {
        return true;
    }
}
