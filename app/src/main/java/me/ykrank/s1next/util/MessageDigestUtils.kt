package me.ykrank.s1next.util

import java.security.MessageDigest


fun MessageDigest.md5(str: String): String {
    this.reset()
    val bytes = this.digest(str.toByteArray(Charsets.UTF_8))
    var result = ""
    for (b in bytes) {
        var temp = Integer.toHexString(b.toInt() and 0xff)
        if (temp.length == 1) {
            temp = "0$temp"
        }
        result += temp
    }
    return result
}