package com.yundou.loans.utils

import androidx.annotation.Keep

/**
 *@ClassName:
 *@Deseription: 定义公共消息传递类
 *@author：wangmingyu
 *@date：2020/8/24 10:55
 */
@Keep
class Message @JvmOverloads constructor(
    var code: Int = 0,
    var msg: String= "",
    var arg1: Int = 0,
    var arg2: Int = 0,
    var obj: Any? = null
)