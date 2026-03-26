package com.yundou.loans.utils

import com.yundou.loans.entity.ChannerItem


/**
 * @Author: fenr
 * 时间: 2025/9/12
 * 类名: ACTIVITY
 * 简述:
 *
 */
class RefershLoansUtils {

    private var channelList = ArrayList<ChannerItem>()

    fun setChannelList(list: ArrayList<ChannerItem>) {
        // 接收外部传入的最新数据（把原样存一份）
        channelList = ArrayList(list)
    }

    // 每次调用把第一个元素移动到末尾
    fun refreshData(): ArrayList<ChannerItem> {
        if (channelList.size <= 1) return ArrayList(channelList) // 安全返回
        val first = channelList.removeAt(0)
        channelList.add(first)
        return ArrayList(channelList) // 返回副本，防止外部误修改内部引用
    }

    fun getChannelList(): ArrayList<ChannerItem> = ArrayList(channelList)
}