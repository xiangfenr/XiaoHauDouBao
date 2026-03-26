package com.yundou.loans.utils

import com.yundou.loans.entity.*
import java.util.HashMap
import kotlin.collections.joinToString
import kotlin.collections.random
import kotlin.toString

/**
 * @Author: fenr
 * 时间: 2025/9/17
 * 类名: ACTIVITY
 * 简述:  表单实体类转换
 *
 */
object FormDataConversionUtils {


    //智享贷上报
    fun matchZXDData(data: ApiOriginData): ZxdAPISaveData {
        val zxdData = ZxdAPISaveData()
        zxdData.phone_md5 = data.phone_md5
        zxdData.real_name = data.real_name
        zxdData.id_card_md5 = data.id_card_md5
        zxdData.sex = data.sex
        zxdData.age = data.age
        zxdData.city_code = data.city_code
        zxdData.city_name = data.city_name
        zxdData.occupation = data.occupation
        zxdData.social_security = data.social_security
        zxdData.sesame_score = data.sesame_score
        zxdData.accumulation_fund = data.accumulation_fund
        zxdData.car_property = data.car_property
        zxdData.house_property = data.house_property
        zxdData.personal_insurance = data.personal_insurance
        zxdData.loan_amount = data.loan_amount
        zxdData.education = data.education
        zxdData.marital_status = data.marital_status
        zxdData.huabei = data.huabei
        zxdData.baitiao = data.baitiao
        zxdData.business = data.business
        zxdData.credit = data.credit
        zxdData.ip = data.ip
        return zxdData
    }

    fun matchToMoLiData(data: ApiOriginData): MoLiFormData {
        val moliData = MoLiFormData()
        moliData.realname = data.real_name
        moliData.id_card_no = data.id_card
        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        when (data.sesame_score) {
            0, 3, 4, 5 -> {
                moliData.zhima_score = "504"
            }

            1 -> {
                moliData.zhima_score = "506"
            }

            2 -> {
                moliData.zhima_score = "505"
            }
        }
        val selectZichan = arrayListOf<Int>()
        if (data.baitiao != 0) {
            selectZichan.add(1)
        }
        if (data.social_security != 0) {
            selectZichan.add(2)
        }
        if (data.accumulation_fund != 0) {
            selectZichan.add(3)
        }
        if (data.car_property != 0) {
            selectZichan.add(4)
        }
        if (data.house_property != 0) {
            selectZichan.add(5)
        }
        if (data.business != 0) {
            selectZichan.add(6)
        }
        //7 信用卡无
        if (data.personal_insurance != 0) {
            selectZichan.add(9)
        }
        moliData.other_assets = selectZichan.joinToString(separator = ",")

        return moliData
    }

    //天下分期 Data转换
    fun matchToTXFQData(data: ApiOriginData): TxfqSaveData {
        val txfqData = TxfqSaveData()
        txfqData.realName = SHA256.RSAEncrypt(
            Constants.TXFQ_PUBLICKEY, data.real_name ?: ""
        )
        txfqData.idCard = SHA256.RSAEncrypt(
            Constants.TXFQ_PUBLICKEY, data.id_card ?: ""
        )
        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        when (data.sesame_score) {
            0, 4, 5 -> {
                txfqData.credit = "600以下"
            }

            1 -> {
                txfqData.credit = "700以上"
            }

            2 -> {
                txfqData.credit = "650-700"
            }

            3 -> {
                txfqData.credit = "600-650"
            }
        }
        if (data.baitiao != 0) {
            txfqData.jdIous = 0
        } else {
            txfqData.jdIous = 1
        }
        if (data.huabei != 0) {
            txfqData.antCreditPay = 0
        } else {
            txfqData.antCreditPay = 1
        }

        //assets 多选择
        val selectZichan = arrayListOf<Int>()
        if (data.house_property != 0) {
            selectZichan.add(1)
        }
        if (data.car_property != 0) {
            selectZichan.add(2)
        }
        if (data.accumulation_fund != 0) {
            selectZichan.add(3)
        }
        if (data.social_security != 0) {
            selectZichan.add(4)
        }
        if (data.personal_insurance != 0) {
            selectZichan.add(5)
        }
        if (data.business != 0) {
            selectZichan.add(6)
        }
        txfqData.assets = selectZichan

        val list = arrayOf("3", "6", "12", "24")
        txfqData.loanPeriod = list.random()

        val list2 = arrayOf(
            "资金周转",
            "日常消费",
            "房屋装修",
            "医疗贷款",
            "旅游贷款",
            "买车贷款",
            "其它"
        )
        txfqData.loanPurpose = list2.random()

        txfqData.loanAmount = data.loan_amount.toString()


        return txfqData
    }


    //笙融 Data转换
    fun matchToShengRongData(data: ApiOriginData): SaveData {
        val srData = SaveData()
        srData.name = data.real_name
        srData.idCardNo = data.id_card
        if (data.car_property == 0) {
            srData.car = "0"
        } else {
            srData.car = "1"
        }
        srData.cityOfWork = data.city_name
        srData.cityOfWorkCode = data.city_code
        if (data.baitiao == 0) {
            srData.creditCard = "0"
        } else {
            srData.creditCard = "1"
        }
        /** 教育程度：0:未知 1:博士 2:硕士 3:大学本科 4:大专 5:高中/中专/技校 6:初中 7:初中以下 */
        //学历 0.高中/中专 1.大专 2.本科 3.硕士 4.博士
        when (data.education) {
            0, 5, 6, 7 -> srData.education = "0"
            1 -> srData.education = "4"
            2 -> srData.education = "3"
            3 -> srData.education = "2"
            4 -> srData.education = "1"
        }
        if (data.house_property == 0) {
            srData.house = "0"
        } else {
            srData.house = "1"
        }
        if (data.personal_insurance == 0) {
            srData.insurance = "0"
        } else {
            srData.insurance = "1"
        }
        srData.loanAmount = data.loan_amount
        val list = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
        srData.loanLimit = list.random()
        val list2 = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        srData.loanUse = list2.random()

        /** 职业：0无 1上班族(私企) 2企业主 3自由职业 4国企(公务员) 5事业单位 6其他 */
        //职业 1.上班族 2.个体户 3.电商主 4.自由职业者 5.企业主 6.其他
        when (data.occupation) {
            0, 6 -> srData.profession = "6"
            1 -> srData.profession = "1"
            2 -> srData.profession = "5"
            3 -> srData.profession = "4"
            4 -> srData.profession = "3"
            5 -> srData.profession = "2"
        }
        if (data.accumulation_fund == 0) {
            srData.reservedFunds = 0
        } else {
            srData.reservedFunds = 1
        }
        val list3 = arrayOf(0, 1)
        srData.revenue = list3.random()

        if (data.social_security == 0) {
            srData.socialSecurity = "0"
        } else {
            srData.socialSecurity = "1"
        }
        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //芝麻分 1.550分以下 2.550-600分 3.600-650分 4.650-700分 5.700分及以上
        when (data.sesame_score) {
            0 -> srData.zhima = "1"
            1 -> srData.zhima = "5"
            2 -> srData.zhima = "4"
            3 -> srData.zhima = "3"
            4 -> srData.zhima = "2"
            5 -> srData.zhima = "1"
        }
        return srData
    }

