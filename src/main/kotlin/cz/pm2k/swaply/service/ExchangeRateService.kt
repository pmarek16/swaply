package cz.pm2k.swaply.service

import cz.pm2k.swaply.client.cnb.CnbWebService
import cz.pm2k.swaply.client.currencylayer.CurrencyLayerWebService
import cz.pm2k.swaply.client.runBlockingWithMdc
import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.require
import cz.pm2k.swaply.helper.readCsv
import cz.pm2k.swaply.mapper.toCurrencyPairs
import cz.pm2k.swaply.model.CurrencyPair
import cz.pm2k.swaply.model.ExchangeRateCsvRow
import cz.pm2k.swaply.model.ExchangeRateDiff
import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
class ExchangeRateService(
    private val cnbWebService: CnbWebService,
    private val currencyLayerWebService: CurrencyLayerWebService,
) {

    fun getCnbRates(): List<CurrencyPair> {
        // read daily exchange rates from CNB
        val csvExchangeRates = runBlockingWithMdc {
            cnbWebService.getExchangeRates()
        }
        // parse CSV
        return readCsv(
            csvContent = csvExchangeRates,
            returnType = ExchangeRateCsvRow::class.java,
            skipLines = 1,
        ).toCurrencyPairs()
    }

    fun getCurrencyLayerDiffs(currencyPairCode: String): ExchangeRateDiff {
        // read daily exchange rates from CNB
        val cnbCurrencyPairs = getCnbRates().associateBy { "${it.base}${it.quote}" }

        require(cnbCurrencyPairs.containsKey(currencyPairCode), ErrorCode.INVALID_REQUEST) {
            "Unsupported currency pair: $currencyPairCode"
        }

        // read daily exchange rates from CurrencyLayer
        val clCurrencyPairs = runBlockingWithMdc {
            currencyLayerWebService.getExchangeRates(
                currencies = cnbCurrencyPairs.values.map { it.quote },
            )
        }.toCurrencyPairs().associateBy { "${it.base}${it.quote}" }

        val cnbRate = cnbCurrencyPairs[currencyPairCode]!!.rate
        val clRate = clCurrencyPairs[currencyPairCode]!!.rate
        return ExchangeRateDiff(
            pairCode = currencyPairCode,
            rateDiff = (clRate - cnbRate).round(6),
        )
    }

}

/**
 * Rounds the number to the specified number of decimal places.
 *
 * @param decimals number of decimal places
 * @param roundingMode rounding mode (default is HALF_UP)
 * @return rounded number
 */
fun Double.round(decimals: Int, roundingMode: RoundingMode = RoundingMode.HALF_UP) = toBigDecimal().setScale(decimals, roundingMode).toDouble()
