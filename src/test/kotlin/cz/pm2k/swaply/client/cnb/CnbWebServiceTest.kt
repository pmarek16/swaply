package cz.pm2k.swaply.client.cnb

import cz.pm2k.swaply.client.WebClientRetry
import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.SwaplyException
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

class CnbWebServiceTest {

    companion object {
        @JvmStatic
        private val mockWebServer = MockWebServer()

        fun createMockResponse(stringData: String) = MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .setBody(stringData)
    }

    private val cnbService = spyk(CnbWebService(WebClient.create(mockWebServer.url("/").toString())))

    @Test
    fun `getExchangeRates - happy path`() {
        // Data
        val csvData = this.javaClass.getResource("/ws/cnb-daily-exchange-rates.txt")!!.readText()

        // Behavior
        mockWebServer.enqueue(createMockResponse(csvData))

        // Test
        val result = runBlocking {
            cnbService.getExchangeRates(retry = WebClientRetry.NEVER)
        }

        // Verify
        assertNotNull(result)
        assertEquals(33, result.lines().size)
    }

    @Test
    fun `getExchangeRates - more retries`() {
        // Data
        val csvData = this.javaClass.getResource("/ws/cnb-daily-exchange-rates.txt")!!.readText()

        // Behavior
        (1..10).forEach { _ ->
            mockWebServer.enqueue(MockResponse().setResponseCode(500))
        }
        mockWebServer.enqueue(createMockResponse(csvData))

        // Test
        val result = runBlocking {
            cnbService.getExchangeRates(retry = WebClientRetry(maxAttempts = 10))
        }

        // Verify
        assertNotNull(result)
        assertEquals(33, result.lines().size)
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
                cnbService.getExchangeRates(retry = WebClientRetry(maxAttempts = 3))
            }
        }

        // Verify
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.httpStatus)
        assertEquals(ErrorCode.CNB_EXCHANGE_RATES_FAILED, exception.errorCode)
    }

    @Test
    fun `getExchangeRates - all retries failed`() {
        // Behavior
        (0..8).forEach { _ ->
            mockWebServer.enqueue(MockResponse().setResponseCode(500))
        }

        // Test
        val exception = assertThrows<SwaplyException> {
            runBlocking {
                cnbService.getExchangeRates(retry = WebClientRetry(maxAttempts = 8))
            }
        }

        // Verify
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.httpStatus)
        assertEquals(ErrorCode.CNB_EXCHANGE_RATES_FAILED, exception.errorCode)
    }

}