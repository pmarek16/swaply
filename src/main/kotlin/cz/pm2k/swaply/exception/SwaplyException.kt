package cz.pm2k.swaply.exception

import cz.pm2k.swaply.enum.ErrorCode
import org.springframework.http.HttpStatus

class SwaplyException(
    val httpStatus: HttpStatus,
    val errorCode: ErrorCode,
    message: String,
): RuntimeException(message)