    data class YxhResultBundle(
        val chooseContentMap: kotlin.collections.HashMap<String, String>,
        val chooseIdMap: kotlin.collections.HashMap<String, String>,
        val yxhData: YXHuaSaveData
    )

    //源小花Data转换 YXHuaSaveData
    fun matchToYXHData(data: ApiOriginData): YxhResultBundle {
        val yxhData = YXHuaSaveData()
        yxhData.realName = data.real_name
        yxhData.idCard = data.id_card
        yxhData.city = data.city_name
        /** 芝麻分：    0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //yxh-芝麻分   "600分以下","600-650分", "650-700分","700分以上"

        //芝麻分id=1  房id=3  车id=4  公积金id=5  社保id=19  保险保单id=6  职业身份id=21
        val chooseContentMap = HashMap<String, String>()
        val chooseIdMap = HashMap<String, String>()
        when (data.sesame_score) {
            0, 4, 5 -> {
                chooseContentMap.put("1", "600分以下")

                chooseIdMap.put("1", "68")
                yxhData.zhima.id = "68"
            }

            1 -> {
                chooseContentMap.put("1", "700分以上")

                chooseIdMap.put("1", "71")
                yxhData.zhima.id = "71"
            }

            2 -> {
                chooseContentMap.put("1", "650-700分")

                chooseIdMap.put("1", "70")
                yxhData.zhima.id = "70"
            }

            3 -> {
                chooseContentMap.put("1", "600-650分")

                chooseIdMap.put("1", "69")
                yxhData.zhima.id = "69"
            }

        }

        /** 房产：0:无   1:有房不接受抵押 2:有房接受抵押 */
        //       "无房", "有房可抵押","有房不抵押"
        when (data.house_property) {
            0 -> {
                chooseContentMap.put("3", "无房")
                chooseIdMap.put("3", "140")
                yxhData.house.id = "140"
            }

            1 -> {
                chooseContentMap.put("3", "有房不抵押")
                chooseIdMap.put("3", "138")
                yxhData.house.id = "138"
            }

            2 -> {
                chooseContentMap.put("3", "有房可抵押")
                chooseIdMap.put("3", "139")
                yxhData.house.id = "139"
            }
        }

        /** 车产：0:无 1:有车不接受抵押 2:有车接受抵押 */
        //"无车", "有车可抵押", "有车不抵押"
        when (data.car_property) {
            0 -> {
                chooseContentMap.put("4", "无车")
                chooseIdMap.put("4", "142")
                yxhData.car.id = "142"
            }

            1 -> {
                chooseContentMap.put("4", "有车不抵押")
                chooseIdMap.put("4", "141")
                yxhData.car.id = "141"
            }

            2 -> {
                chooseContentMap.put("4", "有车可抵押")
                chooseIdMap.put("4", "143")
                yxhData.car.id = "143"
            }
        }

        /** 公积金：0:无 1:6个月以下 2:6个月以上 */
        // "无公积金", "缴纳半年以上", "缴纳半年以下"
        when (data.accumulation_fund) {
            0 -> {
                chooseContentMap.put("5", "无公积金")
                chooseIdMap.put("5", "149")
                yxhData.reservedFunds.id = "149"
            }

            1 -> {
                chooseContentMap.put("5", "缴纳半年以下")
                chooseIdMap.put("5", "148")
                yxhData.reservedFunds.id = "148"
            }

            2 -> {
                chooseContentMap.put("5", "缴纳半年以上")
                chooseIdMap.put("5", "147")
                yxhData.reservedFunds.id = "147"
            }
        }

        /** 社保：0:无 1:6个月以下 2:6个月以上 */
        // "无社保", "缴纳半年以上", "缴纳半年以下"
        when (data.accumulation_fund) {
            0 -> {
                chooseContentMap.put("19", "无社保")
                chooseIdMap.put("19", "163")
                yxhData.socialSecurity.id = "163"
            }

            1 -> {
                chooseContentMap.put("19", "缴纳半年以下")
                chooseIdMap.put("19", "164")
                yxhData.socialSecurity.id = "164"
            }

            2 -> {
                chooseContentMap.put("19", "缴纳半年以上")
                chooseIdMap.put("19", "164")
                yxhData.socialSecurity.id = "164"
            }
        }

        /** 商业保险：0:无 1:6个月以下 2:6个月以上 */
        // "无保单", "缴纳一年以上", "缴纳一年以下"
        when (data.accumulation_fund) {
            0 -> {
                chooseContentMap.put("6", "无保单")
                chooseIdMap.put("6", "146")
                yxhData.insurance.id = "146"
            }

            1 -> {
                chooseContentMap.put("6", "缴纳一年以下")
                chooseIdMap.put("6", "145")
                yxhData.insurance.id = "145"
            }

            2 -> {
                chooseContentMap.put("6", "缴纳一年以上")
                chooseIdMap.put("6", "144")
                yxhData.insurance.id = "144"
            }
        }

        /** 职业：0无 1上班族(私企) 2企业主 3自由职业 4国企(公务员) 5事业单位 6其他 */
        //          "上班族","企业主","个体户", "自由职业"
        when (data.occupation) {
            1 -> {
                chooseContentMap.put("21", "上班族")
                chooseIdMap.put("21", "170")
                yxhData.profession.id = "170"
            }

            2 -> {
                chooseContentMap.put("21", "企业主")
                chooseIdMap.put("21", "171")
                yxhData.profession.id = "171"
            }

            0, 4, 5, 6 -> {
                chooseContentMap.put("21", "个体户")
                chooseIdMap.put("21", "172")
                yxhData.profession.id = "172"
            }

            3 -> {
                chooseContentMap.put("21", "3自由职业")
                chooseIdMap.put("21", "173")
                yxhData.profession.id = "173"
            }
        }

        if (data.credit == 0) {
            chooseContentMap.put("23", "现金收入")
            chooseIdMap.put("23", "179")
            yxhData.revenue.id = "179"
        } else {
            chooseContentMap.put("23", "现金收入")
            chooseIdMap.put("23", "180")
            yxhData.revenue.id = "180"
        }

        val yxhResultBundle = YxhResultBundle(chooseContentMap, chooseIdMap, yxhData)

        return yxhResultBundle
    }

