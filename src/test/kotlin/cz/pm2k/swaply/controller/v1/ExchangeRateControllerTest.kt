package cz.pm2k.swaply.controller.v1

import com.ninjasquad.springmockk.MockkBean
import cz.pm2k.swaply.client.currencylayer.BASE_CURRENCY
import cz.pm2k.swaply.controller.V1_BASE_URL
import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.SwaplyException
import cz.pm2k.swaply.model.CurrencyPair
import cz.pm2k.swaply.model.ExchangeRateDiff
import cz.pm2k.swaply.service.ExchangeRateService
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.test.Test

@WebMvcTest(controllers = [ExchangeRateController::class])
class ExchangeRateControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Value("\${spring.security.user.name}") private val username: String,
    @Value("\${spring.security.user.password}") private val password: String,
) {

    @MockkBean
    lateinit var exchangeRateService: ExchangeRateService

    companion object {
        private const val BASE_URL = "/$V1_BASE_URL/exchange-rate/currency-pairs"
    }

    @Test
    fun `getCurrencyPairs - happy path`() {
        // Data
        val rates = listOf(
            CurrencyPair(BASE_CURRENCY,"HUF", 6.150),
            CurrencyPair(BASE_CURRENCY,"NZD", 13.217),
        )

        // Behaviour
        every { exchangeRateService.getCnbRates() } returns rates

        // Test
        mockMvc.perform(
            get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
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
        every { exchangeRateService.getCnbRates() } throws SwaplyException(errorCode = ErrorCode.CNB_EXCHANGE_RATES_FAILED, message = "CNB service not available")

        // Test
        mockMvc.perform(
            get(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
        )
            .andExpect(status().is5xxServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ErrorCode.CNB_EXCHANGE_RATES_FAILED.httpStatus.value()))
            .andExpect(jsonPath("$.code").value(ErrorCode.CNB_EXCHANGE_RATES_FAILED.name))
            .andExpect(jsonPath("$.message").value("CNB service not available"))
    }

    @Test
    fun `getDifferences - happy path`() {
        // Data
        val currencyPairCode = "CZKHUF"
        val diff = ExchangeRateDiff(
            pairCode = currencyPairCode,
            rateDiff = 0.123456,
        )

        // Behaviour
        every { exchangeRateService.getCurrencyLayerDiffs(currencyPairCode) } returns diff

        // Test
        mockMvc.perform(
            get("/$V1_BASE_URL/exchange-rate/differences/$currencyPairCode")
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.pair").value(currencyPairCode))
            .andExpect(jsonPath("$.rateDifference").value("0.123456"))
    }

    @Test
    fun `getDifferences - currencyLayerWebService not available`() {
        // Data
        val currencyPairCode = "CZKHUF"

        // Behaviour
        every { exchangeRateService.getCurrencyLayerDiffs(currencyPairCode) } throws SwaplyException(errorCode = ErrorCode.CURRENCY_LAYER_EXCHANGE_RATES_FAILED, message = "CurrencyLayerWebService not available")

        // Test
        mockMvc.perform(
            get("/$V1_BASE_URL/exchange-rate/differences/$currencyPairCode")
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(username, password))
        )
            .andExpect(status().is5xxServerError)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(ErrorCode.CURRENCY_LAYER_EXCHANGE_RATES_FAILED.httpStatus.value()))
            .andExpect(jsonPath("$.code").value(ErrorCode.CURRENCY_LAYER_EXCHANGE_RATES_FAILED.name))
            .andExpect(jsonPath("$.message").value("CurrencyLayerWebService not available"))
    }

}