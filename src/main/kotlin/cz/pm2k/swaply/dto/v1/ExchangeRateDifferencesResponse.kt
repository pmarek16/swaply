package cz.pm2k.swaply.dto.v1

import io.swagger.v3.oas.annotations.media.Schema

data class ExchangeRateDifferencesResponse(

    @field:Schema(description = "Currency pair code")
    val pair: String,

    @field:Schema(description = "CurrencyLayer rate difference related to CNB (minus is better, plus is worse)")
    val rateDifference: String,

)
