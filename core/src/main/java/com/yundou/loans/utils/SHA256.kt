package com.yundou.loans.utils


import android.util.Base64
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object SHA256 {

    fun getSHA256(input: String, salt: String): String? {
        return try {
            val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
            digest.update(salt.toByteArray())
            val hash: ByteArray = digest.digest(input.toByteArray(charset("UTF-8")))
            val hexString = StringBuilder()
            for (b in hash) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun encryptMD5(input: String): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(input.toByteArray())
            val messageDigest = digest.digest()
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    private const val AES_ALGORITHM = "AES"
    private const val AES_TRANSFORMATION = "AES/ECB/PKCS5Padding"

    fun AESEncrypt(key: String, content: String): String? {
        return try {
            if (content.isEmpty()) return null
            val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), AES_ALGORITHM)
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            val encryptedBytes = cipher.doFinal(content.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            LogUtils.e("AES 加密异常: $e")
            e.printStackTrace()
            null
        }
    }

    fun AESDecrypt(key: String, encryptedContent: String): String? {
        return try {
            if (encryptedContent.isEmpty()) return null
            val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), AES_ALGORITHM)
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            val decodedBytes = Base64.decode(encryptedContent, Base64.DEFAULT)
            String(cipher.doFinal(decodedBytes), Charsets.UTF_8)
        } catch (e: Exception) {
            LogUtils.e("AES 解密异常: $e")
            e.printStackTrace()
            null
        }
    }

    @Throws(Exception::class)
    fun RSAEncrypt(publicKeyStr: String, input: String): String {
        val decodedKey: ByteArray = Base64.decode(publicKeyStr, Base64.DEFAULT)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey: PublicKey = keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val dataBytes = input.toByteArray()
        val inputLength = dataBytes.size
        val outputStream = ByteArrayOutputStream()
        var offset = 0

        while (inputLength - offset > 0) {
            val chunkSize = if (inputLength - offset > 117) 117 else inputLength - offset
            val encryptedChunk = cipher.doFinal(dataBytes, offset, chunkSize)
            outputStream.write(encryptedChunk)
            offset += chunkSize
        }

        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun generateUniqueUUID(): String {
        return UUID.randomUUID().toString()
    }
}

