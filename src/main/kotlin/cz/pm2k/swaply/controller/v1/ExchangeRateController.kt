package cz.pm2k.swaply.controller.v1

import cz.pm2k.swaply.controller.V1_BASE_URL
import cz.pm2k.swaply.dto.v1.CurrencyPairsResponse
import cz.pm2k.swaply.dto.v1.ExchangeRateDifferencesResponse
import cz.pm2k.swaply.mapper.toDto
import cz.pm2k.swaply.service.ExchangeRateService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["${V1_BASE_URL}/exchange-rate"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class ExchangeRateController(
    private val exchangeRateService: ExchangeRateService,
): IExchangeRateController {

    @GetMapping("/currency-pairs")
    override fun getCurrencyPairs(): CurrencyPairsResponse {
        return exchangeRateService.getCnbRates().toDto()
    }

    @GetMapping("/differences/{currencyPairCode}")
    override fun getDifferences(
        @PathVariable
        currencyPairCode: String,
    ): ExchangeRateDifferencesResponse {
        return exchangeRateService.getCurrencyLayerDiffs(currencyPairCode).toDto()
    }
}