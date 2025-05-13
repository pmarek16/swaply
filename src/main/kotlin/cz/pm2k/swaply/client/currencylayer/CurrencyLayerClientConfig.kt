package cz.pm2k.swaply.client.currencylayer

import cz.pm2k.swaply.client.WebClientProperties
import cz.pm2k.swaply.client.createWebClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class CurrencyLayerClientConfig {

    @Bean
    fun currencyLayerWebClient(properties: CurrencyLayerClientProperties) = createWebClient(properties)

}

@Component
@ConfigurationProperties(prefix = "swaply.client.currencylayer")
class CurrencyLayerClientProperties: WebClientProperties()