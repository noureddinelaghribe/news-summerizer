package com.noureddine.newssummerizer.Utel

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import java.security.SecureRandom
import java.security.MessageDigest


class AESEncryptor() {

    // Strong predefined key - you can change this to your preferred key
    private val userKey: String = "MyStrongSecretKey2024!@#\$%^&*()_+ABCDEFGHIJKLMNOP"

    private val secretKey: SecretKeySpec

    init {
        // Convert user's key to proper AES key format
        secretKey = generateSecretKey(userKey)
    }

    /**
     * Converts user's string key into proper AES SecretKey
     * Uses SHA-256 to ensure consistent 256-bit key
     */
    private fun generateSecretKey(userKey: String): SecretKeySpec {
        val sha = MessageDigest.getInstance("SHA-256")
        val keyBytes = sha.digest(userKey.toByteArray())
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    /**
     * Encrypts text using AES encryption with your custom key
     * @param plainText The text to encrypt
     * @return Base64 encoded encrypted text with IV prepended
     */
    fun encrypt(plainText: String): String {
        try {
            // Generate random IV
            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            // Create cipher and encrypt
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray())

            // Combine IV + encrypted data and encode to Base64
            val combined = iv + encryptedBytes
            return Base64.getEncoder().encodeToString(combined)

        } catch (e: Exception) {
            throw RuntimeException("Encryption failed: ${e.message}")
        }
    }

    /**
     * Decrypts AES encrypted text using your custom key
     * @param encryptedText Base64 encoded encrypted text with IV
     * @return Decrypted plain text
     */
    fun decrypt(encryptedText: String): String {
        try {
            // Decode the encrypted data
            val combined = Base64.getDecoder().decode(encryptedText)

            // Extract IV (first 16 bytes) and encrypted data
            val iv = combined.sliceArray(0..IV_SIZE-1)
            val encryptedBytes = combined.sliceArray(IV_SIZE until combined.size)
            val ivSpec = IvParameterSpec(iv)

            // Create cipher and decrypt
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes)

        } catch (e: Exception) {
            throw RuntimeException("Decryption failed: ${e.message}")
        }
    }

    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private const val IV_SIZE = 16 // 128 bits

        /**
         * Quick encrypt method for one-time use (uses built-in key)
         */
        fun quickEncrypt(text: String): String {
            return AESEncryptor().encrypt(text)
        }

        /**
         * Quick decrypt method for one-time use (uses built-in key)
         */
        fun quickDecrypt(encryptedText: String): String {
            return AESEncryptor().decrypt(encryptedText)
        }
    }

}