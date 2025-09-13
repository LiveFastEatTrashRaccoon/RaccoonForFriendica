@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.livefast.eattrash.raccoonforfriendica.core.encryption

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.NSBundle
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

internal class DefaultSecureKeyStorage : SecureKeyStorage {

    override fun storeKey(alias: String, key: ByteArray) {
        // delete any existing key first
        deleteKey(alias)

        val keyData = key.toCFData()

        // create query dictionary for storing the key
        val query = CFDictionaryCreateMutable(
            allocator = null,
            capacity = 0,
            keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
            valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr,
        )

        // set key class to generic password
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)

        // set service (app identifier) and account (alias)
        val service = NSBundle.mainBundle.bundleIdentifier ?: "DefaultService"
        CFDictionarySetValue(theDict = query, key = kSecAttrService, value = service.toCFString())
        CFDictionarySetValue(theDict = query, key = kSecAttrAccount, value = alias.toCFString())

        // set the key data
        CFDictionarySetValue(theDict = query, key = kSecValueData, value = keyData)

        // set accessibility (available when device is unlocked)
        CFDictionarySetValue(
            theDict = query,
            key = kSecAttrAccessible,
            value = kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
        )

        // add to keychain
        val status = SecItemAdd(attributes = query, result = null)

        CFRelease(query)
        CFRelease(keyData)

        if (status != errSecSuccess) {
            throw Exception("Failed to store key in keychain. Status: $status")
        }
    }

    override fun getKey(alias: String): ByteArray? {
        // create query dictionary for retrieving the key
        val query = CFDictionaryCreateMutable(
            allocator = null,
            capacity = 0,
            keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
            valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr,
        )

        // set key class to generic password
        CFDictionarySetValue(theDict = query, key = kSecClass, value = kSecClassGenericPassword)

        // set service (app identifier) and account (alias)
        val service = NSBundle.mainBundle.bundleIdentifier ?: "DefaultService"
        CFDictionarySetValue(theDict = query, key = kSecAttrService, value = service.toCFString())
        CFDictionarySetValue(theDict = query, key = kSecAttrAccount, value = alias.toCFString())

        // request return of data
        CFDictionarySetValue(theDict = query, key = kSecReturnData, value = kCFBooleanTrue)

        // limit to one result
        CFDictionarySetValue(theDict = query, key = kSecMatchLimit, value = kSecMatchLimitOne)

        memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query, result.ptr)

            CFRelease(query)

            return when (status) {
                errSecSuccess -> {
                    @Suppress("UNCHECKED_CAST")
                    val data = result.value as CFDataRef
                    data.toByteArray()
                }

                errSecItemNotFound -> null
                else -> throw Exception("Failed to retrieve key from keychain. Status: $status")
            }
        }
    }

    private fun deleteKey(alias: String) {
        val query = CFDictionaryCreateMutable(
            allocator = null,
            capacity = 0,
            keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
            valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr,
        )

        CFDictionarySetValue(theDict = query, key = kSecClass, value = kSecClassGenericPassword)

        val service = NSBundle.mainBundle.bundleIdentifier ?: "DefaultService"
        CFDictionarySetValue(theDict = query, key = kSecAttrService, value = service.toCFString())
        CFDictionarySetValue(theDict = query, key = kSecAttrAccount, value = alias.toCFString())

        SecItemDelete(query)
        CFRelease(query)
    }
}

private fun String.toCFString(): CFStringRef =
    CFStringCreateWithCString(alloc = null, cStr = this, encoding = kCFStringEncodingUTF8)!!

private fun ByteArray.toCFData(): CFDataRef = usePinned { pinned ->
    CFDataCreate(allocator = null, bytes = pinned.addressOf(0).reinterpret(), length = this.size.toLong())!!
}

private fun CFDataRef.toByteArray(): ByteArray {
    val length = CFDataGetLength(this).toInt()
    val bytes = CFDataGetBytePtr(this)
    return ByteArray(length) { index -> bytes!![index].toByte() }
}
