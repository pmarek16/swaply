package cz.pm2k.swaply.client.currencylayer

import java.io.Serializable

data class CurrencyLayerLiveResponse(
    val success: Boolean,
    val timestamp: Long? = null,
    val source: String? = null,
    val quotes: Map<String, Double>? = null,
    val error: CurrencyLayerError? = null
): Serializable

data class CurrencyLayerError(
    val code: Int,
    val info: String? = null,
): Serializable