    //转化期贷Data
    fun matchToQiDaiData(data: ApiOriginData): QiDaiSaveData {
        val qiDaiSaveData = QiDaiSaveData()
        qiDaiSaveData.mobile = data.phone
        qiDaiSaveData.age = data.age
        qiDaiSaveData.sex = data.sex
        qiDaiSaveData.province = data.province
        qiDaiSaveData.city = data.city_name
        qiDaiSaveData.name = data.real_name
        qiDaiSaveData.ip = data.ip

        when (data.loan_amount) {
            50000 -> qiDaiSaveData.amount = 1
            100000 -> qiDaiSaveData.amount = 2
            150000 -> qiDaiSaveData.amount = 3
            200000 -> qiDaiSaveData.amount = 4
        }
        val list = arrayOf(0, 1, 2, 3, 4, 5)
        qiDaiSaveData.loanFor = list.random()

        /** data.education: 教育程度：0:未知 1:博士 2:硕士 3:大学本科 4:大专 5:高中/中专/技校 6:初中 7:初中以下 */
        //学历0：大专以下 ， 1： 大专 ，2 本科 ，3 本科及以上
        when (data.education) {
            0, 5, 6, 7 -> qiDaiSaveData.education = 0
            1, 2 -> qiDaiSaveData.education = 3
            3 -> qiDaiSaveData.education = 2
            4 -> qiDaiSaveData.education = 1
        }

        /** occupation 职业：0无 1上班族(私企) 2企业主 3自由职业 4国企(公务员) 5事业单位 6其他 */
        //职业身份            0：上班族，1：企业主，2：个体户，3：自由职业，4：事业单位
        when (data.occupation) {
            0, 1 -> qiDaiSaveData.job = 0
            2 -> qiDaiSaveData.job = 1
            3 -> qiDaiSaveData.job = 3
            4, 5 -> qiDaiSaveData.job = 4
            6 -> qiDaiSaveData.job = 2
        }
        if (data.social_security == 0) {
            qiDaiSaveData.shebao = 0
        } else {
            qiDaiSaveData.shebao = 2
        }

        qiDaiSaveData.gongjijin = data.accumulation_fund
        qiDaiSaveData.house = data.house_property
        if (data.car_property == 0) {
            qiDaiSaveData.vehicle = 0
        } else {
            qiDaiSaveData.vehicle = 1
        }

        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //        芝麻分情况 2：650以下，3：650-700分，4：700分以上
        when (data.sesame_score) {
            0, 3, 4, 5 -> qiDaiSaveData.zhima = 2
            1 -> qiDaiSaveData.zhima = 4
            2 -> qiDaiSaveData.zhima = 3
        }
        /** 信用状况 1:当前无逾期 2:当前有逾期  */
        //当前征信有无逾期 0：当前无逾期，3：当前有逾期
        if (data.credit == 1) {
            qiDaiSaveData.overdue = 0
        } else {
            qiDaiSaveData.overdue = 3
        }

        /** 商业保险：0:无 1:6个月以下 2:6个月以上 */
        //保单       0：未买保险，2：有保险
        if (data.personal_insurance == 0) qiDaiSaveData.insurance = 0
        else qiDaiSaveData.insurance = 2

        val list2 = arrayOf(0, 1, 2, 3)
        qiDaiSaveData.monthIncome = list2.random()

        qiDaiSaveData.idCard = data.id_card
        return qiDaiSaveData
    }

