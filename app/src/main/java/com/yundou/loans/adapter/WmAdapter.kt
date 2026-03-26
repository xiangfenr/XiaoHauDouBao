package com.yundou.loans.adapter


import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.yundou.loans.R
import com.yundou.loans.databinding.WxAdapterItemBinding
import com.yundou.loans.entity.WmInfoData

class WmAdapter :
    BaseQuickAdapter<WmInfoData, BaseDataBindingHolder<WxAdapterItemBinding>>(R.layout.wx_adapter_item) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<WxAdapterItemBinding>,
        item: WmInfoData,
    ) {

        holder.dataBinding?.let {
//            it.tvName.text = item.name
//            it.people.text = "已申请" + item.applyNum + "人"
        }
    }
}