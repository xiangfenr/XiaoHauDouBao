package com.yundou.loans.ui.mine

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.alibaba.fastjson.JSON
import com.umeng.commonsdk.UMConfigure
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.LoginActivityLoginBinding
import com.yundou.loans.model.LoginViewModel
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.utils.AccountType
import com.yundou.loans.utils.Constants
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.utils.Utils
import com.yundou.loans.widget.clickNoRepeat
import com.yundou.loans.widget.gone
import com.yundou.loans.widget.visible


class LoginActivity : CommonActivity<LoginViewModel, LoginActivityLoginBinding>() {

    private var timer: CountDownTimer? = null
    var partner_id: Int = -1
    var xieyiContent: String = ""
    private var inputAccountType = -1  //输入账号的类型

    override fun getLayoutId(): Int {
        return R.layout.login_activity_login
    }

    override fun init() {
        initBaseApp()
        initClicker()
    }

    private fun initBaseApp() {
        val channel = Utils.getChannel(BaseApp.context)
        val androidId = Utils.getAndroidId(BaseApp.context)
        MmkvUtil.getInstance().encode("androidId", androidId)
        BaseApp.context.imsi = androidId
        BaseApp.context.imei = androidId
        Log.e("CHANNEL_VALUE", channel.toString() + "imsi---" + Utils.getAndroidId(BaseApp.context))

        //初始化oaid
        //MdidSdkHelper.InitSdk(applicationContext, true, this);

        initChanne()

        UMConfigure.preInit(this, "674eb7167e5e6a4eebab747c", BaseApp.context.storeid)

        UMConfigure.init(
            this,
            "674eb7167e5e6a4eebab747c",
            BaseApp.context.storeid,
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )

        UMConfigure.getOaid(this) { oaid ->
            Log.i("mobmobmobmob", "oaidoaidoaid$oaid")
            MmkvUtil.getInstance().encode("oaid", oaid)
        }
    }

    //初始化渠道
    private fun initChanne() {
        viewModel.getServer {
            partner_id = it?.partner_id ?: 0
            MmkvUtil.getInstance().encode("partner_id", partner_id)
            Log.i("xiang", "登录页面: partner_id: " + partner_id)
            if (partner_id == 5) { //快易贷要求进登录页面就调用一下
                viewModel.iconV2Get()
            }
            if (partner_id == 6) {
                viewModel.protocolRegisterGet() {
                    xieyiContent = it.content
                }
            }
        }
//        viewModel.getIpLocation { }

    }


