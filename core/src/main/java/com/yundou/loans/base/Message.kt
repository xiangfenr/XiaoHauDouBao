package com.yundou.loans.base

import androidx.annotation.Keep

/**
 * 定义公共消息传递类
 */
@Keep
class Message @JvmOverloads constructor(
    var code: Int = 0,
    var msg: String = "",
    var arg1: Int = 0,
    var arg2: Int = 0,
    var obj: Any? = null
)

