package com.yundou.loans.utils

class Constants {

    companion object {
        const val IDCARD_RECOGNIZE = "recognize_id_card" //身份证识别：1=关闭,2=打开
        const val IS_EDIT_FORM = "is_edit_form" //是否填写表单
        const val IDCARD_AGE = "age" //年龄
        const val PHONE_NUMBER_STATUS = "phone_number_status" //手机账号状态

        //渠道
        const val CHANNEL_VIVO = "1"
        const val CHANNEL_OPPO = "2"
        const val CHANNEL_HUAWEI = "3"
        const val CHANNEL_XIAOMI = "4"
        const val CHANNEL_QQ = "5"
        const val CHANNEL_TEST = "6"
        const val CHANNEL_RONGYAO = "8"

        //当前仅保留本部与魔力，其余合作方常量已移除
        const val PARTNER_BENBU = 1  //本部
        const val PARTNER_MOLI = 6  //魔力
    }
}
