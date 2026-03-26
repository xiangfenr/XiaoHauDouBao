package com.yundou.loans.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;

class CommonAlertController {

    private Context mContext;
    private CommonDialog mDialog;
    private Window mWindow;
    private DialogViewHelp mViewHelp;

    public CommonAlertController(Context context, CommonDialog dialog, Window window) {
        this.mContext = context;
        this.mDialog = dialog;
        this.mWindow = window;
    }

    public CommonDialog getDialog() {
        return mDialog;
    }

    public Context getContext() {
        return mContext;
    }

    public Window getWindow() {
        return mWindow;
    }

    public void setViewHelp(DialogViewHelp viewHelp) {
        this.mViewHelp = viewHelp;
    }

    public void setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        mViewHelp.setOnClickListener(viewId, listener);
    }

    public <T extends View> T getView(@IdRes int viewId) {
        return mViewHelp.getView(viewId);
    }

    public void setText(@IdRes int viewId, CharSequence text) {
        mViewHelp.setText(viewId, text);
    }

    public static class CommonAlertParams {

        public Context mContext;
        public int mThemeResId;

        public boolean mCancelable = true;
        public boolean mCanceledOnTouchOutside = true;

        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnDismissListener mOnDismissListener;
        public DialogInterface.OnKeyListener mOnKeyListener;

        /**
         * 存放要修改的字体
         */
        public SparseArray<CharSequence> mTextArray = new SparseArray<>();
        /**
         * 存放点击事件
         */
        public SparseArray<View.OnClickListener> mClickArray = new SparseArray<>();

        /**
         * 资源布局
         */
        public int mContentLayout;
        /**
         * 自定义view
         */
        public View mContentView;

        /**
         * 默认 宽高
         */
        public int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        /**
         * 设置位置
         */
        public int mGravity = Gravity.CENTER;
        /**
         * 设置动画
         */
        public int mAnimation = 0;
        /**
         * 设置透明度
         */
        public float mAlpha = 1.0f;
        /**
         * 设置会读
         */
        public float mDimAmount = 0.7f;
        public float mPercentWidth = 0;
        public float mPercentHeight = 0;


        public CommonAlertParams(Context context, int themeResId) {
            this.mContext = context;
            this.mThemeResId = themeResId;
        }

        /**
         * 绑定和设置参数
         *
         * @param alertController
         */
        public void apply(CommonAlertController alertController) {
            //设置布局
            DialogViewHelp viewHelp = null;
            if (mContentLayout != 0) {
                viewHelp = new DialogViewHelp(mContext, mContentLayout);
            }

            if (mContentView != null) {
                viewHelp = new DialogViewHelp();
                viewHelp.setContentView(mContentView);
            }

            if (viewHelp == null) {
                throw new IllegalArgumentException("设置参数异常，请设置布局setContentView()");
            }


            //给dialog设置布局
            alertController.getDialog().setContentView(viewHelp.getContentView());

            alertController.setViewHelp(viewHelp);

            //设置文本
            for (int i = 0; i < mTextArray.size(); i++) {
                alertController.setText(mTextArray.keyAt(i), mTextArray.valueAt(i));
            }

            //设置点击事件
            for (int i = 0; i < mClickArray.size(); i++) {
                alertController.setOnClickListener(mClickArray.keyAt(i), mClickArray.valueAt(i));
            }

            //设置其它属性

            Window window = alertController.getWindow();
            WindowManager windowManager = window.getWindowManager();
            Display display = windowManager.getDefaultDisplay();

            //设置位置
            window.setGravity(mGravity);

            //设置动画
            if (mAnimation != 0) {
                window.setWindowAnimations(mAnimation);
            }
//            设置宽高
            WindowManager.LayoutParams attributes = window.getAttributes();

            if (mPercentWidth != 0) {
                attributes.width = (int) (display.getWidth() * mPercentWidth);
            } else {
                attributes.width = mWidth;
            }

            if (mPercentHeight != 0) {
                attributes.height = (int) (display.getHeight() * mPercentHeight);
            } else {
                attributes.height = mHeight;
            }


            attributes.alpha = mAlpha;
            attributes.dimAmount = mDimAmount;
            window.setAttributes(attributes);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        }
    }
}
