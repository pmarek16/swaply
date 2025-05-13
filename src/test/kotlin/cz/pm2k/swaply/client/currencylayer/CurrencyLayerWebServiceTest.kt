package cz.pm2k.swaply.client.currencylayer

import cz.pm2k.swaply.client.WebClientRetry
import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.SwaplyException
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CurrencyLayerWebServiceTest {

    companion object {
        private const val accessKey = "test-access-key"
        private val currencies = listOf("USD", "EUR")

        @JvmStatic
        private val mockWebServer = MockWebServer()

        fun createMockResponse(stringData: String) = MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(stringData)
    }

    private val currencyLayerService = spyk(CurrencyLayerWebService(
        webClient = WebClient.create(mockWebServer.url("/").toString()),
        accessKey = accessKey,
    ))

    @Test
    fun `getExchangeRates - happy path`() {
        // Data
        val jsonData = this.javaClass.getResource("/ws/apilayer-exchange-rates.json")!!.readText()

        // Behavior
        mockWebServer.enqueue(createMockResponse(jsonData))

        // Test
        val result = runBlocking {
            currencyLayerService.getExchangeRates(currencies = currencies, retry = WebClientRetry.NEVER)
        }

        // Verify
        assertNotNull(result)
        assertNotNull(result.quotes)
        assertEquals(31, result.quotes.size)
        val zar = result.quotes["CZKZAR"]
        assertNotNull(zar)
        assertEquals(0.814761, zar)
    }

    @Test
    fun `getExchangeRates - more retries`() {
        // Data
        val jsonData = this.javaClass.getResource("/ws/apilayer-exchange-rates.json")!!.readText()

        // Behavior
        (1..10).forEach { _ ->
            mockWebServer.enqueue(MockResponse().setResponseCode(500))
        }
        mockWebServer.enqueue(createMockResponse(jsonData))

        // Test
        val result = runBlocking {
            currencyLayerService.getExchangeRates(currencies = currencies, retry = WebClientRetry(maxAttempts = 10))
        }

        // Verify
        assertNotNull(result)
        assertNotNull(result.quotes)
        assertEquals(31, result.quotes.size)
    }

    @Test
    fun `getExchangeRates - bad request`() {
        // Behavior
        (0..3).forEach { _ ->
            mockWebServer.enqueue(MockResponse().setResponseCode(400))
        }

        // Test
        val exception = assertThrows<SwaplyException> {
            runBlocking {
                currencyLayerService.getExchangeRates(currencies = currencies, retry = WebClientRetry(maxAttempts = 3))
            }
        }

        // Verify
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.httpStatus)
        Assertions.assertEquals(ErrorCode.CURRENCY_LAYER_EXCHANGE_RATES_FAILED, exception.errorCode)
    }

    @Test
    fun `getExchangeRates - all retries failed`() {
        // Behavior
        (0..7).forEach { _ ->
            mockWebServer.enqueue(MockResponse().setResponseCode(500))
        }

        // Test
        val exception = assertThrows<SwaplyException> {
            runBlocking {
                currencyLayerService.getExchangeRates(currencies = currencies, retry = WebClientRetry(maxAttempts = 7))
            }
        }

        // Verify
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.httpStatus)
        assertEquals(ErrorCode.CURRENCY_LAYER_EXCHANGE_RATES_FAILED, exception.errorCode)
    }

    @Test
    fun `getExchangeRates - error returned`() {
        // Data
        val jsonData = this.javaClass.getResource("/ws/apilayer-exchange-rates-error.json")!!.readText()

        // Behavior
        mockWebServer.enqueue(createMockResponse(jsonData))

        // Test
        val exception = assertThrows<SwaplyException> {
            runBlocking {
                currencyLayerService.getExchangeRates(currencies = currencies, retry = WebClientRetry.NEVER)
            }
        }

        // Verify
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.httpStatus)
        assertEquals(ErrorCode.UNEXPECTED_ERROR, exception.errorCode)
    }

}