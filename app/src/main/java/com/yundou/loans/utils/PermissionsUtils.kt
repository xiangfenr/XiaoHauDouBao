package com.yundou.loans.utils

import android.app.Activity
import androidx.fragment.app.Fragment
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *封装动态权限管理  关联Activity 的协程处理
 * @receiver Activity
 * @param permissions Array<out String>
 * @return Boolean
 */
suspend fun Activity.requestPermissionsForResult(vararg permissions: String): Boolean =
    suspendCoroutine {
        PermissionManager.requestPermission(this,*permissions,callback = object : IPermissionResult {
            override fun getPermissionFailed( requestCode: Int, deniedPermissions: Array<String?>?) {
                LogUtils.e("getPermissionFailed")
                it.resumeWithException(PermissionException())
            }

            override fun getPermissionSuccess(requestCode: Int) {
                LogUtils.e("getPermissionSuccess")
                it.resume(true)
            }

        })
    }

/**
 * 封装动态权限管理 关联fragment 协程处理
 * @receiver Fragment
 * @param permissions Array<out String>
 * @return Boolean
 */
suspend fun Fragment.requestPermissionsForResult(vararg permissions: String): Boolean =
    suspendCoroutine {
        PermissionManager.requestPermission(requireActivity(),*permissions,callback = object : IPermissionResult {
            override fun getPermissionFailed(requestCode: Int, deniedPermissions: Array<String?>?) {
                LogUtils.e("getPermissionFailed")
                it.resumeWithException(PermissionException())
            }

            override fun getPermissionSuccess( requestCode: Int) {
                LogUtils.e("getPermissionSuccess")
                it.resume(true)
            }

        })
    }


