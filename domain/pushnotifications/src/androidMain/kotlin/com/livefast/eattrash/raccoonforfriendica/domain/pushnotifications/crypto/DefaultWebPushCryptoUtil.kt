package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.crypto

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPrivateKeySpec
import java.security.spec.ECPublicKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.encoding.Base64

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultWebPushCryptoUtil : WebPushCryptoUtil {

    override fun generateKeys(): WebPushCryptoUtil.PushKeys {
        // 1. generate P-256 EC KeyPair
        val keyGenerator = KeyPairGenerator.getInstance(WebPushConstants.EC_ALGORITHM)
        keyGenerator.initialize(ECGenParameterSpec(WebPushConstants.EC_CURVE_NAME))
        val keyPair = keyGenerator.generateKeyPair()

        // 2. extract raw uncompressed point for the public key (65 bytes)
        // WebPush requires the public key to be in uncompressed point format: 0x04 || X (32B) || Y (32B)
        val ecPublicKey = keyPair.public as ECPublicKey
        val w = ecPublicKey.w
        val pubKeyBytes = ByteArray(WebPushConstants.SENDER_PUBLIC_KEY_LENGTH)
        pubKeyBytes[0] = WebPushConstants.EC_UNCOMPRESSED_KEY_PREFIX

        // handle BigInteger leading zero byte or short arrays to ensure exactly 32 bytes each
        val affineX = w.affineX.toByteArray()
        val affineY = w.affineY.toByteArray()
        copyBigIntegerToByteArray(
            bigIntBytes = affineX,
            destination = pubKeyBytes,
            destOffset = 1,
            length = WebPushConstants.EC_COORDINATE_LENGTH,
        )
        copyBigIntegerToByteArray(
            bigIntBytes = affineY,
            destination = pubKeyBytes,
            destOffset = 1 + WebPushConstants.EC_COORDINATE_LENGTH,
            length = WebPushConstants.EC_COORDINATE_LENGTH,
        )

        // 3. extract the private key scalar (32 bytes)
        val ecPrivateKey = keyPair.private as ECPrivateKey
        val privKeyBigInt = ecPrivateKey.s.toByteArray()
        val privKeyBytes = ByteArray(WebPushConstants.PRIVATE_KEY_LENGTH)
        copyBigIntegerToByteArray(
            bigIntBytes = privKeyBigInt,
            destination = privKeyBytes,
            destOffset = 0,
            length = WebPushConstants.PRIVATE_KEY_LENGTH,
        )

        // 4. generate 16-byte random auth secret
        val random = SecureRandom()
        val authSecretBytes = ByteArray(WebPushConstants.AUTH_SECRET_LENGTH)
        random.nextBytes(authSecretBytes)

        // 5. encode all to Base64URL without padding
        return WebPushCryptoUtil.PushKeys(
            publicKey = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(pubKeyBytes),
            privateKey = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(privKeyBytes),
            authSecret = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(authSecretBytes),
        )
    }

    override fun decryptPayload(
        encryptedPayload: ByteArray,
        publicKey: String,
        privateKey: String,
        authSecret: String,
    ): String? {
        try {
            // 1. parse the RFC 8291 payload header
            val payload = WebPushPayloadParser.parse(encryptedPayload) ?: return null

            // 2. decode the receiver's stored material
            val receiverPubKeyBytes = Base64.UrlSafe.withPadding(
                Base64.PaddingOption.ABSENT_OPTIONAL,
            ).decode(publicKey)
            val receiverPrivKeyBytes = Base64.UrlSafe.withPadding(
                Base64.PaddingOption.ABSENT_OPTIONAL,
            ).decode(privateKey)
            val authSecretBytes = Base64.UrlSafe.withPadding(
                Base64.PaddingOption.ABSENT_OPTIONAL,
            ).decode(authSecret)

            // 3. reconstruct JCA keys
            val params = getECParameterSpec()
            val receiverPrivateKey = decodePrivateKey(receiverPrivKeyBytes, params)
            val senderPublicKey = decodePublicKey(payload.senderPublicKey, params)

            // 4. ECDH key agreement
            val keyAgreement = KeyAgreement.getInstance(WebPushConstants.KEY_AGREEMENT_ALGORITHM)
            keyAgreement.init(receiverPrivateKey)
            keyAgreement.doPhase(senderPublicKey, true)
            val sharedSecret = keyAgreement.generateSecret()

            // 5. derive keys using HKDF-SHA256 (RFC 8291 Section 3.2 & 3.3)
            val webPushInfo = WebPushConstants.INFO_WEB_PUSH + receiverPubKeyBytes + payload.senderPublicKey
            val ikm = hkdf(
                salt = authSecretBytes,
                ikm = sharedSecret,
                info = webPushInfo,
                length = WebPushConstants.HKDF_IKM_LENGTH,
            )

            val cek = hkdf(
                salt = payload.salt,
                ikm = ikm,
                info = WebPushConstants.INFO_CEK,
                length = WebPushConstants.AES_KEY_LENGTH,
            )

            val nonce = hkdf(
                salt = payload.salt,
                ikm = ikm,
                info = WebPushConstants.INFO_NONCE,
                length = WebPushConstants.AES_NONCE_LENGTH,
            )

            // 6. decrypt using AES/GCM/NoPadding
            val cipher = Cipher.getInstance(WebPushConstants.CIPHER_ALGORITHM)
            cipher.init(
                Cipher.DECRYPT_MODE,
                SecretKeySpec(cek, WebPushConstants.AES_ALGORITHM),
                GCMParameterSpec(WebPushConstants.GCM_TAG_LENGTH_BITS, nonce),
            )
            val decryptedBytes = cipher.doFinal(payload.ciphertext)

            // 7. remove RFC 8188 record padding and extract plaintext
            val plaintextBytes = removePadding(decryptedBytes) ?: return null
            return String(plaintextBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return null
        }
    }

    /**
     * Strips RFC 8188 record padding.
     * The plaintext is terminated by a 0x02 delimiter byte followed by zero or more 0x00 padding bytes.
     */
    private fun removePadding(decryptedBytes: ByteArray): ByteArray? {
        var paddingIndex = decryptedBytes.size - 1
        while (paddingIndex >= 0 && decryptedBytes[paddingIndex] == WebPushConstants.RECORD_PADDING_ZERO) {
            paddingIndex--
        }
        if (paddingIndex < 0 || decryptedBytes[paddingIndex] != WebPushConstants.RECORD_DELIMITER_FINAL) {
            return null
        }
        return decryptedBytes.copyOfRange(0, paddingIndex)
    }

    // region HKDF (RFC 5869)
    private fun hkdf(salt: ByteArray, ikm: ByteArray, info: ByteArray, length: Int): ByteArray {
        val prk = hkdfExtract(salt, ikm)
        return hkdfExpand(prk, info, length)
    }

    private fun hkdfExtract(salt: ByteArray, ikm: ByteArray): ByteArray {
        val mac = Mac.getInstance(WebPushConstants.HMAC_ALGORITHM)
        val safeSalt = if (salt.isNotEmpty()) salt else ByteArray(WebPushConstants.HKDF_IKM_LENGTH)
        mac.init(SecretKeySpec(safeSalt, WebPushConstants.HMAC_ALGORITHM))
        return mac.doFinal(ikm)
    }

    private fun hkdfExpand(prk: ByteArray, info: ByteArray, length: Int): ByteArray {
        val mac = Mac.getInstance(WebPushConstants.HMAC_ALGORITHM)
        mac.init(SecretKeySpec(prk, WebPushConstants.HMAC_ALGORITHM))
        mac.update(info)
        mac.update(WebPushConstants.HKDF_INITIAL_COUNTER)
        return mac.doFinal().copyOf(length)
    }
    // endregion

    // region JCA Crypto helpers
    private fun getECParameterSpec(): ECParameterSpec {
        val kpg = KeyPairGenerator.getInstance(WebPushConstants.EC_ALGORITHM)
        kpg.initialize(ECGenParameterSpec(WebPushConstants.EC_CURVE_NAME))
        return (kpg.generateKeyPair().public as ECPublicKey).params
    }

    private fun decodePublicKey(publicKeyBytes: ByteArray, params: ECParameterSpec): PublicKey {
        require(
            publicKeyBytes.size == WebPushConstants.SENDER_PUBLIC_KEY_LENGTH &&
                publicKeyBytes[0] == WebPushConstants.EC_UNCOMPRESSED_KEY_PREFIX,
        ) {
            "Invalid uncompressed point"
        }
        val x = BigInteger(1, publicKeyBytes.copyOfRange(1, 1 + WebPushConstants.EC_COORDINATE_LENGTH))
        val y = BigInteger(
            1,
            publicKeyBytes.copyOfRange(
                1 + WebPushConstants.EC_COORDINATE_LENGTH,
                WebPushConstants.SENDER_PUBLIC_KEY_LENGTH,
            ),
        )
        val spec = ECPublicKeySpec(ECPoint(x, y), params)
        return KeyFactory.getInstance(WebPushConstants.EC_ALGORITHM).generatePublic(spec)
    }

    private fun decodePrivateKey(privateKeyBytes: ByteArray, params: ECParameterSpec): PrivateKey {
        val s = BigInteger(1, privateKeyBytes)
        val spec = ECPrivateKeySpec(s, params)
        return KeyFactory.getInstance(WebPushConstants.EC_ALGORITHM).generatePrivate(spec)
    }
    // endregion

    // region ByteArray copy helpers
    private fun copyBigIntegerToByteArray(
        bigIntBytes: ByteArray,
        destination: ByteArray,
        destOffset: Int,
        length: Int = WebPushConstants.EC_COORDINATE_LENGTH,
    ) {
        if (bigIntBytes.size == length + 1 && bigIntBytes[0] == 0.toByte()) {
            // strip leading zero byte
            System.arraycopy(bigIntBytes, 1, destination, destOffset, length)
        } else if (bigIntBytes.size <= length) {
            // pad with leading zeros if too short
            val padding = length - bigIntBytes.size
            System.arraycopy(bigIntBytes, 0, destination, destOffset + padding, bigIntBytes.size)
        } else {
            throw IllegalArgumentException("BigInteger byte array is too large")
        }
    }
    // endregion
}
