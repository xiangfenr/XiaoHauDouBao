/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yundou.loans.http.builder

import android.util.Log
import com.yundou.loans.http.EasyHttp
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.TimeUnit

class LogInterceptor @JvmOverloads constructor(
    val tag: String = EasyHttp::class.java.simpleName,
    val logLevel: Level = Level.BODY,
    private val logger: Logger = Logger.DEFAULT
) : Interceptor {

    @Volatile
    private var headersToRedact = emptySet<String>()

    @set:JvmName("level")
    @Volatile
    var level = logLevel

    enum class Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    interface Logger {
        fun log(tag: String, message: String)
        fun showLog(tag: String, message: String, showCount: Int)

        companion object {
            @JvmField
            val DEFAULT: Logger = object : Logger {
                override fun log(tag: String, message: String) {
                    showLog(tag, message, 1000)
                }

                override fun showLog(tag: String, strBuffer: String, showCount: Int) {
                    var log: String = strBuffer.toString()
                    val maxStrLength: Int = 2001 - tag.length
                    while (log.length > maxStrLength) {
                        Log.println(Log.DEBUG, tag, log)
                        log = log.substring(maxStrLength)
                    }
                    Log.println(Log.DEBUG, tag, log)
                }
            }
        }
    }

    fun redactHeader(name: String) {
        val newHeadersToRedact = TreeSet(String.CASE_INSENSITIVE_ORDER)
        newHeadersToRedact += headersToRedact
        newHeadersToRedact += name
        headersToRedact = newHeadersToRedact
    }

    fun setLevel(level: Level) = apply {
        this.level = level
    }

    @JvmName("-deprecated_level")
    @Deprecated(
        message = "moved to var",
        replaceWith = ReplaceWith(expression = "level"),
        level = DeprecationLevel.ERROR
    )
    fun getLevel(): Level = level

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level

        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS
        val copyRequest = request.newBuilder().build()
        val requestBody = request.body

        val connection = chain.connection()
        val requestLog = StringBuilder()
        requestLog.append(" \n┌─────────────────────────────────────────────────────────────────────────────────────\n")
        var requestStartMessage =
            ("--> ${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        if (!logHeaders && requestBody != null) {
            requestStartMessage += " (${requestBody.contentLength()}-byte body)"
        }
        requestLog.append(requestStartMessage + "\n")

        if (logHeaders) {
            val headers = request.headers

            if (requestBody != null) {
                requestBody.contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        requestLog.append("Content-Type: $it\n")
                    }
                }
                if (requestBody.contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        requestLog.append("Content-Length: ${requestBody.contentLength()}\n")
                    }
                }
            }
            if (headers.size > 0) {
                requestLog.append("├ request Headers:\n")
            }
            for (i in 0 until headers.size) {
                requestLog.append("\t${headers.name(i)}: ${headers.value(i)}\n")
            }

            if (!logBody || requestBody == null) {
                requestLog.append("${request.method}\n")
            } else if (bodyHasUnknownEncoding(request.headers)) {
                requestLog.append("${request.method} (encoded body omitted)\n")
            } else if (requestBody.isDuplex()) {
                requestLog.append("${request.method} (duplex request body omitted)\n")
            } else if (requestBody.isOneShot()) {
                requestLog.append("${request.method}(one-shot body omitted)\n")
            } else {
                val buffer = Buffer()
                requestBody.writeTo(buffer)

                val contentType = requestBody.contentType()
                val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8

                if (buffer.isProbablyUtf8()) {
                    requestLog.append("${buffer.readString(charset)}\n")
                    requestLog.append("├ Body: (${requestBody.contentLength()}-byte)\n")
                } else {
                    requestLog.append(" ${request.method} (binary ${requestBody.contentLength()}-byte body omitted)\n")
                }
            }
        }
        requestLog.append("└─────────────────────────────────────────────────────────────────────────────────────")
        logger.log(tag, requestLog.toString())

        val responseLog = StringBuilder()
        responseLog.append(" \n┌─────────────────────────────────────────────────────────────────────────────────────\n")
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.log(tag, "<-- HTTP FAILED: ${request.url}")
            responseLog.append("├ ${copyRequest.method} ${copyRequest.url} ${(chain.connection()?.protocol() ?: "")}\n")
            responseLog.append("├ FAILED: $e\n")
            responseLog.append("└─────────────────────────────────────────────────────────────────────────────────────")
            logger.log(tag, responseLog.toString())
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val builder = response.newBuilder()
        val cloneResponse = builder.build()
        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        responseLog.append("${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms${if (!logHeaders) ", $bodySize body" else ""})\n")
        if (logHeaders) {
            val headers = response.headers
            if (headers.size > 0) {
                responseLog.append("├ response Headers:\n")
            }
            for (i in 0 until headers.size) {
                responseLog.append("│\t${headers.name(i)}: ${headers.value(i)}\n")
            }
            if (!logBody || !response.promisesBody()) {
                responseLog.append("END HTTP")
            } else if (bodyHasUnknownEncoding(response.headers)) {
                responseLog.append("END HTTP (encoded body omitted)")
            } else {
                logger.log(tag, "")
                val source = responseBody.source()
                source.request(Long.MAX_VALUE)
                var buffer = source.buffer

                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val contentType = responseBody.contentType()
                val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8

                if (!buffer.isProbablyUtf8()) {
                    responseLog.append("├ Body: ($buffer.size)\n")
                    responseLog.append("│\tEND HTTP (binary ${buffer.size}-byte body omitted)\n")
                    logger.log(tag, responseLog.toString())
                    return response
                }

                if (contentLength != 0L) {
                    responseLog.append("├ Body: ($bodySize)\n")
                    responseLog.append("│\t${buffer.clone().readString(charset)}\n")
                }

                if (gzippedLength != null) {
                    responseLog.append("│\tEND HTTP (${buffer.size}-byte, $gzippedLength-gzipped-byte body)\n")
                } else {
                    responseLog.append("│\tEND HTTP (${buffer.size}-byte body)\n")
                }
                responseLog.append("└─────────────────────────────────────────────────────────────────────────────────────")
                logger.log(tag, responseLog.toString())
            }
        }

        return response
    }

    private fun logHeader(headers: Headers, i: Int) {
        val value = if (headers.name(i) in headersToRedact) "██" else headers.value(i)
        logger.log(tag, headers.name(i) + ": " + value)
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun Buffer.isProbablyUtf8(): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = size.coerceAtMost(64)
            copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (_: EOFException) {
            return false
        }
    }
}

