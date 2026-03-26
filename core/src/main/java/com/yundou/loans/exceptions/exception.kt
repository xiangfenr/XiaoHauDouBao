package com.yundou.loans.exceptions

import kotlinx.coroutines.CoroutineExceptionHandler

val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    ExceptionHandler.handleException(throwable)
}
