package com.yundou.loans.ui.loan


import android.content.Intent
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.yundou.loans.R
import com.yundou.loans.base.CommonActivity
import com.yundou.loans.databinding.QidaiProduceLayoutBinding
import com.yundou.loans.entity.QiDaiProductInfo
import com.yundou.loans.model.UserViewModel
import com.yundou.loans.ui.CommonWebViewActivity
import com.yundou.loans.utils.MmkvUtil
import com.yundou.loans.widget.gone
import com.yundou.loans.widget.visible
import kotlin.jvm.java

/**
 * 期贷产品页面
 */
class QiDaiProduceActivity : CommonActivity<UserViewModel, QidaiProduceLayoutBinding>() {

    private var productInfoList: List<QiDaiProductInfo>? = null

    override fun getLayoutId(): Int {
        return R.layout.qidai_produce_layout
    }

    override fun isShowActionBar(): Boolean {
        return true
    }

    override fun setTitle(): CharSequence {
        return "产品详情"
    }

    override fun init() {


        val phone = MmkvUtil.getInstance().decodeString("loginphone") ?: ""
        viewModel.qiDaiQueryPlatform(phone) {


            if (null != it) {
                if (it.code == 200) {
                    if (it.data.productList.isEmpty()) {
                        mBinding.noDataLin.visible()
                    } else {
                        mBinding.noDataLin.gone()
                        productInfoList = it.data.productList
                        productInfoList!![0].let { it ->
                            Glide.with(this)
                                .load(it.productLogo)
                                .placeholder(R.mipmap.qidai_logo) // 加载中的占位图
                                .error(R.mipmap.qidai_logo) // 加载错误时的图片
                                .into(mBinding.imgICon)
//                            mBinding.jigouname.text = it.companyName
//                            mBinding.jigougongsiname.text = it.companyAddress

//                            val list: MutableList<ZxdNewAgreeBean> = mutableListOf()
//                            it.agreement.map { it ->
//                                val bean = ZxdNewAgreeBean()
//                                bean.protocolName = it.agreementName
//                                bean.protocolUrl = it.agreementUrl
//                                list.add(bean)
//                            }
//                            adapter.setList(list)


                            it.agreement.forEachIndexed { index, item ->
                                val container = LinearLayout(this).apply {
                                    orientation = LinearLayout.HORIZONTAL

                                    // 前缀
                                    val prefixTv = TextView(context).apply {
                                        text = "阅读并查看协议内容："  // 这里换成你要的前缀
                                        setTextColor(Color.GRAY)
                                        textSize = 14f
                                    }

                                    // 协议名
                                    val nameTv = TextView(context).apply {
                                        text = "《${item.agreementName}》"
                                        setTextColor(Color.BLUE)
                                        textSize = 14f

                                        setOnClickListener {
                                            val intent =
                                                Intent(context, CommonWebViewActivity::class.java)
                                            intent.putExtra("webUrl", item.agreementUrl)
                                            context.startActivity(intent)
                                        }
                                    }
                                    if (index == 0) {
                                        addView(prefixTv)
                                    }
                                    addView(nameTv)
                                }
                                mBinding.agreementLayout.addView(container)
                            }

                        }
                    }
                } else {
                    mBinding.noDataLin.visible()
                    viewModel.defUI.toastEvent.postValue(it.msg)
                }
            } else {
                mBinding.noDataLin.visible()
                viewModel.defUI.toastEvent.postValue("服务异常")
            }


        }


//        mBinding.sragreementCheckbox.setOnCheckedChangeListener { compoundButton, b ->
//            if (b) {
//                adapter.setAllCheckedTrue()
//            } else {
//                adapter.setAllCheckedFalse()
//            }
//        }

        mBinding.agreementbtn.setOnClickListener {
            if (!mBinding.sragreementCheckbox.isChecked) {
                viewModel.defUI.toastEvent.postValue("请先阅读查看以上协议")
            } else {
                val idString = productInfoList!!.joinToString(
                    separator = ",",
                    transform = { it -> it.orgId.toString() })
                viewModel.qiDaiMatchRegister(
                    idString, phone
                ) { resultData ->
                    if (resultData.code == 200) {
                        goSuccessActivity()
                    } else {
                        viewModel.defUI.toastEvent.postValue(resultData.message)

                    }
                }
            }

        }

    }

    private fun goSuccessActivity() {
        startActivity(
            Intent(
                this@QiDaiProduceActivity,
                WmSuccessActivity::class.java
            )
        )
        setResult(RESULT_OK)
        finish()
    }


}