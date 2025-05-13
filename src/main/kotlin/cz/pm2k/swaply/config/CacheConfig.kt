package cz.pm2k.swaply.config

import mu.KotlinLogging
import org.ehcache.event.CacheEvent
import org.ehcache.event.CacheEventListener
import org.ehcache.expiry.ExpiryPolicy
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.util.function.Supplier

@Configuration
@EnableCaching
class CacheConfig {
    companion object {
        const val CACHE_CNB_EXCHANGE_RATES = "cnbExchangeRates"
        const val CACHE_CURRENCY_LAYER_EXCHANGE_RATES = "currencyLayerExchangeRates"
    }
}

@Component
class CacheLogger : CacheEventListener<Any?, Any?> {
    private val logger = KotlinLogging.logger {}

    override fun onEvent(cacheEvent: CacheEvent<*, *>) {
        logger.debug { "Key: ${cacheEvent.key} | EventType: ${cacheEvent.type} | Old value: ${cacheEvent.oldValue} | New value: ${cacheEvent.newValue}" }
    }
}

class CacheExpirationMidnight<K, V>: ExpiryPolicy<K, V> {
    override fun getExpiryForCreation(key: K?, value: V?) = getMidnightDuration()
    override fun getExpiryForAccess(key: K?, value: Supplier<out V?>?) = getMidnightDuration()
    override fun getExpiryForUpdate(key: K?, oldValue: Supplier<out V?>?, newValue: V?) = getMidnightDuration()

    private fun getMidnightDuration(): Duration {
        val now = LocalDateTime.now()
        val midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        return Duration.between(now, midnight)
    }
}