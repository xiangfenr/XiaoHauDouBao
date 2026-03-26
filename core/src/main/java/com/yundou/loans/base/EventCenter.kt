package com.yundou.loans.base

import com.yundou.loans.widget.SingleLiveEvent

/**
 * 跨模块/跨页面的全局事件中心：
 * - core 只负责发事件
 * - app(壳) 决定如何处理（跳转登录页、弹 toast 等）
 */
object EventCenter {
    val globalMessageEvent: SingleLiveEvent<Message> = SingleLiveEvent()

    fun post(message: Message) {
        globalMessageEvent.postValue(message)
    }

    fun postNeedLogin() {
        globalMessageEvent.postValue(Message(code = 401, msg = "NEED_LOGIN"))
    }
}

