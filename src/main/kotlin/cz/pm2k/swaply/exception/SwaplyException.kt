package cz.pm2k.swaply.exception

import cz.pm2k.swaply.enum.ErrorCode
import org.springframework.http.HttpStatus

class SwaplyException(
    val errorCode: ErrorCode,
    val httpStatus: HttpStatus = errorCode.httpStatus,
    message: String,
): RuntimeException(message)

/**
 * @throws SwaplyException with errorCode and message when condition is false
 */
fun require(condition: Boolean, errorCode: ErrorCode, message: () -> String) {
    if (!condition) {
        throw SwaplyException(errorCode = errorCode, message = message())
    }
}