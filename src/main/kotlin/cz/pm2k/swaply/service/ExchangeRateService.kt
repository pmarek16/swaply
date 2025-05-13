package cz.pm2k.swaply.service

import cz.pm2k.swaply.client.cnb.CnbWebService
import cz.pm2k.swaply.client.runBlockingWithMdc
import cz.pm2k.swaply.helper.readCsv
import cz.pm2k.swaply.model.ExchangeRateCsvRow
import org.springframework.stereotype.Service

@Service
class ExchangeRateService(
    private val cnbWebService: CnbWebService,
) {

    fun getCnbRates(): List<ExchangeRateCsvRow> {
        // read daily exchange rates from CNB
        val csvExchangeRates = runBlockingWithMdc {
            cnbWebService.getExchangeRates()
        }
        // parse CSV
        return readCsv(
            csvContent = csvExchangeRates,
            returnType = ExchangeRateCsvRow::class.java,
            skipLines = 1,
        )
    }

    fun getCnbCurrencyPairs(): List<String> {
        return getCnbRates().map { row ->
            "CZK${row.code}"
        }
    }

}