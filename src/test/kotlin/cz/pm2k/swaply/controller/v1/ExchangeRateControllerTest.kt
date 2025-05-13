package cz.pm2k.swaply.controller.v1

import com.ninjasquad.springmockk.MockkBean
import cz.pm2k.swaply.controller.V1_BASE_URL
import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.SwaplyException
import cz.pm2k.swaply.service.ExchangeRateService
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.test.Test

@WebMvcTest(controllers = [ExchangeRateController::class])
class ExchangeRateControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {

    @MockkBean
    lateinit var exchangeRateService: ExchangeRateService

    companion object {
        private const val BASE_URL = "/$V1_BASE_URL/exchange-rate/currency-pairs"
    }

    @Test
    fun `getCurrencyPairs - happy path`() {
        // Data
        val rates = listOf("CZKHUF", "CZKNZD")

        // Behaviour
        every { exchangeRateService.getCnbCurrencyPairs() } returns rates

        // Test
        mockMvc.perform(
            get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.supportedPairs").isArray)
            .andExpect(jsonPath("$.supportedPairs.length()").value(2))
            .andExpect(jsonPath("$.supportedPairs[0]").value("CZKHUF"))
            .andExpect(jsonPath("$.supportedPairs[1]").value("CZKNZD"))
    }

    @Test
    fun `getCurrencyPairs - cnb service not available`() {
        // Behaviour
        val errorCode = ErrorCode.CNB_EXCHANGE_RATES_FAILED
        every { exchangeRateService.getCnbCurrencyPairs() } throws SwaplyException(errorCode.httpStatus, errorCode,"CNB service not available")

        // Test
        mockMvc.perform(
            get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().is5xxServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ErrorCode.CNB_EXCHANGE_RATES_FAILED.httpStatus.value()))
            .andExpect(jsonPath("$.code").value(ErrorCode.CNB_EXCHANGE_RATES_FAILED.name))
            .andExpect(jsonPath("$.message").value("CNB service not available"))
    }

}