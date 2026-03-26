package com.yundou.loans.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yundou.loans.exceptions.ExceptionHandler
import com.yundou.loans.exceptions.ServerException
import com.yundou.loans.utils.LogUtils
import com.yundou.loans.widget.SingleLiveEvent
import kotlinx.coroutines.*

/**
 *@ClassName:
 *@Deseription: 定义基础viewmodel 实现, 处理统一 的ui 事件
 *@author：
 *@date：
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {

    val defUI: UIChange by lazy { UIChange() }

    /**
     * UI事件
     */
    inner class UIChange {
        val showDialog by lazy { SingleLiveEvent<String>() }
        val dismissDialog by lazy { SingleLiveEvent<Void>() }
        val toastEvent by lazy { SingleLiveEvent<String>() }
        val msgEvent by lazy { SingleLiveEvent<Message>() }
    }

    /**
     * 网络请求拦截
     * @param block 正常请求返回
     * @param error 错误回调
     * @param complete  请求结束
     * @param isShowDialog Boolean 是否显示loading
     */
    fun launchGo(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(ResponseThrowable) -> Unit = {
        },
        complete: suspend CoroutineScope.() -> Unit = {},
        isShowDialog: Boolean = true,
        netcheck: suspend CoroutineScope.(ResponseThrowable) -> Unit = {

        },
    ) {
        if (isShowDialog) defUI.showDialog.call()
        launchUI {
            handleException(
                withContext(Dispatchers.IO) {
                    block
                },
                {
                    error(it)
                    netcheck(it)
                },
                {
                    if (isShowDialog){
                        defUI.dismissDialog.call()
                    }
                    complete()
                })
        }
    }

    /**
     * 所有网络请求都在 viewModelScope 域中启动，当页面销毁时会自动
     * 调用ViewModel的  #onCleared 方法取消所有协程
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch { block() }

    /**
     * 异常统一处理
     * @param block 正常返回
     * @param error 错误处理
     * @param complete 完成
     */
    private suspend fun handleException(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(ResponseThrowable) -> Unit,
        complete: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                block()
            } catch (e: Exception) {
                LogUtils.e("xiang",e.message)
                error(ExceptionHandle.handleException(e))
            } finally {
                complete()
            }
        }
    }

    suspend inline fun <T> launchRequestForResult(noinline block: suspend () -> T): DataResult<T> {
        return try {
            val response = block.invoke()
            LogUtils.e("xiang",response.toString())
            if ((response as BaseResponse<*>).isSuccessful()) {
                DataResult.Success(response)
            } else {
                DataResult.Error(
                    ExceptionHandler.handleException(
                        ServerException(
                            response.errorCode,
                            response.errorMsg
                        )
                    )
                )
            }
        } catch (e: Exception) {
            return DataResult.Error(ExceptionHandler.handleException(e))
        }
    }

}

