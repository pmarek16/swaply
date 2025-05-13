package cz.pm2k.swaply.config

import mu.KotlinLogging
import org.ehcache.event.CacheEvent
import org.ehcache.event.CacheEventListener
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
@EnableCaching
class CacheConfig {
    companion object {
        const val CACHE_CNB_EXCHANGE_RATES = "cnbExchangeRates"
    }
}

@Component
class CacheLogger : CacheEventListener<Any?, Any?> {
    private val logger = KotlinLogging.logger {}

    override fun onEvent(cacheEvent: CacheEvent<*, *>) {
        logger.debug { "Key: ${cacheEvent.key} | EventType: ${cacheEvent.type} | Old value: ${cacheEvent.oldValue} | New value: ${cacheEvent.newValue}" }
    }
}
