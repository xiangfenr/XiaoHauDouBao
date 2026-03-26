package com.yundou.loans.base

enum class ERROR(private val code: Int, private val err: String) {

    UNKNOWN(1000, "网络错误，请稍后重试"),
    PARSE_ERROR(1001, "服务数据异常!请稍后再试!"),
    NETWORD_ERROR(1002, "网络错误，请稍后重试"),
    HTTP_ERROR(1003, "服务数据异常!请稍后再试!"),
    SSL_ERROR(1004, "网络错误，请稍后重试"),
    TIMEOUT_ERROR(1006, "连接超时，请稍后重试"),
    ARGUMENT_ERROR(1008, "不合法的参数"),
    RESULT_ERROR(1009, "返回数据结构错误"),
    NULL_ERROR(1010, "操作的对象或属性不存在");

    fun getValue(): String = err
    fun getKey(): Int = code
}

