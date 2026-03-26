package com.yundou.loans.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.yundou.loans.R;

import java.util.ArrayList;
import java.util.Objects;


/**
 * 通用底部菜单Dialog
 *
 * @author ch_zhp
 */
public class BottomMenuDialog extends DialogFragment implements DialogInterface.OnKeyListener {
    private View rootView;
    private ButtomDialogListener mListener;
    private ArrayList<String> items;
    private ArrayList<Integer> colors;
    private int mButtonColor = -1;
    private MyAdapter myAdapter;

    //不添加颜色默认全部蓝色
    public static BottomMenuDialog newInstance(ArrayList<String> items, ButtomDialogListener mListener) {
        BottomMenuDialog fragment = new BottomMenuDialog();
        fragment.setButtomDialogListener(mListener);
        fragment.setItems(items);
        return fragment;
    }

    public static BottomMenuDialog newInstance(ArrayList<String> items, ArrayList<Integer> colors, int buttonColor, ButtomDialogListener mListener) {
        BottomMenuDialog fragment = new BottomMenuDialog();
        fragment.setButtomDialogListener(mListener);
        fragment.setItems(items);
        fragment.setColors(colors);
        fragment.setButtonColor(buttonColor);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.bottom_layout, container, false);
        AnimationUtil.slideToUp(rootView);
        //添加监听
        getDialog().setOnKeyListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = Objects.requireNonNull(getDialog()).getWindow();
        assert window != null;
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        View decorView = window.getDecorView();
        decorView.setPadding(8, 8, 8, 0);
        decorView.setBackground(new ColorDrawable(Color.TRANSPARENT));
        decorView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mListener != null) {
                        mListener.onClick(-1);
                    }
                    slideDown();
                }
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = (ListView) view.findViewById(R.id.lv_menu);
        if (colors != null && colors.size() == 0)
            myAdapter = new MyAdapter(getActivity(), items);
        else
            myAdapter = new MyAdapter(getActivity(), items, colors);

        listView.setAdapter(myAdapter);
        listView.setDivider(ContextCompat.getDrawable(view.getContext(),R.drawable.bottom_menu_line));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slideDown();
                if (mListener != null) {
                    mListener.onClick(position);
                }

            }
        });


        Button cancel = (Button) view.findViewById(R.id.btn_cancel);
        cancel.setText("取消");
        if (mButtonColor != -1)
            cancel.setTextColor(mButtonColor);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown();
                if (mListener != null) {
                    mListener.onClick(-1);
                }

            }
        });
    }

    private void setButtomDialogListener(ButtomDialogListener listener) {
        this.mListener = listener;
    }

    private void setItems(ArrayList<String> items) {
        this.items = items;
    }

    private void setColors(ArrayList<Integer> colors) {
        this.colors = colors;
    }

    private void setButtonColor(int color) {
        this.mButtonColor = color;
    }

    public void slideDown() {
        AnimationUtil.slideToDown(rootView, new AnimationUtil.AnimationEndListener() {
            @Override
            public void onFinish() {
                dismiss();
            }
        });
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mListener != null) {
                mListener.onClick(-1);
            }
            slideDown();
            return true;
        } else {
            return false;

        }
    }

    public interface ButtomDialogListener {
        void onClick(int position);
    }

    static class MyAdapter extends BaseAdapter {
        private ArrayList<String> items;
        private ArrayList<Integer> colors;
        private Context mContext;

        MyAdapter(Context context, ArrayList<String> items) {
            this.mContext = context;
            this.items = items;
        }

        MyAdapter(Context context, ArrayList<String> items, ArrayList<Integer> colors) {
            this.mContext = context;
            this.items = items;
            this.colors = colors;
        }


        @Override
        public int getCount() {
            return (items != null && items.size() > 0) ? items.size() : 0;
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.bottom_item, null);
                holder = new ViewHolder();
                holder.txt_aName = view.findViewById(R.id.tv_item);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (i == 0) {
                view.setBackgroundResource(R.drawable.selector_photo_dialog_top);
            } else if (i == items.size() - 1) {
                view.setBackgroundResource(R.drawable.selector_photo_dialog_bottom);
            } else {
                view.setBackgroundResource(R.drawable.selector_dialog_hint);
            }

            holder.txt_aName.setText(items.get(i));

            if (colors != null && colors.size() != 0)
                holder.txt_aName.setTextColor(colors.get(i));

            return view;
        }

        static class ViewHolder {
            TextView txt_aName;
        }
    }
    @Override
    public void show(FragmentManager manager, String tag) {
        //避免重复添加的异常 java.lang.IllegalStateException: Fragment already added
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
        //避免状态丢失的异常 java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        try {
            super.show(manager, tag);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}


