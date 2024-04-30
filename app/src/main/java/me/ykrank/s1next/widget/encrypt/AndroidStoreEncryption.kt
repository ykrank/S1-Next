package me.ykrank.s1next.widget.encrypt

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec


class AndroidStoreEncryption(private val keyAlias: String) : Encryption {

    @Throws(Exception::class)
    override fun encryptText(text: String): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator =
                KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE
                )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build()
            )
            keyGenerator.generateKey()
        }
        val key = keyStore.getKey(keyAlias, null)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.getIV()
        val encryptedText = cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))
        val encryptedBytes = ByteArray(GCM_IV_LENGTH + encryptedText.size)
        System.arraycopy(iv, 0, encryptedBytes, 0, GCM_IV_LENGTH)
        System.arraycopy(
            encryptedText,
            0,
            encryptedBytes,
            GCM_IV_LENGTH,
            encryptedText.size
        )
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    override fun decryptText(encryptedText: String): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        val key = keyStore.getKey(keyAlias, null)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val iv = ByteArray(GCM_IV_LENGTH)
        val encryptedTextBytes = ByteArray(encryptedBytes.size - GCM_IV_LENGTH)
        System.arraycopy(encryptedBytes, 0, iv, 0, GCM_IV_LENGTH)
        System.arraycopy(
            encryptedBytes,
            GCM_IV_LENGTH,
            encryptedTextBytes,
            0,
            encryptedTextBytes.size
        )
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        val decryptedTextBytes = cipher.doFinal(encryptedTextBytes)
        return String(decryptedTextBytes, StandardCharsets.UTF_8)
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 128
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}