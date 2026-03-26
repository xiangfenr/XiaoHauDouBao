package com.yundou.loans.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
import android.content.pm.PermissionInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yundou.loans.coreui.R
import com.yundou.loans.widget.CommonDialog
import java.util.*

/**
 *@ClassName:PermissionManager
 *@Deseription:  动态权限管理
 *@author：wangmingyu
 *@date：2021/2/3 19:11
 */
class PermissionManager private constructor() {
    //处理用户拒绝权限后是否提示，默认提示
    private var isAlwaysDenied = true
    private var dialog: CommonDialog? = null
    private var iPermissionResult: IPermissionResult? = null
    private var activity: Activity? = null

    /**
     * 获取权限
     * @param activity Activity? 上下文
     * @param permissions MutableList<String> 需要获取权限列表
     * @param code Int 返回code
     */
    @SuppressLint("RestrictedApi")
    private fun getPermission(activity: Activity?, permissions: MutableList<String>, code: Int) {
        val applyPermission: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.let {
                if (it.isFinishing) return
                for (i in permissions.indices) {
                    val permission = permissions[i]
                    //                    LogUtils.e("申请权限：${permission}")
                    if (ContextCompat.checkSelfPermission(
                            it,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        LogUtils.e("未获得的权限：${permission}")
                        applyPermission.add(permission)
                    }
                }
                checkPermissionResult(it, applyPermission, code)
            }
        } else {
            if (activity?.isFinishing == true) return
            activity?.let {
                checkPermissionResult(it, applyPermission, code)
            }

        }
    }

    /**
     * 获取权限组名称
     * @param permissions Array<out String>
     * @return String?
     */
    private fun getPermissionsName(vararg permissions: String): String? {

        if (activity == null) {
            return null
        }
        val pm: PackageManager = this.activity?.packageManager!!
        val arrayList = StringBuffer()
        var strGroup: String
        permissions.forEach {
            val info: PermissionInfo = pm.getPermissionInfo(it, 0)
            LogUtils.e(
                "[" + info.loadLabel(pm).toString() + "] " + info.loadLabel(pm)
                    .toString() + ":\n" + info.loadDescription(pm).toString() + "\n"
            )
            val groupInfo: PermissionGroupInfo = pm.getPermissionGroupInfo(info.group!!, 0)
            LogUtils.e(
                "[" + groupInfo.loadLabel(pm).toString() + "] " + groupInfo.loadLabel(pm)
                    .toString() + ":\n" + groupInfo.loadDescription(pm).toString() + "\n"
            )
            strGroup =
                if ("android.permission-group.UNDEFINED" == groupInfo.loadLabel(pm).toString()) {
                    info.loadLabel(pm).toString()
                } else {
                    groupInfo.loadLabel(pm).toString()
                }
            if (!arrayList.contains(strGroup)) {
                if (arrayList.isNotEmpty()) arrayList.append(",")
                arrayList.append(strGroup)
            }
        }
        return arrayList.toString()
    }

