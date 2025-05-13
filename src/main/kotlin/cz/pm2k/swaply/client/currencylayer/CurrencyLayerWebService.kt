package cz.pm2k.swaply.client.currencylayer

import cz.pm2k.swaply.client.WebClientRetry
import cz.pm2k.swaply.config.CacheConfig.Companion.CACHE_CURRENCY_LAYER_EXCHANGE_RATES
import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.SwaplyException
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate

const val BASE_CURRENCY = "CZK"

@Service
class CurrencyLayerWebService(
    @Qualifier("currencyLayerWebClient")
    private val webClient: WebClient,

    @Value("\${swaply.client.currencylayer.access-key}")
    private val accessKey: String,
) {

    companion object {
        private const val URL_EXCHANGE_RATE = "/live"
        private const val JSON_FORMAT = 1
    }

    @Cacheable(cacheNames = [CACHE_CURRENCY_LAYER_EXCHANGE_RATES], key = "#date")
    suspend fun getExchangeRates(
        currencies: List<String>,
        date: LocalDate = LocalDate.now(),
        retry: WebClientRetry = WebClientRetry(maxAttempts = 3, delayMillis = 500),
    ): CurrencyLayerLiveResponse {
        val response = webClient
            .get()
            .uri {
                it.path(URL_EXCHANGE_RATE)
                    .queryParam("access_key", accessKey)
                    .queryParam("currencies", currencies.joinToString(","))
                    .queryParam("source", BASE_CURRENCY)
                    .queryParam("format", JSON_FORMAT)
                    .build()
            }
            .retrieve()
            .bodyToMono(CurrencyLayerLiveResponse::class.java)
            .retryWhen(retry.fixedDelay(ErrorCode.CURRENCY_LAYER_EXCHANGE_RATES_FAILED))
            .awaitSingle()

        if (!response.success) {
            throw SwaplyException(
                errorCode = ErrorCode.UNEXPECTED_ERROR,
                message = response.error?.info ?: "CurrencyLayer API error"
            )
        }

        return response
    }

}