    fun matchToJiYongQianBaoData(data: ApiOriginData): JIYongSaveData {
        val jyqbData = JIYongSaveData()
        jyqbData.mobile = data.phone
        jyqbData.age = data.age.toString()
        jyqbData.sex = data.sex.toString()
        jyqbData.name = data.real_name
        jyqbData.sfz = data.id_card
        jyqbData.city = data.city_name
        jyqbData.cityCode = data.city_code
        jyqbData.loanAmount = data.loan_amount.toString()
        //贷款期限
        val list = arrayOf(1, 2, 3, 4, 5, 6)
        jyqbData.period = list.random().toString()
        //借款用途
        val list2 = arrayOf(1, 2, 3, 4, 5, 6, 7)
        jyqbData.purpose = list2.random().toString()

        //吉用钱包选项  1=有 0=无
        //智享贷选项 /** 房产：0:无 1:有房不接受抵押 2:有房接受抵押 */
        if (data.car_property == 0) {
            jyqbData.car = 0.toString()
        } else {
            jyqbData.car = 1.toString()
        }
        if (data.house_property == 0) {
            jyqbData.house = 0.toString()
        } else {
            jyqbData.house = 1.toString()
        }
        if (data.personal_insurance == 0) {
            jyqbData.insurance = 0.toString()
        } else {
            jyqbData.insurance = 1.toString()
        }
        if (data.social_security == 0) {
            jyqbData.salary = 0.toString()
        } else {
            jyqbData.salary = 1.toString()
        }
        if (data.accumulation_fund == 0) {
            jyqbData.fund = 0.toString()
        } else {
            jyqbData.fund = 1.toString()
        }

        /** 智相待芝麻分 0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //吉用钱包芝麻分 1:0-600，2:650-700，3:700以上，4:600-650
        when (data.sesame_score) {
            0, 4, 5 -> jyqbData.sesame = "1"
            1 -> jyqbData.sesame = "3"
            2 -> jyqbData.sesame = "2"
            3 -> jyqbData.sesame = "4"
        }
        //企业主
        if (data.business == 0) {
            jyqbData.owners = 0.toString()
        } else {
            jyqbData.owners = 1.toString()
        }
        return jyqbData
    }

    //转化 微融宝
    fun matchToWeiRongBaoData(data: ApiOriginData): WrbSaveData {
        val wrbData = WrbSaveData()
        wrbData.phoneMd5 = data.phone_md5
        wrbData.idCard = data.id_card
        wrbData.city = data.city_name
        wrbData.sex = data.sex
        wrbData.age = data.age
        wrbData.device = 1
        wrbData.ip = data.ip

        /** 智享贷芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //芝麻分：0-无 1-大于700，2-(650-700) 3-(600-650) 4-小于600
        when (data.sesame_score) {
            5 -> wrbData.sesameScore = 4
            else -> wrbData.sesameScore = data.sesame_score
        }
        /** 车产：0:无 1:有车不接受抵押 2:有车接受抵押 */
        //  车产：0-无 1-有车产不抵押 2-有车产可抵押
        wrbData.car = data.car_property
        wrbData.house = data.house_property
        wrbData.fund = data.accumulation_fund
        wrbData.social = data.social_security
        wrbData.insurance = data.personal_insurance
        /** 白条额度：0:无额度 1:5000以上 2:5000以内 */
        //  白条额度：0-无 1-（0-2000） 2-（2000-5000）3-（5000以上）
        when (data.baitiao) {
            0 -> wrbData.baiTiao = 0
            1 -> wrbData.baiTiao = 3
            2 -> wrbData.baiTiao = 2
        }
        //花呗
        when (data.huabei) {
            0 -> wrbData.huaBei = 0
            1 -> wrbData.huaBei = 3
            2 -> wrbData.huaBei = 2
        }
        /** 信用状况：1:当前无逾期 2:当前有逾期 */
        //信用卡：0=无 1=1万以下 2=(1-3万）3=3万以上
        if (data.credit == 1) {
            wrbData.creditCard = 1
        } else {
            wrbData.creditCard = 0
        }
        /** 信用状况：1:当前无逾期 2:当前有逾期 */
        //  逾期记录：6个月内逾期 1-有 0-无
        if (data.credit == 1) {
            wrbData.creditCard = 0
        } else {
            wrbData.creditCard = 1
        }
        /** 职业：0无 1上班族(私企) 2企业主 3自由职业 4国企(公务员) 5事业单位 6其他 */
        //职业：1-上班族 2-公务员 3-企业主 4-个体户 5-自由职业
        when (data.occupation) {
            1 -> wrbData.occupation = 1
            2 -> wrbData.occupation = 3
            3 -> wrbData.occupation = 5
            4 -> wrbData.occupation = 2
            0, 5, 6 -> wrbData.occupation = 4
        }
        /** 教育程度：0:未知 1:博士 2:硕士 3:大学本科 4:大专 5:高中/中专/技校 6:初中 7:初中以下 */
        //学历：1-高中及以下 2-中专 3-大专 4-本科及以上
        when (data.education) {
            0, 6, 7 -> wrbData.education = 1
            1, 2, 3 -> wrbData.occupation = 4
            4 -> wrbData.occupation = 3
            5 -> wrbData.occupation = 2
        }
        //月收入
        val list2 = arrayOf(2, 3, 4)
        wrbData.salary = list2.random()
        //工资发放
        val list3 = arrayOf(0, 1, 2, 3)
        wrbData.paymentForm = list3.random()
        //当前单位工龄
        val list4 = arrayOf(0, 1, 2, 3)
        wrbData.yearsService = list4.random()
        /** 营业执照：0:无 1:有营业执照 */
        //营业执照：0-无 1-注册1年以下 2-注册1-5年 3-注册5年以上
        if (data.business == 0) {
            wrbData.businessLicense = 0
        } else {
            wrbData.businessLicense = 2
        }


        //贷款额度：10000 -（1万以下） 50000 -（1-3万）100000 - （3万以上）
        when (data.loan_amount) {
            50000 -> {
                wrbData.loanAmount = "10000"
            }

            100000 -> {
                wrbData.loanAmount = "50000"
            }

            150000, 200000 -> {
                wrbData.loanAmount = "100000"
            }
        }
        return wrbData
    }

    //转化  吉贷
    fun matchToJiDaiData(data: ApiOriginData): JiDaiUserInfo {
        val jdData = JiDaiUserInfo()

        jdData.name = data.real_name
        jdData.idNum = data.id_card
        jdData.occupation = "2"
        //0=无, 1=700+, 2=650-700, 3=600-650, 4=600
        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        if (data.sesame_score == 5) {
            jdData.zhima = "4"
        } else {
            jdData.zhima = data.sesame_score.toString()
        }


        //社保: 0=无, 1=12个月以上, 2=6-12个月, 3=6个月以下
        ///** 社保：0:无 1:6个月以下 2:6个月以上 */
        if (data.social_security == 2) {
            jdData.shebao = "1"
        } else {
            jdData.shebao = "0"
        }
        if (data.accumulation_fund == 2) {
            jdData.gjj = "1"
        } else {
            jdData.gjj = "0"
        }

        if (data.house_property == 2) {
            jdData.house = "1"
        } else {
            jdData.house = "0"
        }
        if (data.car_property == 2) {
            jdData.car = "1"
        } else {
            jdData.car = "0"
        }
        if (data.personal_insurance == 2) {
            jdData.baodan = "1"
        } else {
            jdData.baodan = "0"
        }
        /** 信用状况：1:当前无逾期 2:当前有逾期 */
        // 信用卡: 0=无, 1=有
        if (data.credit == 1) {
            jdData.baodan = "0"
            jdData.xinyong = "0"
        } else {
            jdData.baodan = "1"
            jdData.xinyong = "1"
        }

        jdData.salaryType = "1"
        jdData.monthIncome = "2"
        jdData.city = data.city_name
        jdData.province = data.province


        return jdData
    }

