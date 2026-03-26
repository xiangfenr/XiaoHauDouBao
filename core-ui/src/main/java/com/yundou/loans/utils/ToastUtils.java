package com.yundou.loans.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.yundou.loans.coreui.R;


@SuppressLint("InflateParams")
public class ToastUtils {
    private static final Typeface LOADED_TOAST_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
    private static Typeface currentTypeface = LOADED_TOAST_TYPEFACE;
    private static int textSize = 16; // in SP

    private static boolean tintIcon = true;
    private static boolean allowQueue = true;

    private static Toast lastToast = null;
    private static Handler handler = null;

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;
    private static Toast currentToast;
    private static ImageView toastIcon;
    private static TextView toastTextView;
    static Drawable drawableFrame;

    /**
     * 短的提示语句
     */
    public static void showShortToast(@NonNull Context context, @NonNull CharSequence message) {
        if (!TextUtils.isEmpty(message)) {
            normal(context, message, Toast.LENGTH_SHORT, null, false);
        }
    } //1
    public static void normal(@NonNull Context context, @NonNull CharSequence message, int duration,
                              Drawable icon, boolean withIcon) {
        custom(context, message, icon, ToastyUtils.getColor(context, R.color.normalColor),
                ToastyUtils.getColor(context, R.color.defaultTextColor), duration, withIcon, true);
    }

    /**
     * 短的提示语句
     */
    public static void showShortToast(@NonNull CharSequence message) {
        if (!TextUtils.isEmpty(message)) {
            showShortToast(Utils.getContext(), message);
        }

    }






    @SuppressLint("ShowToast")
    public static void custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                              @ColorInt int tintColor, @ColorInt int textColor, int duration,
                              boolean withIcon, boolean shouldTint) {
        View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.toast_layout, null);
        toastIcon = toastLayout.findViewById(R.id.toast_icon);
        toastTextView = toastLayout.findViewById(R.id.toast_text);

        if (drawableFrame == null) {
            if (shouldTint)
                drawableFrame = ToastyUtils.tint9PatchDrawableFrame(context, tintColor);
            else
                drawableFrame = ToastyUtils.getDrawable(context, R.mipmap.toast_frame);
        }
        ToastyUtils.setBackground(toastLayout, drawableFrame);


        if (withIcon) {
            if (icon == null)
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            ToastyUtils.setBackground(toastIcon, tintIcon ? ToastyUtils.tintIcon(icon, textColor) : icon);
        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setText(message);
        toastTextView.setTextColor(textColor);
        toastTextView.setTypeface(currentTypeface);
        toastTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);


        if (isMainThread()) {
            if (currentToast == null) {
                currentToast = Toast.makeText(context, "", duration);
            }
            currentToast.setView(toastLayout);
            currentToast.show();
        } else {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper());
            }
            handler.post(() -> {
                if (currentToast == null) {
                    currentToast = Toast.makeText(context, "", duration);
                }
                currentToast.setView(toastLayout);
                currentToast.show();
            });

        }
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }


    public static class Config {
        private Typeface typeface = ToastUtils.currentTypeface;
        private int textSize = ToastUtils.textSize;

        private boolean tintIcon = ToastUtils.tintIcon;
        private boolean allowQueue = true;

        private Config() {
            // avoiding instantiation
        }

        @CheckResult
        public static Config getInstance() {
            return new Config();
        }

        public static void reset() {
            ToastUtils.currentTypeface = LOADED_TOAST_TYPEFACE;
            ToastUtils.textSize = 16;
            ToastUtils.tintIcon = true;
            ToastUtils.allowQueue = true;
        }

        @CheckResult
        public Config setToastTypeface(@NonNull Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        @CheckResult
        public Config setTextSize(int sizeInSp) {
            this.textSize = sizeInSp;
            return this;
        }

        @CheckResult
        public Config tintIcon(boolean tintIcon) {
            this.tintIcon = tintIcon;
            return this;
        }

        @CheckResult
        public Config allowQueue(boolean allowQueue) {
            this.allowQueue = allowQueue;
            return this;
        }

        public void apply() {
            ToastUtils.currentTypeface = typeface;
            ToastUtils.textSize = textSize;
            ToastUtils.tintIcon = tintIcon;
            ToastUtils.allowQueue = allowQueue;
        }
    }
}

