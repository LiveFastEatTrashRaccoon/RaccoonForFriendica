package com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.set
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreCrypto.CCCrypt
import platform.CoreCrypto.kCCAlgorithmAES
import platform.CoreCrypto.kCCDecrypt
import platform.CoreCrypto.kCCEncrypt
import platform.CoreCrypto.kCCOptionPKCS7Padding
import platform.CoreCrypto.kCCSuccess
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecRandomCopyBytes
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecRandomDefault
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.posix.memcpy
import platform.posix.size_tVar
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class, BetaInteropApi::class)
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultEncryptionHelper : EncryptionHelper {

    private val masterKey: ByteArray by lazy {
        getOrCreateMasterKey()
    }

    override fun encrypt(input: String): ByteArray? {
        val inputBytes = input.encodeToByteArray()
        return encryptAesCbc(inputBytes, masterKey)
    }

    override fun decrypt(input: ByteArray): String? {
        val decryptedBytes = decryptAesCbc(input, masterKey) ?: return null
        return decryptedBytes.decodeToString()
    }

    override fun encodeToString(input: ByteArray): String = Base64.encode(input)

    override fun decodeFromString(input: String): ByteArray = Base64.decode(source = input)

    private fun getOrCreateMasterKey(): ByteArray = retrieveMasterKey()
        ?: createAndStoreMasterKey()
        ?: getOrCreateFallbackKey()

    private fun retrieveMasterKey(): ByteArray? = memScoped {
        val result = alloc<CFTypeRefVar>()
        val serviceCf = SERVICE_NAME.toCFString()
        val accountCf = ACCOUNT_NAME.toCFString()

        try {
            withKeychainQuery(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to serviceCf,
                kSecAttrAccount to accountCf,
                kSecReturnData to kCFBooleanTrue,
                kSecMatchLimit to kSecMatchLimitOne,
            ) { query ->
                val status = SecItemCopyMatching(query, result.ptr)
                if (status == errSecSuccess) {
                    val nsData = CFBridgingRelease(result.value) as? NSData
                    nsData?.toByteArray()
                } else {
                    null
                }
            }
        } finally {
            CFRelease(serviceCf)
            CFRelease(accountCf)
        }
    }

    private fun createAndStoreMasterKey(): ByteArray? {
        val keyBytes = ByteArray(KEY_SIZE)
        val status = keyBytes.usePinned { pinned ->
            SecRandomCopyBytes(kSecRandomDefault, KEY_SIZE.toULong(), pinned.addressOf(0))
        }
        if (status != 0) return null

        val nsData = keyBytes.toNSData()
        val nsDataCf = CFBridgingRetain(nsData)
        val serviceCf = SERVICE_NAME.toCFString()
        val accountCf = ACCOUNT_NAME.toCFString()

        return try {
            withKeychainQuery(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to serviceCf,
                kSecAttrAccount to accountCf,
                kSecValueData to nsDataCf,
                kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlock,
            ) { query ->
                val addStatus = SecItemAdd(query, null)
                if (addStatus == errSecSuccess) {
                    keyBytes
                } else {
                    null
                }
            }
        } finally {
            CFRelease(serviceCf)
            CFRelease(accountCf)
            CFRelease(nsDataCf)
        }
    }

    private fun getOrCreateFallbackKey(): ByteArray {
        fallbackKey?.let { return it }
        val newFallback = ByteArray(KEY_SIZE)
        val status = newFallback.usePinned { pinned ->
            SecRandomCopyBytes(kSecRandomDefault, KEY_SIZE.toULong(), pinned.addressOf(0))
        }
        check(status == 0) { "Failed to generate fallback random key" }
        fallbackKey = newFallback
        return newFallback
    }

    private inline fun <T> withKeychainQuery(
        vararg pairs: Pair<COpaquePointer?, COpaquePointer?>,
        block: (CFDictionaryRef) -> T,
    ): T = memScoped {
        val count = pairs.size
        val keys = allocArray<COpaquePointerVar>(count)
        val values = allocArray<COpaquePointerVar>(count)

        for (i in 0 until count) {
            keys[i] = pairs[i].first
            values[i] = pairs[i].second
        }

        val cfDict = CFDictionaryCreate(
            allocator = null,
            keys = keys,
            values = values,
            numValues = count.toLong(),
            keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
            valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr,
        ) ?: error("Failed to create CFDictionary")

        try {
            block(cfDict)
        } finally {
            CFRelease(cfDict)
        }
    }

    private fun String.toCFString(): CFStringRef = CFStringCreateWithCString(null, this, kCFStringEncodingUTF8)
        ?: error("Failed to convert string to CFString")

    private fun encryptAesCbc(input: ByteArray, key: ByteArray): ByteArray? {
        if (key.size != KEY_SIZE) return null
        val iv = ByteArray(IV_SIZE)
        val statusIv = iv.usePinned { pinned ->
            SecRandomCopyBytes(kSecRandomDefault, IV_SIZE.toULong(), pinned.addressOf(0))
        }
        if (statusIv != 0) return null

        val bufferSize = input.size + 16
        val cipherText = ByteArray(bufferSize)

        return memScoped {
            val dataOutMoved = alloc<size_tVar>()
            val status = key.usePinned { pinnedKey ->
                iv.usePinned { pinnedIv ->
                    input.usePinned { pinnedInput ->
                        cipherText.usePinned { pinnedOutput ->
                            CCCrypt(
                                op = kCCEncrypt,
                                alg = kCCAlgorithmAES,
                                options = kCCOptionPKCS7Padding,
                                key = pinnedKey.addressOf(0),
                                keyLength = key.size.toULong(),
                                iv = pinnedIv.addressOf(0),
                                dataIn = if (input.isNotEmpty()) pinnedInput.addressOf(0) else null,
                                dataInLength = input.size.toULong(),
                                dataOut = pinnedOutput.addressOf(0),
                                dataOutAvailable = cipherText.size.toULong(),
                                dataOutMoved = dataOutMoved.ptr,
                            )
                        }
                    }
                }
            }
            if (status == kCCSuccess) {
                val actualSize = dataOutMoved.value.toInt()
                iv + cipherText.copyOf(actualSize)
            } else {
                null
            }
        }
    }

    private fun decryptAesCbc(input: ByteArray, key: ByteArray): ByteArray? {
        if (input.size <= IV_SIZE || key.size != KEY_SIZE) return null

        val iv = input.sliceArray(0 until IV_SIZE)
        val cipherText = input.sliceArray(IV_SIZE until input.size)

        val bufferSize = cipherText.size
        val plainText = ByteArray(bufferSize)

        return memScoped {
            val dataOutMoved = alloc<size_tVar>()
            val status = key.usePinned { pinnedKey ->
                iv.usePinned { pinnedIv ->
                    cipherText.usePinned { pinnedCipher ->
                        plainText.usePinned { pinnedPlain ->
                            CCCrypt(
                                op = kCCDecrypt,
                                alg = kCCAlgorithmAES,
                                options = kCCOptionPKCS7Padding,
                                key = pinnedKey.addressOf(0),
                                keyLength = key.size.toULong(),
                                iv = pinnedIv.addressOf(0),
                                dataIn = if (cipherText.isNotEmpty()) pinnedCipher.addressOf(0) else null,
                                dataInLength = cipherText.size.toULong(),
                                dataOut = pinnedPlain.addressOf(0),
                                dataOutAvailable = plainText.size.toULong(),
                                dataOutMoved = dataOutMoved.ptr,
                            )
                        }
                    }
                }
            }
            if (status == kCCSuccess) {
                val actualSize = dataOutMoved.value.toInt()
                plainText.copyOf(actualSize)
            } else {
                null
            }
        }
    }

    private fun NSData.toByteArray(): ByteArray {
        val size = length.toInt()
        if (size == 0) return ByteArray(0)
        val bytes = ByteArray(size)
        bytes.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, length)
        }
        return bytes
    }

    private fun ByteArray.toNSData(): NSData {
        if (isEmpty()) return NSData()
        return usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
        }
    }

    companion object {
        private const val SERVICE_NAME = "com.livefast.eattrash.raccoonforfriendica"
        private const val ACCOUNT_NAME = "r4f_master_key"
        private const val KEY_SIZE = 32
        private const val IV_SIZE = 16

        private var fallbackKey: ByteArray? = null
    }
}
