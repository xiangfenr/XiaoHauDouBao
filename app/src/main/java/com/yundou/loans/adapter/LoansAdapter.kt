package com.yundou.loans.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.yundou.loans.R
import com.yundou.loans.databinding.HomeAdapterItemBinding
import com.yundou.loans.entity.ChannerItem

class LoansAdapter :
    BaseQuickAdapter<ChannerItem, BaseDataBindingHolder<HomeAdapterItemBinding>>(R.layout.home_adapter_item) {
    override fun convert(
        holder: BaseDataBindingHolder<HomeAdapterItemBinding>,
        item: ChannerItem,
    ) {

        holder.dataBinding?.let {
            Glide.with(context).load(item.icon_image)
                .transform(CenterCrop(), RoundedCorners(20))  // 20 是圆角半径(px)
                .into(it.ivCenterlistitemlistadapterIcon)
            it.tvCenterlistitemlistadapterName.text = item.title
            it.tvCenterlistitemlistadapterLilv.text = item.loan_limit
            it.tvLi.text = item.daily
            it.tvTiem.text = item.month
            it.tvJianjie.text= item.brief_content
        }



    }
}