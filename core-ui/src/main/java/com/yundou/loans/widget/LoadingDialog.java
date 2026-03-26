package com.yundou.loans.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.yundou.loans.coreui.R;
import com.yundou.loans.utils.GlideUtils;


/**
 * 帧动画-进度条
 *
 * @author Lenovo
 */

public class LoadingDialog extends AlertDialog {
    private int layoutResId;
    private ImageView mIvFrame;
    private Context context;

    public LoadingDialog(Context context) {
        this(context, R.layout.view_loading);
    }

    /**
     * 构造方法
     *
     * @param context     上下文
     * @param layoutResId 要传入的dialog布局文件的id
     */
    public LoadingDialog(Context context, int layoutResId) {
        super(context, R.style.loading_dialog);
        this.context = context;
        this.layoutResId = layoutResId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResId);
        mIvFrame = findViewById(R.id.iv_frame);
//        setAnimationIV();
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
    }

//    /**
//     * 第一次加载设置动画
//     */
//    private void setAnimationIV() {
//        frameAnimation = new FrameAnimation(mIvFrame, getRes(), 200, true);
//
//        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
//            @Override
//            public void onAnimationStart() {
//            }
//
//            @Override
//            public void onAnimationEnd() {
////                gotoActivity(GuideActivity.class, true, null);
//            }
//
//            @Override
//            public void onAnimationRepeat() {
//            }
//        });
//    }
    @Override
    public void show() {
        super.show();
       GlideUtils.Companion.getInstance().showImageGif(getContext(), "file:///android_asset/images/refresh_load.gif", mIvFrame);
//        if(frameAnimation!=null){
//            frameAnimation.restartAnimation();
//        }
    }

}
