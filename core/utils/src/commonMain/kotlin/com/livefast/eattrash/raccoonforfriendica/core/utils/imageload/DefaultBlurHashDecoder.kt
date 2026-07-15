package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.withSign

private const val MAX_CACHE_SIZE = 10

/*
 * ORIGINAL SOURCE:
 * https://github.com/woltapp/blurhash/blob/master/Kotlin/lib/src/main/java/com/wolt/blurhashkt/BlurHashDecoder.kt
 *
 * The original code has been modified here to work with Compose (multiplatform) ImageBitmap
 * and Color in order to be used in common source set and shared across platforms.
 */
internal class DefaultBlurHashDecoder(
    private val cacheCosinesX: LruCache<Int, DoubleArray> = LruCache.factory(MAX_CACHE_SIZE),
    private val cacheCosinesY: LruCache<Int, DoubleArray> = LruCache.factory(MAX_CACHE_SIZE),
) : BlurHashDecoder {
    override suspend fun clearCache() {
        cacheCosinesX.clear()
        cacheCosinesY.clear()
    }

    override suspend fun decode(
        blurHash: String?,
        width: Int,
        height: Int,
        punch: Float,
        useCache: Boolean,
    ): ImageBitmap? = withContext(Dispatchers.IO) {
        if (blurHash == null || blurHash.length < 6) return@withContext null
        val numCompEnc = decode83(blurHash, 0, 1)
        val numCompX = (numCompEnc % 9) + 1
        val numCompY = (numCompEnc / 9) + 1
        if (blurHash.length != 4 + 2 * numCompX * numCompY) return@withContext null
        val maxAcEnc = decode83(blurHash, 1, 2)
        val maxAc = (maxAcEnc + 1) / 166f
        val colors =
            Array(numCompX * numCompY) { i ->
                if (i == 0) {
                    val colorEnc = decode83(blurHash, 2, 6)
                    decodeDc(colorEnc)
                } else {
                    val from = 4 + i * 2
                    val colorEnc = decode83(blurHash, from, from + 2)
                    decodeAc(colorEnc, maxAc * punch)
                }
            }
        try {
            composeBitmap(width, height, numCompX, numCompY, colors, useCache)
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            null
        }
    }

    private fun decode83(str: String, from: Int = 0, to: Int = str.length): Int {
        var result = 0
        for (i in from until to) {
            val index = charMap[str[i]] ?: -1
            if (index != -1) {
                result = result * 83 + index
            }
        }
        return result
    }

    private fun decodeDc(colorEnc: Int): FloatArray {
        val r = colorEnc shr 16
        val g = (colorEnc shr 8) and 255
        val b = colorEnc and 255
        return floatArrayOf(srgbToLinear(r), srgbToLinear(g), srgbToLinear(b))
    }

    private fun srgbToLinear(colorEnc: Int): Float {
        val v = colorEnc / 255f
        return if (v <= 0.04045f) {
            (v / 12.92f)
        } else {
            ((v + 0.055f) / 1.055f).pow(2.4f)
        }
    }

    private fun decodeAc(value: Int, maxAc: Float): FloatArray {
        val r = value / (19 * 19)
        val g = (value / 19) % 19
        val b = value % 19
        return floatArrayOf(
            signedPow2((r - 9) / 9.0f) * maxAc,
            signedPow2((g - 9) / 9.0f) * maxAc,
            signedPow2((b - 9) / 9.0f) * maxAc,
        )
    }

    private fun signedPow2(value: Float) = value.pow(2f).withSign(value)

    private suspend fun composeBitmap(
        width: Int,
        height: Int,
        numCompX: Int,
        numCompY: Int,
        colors: Array<FloatArray>,
        useCache: Boolean,
    ): ImageBitmap {
        // use an array for better performance when writing pixel colors
        val imageArray = IntArray(width * height)
        val cosinesX = getArrayForCosinesX(useCache, width, numCompX)
        val cosinesY = getArrayForCosinesY(useCache, height, numCompY)
        for (y in 0 until height) {
            yield()
            for (x in 0 until width) {
                var r = 0f
                var g = 0f
                var b = 0f
                for (j in 0 until numCompY) {
                    for (i in 0 until numCompX) {
                        val cosX = cosinesX[i + numCompX * x]
                        val cosY = cosinesY[j + numCompY * y]
                        val basis = (cosX * cosY).toFloat()
                        val color = colors[j * numCompX + i]
                        r += color[0] * basis
                        g += color[1] * basis
                        b += color[2] * basis
                    }
                }
                imageArray[x + width * y] =
                    Color(linearToSrgb(r), linearToSrgb(g), linearToSrgb(b)).toArgb()
            }
        }

        return imageArray.toComposeImageBitmap(width, height)
    }

    private suspend fun getArrayForCosinesY(useCache: Boolean, height: Int, numCompY: Int): DoubleArray {
        val key = height * numCompY
        val cached = if (useCache) cacheCosinesY.get(key) else null
        if (cached != null) return cached

        val result = DoubleArray(key)
        for (y in 0 until height) {
            for (j in 0 until numCompY) {
                result[j + numCompY * y] = cos(PI * y * j / height)
            }
        }
        if (useCache) cacheCosinesY.put(key, result)
        return result
    }

    private suspend fun getArrayForCosinesX(useCache: Boolean, width: Int, numCompX: Int): DoubleArray {
        val key = width * numCompX
        val cached = if (useCache) cacheCosinesX.get(key) else null
        if (cached != null) return cached

        val result = DoubleArray(key)
        for (x in 0 until width) {
            for (i in 0 until numCompX) {
                result[i + numCompX * x] = cos(PI * x * i / width)
            }
        }
        if (useCache) cacheCosinesX.put(key, result)
        return result
    }

    private fun linearToSrgb(value: Float): Int {
        val v = value.coerceIn(0f, 1f)
        return if (v <= 0.0031308f) {
            (v * 12.92f * 255f + 0.5f).toInt()
        } else {
            ((1.055f * v.pow(1 / 2.4f) - 0.055f) * 255 + 0.5f).toInt()
        }
    }

    private val charMap =
        listOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'U',
            'V',
            'W',
            'X',
            'Y',
            'Z',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'q',
            'r',
            's',
            't',
            'u',
            'v',
            'w',
            'x',
            'y',
            'z',
            '#',
            '$',
            '%',
            '*',
            '+',
            ',',
            '-',
            '.',
            ':',
            ';',
            '=',
            '?',
            '@',
            '[',
            ']',
            '^',
            '_',
            '{',
            '|',
            '}',
            '~',
        ).mapIndexed { i, c -> c to i }.toMap()
}
