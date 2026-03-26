package com.yundou.loans.ui.mine

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Process
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.yundou.loans.R
import com.yundou.loans.base.BaseApp
import com.yundou.loans.databinding.FlashLayoutBinding
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.widget.clickNoRepeat
import android.os.Handler
import android.os.Looper
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.widget.CommonDialog

class FlashActivitty : CommonActivity<UserViewModel, FlashLayoutBinding>() {
//    var webview: WebView? = null

    override fun getLayoutId(): Int {
        return R.layout.flash_layout
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun init() {

        val isFirst = MmkvUtil.getInstance().decodeBoolean("isFirst")
        if (!isFirst) {
            firstDialog()
        } else {
            //延迟一秒进入（避免闪屏过快）
            Handler(Looper.getMainLooper()).postDelayed({
                start()
            }, 1000)
        }
    }
    //首次进入弹出弹窗
    @RequiresApi(Build.VERSION_CODES.N)
    private fun firstDialog() {
        val isFirst = MmkvUtil.getInstance().decodeBoolean("isFirst")
        val submitResultView =
            LayoutInflater.from(this@FlashActivitty)
                .inflate(R.layout.dialog_first_layout, null)

        val privacyTv = submitResultView.findViewById<TextView>(R.id.privacyTv)
        val tv1 = submitResultView.findViewById<TextView>(R.id.tv1)
        tv1.text = "感谢您对 "+resources.getString(R.string.app_name)+" 一直以来的信任."
        val text =
            "我们将通过《用户协议》和《隐私政策》帮助您了解我们为您提供的服务以及我们收集、处理个人信息的方式。"
        val spannableString = SpannableString(text)

        // 定义要变色并可点击的文本范围
        val clickableTextStart1 = text.indexOf("《隐私政策》")
        val clickableTextEnd1 = clickableTextStart1 + "《隐私政策》".length

        // 定义要变色并可点击的文本范围
        val clickableTextStart2 = text.indexOf("《用户协议》")
        val clickableTextEnd2 = clickableTextStart2 + "《用户协议》".length

        // 设置颜色
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE), // 颜色
            clickableTextStart1,
            clickableTextEnd1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 设置颜色
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE), // 颜色
            clickableTextStart2,
            clickableTextEnd2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 设置点击事件
        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // 打开网址或执行其他操作
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BaseApp.context.yinsi))
                widget.context.startActivity(intent)
            }

            // 可选：覆盖 updateDrawState 方法以移除下划线
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // 移除默认的下划线
            }
        }
        // 设置点击事件
        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // 打开网址或执行其他操作
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BaseApp.context.zhuhceXieyi))
                widget.context.startActivity(intent)
            }

            // 可选：覆盖 updateDrawState 方法以移除下划线
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // 移除默认的下划线
            }
        }
        spannableString.setSpan(
            clickableSpan1,
            clickableTextStart1,
            clickableTextEnd1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            clickableSpan2,
            clickableTextStart2,
            clickableTextEnd2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // 应用到 TextView
        privacyTv?.text = spannableString
        privacyTv?.movementMethod = LinkMovementMethod.getInstance() // 必须设置，否则点击无效

        if (!isFirst) {
            val codeDialog = CommonDialog.Builder(this)
                .setContentView(submitResultView)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setCanceledOnTouchOutside(false)
                .setPercentWidth(0.8f)
                .create()
            codeDialog?.findViewById<TextView>(R.id.tv_agree)?.clickNoRepeat {
                start()
                MmkvUtil.getInstance().encode("isFirst", true)
                codeDialog.dismiss()
            }

//            webview = codeDialog.findViewById(R.id.webview)
//            initWebView()

            codeDialog.setCanceledOnTouchOutside(false)
            // 为对话框设置一个 OnKeyListener 监听器以拦截返回键事件
            codeDialog!!.setOnKeyListener { dialog, keyCode, event ->
                // 检测是否是返回键并且是按下（非释放）事件
                keyCode == KeyEvent.KEYCODE_BACK && event.action === KeyEvent.ACTION_DOWN
            }

            codeDialog?.findViewById<TextView>(R.id.tv_cancel)?.clickNoRepeat {
                try {
                    Process.killProcess(Process.myPid())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            codeDialog?.show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
//    private fun initWebView() {
//        webview?.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
//        // 获取WebView的WebSettings对象
//        val webSettings = webview?.getSettings()
//        // 启用缓存
//        webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
//        // 启用JavaScript支持
//        webSettings?.javaScriptEnabled = true
//        // 启用DOM存储API支持
//        webSettings?.domStorageEnabled = true
//        // 启用数据库存储API支持
//        webSettings?.databaseEnabled = true
//        // 设置Web视口的宽度适应屏幕
//        webSettings?.useWideViewPort = true
//        webSettings?.loadWithOverviewMode = true
//        val wSet: WebSettings = webview?.getSettings()!!
//        //从ListView中获得URL
//        wSet.javaScriptEnabled = true
//        //开启缓存，无网络时加载本地内容
//        wSet.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//        //关闭打开第三方浏览器
//        webview?.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(
//                view: WebView,
//                request: WebResourceRequest,
//            ): Boolean {
//                view.loadUrl(request.url.toString())
//                return true
//            }
//        }
//        // 加载网页
//        webview?.loadUrl(BaseApp.context.yinsi)
//    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun start() {
        //是否登录 没有跳转登录页面
        val token = MmkvUtil.getInstance().decodeString("token")

        if (TextUtils.isEmpty(token))
            startActivity(Intent(this, LoginActivity::class.java))
        else {
            val partner_id = MmkvUtil.getInstance().decodeInt("partner_id")
            if (partner_id == 5) {
                viewModel.silenceLogin() {
                    MmkvUtil.getInstance().encode("token", token)
                    startActivity(Intent(this, MainActivity::class.java))
                }
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        finish()
    }
}

