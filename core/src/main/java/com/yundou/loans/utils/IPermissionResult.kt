package com.yundou.loans.utils


/**
 *@ClassName IPermissionResult
 *@Deseription  权限监听
 *@author：wangmingyu
 *@date：2021/2/318:03
 */
interface IPermissionResult {
    fun getPermissionFailed(requestCode: Int, deniedPermissions: Array<String?>?){}
    
    fun getPermissionSuccess(requestCode: Int)
}