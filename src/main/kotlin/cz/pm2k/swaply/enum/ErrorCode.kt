package cz.pm2k.swaply.enum

import org.springframework.http.HttpStatus

enum class ErrorCode(val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR) {
    UNEXPECTED_ERROR,
    INVALID_REQUEST(HttpStatus.BAD_REQUEST),
    CNB_EXCHANGE_RATES_FAILED,
    CURRENCY_LAYER_EXCHANGE_RATES_FAILED,
}