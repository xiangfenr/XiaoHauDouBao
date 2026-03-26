package com.yundou.loans.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.launcher.ARouter
import com.luck.picture.lib.utils.ToastUtils
import com.yundou.loans.utils.EventBusUtil
import com.yundou.loans.widget.LoadingDialog
import java.lang.reflect.ParameterizedType


abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding> : Fragment() {

    private var dialog: MaterialDialog? = null

    protected lateinit var viewModel: VM

    protected lateinit var mBinding: DB

    //是否第一次加载
    var isFirst: Boolean = true

    protected var loadingDialog: LoadingDialog? = null //加载数据的进度对话框

    var activity: Activity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity
    }

    /**
     * 用于属性延迟加载判断，防止出现空异常
     * @return Boolean
     */
    fun isInitialized() = ::viewModel.isInitialized
    fun isMBindingInitialized() = ::mBinding.isInitialized

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val cls =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<*>
        if (ViewDataBinding::class.java != cls && ViewDataBinding::class.java.isAssignableFrom(cls)) {
            mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
            return mBinding.root
        }
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ARouter.getInstance().inject(this)
        if (isEventBus()) EventBusUtil.register(this)
        createViewModel()
        //注册 UI事件
        registorDefUIChange()
        initView(savedInstanceState)
        onVisible()

    }

    open fun initView(savedInstanceState: Bundle?) {}

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    abstract fun layoutId(): Int

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            lazyLoadData()
            isFirst = false

        }
    }

    /**
     * 懒加载
     */
    open fun lazyLoadData() {}

    /**
     * 是否动EventBus
     * @return Boolean
     */
    open fun isEventBus(): Boolean = false

    /**
     * 注册 UI 事件
     */
    private fun registorDefUIChange() {
        viewModel.defUI.showDialog.observe(viewLifecycleOwner, Observer {
           showLoading()
        })
        viewModel.defUI.dismissDialog.observe(viewLifecycleOwner, Observer {
             dismissLoading()

        })
        viewModel.defUI.toastEvent.observe(this, Observer {
            ToastUtils.showToast(context, it)
        })
        viewModel.defUI.msgEvent.observe(this, Observer {
            handleEvent(it)
        })
    }

    open fun handleEvent(msg: Message) {}

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(activity)
            loadingDialog!!.setCancelable(true)
            loadingDialog!!.setCanceledOnTouchOutside(false)
        }
        loadingDialog?.show()
    }

    open fun dismissLoading() {
        loadingDialog?.run {
            if (isShowing) dismiss()
            null
        }
    }

    /**
     * 是否和 Activity 共享 ViewModel,默认不共享
     * Fragment 要和宿主 Activity 的泛型是同一个 ViewModel
     */
    open fun isShareVM(): Boolean = false


    /**
     * 创建 ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    private fun createViewModel() {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val tp = type.actualTypeArguments[0]
            val tClass = tp as? Class<VM> ?: BaseViewModel::class.java
            val viewModelStore =
                if (isShareVM()) requireActivity().viewModelStore else this.viewModelStore
            viewModel = ViewModelProvider(
                viewModelStore,
                ViewModelProvider.NewInstanceFactory()
            ).get(tClass) as VM
            lifecycle.addObserver(viewModel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isEventBus())
            EventBusUtil.unregister(this)
    }
}