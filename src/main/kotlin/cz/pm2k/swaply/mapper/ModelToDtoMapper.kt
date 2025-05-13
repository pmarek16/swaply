package cz.pm2k.swaply.mapper

import cz.pm2k.swaply.dto.v1.CurrencyPairsResponse
import cz.pm2k.swaply.dto.v1.ExchangeRateDifferencesResponse
import cz.pm2k.swaply.model.CurrencyPair
import cz.pm2k.swaply.model.ExchangeRateDiff
import java.text.DecimalFormatSymbols
import java.util.Locale

val diffFormatter = java.text.DecimalFormat("#.######", DecimalFormatSymbols.getInstance(Locale.US))

fun List<CurrencyPair>.toDto() = CurrencyPairsResponse(
    supportedPairs = this.map { it.code }
)

fun ExchangeRateDiff.toDto() = ExchangeRateDifferencesResponse(
    pair = this.pairCode,
    rateDifference = diffFormatter.format(this.rateDiff)
)
