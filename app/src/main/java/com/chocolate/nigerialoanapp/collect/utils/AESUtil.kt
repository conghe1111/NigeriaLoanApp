package com.chocolate.nigerialoanapp.collect.utils

import android.util.Base64
import com.chocolate.nigerialoanapp.BuildConfig
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESUtil {

//    AesCBCKey string = "33f24fca6d6298fd1b7ff147559be437"
//    AesCBCIV  string = "a085a68b22ec1debea760810a4515506"

    const val IS_DEBUG = !BuildConfig.USE_ONLINE_API

    private const val AES_CBC_KEY_D = "33f24fca6d6298fd1b7ff147559be437"
    private const val AES_CBC_IV_D = "a085a68b22ec1debea760810a4515506"
    private const val AES_CBC_KEY_R = "33f24fca6d6298fd1b7ff147559be437"
    private const val AES_CBC_IV_R = "a085a68b22ec1debea760810a4515506"
    private const val AES_MODE = "AES/CBC/PKCS5Padding"
    private const val AES = "AES"

    private val CHARSET = Charsets.UTF_8

    private fun aesCBCKey(): String{
        return if (IS_DEBUG){
            AES_CBC_KEY_D
        }else{
            AES_CBC_KEY_R
        }
    }

    private fun aesCBCIV(): String{
        return if (IS_DEBUG){
            AES_CBC_IV_D
        }else{
            AES_CBC_IV_R
        }
    }

    fun encrypt(encryptData: String, key: String = aesCBCKey(), iv: String = aesCBCIV()): String{
        val ivPart = IvParameterSpec(decodeHex(iv))
        val keyPart = SecretKeySpec(decodeHex(key), AES)
        val dataPart = encryptData.toByteArray(CHARSET)
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, keyPart, ivPart)
        val encryptPart = cipher.doFinal(dataPart)
        val encryptRes = String(Base64.encode(encryptPart, Base64.DEFAULT), CHARSET)
        return encryptRes
    }

    fun decrypt(decryptData: String, key: String = aesCBCKey(), iv: String = aesCBCIV()): String{
        val ivPart = IvParameterSpec(decodeHex(iv))
        val keyPart = SecretKeySpec(decodeHex(key), AES)
        val dataPart = Base64.decode(decryptData, Base64.DEFAULT)
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.DECRYPT_MODE, keyPart, ivPart)
        val decryptPart = cipher.doFinal(dataPart)
        val decryptRes = String(decryptPart, CHARSET)
        return decryptRes
    }

    private fun decodeHex(input: String): ByteArray? {
        return decodeHex(input.toCharArray())
    }

    private fun decodeHex(data: CharArray): ByteArray {
        val len = data.size
        if (len and 0x01 != 0) {
            throw Throwable("Odd number of characters.")
        }
        val out = ByteArray(len shr 1)

        // two characters form the hex value.
        var i = 0
        var j = 0
        while (j < len) {
            var f = toDigit(data[j], j) shl 4
            j++
            f = f or toDigit(data[j], j)
            j++
            out[i] = (f and 0xFF).toByte()
            i++
        }
        return out
    }

    fun toDigit(ch: Char, index: Int): Int {
        val digit = ch.digitToIntOrNull(16) ?: -1
        if (digit == -1) {
            throw Throwable("Illegal hexadecimal character $ch at index $index")
        }
        return digit
    }

}