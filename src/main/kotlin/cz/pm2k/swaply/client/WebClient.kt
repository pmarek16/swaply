package cz.pm2k.swaply.client

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger {}

fun createWebClient(properties: WebClientProperties): WebClient {
    val httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.connectionTimeout.toInt())
        .doOnConnected { conn ->
            conn.addHandlerLast(ReadTimeoutHandler(properties.readTimeout, TimeUnit.MILLISECONDS))
                .addHandlerLast(WriteTimeoutHandler(properties.writeTimeout, TimeUnit.MILLISECONDS))
        }

    Schedulers.onScheduleHook("mdc", ::mdcScheduleHook)

    return WebClient.builder()
        .exchangeStrategies(ExchangeStrategies.builder().codecs {
            it.defaultCodecs().maxInMemorySize(15_000_000) // in bytes
        }.build())
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .baseUrl(properties.url)
        .filter(copyMdc())
        .filter(logRequest())
        .build()
}

private fun mdcScheduleHook(runnable: Runnable): Runnable {
    val mdcMap = MDC.getCopyOfContextMap()
    return Runnable {
        mdcMap?.let { MDC.setContextMap(mdcMap) }
        try {
            runnable.run()
        } finally {
            MDC.clear()
        }
    }
}

private fun copyMdc() = ExchangeFilterFunction { request, next ->
    // here runs on main(request's) thread
    val mdcMap = MDC.getCopyOfContextMap()
    next.exchange(request).doOnNext {
        // here runs on reactor's thread
        mdcMap?.let { MDC.setContextMap(mdcMap) }
    }
}

private fun logRequest(): ExchangeFilterFunction {
    return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
        logger.info("Call REST: ${clientRequest.method()} ${clientRequest.url()}")
        Mono.just(clientRequest)
    }
}

/**
 * Run coroutine in blocking mode. Default context is IO dispatcher with MDC context.
 */
fun <T> runBlockingWithMdc(
    context: CoroutineContext = Dispatchers.IO + MDCContext(),
    block: suspend CoroutineScope.() -> T,
): T = runBlocking(context) { block() }