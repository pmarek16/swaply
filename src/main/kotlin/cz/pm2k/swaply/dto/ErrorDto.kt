package cz.pm2k.swaply.dto

import cz.pm2k.swaply.enum.ErrorCode
import io.swagger.v3.oas.annotations.media.Schema

data class ErrorDto(

    @field:Schema(description = "Application internal error code")
    val errorCode: ErrorCode,

    @field:Schema(description = "Detailed error message.")
    val message: String,

    @field:Schema(description = "Additional parameters for error message")
    val params: Map<String, String> = emptyMap(),

)