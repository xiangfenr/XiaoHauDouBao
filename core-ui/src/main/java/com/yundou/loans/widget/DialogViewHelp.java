package com.yundou.loans.widget;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class DialogViewHelp {

    private View mContentView;

    private SparseArray<WeakReference<View>> mViews;

    public DialogViewHelp() {
        mViews = new SparseArray<>();
    }

    public DialogViewHelp(Context context, int contentLayout) {
        this();
        this.mContentView = LayoutInflater.from(context).inflate(contentLayout, null);
    }

    public void setContentView(View contentView) {
        this.mContentView = contentView;
    }

    public <T extends View> T getView( int viewId) {
        WeakReference<View> viewWeakReference = mViews.get(viewId);
        View view = null;
        if (viewWeakReference != null) {
            view = viewWeakReference.get();
        }
        if (view == null) {
            view = mContentView.findViewById(viewId);
            if (view != null) {
                mViews.put(viewId, new WeakReference<View>(view));
            }
        }
        return (T) view;
    }

    public void setText( int viewId, CharSequence text) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setText(text);
        }
    }

    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    public View getContentView() {
        return mContentView;
    }
}
