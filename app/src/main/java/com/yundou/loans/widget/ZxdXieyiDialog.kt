package com.yundou.loans.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.yundou.loans.databinding.ZxdXieyiDialogBinding
import com.yundou.loans.entity.ZxdFormResultBean
import com.yundou.loans.ui.CommonWebViewActivity


/**
 * @Author: fenr
 * 时间: 2025/2/6
 * 类名: ACTIVITY
 * 简述: 智享贷  匹配到产品的显示
 *
 */
class ZxdXieyiDialog(
    private var mContext: Context,
    private var resultBean: ZxdFormResultBean
) : XsBaseBottomDialog<ZxdXieyiDialogBinding>(mContext) {

    override var isHideable: Boolean = false

    override fun inflateBinding(): ZxdXieyiDialogBinding {
        return ZxdXieyiDialogBinding.inflate(layoutInflater)
    }

    override fun initData() {
        Glide.with(mContext).load(resultBean.logoUrl).into(binding.jigouImg)

        binding.jigougongsiname.text = "公司名称: ${resultBean.productOrgName}"
        if (resultBean.productName.isNullOrEmpty()) {
            binding.jigoupingtai.gone()
        } else {
            binding.jigoupingtai.visible()
            binding.jigoupingtai.text = " ${resultBean.productName}"
        }

        if (!resultBean.agreementList.isNullOrEmpty()) {
            Log.i("xiang1", Gson().toJson(resultBean.agreementList))

            // 提取 protocolName
            val listpro: List<String> = resultBean.agreementList.map { "《${it.protocolName!!}》" }
            Log.i("xiang1", Gson().toJson(listpro))

            binding.tagsLayout.setTags(listpro)
            binding.tagsLayout.onTagClickListener = object : TagsLayout.OnTagClickListener {
                override fun onTagClick(position: Int, text: String) {
                    val protocolUrl = resultBean.agreementList[position].protocolUrl
                    val intent = Intent(mContext, CommonWebViewActivity::class.java)
                    intent.putExtra("webUrl", protocolUrl)
                    mContext.startActivity(intent)
                }
            }

        }


    }

    override fun initLiveData() {
    }

    override fun initListener() {

        binding.agreementbtn.setOnClickListener {
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