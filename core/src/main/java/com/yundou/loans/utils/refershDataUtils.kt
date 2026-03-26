package com.yundou.loans.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.yundou.loans.entity.ChannerItem

/**
 * @Author: fenr
 * 时间: 2024/12/24
 * 类名: ACTIVITY
 * 简述: 首页的贷超排序 处理  同位的 下沉处理
 *
 */
@RequiresApi(Build.VERSION_CODES.N)
class refershDataUtils {
    private var channelList = ArrayList<ChannerItem>()
    private val sortStateMap = mutableMapOf<Int, Int>() // 用于记录每个 sort 分组的下沉次数

    // 每次刷新时都调用该方法，设置新的列表并处理下沉操作
    fun setChannelList(list: ArrayList<ChannerItem>) {
        this.channelList = ArrayList(list) // 每次传入相同的数据列表
    }

    // 刷新数据并根据规则排序
    fun refreshData(): ArrayList<ChannerItem> {
        // 对 channelList 按照 sort 分组
        val sortGroups = mutableMapOf<Int, MutableList<ChannerItem>>()

        // 根据 sort 值分组
        channelList.forEach { item ->
            sortGroups.putIfAbsent(item.sort, mutableListOf())
            sortGroups[item.sort]?.add(item)
        }

        // 处理每个 sort 分组
        sortGroups.forEach { (sortValue, items) ->
            val currentState = sortStateMap.getOrDefault(sortValue, 0) // 获取当前分组的下沉次数

            // 每次刷新时，对当前分组进行下沉操作
            val moveCount = (currentState + 1) % items.size // 确保循环下沉
            val rotatedList = rotateList(items, moveCount)

            // 更新排序后的元素
            sortGroups[sortValue] = rotatedList

            // 更新下沉次数
            sortStateMap[sortValue] = moveCount
        }

        // 合并所有组并更新 channelList
        val newList = ArrayList<ChannerItem>()
        // 遍历所有分组，按照 sort 的大小顺序合并
        sortGroups.entries.sortedBy { it.key }.forEach { entry ->
            newList.addAll(entry.value)
        }

        channelList = newList // 更新 channelList

        return channelList
    }

    // 将列表进行循环下沉处理
    private fun rotateList(items: MutableList<ChannerItem>, moveCount: Int): MutableList<ChannerItem> {
        if (moveCount == 0 || items.size <= 1) return items
        val rotatedList = items.toMutableList()
        // 下沉操作：将最后一个元素移到前面
        repeat(moveCount) {
            val lastItem = rotatedList.removeAt(rotatedList.size - 1)
            rotatedList.add(0, lastItem)
        }
        return rotatedList
    }

    // 获取当前的 channelList，用于展示
    fun getLoansList(): ArrayList<ChannerItem> {
        return channelList
    }
}







