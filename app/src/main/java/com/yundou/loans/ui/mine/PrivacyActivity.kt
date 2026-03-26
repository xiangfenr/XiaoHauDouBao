package com.yundou.loans.ui.mine

import android.annotation.SuppressLint
import android.text.TextUtils
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.PrvavcyLayoutBinding
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.LogUtils

class PrivacyActivity : CommonActivity<UserViewModel, PrvavcyLayoutBinding>() {

    override fun setTitle(): CharSequence {
        return "隐私政策"
    }

    override fun isShowActionBar(): Boolean {
        return true
    }


    override fun getLayoutId(): Int {
        return R.layout.prvavcy_layout
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val webUrl = intent.getStringExtra("webUrl")
        LogUtils.d("webUrl", webUrl.toString())
        if (!TextUtils.isEmpty(webUrl)) {
            mBinding.webview.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
            // 获取WebView的WebSettings对象
            val webSettings = mBinding.webview.getSettings()
            // 启用缓存
            webSettings.cacheMode = WebSettings.LOAD_DEFAULT
            // 启用JavaScript支持
            webSettings.javaScriptEnabled = true
            // 启用DOM存储API支持
            webSettings.domStorageEnabled = true
            // 启用数据库存储API支持
            webSettings.databaseEnabled = true
            // 设置Web视口的宽度适应屏幕
            webSettings.useWideViewPort = true
            webSettings.loadWithOverviewMode = true
            val wSet: WebSettings = mBinding.webview.getSettings()
            //从ListView中获得URL
            wSet.javaScriptEnabled = true
            //开启缓存，无网络时加载本地内容
            wSet.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            //关闭打开第三方浏览器
            //关闭打开第三方浏览器
            mBinding.webview.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest,
                ): Boolean {
                    view.loadUrl(request.url.toString())
                    return true
                }
            })
            // 加载网页
            mBinding.webview.loadUrl(webUrl.toString())
        }

    }
}