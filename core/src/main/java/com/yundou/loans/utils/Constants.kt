package com.yundou.loans.utils

class Constants {

    companion object {
        const val IDCARD_RECOGNIZE = "recognize_id_card" //身份证识别：1=关闭,2=打开
        const val IS_EDIT_FORM = "is_edit_form" //是否填写表单
        const val IDCARD_AGE = "age" //年龄
        const val PHONE_NUMBER_STATUS = "phone_number_status" //手机账号状态

//        const val IS_OPPO_TEST_ACCOUNT = "13477799155" //这个账号oppo 打开, 还款记录, 其余的都不开

        //渠道
        const val CHANNEL_VIVO = "1"
        const val CHANNEL_OPPO = "2"
        const val CHANNEL_HUAWEI = "3"
        const val CHANNEL_XIAOMI = "4"
        const val CHANNEL_QQ = "5"
        const val CHANNEL_TEST = "6"
        const val CHANNEL_RONGYAO = "8"

        //第三方对应的partnerID值
        const val PARTNER_BENBU = 1  //本部
        const val PARTNER_SHENGR = 3  //笙融
        const val PARTNER_ZXD = 4   //智享贷
        const val PARTNER_KYD = 5   //快易贷(废弃)
        const val PARTNER_MOLI = 6  //魔力
        const val PARTNER_TWOP = 7  //二项目
        const val PARTNER_JIYONGQB = 8  //吉用钱包
        const val PARTNER_YANGXINHUA = 9  //阳薪花(废弃)
        const val PARTNER_JIYONGBANG = 10  //吉用帮
        const val PARTNER_YOUQIANQB = 11//有钱钱包
        const val PARTNER_TXFQ = 12 //天下分期
        const val PARTNER_YUANXIAOHUA = 13 //源小花
        const val PARTNER_QIDAI = 14 //期贷
        const val PARTNER_JIDAI = 15 //吉贷
        const val PARTNER_YUEXIANG = 16 //悦享
        const val PARTNER_SHANDAIMIAO = 17 //闪贷喵

        //吉贷钱包
        const val JIDAI_ORGID = "179"
        const val JIDAI_KEY = "11E47xULRrWI14AG"

        const val WQB_ORANGE_ORGID = "20645"
        const val WQB_ORANGE_CHANNWL = "zhyb"
        const val WQB_ORANGE_APIKEY = "H9ke88m89mh921pp"

        //吉用钱包
        const val JIYONG_PUBLICKEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbeJ2/hbXYci1l/UhU6/7rMj8Mq4JBscNlFFfkpL//BBLGMnj0CmuxAG1reWu45ChLqA83rO2XjgN6m7w/xkayMnM7e9qF0YNdicl0fTay872AJqcGY2GtLK7Pgaom56hwbCB9SuFrQy/ySmJAhBSqO50x1HwQtnc5WuGOy31EbQIDAQAB"
        const val JIYONG_CHANNEL = "wmyjj"
        const val JIYONG_PROTOCOL1 = "https://jh5.yihua16888.com/protocols/grgxxy.html"
        const val JIYONG_PROTOCOL2 = "https://jh5.yihua16888.com/protocols/mzsm.html"

        //正式
        const val YXH_AESKEY = "flOh2xYoRdfAT8yd"
        const val YXH_ChannelCode = "5VIWebs"
        const val YXH_USERID = "yxh_userid"

        //吉用帮
        const val JYB_distributorId = "2847"
        //二项目密
        const val TWOP_SIGB = "littlefish"

        //笙融Header
        const val SR_CHANNEL = "ssxd"
        const val SR_Platform = "ANDROID"
        const val MYSMS_CHANNEL = "ssxd"

        //有钱钱包 Channel_code  正式 Wffrff  测试83EsPZ
        const val YQQB_CHANNELCODE = "Wffrff"

        const val TXFQ_PUBLICKEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnqz9ck/ngf8O06tVYlfZ/4KjqEf6RDr/3T5ADVzwqujelY0JRiloSz/ecvpzACvAILm4UG/m5s0itDocdxPNmHtvfl7UzqHr95ayviI9Sgfwc4lmWP0cnX+MX/qaVGySlcpowZCH7ngOXLvv94Bw/E6joRsADUSKqSelqTSMOVQIDAQAB"
        const val TXFQ_CHANNEL = "wmyoppo"

        //智享贷新接口相关参数
        const val ZXD_PUBLICK_KEY = "CuJAIERIEzUAzNXy"
        const val ZXD_CHANNELCODE = "weimiaochu-md5"
        const val ZXD_STATUS = "zxd_status"
        const val ZXD_CLICKDEAL = "zxd_click_deal"

        //逾期贷款处理
        const val YQDKCL_PUBLICK_KEY = "i5BerLIgbMvRh289c6VfJ1zxjNQuW0DY"
        const val YQDKCL_WEBURL =
            "https://dcyqh5.zhiyunjishu.cn/dcyq_h5_v2/#/pages/home/index?channel=zy_dcyq_wmy&content="

        //源小花相关配置
        const val YXH_APP_ID = "9e47866481038138ae7b2422fa916ce6"
        const val YXH_SECRET = "64eab087135939078a22e9e248552dec"
        const val YXH_PACKAGE_NAME = "CHANNEL_WEIMIAOYONG"
        const val YXH_VERSION = "1.0.0"
        const val YXH_NONCE = "123456789"
        const val TEST_TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJodHRwOi8vb3BlbmFwaS5kZXYuanVqaW5jaGVuZy5jb206NDQzL3Zlc3Qva2toL29hdXRoL2xvZ2luL21vYmlsZSIsImlhdCI6MTc1Njg3OTMwNCwiZXhwIjoxNzU3MTM4NTA0LCJuYmYiOjE3NTY4NzkzMDQsImp0aSI6IkZZQkFYdk9BOVVBOG13SlMiLCJzdWIiOiI0MDExNzI2OTE3NyIsInBydiI6ImY2YjcxNTQ5ZGI4YzJjNDJiNzU4MjdhYTQ0ZjAyYjdlZTUyOWQyNGQiLCJtb2JpbGUiOjE4MzAxNjg1NTk2fQ.yIA9GRCW18Qrq6OfFQeyxqnzlv5LokuTwZHmljdpJeUtKrZJhDVRtQUKd_ws5thfsMmlwJY25-e6nCLAq-6HEg"

        //期贷
        const val QIDAI_PUBLICK_KEY = "yoarkJ2JpdcnrPQd"
        const val QIDAI_CHANNELCODE = "4357" //正式=4357 测试=52679

        //微融宝
        const val WEIRONGBAO_CHANNWL_CODE = "weimiaoytwo-ch"
        const val WEIRONGBAO_APPKEY = "D65hSxptSnavsXw2"

        //小虾渠道
        const val SHRIMP_ZXD = 17  //智享贷
        const val SHRIMP_WRB = 18  //微融宝
        const val SHRIMP_XIAOFU = 19 //小福借款
        const val SHRIMP_LONGYAN = 29 //龙焱
        const val SHRIMP_WEIYIN = 21 //微银
        const val SHRIMP_JIYIHUA_MD5 = 24 //吉意花MD5
        const val SHRIMP_JIYIHUA_MASK = 25 //吉意花掩码
        //八戒
        const val SHRIMP_BAJIE = 28  //八戒掩码
        const val SHRIMP_JIYONGQIANBAO = 30  //吉用钱包

        //吉贷相关参数
        const val JIDAI_APPID = "2"
        const val JIDAI_CHANNELID = "3"
        const val JIDAI_CHANNELCODE = "test"
        const val JIDAI_SHOUCIXZTYPE = "2" //2 短信


        //小福相关参数
        val XIAOFU_CHANNEL_NUM = "QD843"
        const val XIAOFU_PUBLICK_KEY = "PBloA7Rktc0skqZF"
        const val XIAOFU_IV = "A3rSUqVhK37buplK"
        const val XIAOFU_FACTOR = "IgB1yNTevvBVrCiY"

        //龙炎密钥
        const val LONGYAN_APPKEY = "6454A9AFCC693CAA"

        //微银相关参数
        val WEIYIN_APPKEY = "lERAMn405eM646h8"
        val WEIYIN_ORGID = "202305663"


        //吉意花MD5--相关参数
        val JIYIHUA_MD5_CHANNEL = "dIxf4M5w"  //测试环境 58ngCWdm   正式环境 dIxf4M5w
        val JIYIHUA_MD5_APPKEY = "mP28wkvIPPao3FYC"  //测试环境  718k0A7KVLW9sUE4  正式环境  mP28wkvIPPao3FYC

        //吉意花掩码
        val JIYIHUA_MASK_CHANNEL = "r1og3TyE" //测试环境 SSJcPmwc 正式环境 r1og3TyE
        val JIYIHUA_MASK_APPKEY = "0jIR6Gb8BFoJDt7R"  //测试环境  kW2c7X0cuV8x0G53  正式环境  0jIR6Gb8BFoJDt7R


        //八戒相关参数
        val BAJIE_APPKEY = "b81281e4813a174a"
        val BAJIE_CHANNEL = "Av2RVE7znyvyF9xkZmLceE2dEwTW5DfBMbqJRGfuTL"  //正式环境  Av2RVE7znyvyF9xkZmLceE2dEwTW5DfBMbqJRGfuTL

        //闪贷喵参数
        val SHANDAIMIAO_APPKEY= "n8UX5NUYr6X1uL02"
        val SHANDAIMIAO_ORGID = "384"

        //****吉用钱包全流程相关参数
         const val JYQB_CHANNELCODE = "jwmyjj"
         const val JYQB_PULICK_KEY = "9LjlHF5ji9TAyrxNiCcjqY5usVHXRs5V"
         const val JYQB_IV = "rkz02Ueu"
    }
}

