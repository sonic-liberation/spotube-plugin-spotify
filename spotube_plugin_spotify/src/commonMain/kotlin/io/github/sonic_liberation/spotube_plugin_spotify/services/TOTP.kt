/*
 * Copyright (C) 2026 Sonic Liberation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.sonic_liberation.spotube_plugin_spotify.services

import com.osmerion.kotlin.io.encoding.Base32
import dev.krtirtho.plugin_interfaces.host_apis.CryptoAPI
import dev.krtirtho.plugin_interfaces.host_apis.HashAlgorithms
import dev.krtirtho.plugin_interfaces.host_apis.HMACEncodingFormats
import dev.krtirtho.plugin_interfaces.host_apis.MACSignatureAlgorithms
import kotlin.time.Clock


enum class TOTPAlgorithm {
    SHA1,
    SHA256,
    SHA512
}

enum class TOTPEncoding {
    ASCII,
    BASE32
}

data class TOTPOptions(
    val digits: Int = 6,
    val timestamp: Long? = null,
    val algorithm: TOTPAlgorithm = TOTPAlgorithm.SHA1,
    val encoding: TOTPEncoding = TOTPEncoding.BASE32,
    val explicitZeroPad: Boolean = false,
    val period: Int = 30
)

data class TOTPResult(
    val otp: String,
    val expires: Long
)

class TOTP(val cryptoAPI: CryptoAPI) {
    suspend fun generate(key: String, options: TOTPOptions = TOTPOptions()): TOTPResult {
        val timestamp = options.timestamp ?: Clock.System.now().epochSeconds
        val period = options.period

        val counter = timestamp / period
        val expiresAt = (counter + 1) * period

        val keyBytes = when (options.encoding) {
            TOTPEncoding.BASE32 -> Base32.decode(key)
            TOTPEncoding.ASCII -> key.encodeToByteArray()
        }

        val counterBytes = counter.toByteArray(8)


        val signature = cryptoAPI.signWithMACKey(
            algorithm = MACSignatureAlgorithms.HMAC(
                hashAlgorithm = options.algorithm.toHashAlgorithm(),
                format = HMACEncodingFormats.RAW
            ),
            key = keyBytes,
            data = counterBytes
        )

        val offset = (signature[signature.size - 1].toInt() and 0xF)
        val binary = (
                ((signature[offset].toInt() and 0x7F) shl 24) or
                        ((signature[offset + 1].toInt() and 0xFF) shl 16) or
                        ((signature[offset + 2].toInt() and 0xFF) shl 8) or
                        (signature[offset + 3].toInt() and 0xFF)
                )

        val divisor = when (options.digits) {
            6 -> 1000000
            8 -> 100000000
            else -> {
                var d = 1
                repeat(options.digits) { d *= 10 }
                d
            }
        }
        val otp = binary % divisor

        val otpStr = otp.toString().padStart(options.digits, '0')

        return TOTPResult(otpStr, expiresAt)
    }

    suspend fun validate(
        key: String,
        token: String,
        options: TOTPOptions = TOTPOptions(),
        window: Int = 1
    ): Boolean {
        val timestamp = options.timestamp ?: Clock.System.now().epochSeconds
        val period = options.period
        val currentCounter = timestamp / period

        for (i in -window..window) {
            val counter = currentCounter + i
            val windowOptions = options.copy(timestamp = counter * period)

            try {
                val result = generate(key, windowOptions)
                if (result.otp == token) {
                    return true
                }
            } catch (e: Exception) {
                continue
            }
        }

        return false
    }

    fun getRemainingSeconds(options: TOTPOptions = TOTPOptions()): Int {
        val timestamp = options.timestamp ?: Clock.System.now().epochSeconds
        val period = options.period
        val counter = timestamp / period
        val expiresAt = (counter + 1) * period
        return (expiresAt - timestamp).toInt()
    }

    private fun Long.toByteArray(size: Int): ByteArray {
        val result = ByteArray(size)
        var value = this
        for (i in size - 1 downTo 0) {
            result[i] = value.toInt().and(0xFF).toByte()
            value = value.shr(8)
        }
        return result
    }

    private fun TOTPAlgorithm.toHashAlgorithm(): HashAlgorithms = when (this) {
        TOTPAlgorithm.SHA1 -> HashAlgorithms.SHA1
        TOTPAlgorithm.SHA256 -> HashAlgorithms.SHA256
        TOTPAlgorithm.SHA512 -> HashAlgorithms.SHA512
    }
}
