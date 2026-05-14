package com.yundou.loans.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.coreui.R
import com.yundou.loans.coreui.databinding.CommonWebLayoutBinding
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.utils.LogUtils
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.widget.clickNoRepeat

class CommonWebViewActivity : CommonActivity<UserViewModel, CommonWebLayoutBinding>() {

    override fun isShowActionBar(): Boolean {
        return false
    }

    override fun setTitle(): CharSequence {
        return ""
    }

    override fun getLayoutId(): Int {
        return R.layout.common_web_layout
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {

        val webUrl = intent.getStringExtra("webUrl") ?: ""

        LogUtils.e("webview--url --: $webUrl")

        // 逾期处理的上报
        if (webUrl.contains("zhiyunjishu")) {
            val phone = MmkvUtil.Companion.getInstance().decodeString("loginphone") ?: ""
            val partnerId = MmkvUtil.Companion.getInstance().decodeInt("partner_id")

            viewModel.daikuanYuqiPush(
                "40",
                partnerId.toString(),
                phone
            ) {

            }
        }

        LogUtils.d("webUrl", webUrl)

        if (webUrl.isNotEmpty()) {

            mBinding.webview.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

            val webSettings = mBinding.webview.settings

            // 开启 JS
            webSettings.javaScriptEnabled = true

            // DOM 存储
            webSettings.domStorageEnabled = true

            // 数据库
            webSettings.databaseEnabled = true

            // 自适应屏幕
            webSettings.useWideViewPort = true
            webSettings.loadWithOverviewMode = true

            // 缓存
            webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

            // 不打开第三方浏览器
            mBinding.webview.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest,
                ): Boolean {

                    val url = request.url.toString()

                    LogUtils.d("WebViewUrl", url)

                    // 微信跳转
                    if (url.startsWith("weixin")) {

                        try {

                            val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }

                            weiXinIntentResultLauncher.launch(intent)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@CommonWebViewActivity,
                                "无法打开微信",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        return true
                    }

                    // APK / ZIP / PDF 等文件，统一交给浏览器
                    if (isFileUrl(url)) {

                        openByBrowser(url)

                        return true
                    }

                    // 正常网页
                    view.loadUrl(url)

                    return true
                }
            }

            mBinding.webview.webChromeClient = object : WebChromeClient() {

                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)

                    if (title.startsWith("https://")) {
                        return
                    }

                    if (title.isNotEmpty()) {
                        mBinding.toolbarTitle.text = title
                    }
                }

                override fun onProgressChanged(
                    view: WebView?,
                    newProgress: Int
                ) {

                    mBinding.webProgressBar.visibility =
                        if (newProgress < 100) View.VISIBLE else View.GONE

                    mBinding.webProgressBar.progress = newProgress
                }
            }

            // 加载网页
            if (
                webUrl.startsWith("http://") ||
                webUrl.startsWith("https://") ||
                webUrl.startsWith("file://")
            ) {

                mBinding.webview.loadUrl(webUrl)

            } else {

                mBinding.webview.loadDataWithBaseURL(
                    null,
                    webUrl,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }

        mBinding.ivLeftClose.clickNoRepeat {
            onBackPressed()
        }
    }

    /**
     * 判断是否文件链接
     */
    private fun isFileUrl(url: String): Boolean {

        val lowerUrl = url.lowercase()

        return lowerUrl.endsWith(".apk") ||
                lowerUrl.endsWith(".zip") ||
                lowerUrl.endsWith(".pdf") ||
                lowerUrl.endsWith(".rar") ||
                lowerUrl.endsWith(".7z") ||
                lowerUrl.endsWith(".doc") ||
                lowerUrl.endsWith(".docx") ||
                lowerUrl.endsWith(".xls") ||
                lowerUrl.endsWith(".xlsx")
    }

    /**
     * 使用系统浏览器打开
     */
    private fun openByBrowser(url: String) {

        try {

            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )

            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(browserIntent)

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "无法打开浏览器",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBackPressed() {

        if (mBinding.webview.canGoBack()) {

            val currentUrl = mBinding.webview.url

            mBinding.webview.goBack()

            mBinding.webview.webViewClient = object : WebViewClient() {

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {

                    if (url == currentUrl) {
                        finish()
                    }
                }
            }

        } else {

            finish()
        }
    }

    override fun onDestroy() {

        mBinding.webview.apply {
            stopLoading()
            webChromeClient = null
            destroy()
        }

        super.onDestroy()
    }

    private val weiXinIntentResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_CANCELED) {
            finish()
        }
    }
}