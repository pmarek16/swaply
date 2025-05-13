package cz.pm2k.swaply.client.cnb

import cz.pm2k.swaply.client.WebClientProperties
import cz.pm2k.swaply.client.createWebClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class CnbClientConfig {

    @Bean
    fun cnbWebClient(properties: CnbClientProperties) = createWebClient(properties)

}

@Component
@ConfigurationProperties(prefix = "swaply.client.cnb")
class CnbClientProperties: WebClientProperties()