    //转化  小福
    fun matchToXiaoFuData(data: ApiOriginData): XiaoFuUserData {
        val xFData = XiaoFuUserData()
        xFData.phone_mask = data.phone?.take(9) //截取手机号前9位
        xFData.name = XiaoFuAESUtils.md5(data.real_name)
        xFData.age = data.age ?: 0
        xFData.city_name = data.city_name
        xFData.city_code = data.city_code?.toInt()
        xFData.gender = data.sex

        when (data.loan_amount) {
            50000 -> {
                xFData.quota = 1
            }

            100000 -> {
                xFData.quota = 2

            }

            150000, 200000 -> {
                xFData.quota = 4

            }
        }

        //自定义企业主  0=无  1=企业主   /** 营业执照：0:无 1:有营业执照 */
        // 职业 0:未知 1：上班族 2：自由职业 4：企业主
        if (data.qiYeZhu == 1) {
            xFData.job = 4
        } else if (data.business == 1) {
            xFData.job = 2
        } else {
            xFData.job = 1
        }


        /** 房产：0:无 1:有房不接受抵押 2:有房接受抵押 */
        if (data.house_property == 0) {
            xFData.house = 0
        } else {
            xFData.house = 4
        }
        if (data.car_property == 0) {
            xFData.car = 0
        } else {
            xFData.car = 4
        }
        if (data.social_security == 0) {
            xFData.social = 0
        } else {
            xFData.social = 4
        }
        if (data.accumulation_fund == 0) {
            xFData.fund = 0
        } else {
            xFData.fund = 4
        }
        if (data.personal_insurance == 0) {
            xFData.insurance = 0
        } else {
            xFData.insurance = 4
        }


        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //芝麻分情况： 0:未知 1：600分以下 2：600-650 4:650-700 8：700+
        when (data.sesame_score) {
            0 -> {
                xFData.zm = 0
            }

            1 -> {
                xFData.zm = 8
            }

            2 -> {
                xFData.zm = 4
            }

            3 -> {
                xFData.zm = 2
            }

            4, 5 -> {
                xFData.zm = 1
            }
        }
        /** 信用状况：1:当前无逾期 2:当前有逾期 */
        //信用情况 0未填 1有逾期 2无逾期
        if (data.credit == 1) {
            xFData.credit = 2
        } else {
            xFData.credit = 1
        }

        /** 花呗额度：0:无额度 1:5000以上 2:5000以内 */
        //白条花呗 0: 未知 1: 额度5000以下 2:额度5000以上
        if (data.huabei == 0 || data.baitiao == 0) {
            xFData.baitiao_huabei = 0
        } else {
            xFData.baitiao_huabei = 2
        }
        xFData.ip = data.ip
        xFData.os = 2

        return xFData
    }

    // 转化  龙焱
    fun matchToLongYanData(data: ApiOriginData): LongYanSaveData {
        val lYData = LongYanSaveData()
        lYData.phone_code = data.phone?.take(8) //截取手机号前8位
        lYData.name_md5 = SHA256.encryptMD5(data.real_name ?: "")
        lYData.idno_md5 = SHA256.encryptMD5(data.id_card ?: "")
        lYData.working_city = data.city_name
        lYData.age = data.age ?: 0
        lYData.sex = data.sex

        /** 现有  公积金：0:无 1:6个月以下 2:6个月以上 */
        /**   龙焱 公积金：1：6个月以上、2：6个月以下、3：无、99：未知*/
        when (data.accumulation_fund) {
            0 -> {
                lYData.gjj = 3
            }

            1 -> {
                lYData.gjj = 2

            }

            2 -> {
                lYData.gjj = 1

            }
        }
        /** 社保：0:无 1:6个月以下 2:6个月以上 */
        /** 社保：1：6个月以上、2：6个月以下、3：无、99：未知 */
        when (data.social_security) {
            0 -> {
                lYData.shebao = 3
            }

            1 -> {
                lYData.shebao = 2

            }

            2 -> {
                lYData.shebao = 1

            }
        }
        /**  50000  100000 150000 200000*/
        /** 贷款额度：1：1-3万、2：3-5万、3：5-10万、4：10万以上 */
        when (data.loan_amount) {
            50000 -> {
                lYData.loan_amount = 1
            }

            100000 -> {
                lYData.loan_amount = 2
            }

            150000 -> {
                lYData.loan_amount = 3
            }

            200000 -> {
                lYData.loan_amount = 4
            }
        }
        /** 房产：0:无 1:有房不接受抵押 2:有房接受抵押 */
        /** 房：1：有、2：无、99：未知 */
        if (data.house_property == 0) {
            lYData.house = 2
        } else {
            lYData.house = 1
        }
        /** 现有 车产：0:无 1:有车不接受抵押 2:有车接受抵押 */
        /** 车：1：有、2：无 、99：未知  */
        /** 车辆状态：1：全款车、2：按揭车、99：未知
        车辆价值：1：5-10万、2：10-20万、3：20万以上、99：未知
         */

        if (data.car_property == 0) {
            lYData.car = 2
        } else {
            //有车状态下 默认全款车  价值为5-10万
            lYData.car = 1
            lYData.car_status = 1
            lYData.car_price = 1
        }
        /** 信用状况：1:当前无逾期 2:当前有逾期 */
        //逾期情况：1：有、2：无、99：未知
        if (data.credit == 1) {
            lYData.overdue = 2
        } else {
            lYData.overdue = 1
        }

        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //芝麻分情况：芝麻分：1：600以下、2：600-650、3：650-700、4：700以上、5：无、99：未知
        when (data.sesame_score) {
            0 -> {
                lYData.zhima = 5
            }

            1 -> {
                lYData.zhima = 4
            }

            2 -> {
                lYData.zhima = 3
            }

            3 -> {
                lYData.zhima = 2
            }

            4, 5 -> {
                lYData.zhima = 1
            }
        }

        /**职业：1：上班族、2：无固定职业、3：企业主、4：个体户、5：公务员/国企/事业单位、99：未知*/
        lYData.occupation = 1 //默认上班族

        /** 商业保险：0:无 1:6个月以下 2:6个月以上 */
        /** 保单：1：一年以上、2：一年以下、3：无、99：未知 */
        when (data.personal_insurance) {
            0 -> {
                lYData.insurance = 3
            }

            else -> {//默认一年以上表单
                lYData.insurance = 1

            }
        }

        lYData.ip = data.ip


        return lYData
    }