    private fun initClicker() {
//监听手机号
        mBinding.etLoginPhione.addTextChangedListener(object : TextWatcher {
            private val maxLength = 10 // 设置最大字符数为10
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // 文本改变之前的回调，这里不需要处理
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 文本改变时的回调
                if (s.length == 11) {
                    mBinding.etLoginPhione.text.let { phone ->

                        viewModel.getMobileInfo(phone.toString()) {
                            inputAccountType=it.type
                            MmkvUtil.getInstance().encode(Constants.PHONE_NUMBER_STATUS,it.type)
                            //2=审核人员账号 4=提审核的账号
                            if (it.type == AccountType.APP_STORE_REVIEWER.code || it.type == AccountType.SUBMITTED_TEST_ACCOUNT.code) {
                                //切本部, partner_id设置1
                                partner_id = 1
                                MmkvUtil.getInstance().encode("partner_id", 1)
                            } else if ( it.type == AccountType.SELF_TEST_PHONE.code || it.type== AccountType.NORMAL_NEW_USER.code
                                || it.type== AccountType.HISTORICAL_ACCOUNT.code) { //3=自测账号 0=新号 5=历史账号
                                initChanne()
                            }
                            Log.d("STATE", "onTextChanged: it -- > " + JSON.toJSONString(it))
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                // 文本改变之后的回调，这里不需要处理
            }
        })

        mBinding.tvLoginYszc.clickNoRepeat {
            val intent = Intent(this, PrivacyActivity::class.java)
            intent.putExtra("webUrl", BaseApp.context.yinsi)
            startActivity(intent)
        }
        mBinding.tvLoginZcxy.clickNoRepeat {
            if (!TextUtils.isEmpty(xieyiContent)) {
                val intent = Intent(this, CommonWebViewActivity::class.java)
                intent.putExtra("webUrl", xieyiContent)
                startActivity(intent)
            } else {
                val intent = Intent(this, CommonWebViewActivity::class.java)
                intent.putExtra("webUrl", BaseApp.context.zhuhceXieyi)
                startActivity(intent)
            }
        }

        //验证码登录
//        mBinding.tvYzmlogin.clickNoRepeat {
//            mBinding.tvLoginType.text = "验证码登录"
//            mBinding.codeloginImg.visible()
//            mBinding.pwdLoginImg.gone()
//            mBinding.rlLoginMima.gone()
//            // mBinding.tvLoginTishi.gone()
//            // mBinding.tvMima.visible()
//            mBinding.rlLoginYanzhengma.visible()
//            mBinding.etLoginCode.visible()
//        }

        //密码登录
//        mBinding.tvPwdLogin.clickNoRepeat {
//            mBinding.tvLoginType.text = "密码登录"
//            mBinding.codeloginImg.gone()
//            mBinding.pwdLoginImg.visible()
//            //   mBinding.tvMima.gone()
//            mBinding.rlLoginYanzhengma.gone()
//            mBinding.rlLoginMima.visible()
//            mBinding.etLoginCode.gone()
//            // mBinding.tvLoginTishi.visible()
//        }

        //用户注册
        mBinding.tvZhuce.clickNoRepeat {
            mBinding.tvLoginType.text = "用户注册"
            mBinding.rlLoginMima.visible()
            mBinding.rlLoginYanzhengma.visible()
        }


        //获取验证码
        mBinding.tvLoginGetcode.clickNoRepeat {
            if (mBinding.tvLoginGetcode.text == "获取验证码") {

                if (partner_id == 0 || partner_id == -1) {
                    viewModel.defUI.toastEvent.postValue("服务配置获取失败,请退出重试")
                    initChanne()
                    return@clickNoRepeat
                }

//                if (viewModel.ipLocationLiveData.value?.isNotEmpty() == true) {
//                    viewModel.defUI.toastEvent.postValue(viewModel.ipLocationLiveData.value)
//                    viewModel.getIpLocation {}
//                    return@clickNoRepeat
//                }

                if (TextUtils.isEmpty(mBinding.etLoginPhione.text.toString().trim())) {
                    viewModel.defUI.toastEvent.postValue("请输入手机号码")
                    return@clickNoRepeat
                }

                if (!isValidChineseMobileNumber(mBinding.etLoginPhione.text.toString().trim())) {
                    viewModel.defUI.toastEvent.postValue("请检查您的手机号")
                    return@clickNoRepeat
                }
                if (inputAccountType==AccountType.CANCELLED_ACCOUNT.code ) {
                    viewModel.defUI.toastEvent.postValue("您的手机号已注销")
                    return@clickNoRepeat
                }

                //关闭输入框
                val view = this.currentFocus
                // 获取输入法管理器
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                // 隐藏输入法
                imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                if (!mBinding.cbLogin.isChecked) {
                    viewModel.defUI.toastEvent.postValue("请阅读并同意《服务协议》和《隐私政策》")
                    return@clickNoRepeat
                }
                when (partner_id) {
                    Constants.PARTNER_BENBU -> {  //本部发送短信
                        viewModel.getSmsCode(
                            mBinding.etLoginPhione.text.toString().trim()
                        ) {
                            countDownTimer()
                        }

                    }

                    Constants.PARTNER_SHENGR -> {   //笙融-短信
                        //微妙用---
                        viewModel.getWmCode(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_ZXD -> {  //智享贷-短信
                        viewModel.zxdGetCode(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_MOLI -> {  //魔力28
                        viewModel.moliGetCode(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_TWOP -> { //二项目
                        viewModel.twopSendCode(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_JIYONGQB -> { //吉用钱包
                        viewModel.jiYongSendCode(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_JIYONGBANG -> {  //吉用帮
                        viewModel.jiYongBangSendCode(
                            mBinding.etLoginPhione.text.toString().trim()
                        ) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_YOUQIANQB -> {//有钱钱包
                        Log.d(
                            "Login",
                            "initClicker: 手机号码 -- > " + mBinding.etLoginPhione.text.toString()
                                .trim()
                        )
                        viewModel.yqqbSendCode(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_TXFQ -> {
                        viewModel.txfqSendCode(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_YUANXIAOHUA -> {
                        viewModel.yxhSendCode(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_QIDAI -> {
                        viewModel.qiDaiSendCode(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
//                            mBinding.etLoginCode.setText(it)
                        }
                    }

                    Constants.PARTNER_JIDAI, Constants.PARTNER_YUEXIANG -> {
                        viewModel.jiDaiCodeGet(mBinding.etLoginPhione.text.toString().trim()) {
                            countDownTimer()
                        }
                    }

                    Constants.PARTNER_SHANDAIMIAO -> {
                        viewModel.shanDaiMiaoSendCode(
                            mBinding.etLoginPhione.text.toString().trim()
                        ) {
                            countDownTimer()
                        }
                    }
                }

            }
        }

        //登录
        mBinding.tvLoginLogin.clickNoRepeat {

            if (partner_id == 0 || partner_id == -1) {
                viewModel.defUI.toastEvent.postValue("服务配置获取失败,请退出重试")
                initChanne()
                return@clickNoRepeat
            }

//            if (viewModel.ipLocationLiveData.value?.isNotEmpty() == true) {
//                viewModel.defUI.toastEvent.postValue(viewModel.ipLocationLiveData.value)
//                viewModel.getIpLocation {}
//                return@clickNoRepeat
//            }

            if (TextUtils.isEmpty(mBinding.etLoginPhione.text.toString().trim())) {
                viewModel.defUI.toastEvent.postValue("请输入手机号码")
                return@clickNoRepeat
            }

            if (mBinding.tvLoginType.text.equals("验证码登录")) {
                if (TextUtils.isEmpty(mBinding.etLoginCode.text.toString().trim())) {
                    viewModel.defUI.toastEvent.postValue("请输入验证码")
                    return@clickNoRepeat
                }
            } else {
                if (TextUtils.isEmpty(mBinding.etLoginPassword.text.toString().trim())) {
                    viewModel.defUI.toastEvent.postValue("请输入密码")
                    return@clickNoRepeat
                }
            }
            if (inputAccountType==AccountType.CANCELLED_ACCOUNT.code ) {
                viewModel.defUI.toastEvent.postValue("您的手机号已注销")
                return@clickNoRepeat
            }

            if (!mBinding.cbLogin.isChecked) {
                viewModel.defUI.toastEvent.postValue("请阅读并同意《服务协议》和《隐私政策》")
                return@clickNoRepeat
            }

            if (inputAccountType == AccountType.APP_STORE_REVIEWER.code || inputAccountType == AccountType.SUBMITTED_TEST_ACCOUNT.code) {
                //账号是  2、应用商店审核 和 4、提交测试账号 走密码登录
                viewModel.getLogin(
                    2, mBinding.etLoginPhione.text.trim().toString(),
                    mBinding.etLoginCode.text.trim().toString(),
                    mBinding.etLoginCode.text.trim().toString()
                ) {
                    loginSucces(it)
                    MmkvUtil.getInstance().encode("partner_id", 1)
                    viewModel.reportPointRequest(1)
                }
            } else {  //其余情况--验证码登录
                when (partner_id) {
                    Constants.PARTNER_BENBU -> {  //本部登录
                        viewModel.getLogin(
                            1, mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString(),
                            mBinding.etLoginPassword.text.trim().toString()
                        ) {
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_SHENGR -> {  //笙融
                        viewModel.getWmLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString()
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_ZXD -> { //智享贷
                        viewModel.zxdcodeLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString(),
                            "weimiaoyong"
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_MOLI -> { //魔力登录
                        viewModel.moliCodeLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString(),
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_TWOP -> {  //二项目登录
                        viewModel.twopLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString(),
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_JIYONGQB -> {//吉用钱包登录
                        viewModel.jiYongLoginPost(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString(),
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_JIYONGBANG -> { //吉用帮登录
                        viewModel.jiYongBangLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString()
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_YOUQIANQB -> {//有钱钱包
                        Log.d(
                            "Login",
                            "initClicker: 手机号码 -- > " + mBinding.etLoginPhione.text.toString()
                                .trim()
                        )
                        viewModel.yqqbLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString()
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_TXFQ -> {
                        viewModel.txfqLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString()
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_YUANXIAOHUA -> {
                        viewModel.yxhCodeLogin(
                            mBinding.etLoginPhione.text?.trim().toString(),
                            mBinding.etLoginCode.text?.trim().toString()
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_QIDAI -> {
                        viewModel.qiDaiLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString()
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_JIDAI, Constants.PARTNER_YUEXIANG -> {
                        viewModel.jiDaiLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString()
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }

                    Constants.PARTNER_SHANDAIMIAO -> {
                        viewModel.shanDaiMiaoLogin(
                            mBinding.etLoginPhione.text.trim().toString(),
                            mBinding.etLoginCode.text.trim().toString()
                        ) {
                            //数据埋点
                            loginSucces(it)
                            viewModel.reportPointRequest(1)
                        }
                    }
                }
            }
        }
    }

    fun isValidChineseMobileNumber(number: String): Boolean {
        // 定义正则表达式：以1开头，第二位是3-9，后面跟着9位数字
        val phonePattern = "^1[3-9]\\d{9}$".toRegex()

        return number.matches(phonePattern)
    }

    //登录成功 存贮token
    fun loginSucces(token: String) {
        MmkvUtil.getInstance().encode("token", token)
        MmkvUtil.getInstance().encode("loginphone", mBinding.etLoginPhione.text.trim().toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

    /*
     *倒计时开始¬
     */
    private fun countDownTimer() {
        var num = 60
        timer = object : CountDownTimer((num + 1) * 1000L, 1000L) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if (num == 0) {
                    num = 0
                } else {
                    num--
                }
                mBinding.tvLoginGetcode.text = "$num s"
            }

            override fun onFinish() {
                mBinding.tvLoginGetcode.text = "获取验证码"
            }
        }
        timer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    /*override fun OnSupport(p0: Boolean, idSupplier: IdSupplier?) {
        onIdsAvalid(idSupplier?.oaid);

    }

    private fun onIdsAvalid(oaid: String?) {
        runOnUiThread {
            MmkvUtil.getInstance().encode("oaid", oaid)
        };
    }*/
}