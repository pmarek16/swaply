package cz.pm2k.swaply.helper

import cz.pm2k.swaply.model.ExchangeRateCsvRow
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class CsvHelperTest {

    @Test
    fun `readCsv - happy path`() {
        // Data
        val csvExample = """
            12.05.2025 #89
            země|měna|množství|kód|kurz
            Austrálie|dolar|1|AUD|14,373
            Brazílie|real|1|BRL|3,949
            Bulharsko|lev|1|BGN|12,772
            Čína|žen-min-pi|1|CNY|3,123
        """.trimIndent()

        // Test
        val result = readCsv(
            csvContent = csvExample,
            returnType = ExchangeRateCsvRow::class.java,
            skipLines = 1
        )

        // Verify
        assertEquals(4, result.size)
        assertEquals("Austrálie", result[0].country)
        assertEquals("dolar", result[0].currency)
        assertEquals(1, result[0].amount)
        assertEquals("AUD", result[0].code)
        assertEquals(14.373, result[0].rate)
        assertEquals("Brazílie", result[1].country)
        assertEquals("real", result[1].currency)
        assertEquals(1, result[1].amount)
        assertEquals("BRL", result[1].code)
        assertEquals(3.949, result[1].rate)
        assertEquals("Bulharsko", result[2].country)
        assertEquals("lev", result[2].currency)
        assertEquals(1, result[2].amount)
        assertEquals("BGN", result[2].code)
        assertEquals(12.772, result[2].rate)
        assertEquals("Čína", result[3].country)
        assertEquals("žen-min-pi", result[3].currency)
        assertEquals(1, result[3].amount)
        assertEquals("CNY", result[3].code)
        assertEquals(3.123, result[3].rate)
    }

}