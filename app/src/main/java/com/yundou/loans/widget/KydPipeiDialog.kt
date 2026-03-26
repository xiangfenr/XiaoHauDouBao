package com.yundou.loans.widget

import android.content.Context
import android.content.Intent
import com.yundou.loans.databinding.KydXieyiDialogBinding
import com.yundou.loans.entity.MatchingInstitutionsX
import com.yundou.loans.ui.CommonWebViewActivity


/**
 * @Author: fenr
 * 时间: 2025/1/8
 * 类名: ACTIVITY
 * 简述: 快易贷  匹配机构的弹窗
 *
 */
class KydPipeiDialog(
    private var mContext: Context,
    private var xieyiContent: String,
    var matchBean: MatchingInstitutionsX
) : XsBaseBottomDialog<KydXieyiDialogBinding>(mContext) {

    override var isHideable: Boolean = false

    override fun inflateBinding(): KydXieyiDialogBinding {
        return KydXieyiDialogBinding.inflate(layoutInflater)
    }

    override fun initData() {
        binding.jigouname.text = matchBean.productName
        binding.jigougongsiname.text = "公司名称: ${matchBean.companyName}"
        if (matchBean.informationServicePlatform.isNullOrEmpty()) {
            binding.jigoupingtai.gone()
        } else {
            binding.jigoupingtai.visible()
            binding.jigoupingtai.text = "信息服务平台: ${matchBean.informationServicePlatform}"
        }

        //  val spannedText = HtmlCompat.fromHtml(xieyiContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
        //  binding.xieyiContent.text = spannedText
        //webview加载行内样式的文本
        binding.xieyiWebview.settings.javaScriptEnabled = true
        binding.xieyiWebview.loadDataWithBaseURL(null, xieyiContent, "text/html", "UTF-8", null)

        //个人信息授权
        binding.gerenxinxi.setOnClickListener {
            val intent = Intent(mContext, CommonWebViewActivity::class.java)
            intent.putExtra("webUrl", xieyiContent)
            mContext.startActivity(intent)
        }

        //机构返回协议
        if (!matchBean.protocolAddressList.isNullOrEmpty()) {
            binding.jigoufanhuixieyi.visible()
            binding.jigoufanhuixieyi.text = "《${matchBean.protocolAddressList[0].showName}》"
            binding.jigoufanhuixieyi.setOnClickListener {
                val intent = Intent(mContext, CommonWebViewActivity::class.java)
                intent.putExtra("webUrl", matchBean.protocolAddressList[0].showUrl)
                mContext.startActivity(intent)
            }
        }

    }

    override fun initLiveData() {
    }

    override fun initListener() {

        binding.agreementbtn.setOnClickListener {
            //我在本群昵称
            xieyiClick?.agreementClick()
        }

    }

    override fun initAfterView() {


    }


    private var xieyiClick: IXieyiDialogClick? = null

    fun setXieyiDialogClick(click: IXieyiDialogClick?) {
        this.xieyiClick = click
    }

    interface IXieyiDialogClick {
        fun agreementClick()

    }


}