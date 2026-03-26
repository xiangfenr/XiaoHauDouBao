package com.yundou.loans.utils

import android.R.attr.entries

enum class AccountType(val code: Int) {
    /**
     * 0、正常新用户
     * 登录方式: 验证码登录
     * 是否展示贷超: yes
     **/
    NORMAL_NEW_USER(0),

    /**
     *  1、注销账号 (客户端拦截登录)
     *  登录方式: no
     */
    CANCELLED_ACCOUNT(1),

    /**
     * 2、应用商店审核 (客户端切到本服)
     * 登录方式: 密码登录
     * 是否展示贷超: no
     * ***/
    APP_STORE_REVIEWER(2),

    /** 3、自测手机号 (客户端自己测试使用，不用切到本服)
     * 登录方式: 验证码登录
     * 是否展示贷超: yes
     * **/
    SELF_TEST_PHONE(3),

    /** 4、提交测试账号 (客户端切到本服)
     * 登录方式: 密码登录
     * 是否展示贷超: no
     * **/
    SUBMITTED_TEST_ACCOUNT(4),

    /** 5、历史账号 走合作方,不展示贷超
     * 登录方式: 验证码登录
     * 是否展示贷超: no
     * **/
    HISTORICAL_ACCOUNT(5);       //


}