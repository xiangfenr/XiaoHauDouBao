package com.yundou.loans.utils

import android.content.SharedPreferences
import android.os.Parcelable
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import java.util.*

/**
 * MMKV存储Utils
 */
class MmkvUtil(name: String) {

    private var mmkv: MMKV? = null

    init {
        mmkv = MMKV.mmkvWithID(name)
    }

    fun encode(key: String, value: Any?) {
        when (value) {
            is String -> mmkv?.encode(key, value)
            is Float -> mmkv?.encode(key, value)
            is Boolean -> mmkv?.encode(key, value)
            is Int -> mmkv?.encode(key, value)
            is Long -> mmkv?.encode(key, value)
            is Double -> mmkv?.encode(key, value)
            is ByteArray -> mmkv?.encode(key, value)
            is Nothing -> return
        }
    }

    fun <T> setArray(list: ArrayList<T>, name: String): Boolean {
        if (list.isEmpty()) {
            mmkv?.putInt(name + "size", 0)
            val size: Int? = mmkv?.getInt(name + "size", 0)
            for (i in 0..size!!) {
                if (mmkv?.getString(name + i, null) != null) {
                    mmkv?.remove(name + i)
                }
            }
        } else {
            mmkv?.putInt(name + "size", list.size)
            if (list.size > 20) {
                list.removeAt(0)
            }
            for (i in 0..list.size!!) {
                mmkv?.remove(name + i)
                mmkv?.remove(Gson().toJson(list[i]))
                mmkv?.putString(name + i, Gson().toJson(list[i]))
            }
        }
        return mmkv?.commit() == true
    }

    fun <T> getArray(name: String, bean: T): ArrayList<T> {
        val list = arrayListOf<T>()
        val size = mmkv?.getInt(name + "size", 0)
        for (i in 0..size!!) {
            // 保留原逻辑（历史代码）
        }
        return list
    }

    fun <T : Parcelable> encode(key: String, t: T?) {
        if (t == null) return
        mmkv?.encode(key, t)
    }

    fun encode(key: String, sets: Set<String>?) {
        if (sets == null) return
        mmkv?.encode(key, sets)
    }

    fun decodeInt(key: String): Int = mmkv!!.decodeInt(key, 0)
    fun decodeDouble(key: String): Double? = mmkv?.decodeDouble(key, 0.00)
    fun decodeLong(key: String): Long? = mmkv?.decodeLong(key, 0L)
    fun decodeBoolean(key: String): Boolean = mmkv!!.decodeBool(key, false)
    fun decodeFloat(key: String): Float? = mmkv?.decodeFloat(key, 0F)
    fun decodeByteArray(key: String): ByteArray? = mmkv?.decodeBytes(key)
    fun decodeString(key: String, value: String = ""): String? = mmkv?.decodeString(key, value)
    fun <T : Parcelable> decodeParcelable(key: String, tClass: Class<T>): T? =
        mmkv?.decodeParcelable(key, tClass)

    fun decodeStringSet(key: String): Set<String>? =
        mmkv?.decodeStringSet(key, Collections.emptySet())

    fun removeKey(key: String) {
        mmkv?.removeValueForKey(key)
    }

    fun clearAll() {
        mmkv?.clearAll()
    }

    fun copySharedPreferences(sharedPreferences: SharedPreferences) {
        mmkv?.importFromSharedPreferences(sharedPreferences)
        sharedPreferences.edit().clear().commit()
    }

    companion object {
        var akey: String = ""
        fun getInstance(name: String = "MCP_DATA"): MmkvUtil {
            val instance: MmkvUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
                MmkvUtil(name)
            }
            return instance
        }
    }
}