    // 转化  微银
    fun matchToWeiYinData(data: ApiOriginData): WeiYinSaveData {
        val wyData = WeiYinSaveData()

        wyData.phoneMd5 = data.phone_md5
        wyData.phone = data.phone
        wyData.ip = data.ip
        wyData.name = data.real_name
        wyData.city = data.city_name
        wyData.cityCode = data.city_code
        wyData.age = data.age
        wyData.gender = data.sex
        wyData.idCardSixPrefix = data.id_card?.take(6)
        val loanTimelist = arrayOf(2, 3, 4, 5)
        wyData.loanTime = loanTimelist.random()

        //职业 1(上班族) 2(自由职业)3(私营企业主)4(国企/事业单位/公务员)
        wyData.profession = 1


        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //芝麻分 1(600-650) 2(650-700) 3(700以上) 4(无) 5(600以下)
        when (data.sesame_score) {
            0 -> {
                wyData.zhima = 4
            }

            1 -> {
                wyData.zhima = 3
            }

            2 -> {
                wyData.zhima = 2
            }

            3 -> {
                wyData.zhima = 1
            }

            4, 5 -> {
                wyData.zhima = 5
            }
        }

        /** 公积金：0:无 1:6个月以下 2:6个月以上 */
        //公积金1(6个月以下) 2(6-12个月) 3(12个月以上) 4(无)
        if (data.accumulation_fund == 0) {
            wyData.providentFund = 4
        } else {
            wyData.providentFund = 3
        }
        // 社保1(6个月以下) 2(6-12个月) 3(12个月以上) 4(无)
        if (data.social_security == 0) {
            wyData.socialSecurity = 4
        } else {
            wyData.socialSecurity = 2
        }

        if (data.personal_insurance == 0) {
            wyData.commericalInsurance = 3
        } else {
            wyData.commericalInsurance = 1
        }

        if (data.house_property == 0) {
            wyData.house = 2
        } else {
            wyData.house = 1
        }
        /** 信用状况：1:当前无逾期 2:当前有逾期 */
        //逾期记录   1(信用良好)2(当前逾期中)
        wyData.overdue = data.credit

        //名下车产1(有车产) 2(无车产)
        if (data.car_property == 0) {
            wyData.vehicle = 2
        } else {
            wyData.vehicle = 1
        }

        /**  50000  100000 150000 200000*/
        // 贷款额度1(30000) 2(50000) 3(100000) 4(200000)
        when (data.loan_amount) {
            50000 -> {
                wyData.loanAmount = 1
            }

            100000 -> {
                wyData.loanAmount = 2
            }

            150000 -> {
                wyData.loanAmount = 3
            }

            200000 -> {
                wyData.loanAmount = 4
            }
        }
        return wyData


    }

    //转化  吉意花
    fun matchToJiYiHuaData(data: ApiOriginData, channel: Int): JiYiHuaSaveData {
        val huaData = JiYiHuaSaveData()
        if (channel == Constants.SHRIMP_JIYIHUA_MASK) {
            huaData.phoneMask = data.phone?.take(8)
        } else {
            huaData.phoneMd5 = data.phone_md5
        }
        huaData.userName = data.real_name
        /** 性别：1:男 2:女 */
        //性别：0-女 1-男
        if (data.sex == 1) {
            huaData.sex = data.sex
        } else {
            huaData.sex = 0
        }
        huaData.age = data.age ?: 0
        huaData.ip = data.ip

        huaData.loanMoney = data.loan_amount
        huaData.cityName = data.city_name
        huaData.cityCode = data.city_code?.toInt()

        //自定义企业主  0=无  1=企业主   /** 营业执照：0:无 1:有营业执照 */
        //  职业信息：1-上班族 2-(公务员/事业) 3-私营业主 4-个体户 5-其他  默认：1
        if (data.qiYeZhu == 1) {
            huaData.job = 3
        } else if (data.business == 1) {
            huaData.job = 4
        } else {
            huaData.job = 1
        }


        huaData.monthlyIncome = 2 //默认(4000-8000)
        huaData.busLicense = 1 //默认未办理
        //社保：1-无 2-(6个月以下) 3-(6个月以上)
        if (data.social_security == 0) {
            huaData.has = 1
        } else {
            huaData.has = 2
        }
        //公积金：1-无 2-(6个月以下) 3-(6个月以上)
        if (data.accumulation_fund == 0) {
            huaData.fund = 1
        } else {
            huaData.fund = 2
        }
        /** 房产信息：1-无 2-按揭房 3-全款房 */
        if (data.house_property == 0) {
            huaData.house = 1
        } else {
            huaData.house = 2
        }
        //车产信息：1-无 2-按揭车 3-全款车
        if (data.car_property == 0) {
            huaData.car = 1
        } else {
            huaData.car = 2
        }
        //保单：1-无 2-(6个月以下) 3-(6个月以上)
        if (data.personal_insurance == 0) {
            huaData.policy = 1
        } else {
            huaData.policy = 2
        }


        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //芝麻分：1-无 2-(600分以下) 3-(600~650分) 4-(650~700分) 5-(700分以上)
        when (data.sesame_score) {
            0 -> {
                huaData.zhiMa = 1
            }

            1 -> {
                huaData.zhiMa = 5
            }

            2 -> {
                huaData.zhiMa = 4
            }

            3 -> {
                huaData.zhiMa = 3
            }

            4, 5 -> {
                huaData.zhiMa = 2
            }
        }

        //学历：1-无学历 2-初中 3-中专 4-高中 5-大专 6-本科及以上 默认：1
        huaData.education = 1


        huaData.platform = "Android"

        return huaData
    }


