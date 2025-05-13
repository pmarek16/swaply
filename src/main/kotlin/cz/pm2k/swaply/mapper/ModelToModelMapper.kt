package cz.pm2k.swaply.mapper

import cz.pm2k.swaply.client.currencylayer.BASE_CURRENCY
import cz.pm2k.swaply.client.currencylayer.CurrencyLayerLiveResponse
import cz.pm2k.swaply.model.CurrencyPair
import cz.pm2k.swaply.model.ExchangeRateCsvRow

fun List<ExchangeRateCsvRow>.toCurrencyPairs() = map { CurrencyPair(
    base = BASE_CURRENCY,
    quote = it.code,
    rate = 1 / it.rate * it.amount,
) }

fun CurrencyLayerLiveResponse.toCurrencyPairs() = quotes?.map { (key, value) ->
    CurrencyPair(
        base = key.substring(0, 3),
        quote = key.substring(3),
        rate = value,
    )
} ?: emptyList()
