package com.yundou.loans.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.yundou.loans.coreui.R;


/**
 * 自定义Dialog
 */
public class CommonDialog extends Dialog {

    private CommonAlertController alertController;

    protected CommonDialog(@NonNull Context context) {
        super(context);
    }

    protected CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        alertController = new CommonAlertController(context, this, getWindow());
    }

    public void setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        alertController.setOnClickListener(viewId, listener);
    }

    public <T extends View> T getView(@IdRes int viewId) {
        return alertController.getView(viewId);
    }

    public void setText(@IdRes int viewId, CharSequence text) {
        alertController.setText(viewId, text);
    }

    public static class Builder {

        private final CommonAlertController.CommonAlertParams P;

        private final int mThemeResId;
        private CommonDialog dialog;


        public Builder(Context context) {
            this(context, R.style.CommonDialog);
        }

        public Builder(Context context, @StyleRes int themeResId) {
            P = new CommonAlertController.CommonAlertParams(context, themeResId);
            this.mThemeResId = themeResId;
        }

        /**
         * 设置内容view
         *
         * @param view
         * @return
         */
        public Builder setContentView(View view) {
            P.mContentView = view;
            P.mContentLayout = 0;
            return this;
        }

        /**
         * 设置布局
         *
         * @param layoutRes
         * @return
         */
        public Builder setContentView(@LayoutRes int layoutRes) {
            P.mContentView = null;
            P.mContentLayout = layoutRes;
            return this;
        }

        /**
         * 设置文本
         *
         * @param viewId
         * @param text
         * @return
         */
        public Builder setText(@IdRes int viewId, CharSequence text) {
            P.mTextArray.put(viewId, text);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            P.mCanceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        /**
         * 设置点击事件
         *
         * @param viewId
         * @param clickListener
         * @return
         */
        public Builder setOnClickListener(int viewId, View.OnClickListener clickListener) {
            P.mClickArray.put(viewId, clickListener);
            return this;
        }


        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * Sets the callback that will be called if a key is dispatched to the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        /**
         * 设置宽度全屏
         *
         * @return
         */
        public Builder setFullWidth() {
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

//        /**
//         * 从底部弹出
//         * 是否带动画
//         *
//         * @param isAnimation
//         * @return
//         */
        public Builder fromBottom() {
//            if (isAnimation) {
//                P.mAnimation = R.style.dialog_from_bottom_anim;
//            }
            P.mGravity = Gravity.BOTTOM;
            return this;
        }

        /**
         * 设置进出动画
         *
         * @param animation
         * @return
         */
        public Builder setAnimation(@StyleRes int animation) {
            P.mAnimation = animation;
            return this;
        }

        /**
         * 设置位置
         *
         * @param gravity
         * @return
         */
        public Builder setGravity(int gravity) {
            P.mGravity = gravity;
            return this;
        }

        /**
         * 设置宽高
         *
         * @param width
         * @param height
         * @return
         */
        public Builder setWidthAndHeight(int width, int height) {
            P.mWidth = width;
            P.mHeight = height;
            return this;
        }

        /**
         * 按百分比设置宽
         *
         * @return
         */
        public Builder setPercentWidth(float percentWidth) {
            P.mPercentWidth = percentWidth;
            return this;
        }

        /**
         * 按百分比设置高
         *
         * @return
         */
        public Builder setPercentHeight(float percentHeight) {
            P.mPercentHeight = percentHeight;
            return this;
        }

        /**
         * 设置透明度
         *
         * @param alpha
         * @return
         */
        public Builder setAlpha(float alpha) {
            P.mAlpha = alpha;
            return this;
        }


        /**
         * 设置灰度
         *
         * @param dimAmount
         * @return
         */
        public Builder setDimAmount(float dimAmount) {
            P.mDimAmount = dimAmount;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }


        public CommonDialog create() {
            dialog = new CommonDialog(P.mContext, mThemeResId);
            P.apply(dialog.alertController);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setCanceledOnTouchOutside(P.mCanceledOnTouchOutside);
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        public CommonDialog show() {
            final CommonDialog dialog = create();
            // TODO: 2018/11/1 activity关闭 在显示dialog 报错
            try {
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dialog;
        }

    }
}
