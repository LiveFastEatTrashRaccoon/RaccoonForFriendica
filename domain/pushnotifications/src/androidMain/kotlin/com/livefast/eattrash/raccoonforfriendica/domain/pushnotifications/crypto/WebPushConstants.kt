package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.crypto

/**
 * Constants defined in:
 * - RFC 8291: Message Encryption for Web Push
 * - RFC 8188: Encrypted Content-Encoding for HTTP (`aes128gcm`)
 * - RFC 5869: HMAC-based Extract-and-Expand Key Derivation Function (HKDF)
 * - SEC 1: Elliptic Curve Cryptography (secp256r1 / NIST P-256)
 */
internal object WebPushConstants {
    // RFC 8291 & RFC 8188 header and payload sizes (in bytes)
    const val SALT_LENGTH = 16
    const val RECORD_SIZE_LENGTH = 4
    const val KEY_ID_LEN_LENGTH = 1
    const val SENDER_PUBLIC_KEY_LENGTH = 65
    const val AUTH_TAG_LENGTH = 16
    const val GCM_TAG_LENGTH_BITS = 128
    const val HEADER_LENGTH =
        SALT_LENGTH + RECORD_SIZE_LENGTH + KEY_ID_LEN_LENGTH + SENDER_PUBLIC_KEY_LENGTH
    const val MIN_CIPHERTEXT_LENGTH = AUTH_TAG_LENGTH
    const val MIN_PAYLOAD_LENGTH = HEADER_LENGTH + MIN_CIPHERTEXT_LENGTH

    // Elliptic curve & key material
    const val EC_ALGORITHM = "EC"
    const val EC_CURVE_NAME = "secp256r1"
    const val EC_UNCOMPRESSED_KEY_PREFIX: Byte = 0x04
    const val EC_COORDINATE_LENGTH = 32
    const val PRIVATE_KEY_LENGTH = 32
    const val AUTH_SECRET_LENGTH = 16

    // RFC 8188 record delimiter and padding
    const val RECORD_DELIMITER_FINAL: Byte = 0x02
    const val RECORD_PADDING_ZERO: Byte = 0x00

    // Cryptographic algorithms (JCA)
    const val KEY_AGREEMENT_ALGORITHM = "ECDH"
    const val HMAC_ALGORITHM = "HmacSHA256"
    const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
    const val AES_ALGORITHM = "AES"

    // HKDF (RFC 5869)
    const val HKDF_IKM_LENGTH = 32
    const val AES_KEY_LENGTH = 16
    const val AES_NONCE_LENGTH = 12
    const val HKDF_INITIAL_COUNTER: Byte = 0x01

    // HKDF info labels (RFC 8291 Section 3.2 & 3.3)
    val INFO_WEB_PUSH: ByteArray = "WebPush: info\u0000".toByteArray(Charsets.US_ASCII)
    val INFO_CEK: ByteArray = "Content-Encoding: aes128gcm\u0000".toByteArray(Charsets.US_ASCII)
    val INFO_NONCE: ByteArray = "Content-Encoding: nonce\u0000".toByteArray(Charsets.US_ASCII)
}