    /**
     * 被拒绝的权限，展示dialog
     * @param activity Activity
     * @param str String
     */
    private fun showPermissionManagerDialog(str: String?,msg: String?) {
        if (activity?.isFinishing == true) return
        if(dialog == null){
            dialog = CommonDialog.Builder(activity)
                .setContentView(R.layout.layout_common_dialog)
                .setPercentWidth(0.8f)
                .setCanceledOnTouchOutside(false)
                .setGravity(Gravity.CENTER)
                .create()
        }
        dialog?.apply {
            getView<TextView>(R.id.tv_dialog_tip)?.text = if (TextUtils.isEmpty(msg)) "获取" + str + "权限被禁用" else msg
            getView<TextView>(R.id.dialog_title)?.text = "执行当前业务需要请在->设置-应用管理-${activity?.getString(
                com.yundou.loans.R.string.app_name)!!}-权限管理 (将${str}权限打开)"
            getView<TextView>(R.id.dialog_confirm)?.text = "去设置"
            setOnClickListener(R.id.dialog_confirm){
                dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + activity?.packageName)
                activity?.startActivity(intent)
            }
            setOnClickListener(R.id.dialog_cancel){
                dismiss()
            }
            let {
                if (!it.isShowing){
                    it.show()
                }
            }
        }
    }

    /**
     * 检查权限是否获取成功
     * @param activity Activity
     * @param list MutableList<String>
     * @param requestCode Int
     */
    private fun checkPermissionResult(
        activity: Activity, list: MutableList<String>, requestCode: Int,
    ) {
        val permissions = list.toTypedArray()
        if (list.isNotEmpty() && list.size > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(permissions, requestCode)
            }
        } else {
            iPermissionResult?.getPermissionSuccess(requestCode)
        }
    }

    /**
     * 请求权限，同时检查是否有被拒绝的权限
     * @param activity Activity
     * @param code Int
     * @param permissions Array<String>
     * @param results IntArray
     * @param callback IPermissionResult
     */
    private fun requestResult(
        code: Int, vararg permissions: String, results: IntArray,msg: String? = null
    ) {
        val deniedPermissions: MutableList<String> = ArrayList()
        LogUtils.e("requestResult requestCode ${code} ")
        for (i in results.indices) {
            if (results[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i])
            }
        }
        if (deniedPermissions.size > 0) {
            if (hasAlwaysDeniedPermission(*permissions) && isAlwaysDenied) {
                showPermissionManagerDialog(getPermissionsName(*permissions),msg)
            }
            iPermissionResult?.getPermissionFailed(code, deniedPermissions.toTypedArray())
        } else {
            iPermissionResult?.getPermissionSuccess(code)
        }
    }

    /**
     * 是否彻底拒绝了某项权限
     */
    private fun hasAlwaysDeniedPermission(vararg deniedPermissions: String): Boolean {
        var rationale: Boolean
        if (activity == null) return false
        if (activity?.isFinishing == true) return false
        for (permission in deniedPermissions) {
            rationale = ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)
            if (!rationale) {
                return true
            }
        }
        return false
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var build: Builder? = null

        /**
         * 定义静态调用方法
         * @param activity Activity
         * @param permissions Array<out String>
         * @param code Int
         * @param callback IPermissionResult
         */
        fun requestPermission(
            activity: Activity, vararg permissions: String, code: Int = 99, callback: IPermissionResult,
        ) {
            //            if (build == null) {
            build = Builder().setActivity(activity).setRequestPermission(*permissions)
                .setRequestCode(code).setCallback(callback)
            build?.build()
            //            }
            build?.reqeust()
        }

        fun requestPermission(
            activity: Activity,
            vararg permissions: String,
            code: Int = 99,
            success: (Int) -> Unit?,
            field: (Int, Array<String?>?) -> Unit = { _: Int, _: Array<String?>? -> }
        ) {
            build = Builder().setActivity(activity).setRequestPermission(*permissions)
                .setRequestCode(code).setCallback(object : IPermissionResult {
                override fun getPermissionFailed(
                    requestCode: Int,
                    deniedPermissions: Array<String?>?
                ) {
                    field.invoke(requestCode, deniedPermissions)
                }

                override fun getPermissionSuccess(requestCode: Int) {
                    success.invoke(requestCode)
                }

            })
            build?.build()
            build?.reqeust()
        }

        fun requestResult(code: Int, vararg permissions: String, results: IntArray) {
            build?.permissionManager?.requestResult(code, *permissions, results = results)
        }

        fun requestResult(code: Int, vararg permissions: String, results: IntArray,msg: String?) {
            build?.permissionManager?.requestResult(code, *permissions, results = results, msg = msg)
        }

    }

    /**
     * 构建者 来构建permissionManager 需要的参数
     * @property activity Activity?
     * @property permissionManager PermissionManager
     * @property requestCode Int
     * @property permissions MutableList<String>
     * @property iPermissionResult IPermissionResult
     * @property isAlwaysDenied Boolean
     */
    class Builder {
        private var activity: Activity? = null
        lateinit var permissionManager: PermissionManager
        private var requestCode = 99
        private lateinit var permissions: MutableList<String>
        lateinit var iPermissionResult: IPermissionResult

        //处理用户拒绝权限后是否提示，默认提示
        var isAlwaysDenied = true
        //        private var permissions: Array<String>?=null
        /**
         * 设置请求code
         * @param code Int
         * @return Builder
         */
        fun setRequestCode(code: Int = 99): Builder {
            requestCode = code
            return this
        }

        fun setActivity(activity: Activity): Builder {
            this.activity = activity
            return this
        }

        /**
         * 设置权限被拒后下次执行后进行提示
         * @param isTip Boolean
         * @return Builder
         */
        fun setAlwaysDenied(isTip: Boolean = true): Builder {
            this.isAlwaysDenied = isTip
            return this
        }

        /**
         * 设置需要获取的权限
         * @param requestPermissions Array<out String>
         * @return Builder
         */
        fun setRequestPermission(vararg requestPermissions: String): Builder {
            if (requestPermissions.isNullOrEmpty()) return this
            permissions = requestPermissions.toMutableList()
            return this
        }

        /**
         * 设置权限返回
         * @param callback IPermissionResult?
         * @return Builder
         */
        fun setCallback(callback: IPermissionResult): Builder {
            iPermissionResult = callback
            return this
        }

        /**
         * 执行配置
         */
        fun build() {
            //判断当前属性是否初始化
            if (!::permissions.isInitialized) {
                Log.e("PermissionManager", "permissions 未初始化")
                ToastUtils.showShortToast("permissions 未初始化")
                return
            }
            if (!::permissionManager.isInitialized) {
                permissionManager = PermissionManager()
            }
            if (::iPermissionResult.isInitialized) {
                permissionManager.iPermissionResult = iPermissionResult
                permissionManager.isAlwaysDenied = isAlwaysDenied
            }
            permissionManager.activity = activity
        }

        fun reqeust() {
            permissionManager.getPermission(permissionManager.activity, permissions, requestCode)
        }

        init {
            if (!::permissionManager.isInitialized) {
                permissionManager = PermissionManager()
            }
        }
    }

}