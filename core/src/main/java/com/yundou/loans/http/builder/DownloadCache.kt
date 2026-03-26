package com.yundou.loans.http.builder

import java.util.*

interface DownloadCache {

    /**
     * 插入或者更新对象
     */
    fun insertOrUpdate(`object`: Any?): Int

    /**
     * 删除对象
     */
    fun delete(var1: Any?): Int

    /**
     * 查询数据总数
     */
    fun <T> queryCount(var1: Class<T>?): Long

    /**
     * 查询列表
     */
    fun <T> query(var1: Class<T>?): ArrayList<T>?

    /**
     * 查询所有列表
     */
    fun <T> queryAll(): ArrayList<T>?

    /**
     * 根据ID查询数据
     */
    fun <T> queryById(var1: Long, var2: Class<T>?): T?

    /**
     * 根据url查询数据
     */
    fun <T> queryByUrl(url: String): T?
}

