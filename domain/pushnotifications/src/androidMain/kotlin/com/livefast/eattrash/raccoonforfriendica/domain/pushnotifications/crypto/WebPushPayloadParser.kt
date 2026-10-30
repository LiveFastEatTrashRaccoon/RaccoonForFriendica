package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.crypto

/**
 * Representation of an RFC 8188 / RFC 8291 `aes128gcm` parsed payload.
 *
 * Wire format:
 * +----------------+-------------------+---------------+-----------------------+---------------------+
 * | salt (16)      | record size (4)   | idlen (1)     | keyid (65)            | ciphertext (>=16)   |
 * +----------------+-------------------+---------------+-----------------------+---------------------+
 */
internal data class WebPushPayload(
    val salt: ByteArray,
    val recordSize: Long,
    val senderPublicKey: ByteArray,
    val ciphertext: ByteArray,
) {
    override fun equals(other: Any?): Boolean = (this === other) || (
        (other is WebPushPayload) &&
            salt.contentEquals(other.salt) &&
            (recordSize == other.recordSize) &&
            senderPublicKey.contentEquals(other.senderPublicKey) &&
            ciphertext.contentEquals(other.ciphertext)
        )

    override fun hashCode(): Int {
        var result = salt.contentHashCode()
        result = (31 * result) + recordSize.hashCode()
        result = (31 * result) + senderPublicKey.contentHashCode()
        result = (31 * result) + ciphertext.contentHashCode()
        return result
    }
}

internal object WebPushPayloadParser {
    /**
     * Parses the raw encrypted payload into a structured [WebPushPayload].
     *
     * @param encryptedPayload raw bytes received from the push service
     * @return [WebPushPayload] if the header and payload are valid according to RFC 8291, or null
     */
    fun parse(encryptedPayload: ByteArray): WebPushPayload? {
        if (encryptedPayload.size < WebPushConstants.MIN_PAYLOAD_LENGTH) {
            return null
        }

        var offset = 0
        val salt = encryptedPayload.copyOfRange(0, WebPushConstants.SALT_LENGTH)
        offset += WebPushConstants.SALT_LENGTH

        val recordSize = (
            (encryptedPayload[offset].toLong() and 0xFF shl 24) or
                (encryptedPayload[offset + 1].toLong() and 0xFF shl 16) or
                (encryptedPayload[offset + 2].toLong() and 0xFF shl 8) or
                (encryptedPayload[offset + 3].toLong() and 0xFF)
            )
        offset += WebPushConstants.RECORD_SIZE_LENGTH

        val idlen = encryptedPayload[offset].toInt() and 0xFF
        offset += WebPushConstants.KEY_ID_LEN_LENGTH
        if (idlen != WebPushConstants.SENDER_PUBLIC_KEY_LENGTH) {
            return null
        }

        val senderPubKey = encryptedPayload.copyOfRange(
            offset,
            offset + WebPushConstants.SENDER_PUBLIC_KEY_LENGTH,
        )
        if (senderPubKey.isEmpty() || (senderPubKey[0] != WebPushConstants.EC_UNCOMPRESSED_KEY_PREFIX)) {
            return null
        }
        offset += WebPushConstants.SENDER_PUBLIC_KEY_LENGTH

        val ciphertext = encryptedPayload.copyOfRange(offset, encryptedPayload.size)

        return WebPushPayload(
            salt = salt,
            recordSize = recordSize,
            senderPublicKey = senderPubKey,
            ciphertext = ciphertext,
        )
    }
}
