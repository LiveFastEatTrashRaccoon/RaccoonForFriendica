package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.utils

import android.util.Base64
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security

/*
 * Inspired from:
 *
 * https://github.com/tuskyapp/Tusky/blob/f8cf38c81b2a0dfa613daca09dfe4c67b430ce1e/app/src/main/java/com/keylesspalace/tusky/util/CryptoUtil.kt
 */
internal object CryptoUtil {
    private const val CURVE_PRIME256_V1 = "prime256v1"
    private const val SHA1_PRNG = "SHA1PRNG"
    private const val ECDH = "EC"
    private const val BASE64_FLAGS = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP

    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }

    private fun secureRandomBytes(len: Int): ByteArray {
        val ret = ByteArray(len)
        SecureRandom.getInstance(SHA1_PRNG).nextBytes(ret)
        return ret
    }

    fun secureRandomBytesEncoded(length: Int): String = Base64.encodeToString(secureRandomBytes(length), BASE64_FLAGS)

    fun generateECKeyPair(): EncodedKeyPair {
        val spec = ECNamedCurveTable.getParameterSpec(CURVE_PRIME256_V1)
        val gen = KeyPairGenerator.getInstance(ECDH, BouncyCastleProvider.PROVIDER_NAME)
        gen.initialize(spec)
        val keyPair = gen.genKeyPair()
        val pubKey = keyPair.public as ECPublicKey
        val privKey = keyPair.private as ECPrivateKey
        val encodedPubKey = Base64.encodeToString(pubKey.q.getEncoded(false), BASE64_FLAGS)
        val encodedPrivKey = Base64.encodeToString(privKey.d.toByteArray(), BASE64_FLAGS)
        return EncodedKeyPair(encodedPubKey, encodedPrivKey)
    }
}
