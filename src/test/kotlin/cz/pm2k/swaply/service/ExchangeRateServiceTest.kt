package cz.pm2k.swaply.service

import cz.pm2k.swaply.client.cnb.CnbWebService
import cz.pm2k.swaply.client.currencylayer.CurrencyLayerLiveResponse
import cz.pm2k.swaply.client.currencylayer.CurrencyLayerWebService
import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.SwaplyException
import cz.pm2k.swaply.mapper.diffFormatter
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ExchangeRateServiceTest {

    companion object {
        val cnbRates = """
                12.05.2025 #89
                země|měna|množství|kód|kurz
                USA|dolar|1|USD|22,495
                EMU|euro|1|EUR|24,980
            """.trimIndent()

        val clRates = CurrencyLayerLiveResponse(
            success = true,
            quotes = mapOf(
                "CZKUSD" to 0.044447,
                "CZKEUR" to 0.041143,
            )
        )
    }

    private val cnbWebService = mockk<CnbWebService>()
    private val currencyLayerWebService = mockk<CurrencyLayerWebService>()

    private val exchangeRateService = spyk(ExchangeRateService(
            cnbWebService = cnbWebService,
            currencyLayerWebService = currencyLayerWebService,
        ))

    @Test
    fun `getCnbRates - happy path`() {
        // Data
        val csvData = this.javaClass.getResource("/ws/cnb-daily-exchange-rates.txt")!!.readText()

        // Behavior
        coEvery { cnbWebService.getExchangeRates(any()) } returns csvData

        // Test
        val result = exchangeRateService.getCnbRates()

        // Verify
        assertNotNull(result)
        assertEquals(31, result.size)
        val chf = result.find { it.quote == "CHF" }
        assertNotNull(chf)
        assertEquals("CZK", chf.base)
        assertEquals("CZKCHF", chf.code)
        assertEquals(1 / 26.686, chf.rate)
    }

    @ParameterizedTest
    @CsvSource(value = [
        "CZKUSD, -0.000007",
        "CZKEUR, 0.001111",
    ])
    fun `getCurrencyLayerDiffs - happy path`(pair: String, diff: String) {
        // Behavior
        coEvery { cnbWebService.getExchangeRates(any()) } returns cnbRates
        coEvery { currencyLayerWebService.getExchangeRates(any(), any()) } returns clRates

        // Test
        val result = exchangeRateService.getCurrencyLayerDiffs(pair)

        // Verify
        assertNotNull(result)
        assertEquals(pair, result.pairCode)
        assertEquals(diff, diffFormatter.format(result.rateDiff))
    }

    @Test
    fun `getCurrencyLayerDiffs - unknown pair`() {
        // Behavior
        coEvery { cnbWebService.getExchangeRates(any()) } returns cnbRates
        coEvery { currencyLayerWebService.getExchangeRates(any(), any()) } returns clRates

        // Test
        val exception = assertThrows<SwaplyException> {
            exchangeRateService.getCurrencyLayerDiffs("CZKHUF")
        }

        // Verify
        assertEquals("Unsupported currency pair: CZKHUF", exception.message)
        assertEquals(ErrorCode.INVALID_REQUEST, exception.errorCode)
        assertEquals(ErrorCode.INVALID_REQUEST.httpStatus, exception.httpStatus)
    }

}