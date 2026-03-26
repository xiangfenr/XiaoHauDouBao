package com.yundou.loans.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.yundou.loans.entity.ChannerItem

/**
 * @Author: fenr
 * 时间: 2024/12/24
 * 类名: ACTIVITY
 * 简述:
 *
 */
@RequiresApi(Build.VERSION_CODES.N)
class ChannelManager {

    private var channelList = ArrayList<ChannerItem>()

    // 用于 sink=1 时记录每个 sort 分组轮训次数
    private val sortStateMap = mutableMapOf<Int, Int>()


    fun setChannelList(list: ArrayList<ChannerItem>) {
        channelList = ArrayList(list)
    }

    // sink = 1 → 按 sort 分组轮训
    // sink = 2 → 整体轮训
    fun refreshData(sink: Int): ArrayList<ChannerItem> {
        return if (sink == 1) {
            refreshBySortGroup()
        } else {
            refreshAsWhole()
        }
    }

    /** -----------------------------
     *      sink = 1：按 sort 分组轮训
     * ----------------------------- **/
    private fun refreshBySortGroup(): ArrayList<ChannerItem> {
        // 保留原始顺序的分组
        val sortGroups = linkedMapOf<Int, MutableList<ChannerItem>>()

        // 按 sort 分组，组内顺序保留
        channelList.forEach { item ->
            sortGroups.putIfAbsent(item.sort, mutableListOf())
            sortGroups[item.sort]!!.add(item)
        }

        // 对每个分组轮训
        sortGroups.forEach { (sortValue, items) ->
            val currentState = sortStateMap.getOrDefault(sortValue, 0)
            val moveCount = (currentState + 1) % items.size
            sortGroups[sortValue] = rotateList(items, moveCount)
            sortStateMap[sortValue] = moveCount
        }

        // 拼回列表，保留服务器原有组顺序
        val newList = ArrayList<ChannerItem>()
        sortGroups.values.forEach { newList.addAll(it) }

        channelList = newList
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

    /** -----------------------------
     *      sink = 2：整个列表整体轮训
     * ----------------------------- **/

    // 用于记录点击顺序（沉底顺序）
    private val sunkList = ArrayList<Int>()   // 保存 itemId 或唯一标识


    // 用户点击某个 item → 请求沉底
    fun clickItem(itemId: Int) {
        // 如果已经存在，先移除掉（防止重复）
        sunkList.remove(itemId)

        // 再插入尾部（沉底顺序）
        sunkList.add(itemId)
    }

    private fun refreshAsWhole(): ArrayList<ChannerItem> {
        if (channelList.isEmpty()) return channelList

        val newList = ArrayList<ChannerItem>()

        // 1. 先加入未沉底的元素（保持服务器原顺序）
        val notSunk = ArrayList<ChannerItem>()
        for (item in channelList) {
            if (item.id !in sunkList) {
                notSunk.add(item)
            }
        }

        newList.addAll(notSunk)
        // 2. 再按照 sunkList 顺序加入已沉底的元素
        sunkList.forEach { sunkId ->
            val item = channelList.find { it.id == sunkId }
            if (item != null) newList.add(item)
        }

       if (newList.isNotEmpty()){
           channelList.clear()
           channelList = newList
       }
        return channelList
    }



    // 获取当前的 channelList，用于展示
    fun getChannelList(): ArrayList<ChannerItem> {
        return channelList
    }
}





