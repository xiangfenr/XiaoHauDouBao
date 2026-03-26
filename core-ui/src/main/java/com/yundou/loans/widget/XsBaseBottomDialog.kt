package com.yundou.loans.widget

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yundou.loans.coreui.R


abstract class XsBaseBottomDialog<T : ViewBinding>(context: Context) : BottomSheetDialog(context,
    R.style.CommonDialog) {

    var binding: T
    protected open val height: Int
        protected get() = LayoutParams.WRAP_CONTENT

    private var bottomSheet: FrameLayout? = null
    protected open var isHideable = false
    protected open var isCanceledOnTouch = true

    init {
        binding = inflateBinding()
        setContentView(binding.root)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initLiveData()
        initListener()
        initAfterView()
        setMaskLayout()
    }

    override fun onStart() {
        super.onStart()
        //点击外部可以关闭
        setCanceledOnTouchOutside(isCanceledOnTouch)
        bottomSheet = delegate.findViewById(com.google.android.material.R.id.design_bottom_sheet)
//        behavior = BottomSheetBehavior.from(binding.root)
        if (bottomSheet != null) {
            val layoutParams = bottomSheet!!.layoutParams
            layoutParams.height = height
            bottomSheet!!.layoutParams = layoutParams

            behavior?.peekHeight = height

            //初始为展开状态
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED

            behavior?.isHideable = isHideable
            //向下滑动可隐藏
            behavior?.isDraggable = isHideable
        }
        // 获取Dialog的Window对象
        window?.let {         //设置窗体的属性
            val lp = it.attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            //设置dialog从中间弹出
            it.setGravity(Gravity.BOTTOM)
            //将属性给窗体
            it.attributes = lp
        }
    }
    abstract fun inflateBinding(): T
    abstract fun initData()

    abstract fun initLiveData()

    abstract fun initListener()

    abstract fun initAfterView()


    open fun setMaskLayout() {
//        val color = SPUtils.getInstance(ConstantsCore.XS_SP_NAME)
//            .getString(ConstantsCore.XS_SP_FILTER_COLOR)
//        if (!color.isNullOrEmpty()) {
//            val overlayDrawable: Drawable = ColorDrawable(Color.parseColor(color))
//            window?.decorView?.foreground = overlayDrawable
//        }
    }

}