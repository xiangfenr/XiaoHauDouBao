package com.yundou.loans.utils

import android.content.Context
import android.os.Environment
import com.alibaba.fastjson.JSON
import com.yundou.loans.entity.WorldAreaCode
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader


object FileUtils {
    fun getFilesDir(type: String): File {
        return Utils.getContext().getExternalFilesDir(type)!!
    }

    fun createApkFile(apkName: String): File? {
        val apkDir: File = getFilesDir("APK")
        if (!apkDir.exists()) {
            apkDir.mkdir()
        }
        val file = File(apkDir, apkName)
        val exists = file.exists()
        if (exists) {
            file.delete()
        }
        LogUtils.d("文件是否存在$exists")
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    fun delete(delFile: String): Boolean {
        val file = File(delFile)
        return try {
            if (!file.exists()) {
                LogUtils.e("删除文件失败:${delFile}不存在！")
                false
            } else {
                if (file.isFile) deleteSingleFile(delFile) else deleteDirectory(delFile)
            }
        } catch (e: Exception) {
            false
        }
    }


    /**
     * 删除单个文件
     */
    private fun deleteSingleFile(delFile: String): Boolean {
        val file = File(delFile)
        return try {
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (!file.exists() && file.isFile) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 删除目录及目录下的文件
     */
    private fun deleteDirectory(delFile: String): Boolean {
        var filePath = delFile
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (filePath.endsWith(File.separator))
            filePath = "filePath${File.separator}"
        val dirFile = File(filePath)
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory)) {
            return false
        }
        var flag = true
        // 删除文件夹中的所有文件包括子目录
        val listFiles = dirFile.listFiles()
        if (listFiles.isNotEmpty()) {
            listFiles.map {
                if (it.isFile) { // 删除子文件
                    flag = deleteSingleFile(it.absolutePath)
                    if (!flag) {
                        return@map
                    }
                } else if (it.isDirectory) {   // 删除子目录
                    flag = deleteDirectory(it.absolutePath)
                    if (!flag) {
                        return@map
                    }
                }
            }
        }

        if (!flag) return false
        return dirFile.delete()

    }

    fun getDiskCacheDir(context: Context): String? {
        var cachePath: String? = null
        cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                context.externalCacheDir!!.path
            } else {
                context.cacheDir.path
            }
        return cachePath
    }

    /**
     * 读取世界各国的区号
     */
    fun readWorldAreaCode(context: Context): MutableList<WorldAreaCode>? {
        var br: BufferedReader? = null
        val fileName = "address.json"
        try {
            br = BufferedReader(InputStreamReader(context.resources.assets.open(fileName)))
            var line: String? = null
            val sb = StringBuilder()
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            br.close()
            val json = sb.toString()
            val codeList = JSON.parseArray(json, WorldAreaCode::class.java)

            return codeList
        } catch (e: Exception) {
        } finally {
            br?.close()
        }
        return null
    }

}