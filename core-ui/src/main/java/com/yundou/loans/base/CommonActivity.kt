package com.yundou.loans.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.luck.picture.lib.utils.ToastUtils
import com.yundou.loans.coreui.R
import com.yundou.loans.utils.EventBusUtil
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

abstract class CommonActivity<VM : BaseViewModel, DB : ViewDataBinding>() : CommonReceiver() {

    private var dialog: MaterialDialog? = null


    /**
     * 替换内容 的view绑定
     */
    private var mActivity: AppCompatActivity? = null
    lateinit var viewModel: VM

    //标题
    lateinit var textView: TextView

    //左边关闭按钮
    lateinit var ivLeftClose: ImageView
    lateinit var ivRightClose: ImageView

    /**
     * 父类绑定
     */
    protected lateinit var mBinding: DB
    private var mActionBarToolbar: Toolbar? = null

    //标题栏分割线
    private var mActionBarDivide: View? = null

    //右边标题
    lateinit var tvRight: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        //判断是否使用actionBar
        //如果getLayoutId 返回小于等于0 的地址那么就不执行处理布局的逻辑
        //我们就认为该继承类不想使用公共组件a
        if (getLayoutId() > 0) {
            if (isShowActionBar()) {
                setContentView(R.layout.activity_common)
                addSubView(getLayoutId())
            } else {
                if (isEnableDatabinding()) {
                    mBinding = DataBindingUtil.setContentView(this, getLayoutId())
                    mBinding.lifecycleOwner = this
                    createViewModel()
                } else setContentView(getLayoutId())
            }
        }
        initActionBar()
        init()
        // 只有当子类有 @Subscribe 方法时才注册 EventBus
        if (isEventBus() && hasSubscriberMethods()) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBusUtil.register(this)
            }
        }
    }
    
    /**
     * 检查是否有 @Subscribe 方法
     */
    private fun hasSubscriberMethods(): Boolean {
        return try {
            val clazz = this.javaClass
            val methods = clazz.declaredMethods
            methods.any { method ->
                method.isAnnotationPresent(org.greenrobot.eventbus.Subscribe::class.java)
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 将view 加入content
     */
    private fun addSubView(layoutId: Int) {
        if (layoutId <= 0) return
        val view: ViewStub = findViewById(R.id.center_common)
        view.layoutResource = layoutId
        view.setOnInflateListener { _, p1 ->
            if (isEnableDatabinding()) {
                mBinding = p1?.let { DataBindingUtil.bind<DB>(it) }!!
                mBinding.lifecycleOwner = mActivity

                createViewModel()

            }
        }
        view.inflate()
    }

    /**
     * 创建viewModel
     */
    private fun createViewModel() {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val tp = type.actualTypeArguments[0]
            //            val tClass = tp as Class<VM>
            viewModel =
                ViewModelProvider.AndroidViewModelFactory(application).create(tp as Class<VM>)
            registorDefUIChange()
        }

    }

    /**
     * 判断延迟加载的viewmodel 是否加载成功
     * @return Boolean
     */
    fun isInitialized() = ::viewModel.isInitialized

    /**
     * 获取布局id 如果不使用
     */
    abstract fun getLayoutId(): Int

    /**
     * 初始化
     */
    abstract fun init()

    /**
     * 是否把状态栏改为深色模式
     * @return Boolean
     */
    open fun isDarkStatusBar(): Boolean = false

    /**
     * 是否启用Databinding
     * @return Boolean
     */
    open fun isEnableDatabinding(): Boolean = true

    /**
     * 是否显示在线客服悬浮按钮
     * @return Boolean
     */
    open fun isShowOnlineService(): Boolean = false


    /**
     * 是否动EventBus
     * @return Boolean
     */
    open fun isEventBus(): Boolean = true

    /**
     * 是否显示标题分割线，默认不显示
     */
    open fun isShowDivide(): Boolean = true

    /**
     * 设置标题
     */
    open fun setTitle(): CharSequence? = title


    /**
     * 是否显示返回建
     * @return Boolean
     */
    open fun isShowBack(): Boolean = true
    open fun handleEvent(msg: Message) {}

    private var materialDialog: MaterialDialog? = null
    private var loadingCount = 0
    private var dialogShowTime = 0L
    private val minShowTime = 500L // 最少显示 300ms，防闪烁

    private fun registorDefUIChange() {
        viewModel.defUI.showDialog.observe(this) {
            if (materialDialog == null) {
                materialDialog = MaterialDialog(this)
                    .cancelable(false)
                    .cornerRadius(8f)
                    .customView(R.layout.custom_progress_dialog_view, noVerticalPadding = true)
                    .lifecycleOwner(this)
                    .maxWidth(R.dimen.d_dp_120)
            }

            loadingCount++
            if (loadingCount < 0) loadingCount = 0

            // 第一次 show 时才真正显示
            if (!materialDialog!!.isShowing) {
                materialDialog?.show()
                dialogShowTime = System.currentTimeMillis()
            }
            Log.d("DialogLog", "showDialog -> count = $loadingCount")
        }

        viewModel.defUI.dismissDialog.observe(this) {
            // 延迟执行，确保线程安全
            Handler(Looper.getMainLooper()).post {
                loadingCount--
                if (loadingCount < 0) loadingCount = 0

                Log.d("DialogLog", "dismissDialog -> count = $loadingCount")

                if (loadingCount == 0) {
                    // 保证最短显示时间
                    val elapsed = System.currentTimeMillis() - dialogShowTime
                    val delay = if (elapsed < minShowTime) (minShowTime - elapsed) else 0L

                    Handler(Looper.getMainLooper()).postDelayed({
                        materialDialog?.run {
                            if (isShowing) dismiss()
                        }
                    }, delay)
                }
            }
        }

        viewModel.defUI.toastEvent.observe(this) {
            ToastUtils.showToast(this, it)
        }

        viewModel.defUI.msgEvent.observe(this) {
            handleEvent(it)
        }
    }

    /**
     * 获取toolbar 控件
     */
    open fun getActionBarToolbar(): Toolbar? {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = findViewById(R.id.toolbar_actionbar)
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar)
            }
        }
        return mActionBarToolbar
    }

    /**
     * 初始化标题栏
     * 注意title 默认取 AndroidManifest.xml 下的label
     */
    fun initActionBar() {
        if (getActionBarToolbar() == null || !isShowActionBar()) {
            return
        }
//        mActionBarDivide = findViewById(R.id.toolbar_divide)
//        mActionBarDivide!!.visibility = if (isShowDivide()) View.VISIBLE else View.GONE

        delegate.supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (isShowDivide())
            supportActionBar?.elevation = 1F
        //给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
        supportActionBar!!.setDisplayHomeAsUpEnabled(isShowBack())
        getActionBarToolbar()?.setNavigationOnClickListener { onBackPressed() }
        //使自定义的普通View能在title栏显示，即actionBar.setCustomView能起作用，对应ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(false)
        //这个小于4.0版本的默认值为true的。但是在4.0及其以上是false,决定左上角的图标是否可以点击。。
        supportActionBar?.setHomeButtonEnabled(isShowBack())
        //使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，否则，显示应用程序图标，
        // 对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
        //其中setHomeButtonEnabled和setDisplayShowHomeEnabled共同起作用，
        //如果setHomeButtonEnabled设成false，即使setDisplayShowHomeEnabled设成true，图标也不能点击
        //        supportActionBar!!.setDisplayShowHomeEnabled(true)
        //对应ActionBar.DISPLAY_SHOW_TITLE。
        supportActionBar?.setDisplayUseLogoEnabled(false)
        if (mActionBarToolbar != null) {
            textView = mActionBarToolbar!!.findViewById(R.id.toolbar_title) as TextView
            ivLeftClose = mActionBarToolbar!!.findViewById(R.id.ivLeftClose) as ImageView
            tvRight = mActionBarToolbar!!.findViewById(R.id.tv_right) as TextView
            ivRightClose = mActionBarToolbar!!.findViewById(R.id.ivRightClose) as ImageView
            if (TextUtils.isEmpty(setTitle())) {
                textView.text = title
            } else {
                textView.text = setTitle()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // 只有注册了才需要注销
        if (EventBus.getDefault().isRegistered(this)) {
            EventBusUtil.unregister(this)
        }
    }

    fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = currentFocus
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
        }
    }




}