package cz.pm2k.swaply.dto

import cz.pm2k.swaply.enum.ErrorCode
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Standard error response for either validation error or app execution fail")
data class ErrorResponse(

    @field:Schema(description = "Http status code.", requiredMode = Schema.RequiredMode.REQUIRED, example = "400")
    val status: Int,

    @field:Schema(description = "Application internal error code", requiredMode = Schema.RequiredMode.REQUIRED)
    val code: ErrorCode,

    @field:Schema(description = "Error message")
    val message: String? = null,

)