package cz.pm2k.swaply.controller.v1

import cz.pm2k.swaply.controller.ApiResponsesOk
import cz.pm2k.swaply.dto.v1.CurrencyPairsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated

@Validated
@Tag(name = "exchange-rate", description = "Exchange rate API")
interface IExchangeRateController {

    @Operation(summary = "Get list of supported currency pairs")
    @ApiResponsesOk
    fun getCurrencyPairs(): CurrencyPairsResponse

}