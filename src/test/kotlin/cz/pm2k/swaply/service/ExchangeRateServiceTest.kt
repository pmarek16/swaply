package cz.pm2k.swaply.service

import cz.pm2k.swaply.client.cnb.CnbWebService
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ExchangeRateServiceTest {

    private val cnbWebService = mockk<CnbWebService>()

    private val exchangeRateService = spyk(ExchangeRateService(
            cnbWebService = cnbWebService,
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
        val chf = result.find { it.code == "CHF" }
        assertNotNull(chf)
        assertEquals("frank", chf.currency)
        assertEquals("Švýcarsko", chf.country)
        assertEquals(1, chf.amount)
        assertEquals(26.686, chf.rate)
    }

    @Test
    fun `getCnbCurrencyPairs - happy path`() {
        // Data
        val csvData = this.javaClass.getResource("/ws/cnb-daily-exchange-rates.txt")!!.readText()

        // Behavior
        coEvery { cnbWebService.getExchangeRates(any()) } returns csvData

        // Test
        val result = exchangeRateService.getCnbCurrencyPairs()

        // Verify
        assertNotNull(result)
        assertEquals(31, result.size)
        val chf = result.find { it.contains("CHF") }
        assertNotNull(chf)
        assertEquals("CZKCHF", chf)
    }


}