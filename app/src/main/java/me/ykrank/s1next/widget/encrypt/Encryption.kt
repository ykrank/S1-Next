package me.ykrank.s1next.widget.encrypt

interface Encryption {
    @Throws(Exception::class)
    fun encryptText(text: String): String

    @Throws(Exception::class)
    fun decryptText(encryptedText: String): String
}