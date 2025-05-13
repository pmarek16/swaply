package cz.pm2k.swaply.client

import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.SwaplyException
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import java.time.Duration
import java.time.temporal.ChronoUnit

data class WebClientRetry(
    val maxAttempts: Long,
    val delayMillis: Long = 0,
    val jitterFactor: Double = 0.0,
) {
    val duration: Duration = Duration.of(delayMillis, ChronoUnit.MILLIS)

    companion object {
        val NEVER = WebClientRetry(
            maxAttempts = 0,
        )
    }

    fun fixedDelay(
        exhaustedError: ErrorCode,
        httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    ): Retry = Retry
        .fixedDelay(maxAttempts, duration).jitter(jitterFactor)
        .filter { ex -> ex is WebClientResponseException }
        .onRetryExhaustedThrow { _, retrySignal ->
            throw SwaplyException(
                httpStatus = httpStatus,
                errorCode = exhaustedError,
                message = retrySignal.failure().localizedMessage
            )
        }

}