package net.nekomura.dcbot.Utils

import okhttp3.internal.and
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Md5 {

    fun toMD5(inStr: String): String? {
        val charArray = inStr.toCharArray()
        val byteArray = ByteArray(charArray.size)
        for (i in charArray.indices) {
            byteArray[i] = charArray[i].toByte()
        }
        return byteArray.toMD5()
    }

    fun ByteArray.toMD5(): String? {
        val md5: MessageDigest
        md5 = try {
            MessageDigest.getInstance("MD5")
        } catch (var6: NoSuchAlgorithmException) {
            throw RuntimeException(var6)
        }
        val md5Bytes = md5.digest(this)
        val hexValue = StringBuilder()
        for (md5Byte in md5Bytes) {
            val i: Int = md5Byte and 255
            if (i < 16) {
                hexValue.append("0")
            }
            hexValue.append(Integer.toHexString(i))
        }
        return hexValue.toString()
    }

}