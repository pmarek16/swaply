package cz.pm2k.swaply.client.cnb

import cz.pm2k.swaply.client.WebClientRetry
import cz.pm2k.swaply.config.CacheConfig.Companion.CACHE_CNB_EXCHANGE_RATES
import cz.pm2k.swaply.enum.ErrorCode
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class CnbWebService(
    @Qualifier("cnbWebClient")
    private val webClient: WebClient,
) {

    companion object {
        private const val URL_EXCHANGE_RATE = "/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.txt"
        private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }

    @Cacheable(cacheNames = [CACHE_CNB_EXCHANGE_RATES], key = "#date")
    suspend fun getExchangeRates(
        date: LocalDate = LocalDate.now(),
        retry: WebClientRetry = WebClientRetry(maxAttempts = 3, delayMillis = 500),
    ): String = webClient
        .get()
        .uri {
            it.path(URL_EXCHANGE_RATE)
                .queryParam("date", dateFormatter.format(date))
                .build()
        }
        .retrieve()
        .bodyToMono(String::class.java)
        .retryWhen(retry.fixedDelay(ErrorCode.CNB_EXCHANGE_RATES_FAILED))
        .awaitSingle()

}