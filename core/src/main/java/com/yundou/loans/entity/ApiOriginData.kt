package com.yundou.loans.entity

/**
 * API撞库 通用的 数据Data
 */
data class ApiOriginData(
    /** 中国大陆手机号 MD5加密小写 */
    var phone_md5: String? = null,

    /** 姓名（可选） */
    var real_name: String? = null,

    /** 身份证号 MD5加密 */
    var id_card_md5: String? = null,

    /** 性别：1:男 2:女 */
    var sex: Int? = -1,

    /** 年龄 */
    var age: Int? = -1,

    /** 城市代码 */
    var city_code: String? = null,

    /** 城市名称，所在城市，例如‘北京市’，需要带市 */
    var city_name: String? = null,

    /** 职业：0无 1上班族(私企) 2企业主 3自由职业 4国企(公务员) 5事业单位 6其他 */
    var occupation: Int? = -1,

    /** 社保：0:无 1:6个月以下 2:6个月以上 */
    var social_security: Int? = -1,

    /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
    var sesame_score: Int? = -1,

    /** 公积金：0:无 1:6个月以下 2:6个月以上 */
    var accumulation_fund: Int? = -1,

    /** 车产：0:无 1:有车不接受抵押 2:有车接受抵押 */
    var car_property: Int? = -1,

    /** 房产：0:无 1:有房不接受抵押 2:有房接受抵押 */
    var house_property: Int? = -1,

    /** 商业保险：0:无 1:6个月以下 2:6个月以上 */
    var personal_insurance: Int? = -1,

    /** 借款金额，单位：元 */
    var loan_amount: Int? = -1,

    /** 教育程度：0:未知 1:博士 2:硕士 3:大学本科 4:大专 5:高中/中专/技校 6:初中 7:初中以下 */
    var education: Int? = -1,

    /** 婚姻状况：0未知 1已婚 2未婚 3离异 4丧偶 5再婚 */
    var marital_status: Int? = -1,

    /** 花呗额度：0:无额度 1:5000以上 2:5000以内 */
    var huabei: Int? = -1,

    /** 白条额度：0:无额度 1:5000以上 2:5000以内 */
    var baitiao: Int? = -1,

    /** 营业执照：0:无 1:有营业执照 */
    var business: Int? = -1,

    /** 信用状况：1:当前无逾期 2:当前有逾期 */
    var credit: Int? = -1,

    //自定义企业主  0=无  1=企业主
    var qiYeZhu: Int? = -1,

    /** 用户IP地址 */
    var ip: String? = null,

    var phone: String? = null, //进件的时候提供
    var id_card: String? = null, //进件的时候提供

    var cityNoShi: String? = null,

    //只是本服务器使用
    var partner_id: Int? = null,
    var price: Double? = null,
    var channel_id: String? = null,

    //期贷需要
    var province: String? = null,
)

