package com.yundou.loans.widget

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.yundou.loans.adapter.JiDaiAdapter
import com.yundou.loans.databinding.JidaiXieyiDialogBinding
import com.yundou.loans.entity.JIDaiProductData

/**
 * @Author: fenr
 * 时间: 2025/1/8
 * 类名: ACTIVITY
 * 简述: 吉贷 协议 弹窗
 *
 */
class JiDaiXieYiDialog(
    private var mContext: Context,
    private var jiDaiData: JIDaiProductData
) : XsBaseBottomDialog<JidaiXieyiDialogBinding>(mContext) {

    override var isHideable: Boolean = false
    private var adapter: JiDaiAdapter? = null

    override fun inflateBinding(): JidaiXieyiDialogBinding {
        return JidaiXieyiDialogBinding.inflate(layoutInflater)
    }

    override fun initData() {
        //webview加载行内样式的文本

        isCanceledOnTouch = false
        binding.cName.text = "名称: ${jiDaiData.productOrgName}"
        binding.cProduct.text = "产品: ${jiDaiData.productName}"

        initAdapter()
        adapter?.setList(jiDaiData.protocolList)
        setCancelable(false)
    }

    override fun initLiveData() {
    }

    override fun initListener() {

        binding.agreementbtn.setOnClickListener {
            if (adapter!!.areaAllChecked()) {
                xieyiClick?.agreementClick()
            } else {
                Toast.makeText(context, "请阅读并同意相关协议", Toast.LENGTH_LONG)
                    .show()
            }

        }


    }

    /*
   *初始化adapter
   */
    private fun initAdapter() {
        adapter = JiDaiAdapter()
        binding.xieyRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.xieyRecyclerview.adapter = adapter

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