    //转化  八戒
    fun matchToBaJieData(data: ApiOriginData): BaJieSaveData {
        val bajieData = BaJieSaveData()
        bajieData.phone = data.phone?.take(8)
        bajieData.channelSignature = Constants.BAJIE_CHANNEL
        /** 性别：1:男 2:女 */
        // 性别：0. 女  1. 男
        if (data.sex == 1) {
            bajieData.sex = 1
        } else {
            bajieData.sex = 0
        }
        bajieData.age = data.age
        bajieData.nameMd5 = SHA256.encryptMD5(data.real_name!!)
        bajieData.idCardMd5 = data.id_card_md5
        bajieData.city = data.cityNoShi

        /** 社保：0:无 1:6个月以下 2:6个月以上 */
        //本地社保：1. 无社保  2. 缴纳未满6个月  3. 缴纳6个月以上
        if (data.social_security == 0) {
            bajieData.socialSecurity = 1
        } else {
            bajieData.socialSecurity = 3
        }
        /** 公积金：0:无 1:6个月以下 2:6个月以上 */
        //本地公积金：1. 无公积金  2. 缴纳未满6个月  3. 缴纳6个月以上
        if (data.accumulation_fund == 0) {
            bajieData.accumulationFund = 1
        } else {
            bajieData.accumulationFund = 3
        }
        /** 车产：0:无 1:有车不接受抵押 2:有车接受抵押 */
        //名下车产：1. 无车产  2. 有车产
        if (data.car_property == 0) {
            bajieData.carProduction = 1
        } else {
            bajieData.carProduction = 2
        }
        /** 房产：0:无 1:有房不接受抵押 2:有房接受抵押 */
        //名下房产：1. 无房产  2. 有房产按揭  3. 全款房
        if (data.house_property == 0) {
            bajieData.estate = 1
        } else {
            bajieData.estate = 2
        }
        /** 商业保险：0:无 1:6个月以下 2:6个月以上 */
        // 个人保险：1. 无保单  2. 缴纳不足一年  3. 缴纳1年以上  4. 缴纳2年以上
        if (data.personal_insurance == 0) {
            bajieData.unitSocialSecurity = 1
        } else {
            bajieData.unitSocialSecurity = 3
        }
        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //芝麻分，例如：650、700
        when (data.sesame_score) {
            0 -> {
                bajieData.sesame = 0
            }

            1 -> {
                bajieData.sesame = 700
            }

            2 -> {
                bajieData.sesame = 650
            }

            3 -> {
                bajieData.sesame = 600
            }

            4 -> {
                bajieData.sesame = 550
            }

            5 -> {
                bajieData.sesame = 500
            }
        }

        //自定义企业主  0=无  1=企业主   /** 营业执照：0:无 1:有营业执照 */
        // 职业身份：1. 上班族  2. 公务员或事业单位  3. 私营业主（有营业执照）  4. 个体户（无营业执照）  5. 其他职业
        if (data.qiYeZhu == 1) {
            bajieData.professionalIdentity = 3
        } else if (data.business == 1) {
            bajieData.professionalIdentity = 4
        } else {
            bajieData.professionalIdentity = 1
        }


        /** 信用状况：1:当前无逾期 2:当前有逾期 */
        //是否有信用卡(1 无 2 有)
        if (data.credit == 1) {
            bajieData.customerCreditCard = 2
        } else {
            bajieData.customerCreditCard = 1
        }
        // 学历：1. 初中及以下  2. 高中  3. 中专  4. 大专  5. 本科  6. 研究生及以上
        val list1 = arrayOf(1, 2, 3, 4, 5, 6)
        bajieData.highestEducation = list1.random()

        //月收入
        val list2 = arrayOf("5000", "8000", "10000")
        bajieData.monthlyIncome = list2.random()

        // 工资发放形式：1. 银行卡  2. 现金  3. 自存
        val list3 = arrayOf("1", "2", "3")
        bajieData.customerFormOfPayroll = list3.random()

        // 当前单位工龄：1. 0~6个月  2. 6~12个月  3. 12个月以上
        val list4 = arrayOf("1", "2", "3")
        bajieData.lengthOfService = list4.random()

        // 贷款用途：1. 个人日常消费  2. 装修  3. 旅游  4. 教育  5. 医疗  6. 婚庆开销  7. 购置车  8. 购置家具家电  9. 购置货物生产设备  10. 创业经营
        val list5 = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        bajieData.loanPurpose = list5.random()

        bajieData.ip = data.ip
        bajieData.deviceType = 0
        return bajieData

    }

    //转化  闪贷喵
    fun matchToShanDaiMiaoData(data: ApiOriginData): ShanDaiMiaoSaveData {
        val sdmData = ShanDaiMiaoSaveData()
        sdmData.name = data.real_name
        sdmData.idCard = data.id_card
        sdmData.phone = data.phone
        sdmData.gender = data.sex
        sdmData.age = data.age
        sdmData.city = data.city_name

        //“1-3个月，2-6个月，3-12个月，4-24个月，5-36个月”
        val list1 = arrayOf(1, 2, 3, 4, 5)
        sdmData.loanTerm = list1.random()

        //自定义企业主  0=无  1=企业主   /** 营业执照：0:无 1:有营业执照 */
        // 用户职业：1-上班族，2-自由职业者，3-私营企业主，4-国企/事业单位/公务员
        if (data.qiYeZhu == 1) {
            sdmData.occupation = 3
        } else if (data.business == 1) {
            sdmData.occupation = 2
        } else {
            sdmData.occupation = 1
        }


        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //芝麻分，1-600-650，2-650-700，3-700以上，4-无，5-600以下
        when (data.sesame_score) {
            0 -> {
                sdmData.sesameScore = 4
            }

            1 -> {
                sdmData.sesameScore = 3
            }

            2 -> {
                sdmData.sesameScore = 2
            }

            3 -> {
                sdmData.sesameScore = 1
            }

            4 -> {
                sdmData.sesameScore = 5
            }

            5 -> {
                sdmData.sesameScore = 5
            }
        }
        /** 公积金：0:无 1:6个月以下 2:6个月以上 */
        //1-6个月以下，2 6-12个月，3-12个月以上，4-无公积金
        if (data.accumulation_fund == 0) {
            sdmData.housingFund = 4
        } else {
            sdmData.housingFund = 2
        }
        //1-6个月以下，2-6-12个月，3-12个月以上，4-无社保”
        if (data.social_security == 0) {
            sdmData.socialInsurance = 4
        } else {
            sdmData.socialInsurance = 2
        }

        if (data.personal_insurance == 0) {
            sdmData.commercialInsurance = 4
        } else {
            sdmData.commercialInsurance = 2
        }
        //“1-有房产（可抵押），2-有房产（不可抵押），3-无房产”
        if (data.house_property == 0) {
            sdmData.realEstate = 3
        } else {
            sdmData.realEstate = 2
        }
        /** 信用状况：1:当前无逾期 2:当前有逾期 */
        //“1-有，2-无”
        sdmData.creditCard = data.credit
        //1-信用良好，2-有逾期记录
        sdmData.creditRecord = data.credit

        //“1-有车可抵押，2-有车不抵押，3-无车
        if (data.car_property == 0) {
            sdmData.carProperty = 3
        } else {
            sdmData.carProperty = 2
        }

        /**  50000  100000 150000 200000*/
        // 需传具体数字，最少5位数）如10000/2000000”
        sdmData.loanAmount = data.loan_amount

        /** 花呗额度：0:无额度 1:5000以上 2:5000以内 */
        //“1-20000以上，2-5000-20000，3-0-5000”
        if (data.huabei == 0) {
            sdmData.huabeiLimit = 3
        } else {
            sdmData.huabeiLimit = 1
        }

        //1-无执照，2-注册1年以下，3-注册1年以上”
        if (data.business == 0) {
            sdmData.companySituation = 1
        } else {
            sdmData.companySituation = 2
        }

        //当职业为公务员、事业单位员工、私营企业员工时必传：1-3000及以下，2-3000-5000，3-5000-10000，4-10000-20000，5-20000-50000，6-50000及以上
        sdmData.monthIncome = 3
        sdmData.education = 2
        sdmData.client_ip = data.ip
        sdmData.deviceType = 2

        return sdmData
    }

