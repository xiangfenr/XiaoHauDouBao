package com.yundou.loans.base

import com.yundou.loans.exceptions.ApiException

sealed class DataResult<out T> {
    data class Success<T>(val response: T) : DataResult<T>()
    data class Error(val exception: ApiException) : DataResult<Nothing>()
}

