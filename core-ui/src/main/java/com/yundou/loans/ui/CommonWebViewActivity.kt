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

    private val downloadUrlList = listOf(".pdf", ".zip", ".apk") // 需要拦截的后缀
    private val PERMISSION_REQUEST_CODE = 1001
    private var pendingDownloadUrl: String? = null // 用于在权限申请后继续下载
    private var myDownloadId: Long = -1L

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
        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        val webUrl = intent.getStringExtra("webUrl") ?: "".trim()
        //测试下载的静态网页
        // val webUrl = "file:///android_asset/index.html"

        LogUtils.e("webview--url --: $webUrl")

        //逾期处理的上报
        if (webUrl.contains("zhiyunjishu")) {
            val phone = MmkvUtil.Companion.getInstance().decodeString("loginphone")?:""
            val partner_id = MmkvUtil.Companion.getInstance().decodeInt("partner_id")
            viewModel.daikuanYuqiPush("40", partner_id.toString(), phone) {

            }
        }

        LogUtils.d("webUrl", webUrl.toString())
        if (!TextUtils.isEmpty(webUrl)) {
            mBinding.webview.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
            // 获取WebView的WebSettings对象
            val webSettings = mBinding.webview.getSettings()
            // 启用缓存
            // webSettings.cacheMode = WebSettings.LOAD_DEFAULT
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
            mBinding.webview.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest,
                ): Boolean {
                    val url = request.url.toString()
                    if (url.startsWith("weixin")) {
                        val intent = Intent("android.intent.action.VIEW", Uri.parse(url))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        weiXinIntentResultLauncher.launch(intent)

                        return true
                    } else if (downloadUrlList.any { url.endsWith(it, ignoreCase = true) }) {
                        if (hasStoragePermission()) {

                            if (isHuaweiDevice() || isHarmonyOs()) {
                                // 跳转到浏览器进行下载
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(browserIntent)
                            } else {
                                // 使用原来的 DownloadManager 下载
                                startDownload(url)
                            }


                        } else {
                            pendingDownloadUrl = url
                            requestStoragePermission()
                        }
                        return true // 拦截跳转
                    } else {
                        view.loadUrl(request.url.toString())
                    }
                    return true
                }
            })


            mBinding.webview.webChromeClient = object : WebChromeClient() {

                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
                    if (title.startsWith("https://")) {
                        return
                    }
                    if (!TextUtils.isEmpty(title)) {
                        mBinding.toolbarTitle.text = title
                    }
                }


                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    mBinding.webProgressBar.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
                    mBinding.webProgressBar.progress = newProgress
                }
            }


            // 加载网页
            if (webUrl.startsWith("http://") || webUrl.startsWith("https://") || webUrl.startsWith(
                    "file://"
                )
            ) {
                mBinding.webview.loadUrl(webUrl.toString())
            } else {
                mBinding.webview.loadDataWithBaseURL(null, webUrl, "text/html", "UTF-8", null)
            }
        }

        mBinding.ivLeftClose.clickNoRepeat {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        if (mBinding.webview.canGoBack()) {
            val currentUrl = mBinding.webview.url // 记录当前 URL
            mBinding.webview.goBack() // 先尝试回退
            // 监听 onPageFinished 确保页面真的变了
            mBinding.webview.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    if (url == currentUrl) {
                        // 如果页面没有变化，说明 goBack() 没有效果，直接关闭 Activity
                        finish()
                    }
                }
            }
        } else {
            finish() // 没有历史记录，直接退出
        }
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上不需要外部存储权限
            true
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pendingDownloadUrl?.let {
                    startDownload(it)
                }
            } else {
                Toast.makeText(this, "请授权存储权限以下载文件", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startDownload(url: String) {
        val fileName = URLUtil.guessFileName(url, null, null)
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(fileName)
            setDescription("正在下载...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setMimeType("application/vnd.android.package-archive") // 指定 MIME 类型
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            } else {
                setDestinationInExternalFilesDir(
                    this@CommonWebViewActivity,
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName
                )
            }
        }

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        myDownloadId = downloadManager.enqueue(request)
        Toast.makeText(this, "开始下载：$fileName", Toast.LENGTH_SHORT).show()
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == myDownloadId) {
                    installApk(id)
                }
            }
        }
    }

    private fun installApk(downloadId: Long) {
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val uri = downloadManager.getUriForDownloadedFile(downloadId) ?: return

        // Android 8.0+ 权限检测
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
                Toast.makeText(this, "请允许安装未知来源应用", Toast.LENGTH_LONG).show()
                return
            }
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
    }

    private fun isHuaweiDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("huawei") || manufacturer.contains("honor")
    }

    private fun isHarmonyOs(): Boolean {
        return try {
            val clazz = Class.forName("ohos.system.version.SystemVersion")
            clazz != null
        } catch (e: Exception) {
            false
        }
    }

    private val weiXinIntentResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_CANCELED) {
            finish()
        }
    }
}