    //转化 下虾撞库
    fun matchToFishMatchData(data: ApiOriginData): FishMatchSaveData {
        val fishData = FishMatchSaveData()
        fishData.realname = data.real_name
        fishData.mobile = data.phone
        fishData.id_card_no = data.id_card
        // 性别: 1【女】；2【男】
        if (data.sex == 1) {
            fishData.sex = 2
        } else {
            fishData.sex = 1
        }

        fishData.age = data.age
        fishData.city_name = data.city_name
        fishData.city_code = data.city_code

        //自定义企业主  0=无  1=企业主   /** 营业执照：0:无 1:有营业执照 */
//        职业：0【上班族】；1【企业主】；2【个体户】；3【自由职业】; 4【公务员/事业】
        if (data.qiYeZhu == 1) {
            fishData.career = 1
        } else if (data.business == 1) {
            fishData.career = 2
        } else {
            fishData.career = 0
        }

        // 工资/收入发放方式: 0【无】；1【现金发放】；2【转账工资】；3【银行代发】
        val list1 = arrayOf(0, 2, 3)
        fishData.monthly_income_type = list1.random()

//        月收入 单位：元
        val list2 = arrayOf(5000, 6000, 10000)
        fishData.monthly_income = list2.random()


        /** 社保：0:无 1:6个月以下 2:6个月以上 */
        // 社保：0【无】；1【3个月以下】；2【3-6个月】；3【6个月以上】
        if (data.social_security == 0) {
            fishData.social = 0
        } else {
            fishData.social = 3
        }

        if (data.accumulation_fund == 0) {
            fishData.fund = 0
        } else {
            fishData.fund = 3
        }

        /** 房产：0:无 1:有房不接受抵押 2:有房接受抵押 */
        // 房产情况: 0【无】；1【有按揭商品房】；2【有全款商品房】；3【有自建房】；4【仅外地有房】
        if (data.house_property == 0) {
            fishData.fund = 0
        } else {
            fishData.fund = 1
        }

        /** 车产：0:无 1:有车不接受抵押 2:有车接受抵押 */
        // 车产情况: 0【无车】；1【有按揭汽车】；2【有全款汽车】
        if (data.car_property == 0) {
            fishData.fund = 0
        } else {
            fishData.fund = 1
        }
        /** 商业保险：0:无 1:6个月以下 2:6个月以上 */
        //  保险情况：0【无】；1【投保险人寿险且保两年以下】；2【投保险人寿险且保两年以上】
        if (data.personal_insurance == 0) {
            fishData.fund = 0
        } else {
            fishData.fund = 2
        }

        /** 芝麻分：0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        // 芝麻分：0【无】；1【580分以下】；2【580-599分】；3【600-619分】；4【620-649分】；5【650-699分】；6【700分以上】
        when (data.sesame_score) {
            0 -> {
                fishData.zhima_score = 0
            }

            1 -> {
                fishData.zhima_score = 6
            }

            2 -> {
                fishData.zhima_score = 5
            }

            3 -> {
                fishData.zhima_score = 4
            }

            4 -> {
                fishData.zhima_score = 3
            }

            5 -> {
                fishData.zhima_score = 1
            }
        }

        /** 营业执照：0:无 1:有营业执照 */
        // 营业执照：0【无】；1【有】
        if (data.business == 0) {
            fishData.business_license = 0
        } else {
            fishData.business_license = 1
        }

        /** 信用状况：1:当前无逾期 2:当前有逾期 */
        // 信用逾期情况: 0【无信用卡或贷款】；1【1年内逾期超过3次】；2【1年内逾期少于3次】；3【无逾期】；4【近一年无逾期】
        if (data.credit == 1) {
            fishData.credit_card_overdue = 3
        } else {
            fishData.credit_card_overdue = 2
        }
        fishData.ip = data.ip

        fishData.demand_amount = data.loan_amount.toString()

        return fishData

    }

    //吉用钱包全流程
    fun matchToJYQBQlcData(data: ApiOriginData): JYQBqlcUserData {
        val jyqbData = JYQBqlcUserData()

        jyqbData.md5Mobile = data.phone_md5
        jyqbData.realName = data.real_name
        jyqbData.idCard = data.id_card_md5
        jyqbData.sex = data.sex
        jyqbData.age=data.age
        jyqbData.cityName = data.city_name
        jyqbData.cityCode = data.city_code
        jyqbData.loanAmount =data.loan_amount

        //贷款期限
        val list = arrayOf(0,1, 2, 3, 4, 5)
        jyqbData.loanPeriod = list.random()

        //借款用途
        jyqbData.loanPurpose = 1 //日常消费

        jyqbData.applyIp = data.ip


        //吉用钱包选项  1=有 0=无
        //智享贷选项 /** 房产：0:无 1:有房不接受抵押 2:有房接受抵押 */
        if (data.car_property == 0) {
            jyqbData.car = 0
        } else {
            jyqbData.car = 1
        }
        if (data.house_property == 0) {
            jyqbData.house = 0
        } else {
            jyqbData.house = 1
        }
        if (data.personal_insurance == 0) {
            jyqbData.insurance = 0
        } else {
            jyqbData.insurance = 1
        }
        if (data.social_security == 0) {
            jyqbData.social = 0
        } else {
            jyqbData.social = 1
        }
        if (data.accumulation_fund == 0) {
            jyqbData.gjj = 0
        } else {
            jyqbData.gjj = 1
        }

        /** 智相待芝麻分 0:无 1:700以上 2:650-699 3:600-649 4:550-599 5:550以下 */
        //吉用钱包芝麻分 1:0-600、2:600-650、3:650-700、4:700以上
        when (data.sesame_score) {
            0, 4, 5 -> jyqbData.zmf = 1
            1 -> jyqbData.zmf = 4
            2 -> jyqbData.zmf = 3
            3 -> jyqbData.zmf = 2
        }
        //企业主
        if (data.qiYeZhu == 1) {
            jyqbData.qyz = 1
        } else{
            jyqbData.qyz = 0
        }

        return jyqbData
    }


}