package com.yundou.loans.base

class BaseResponse<T>(
    val data: T,
    val errorCode: Int,
    val errorMsg: String,
) {
    fun isSuccessful(): Boolean = errorCode == 0
}

