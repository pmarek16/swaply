package cz.pm2k.swaply.controller

import cz.pm2k.swaply.dto.ErrorResponse
import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.SwaplyException
import jakarta.validation.ConstraintViolationException
import mu.KotlinLogging
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException

const val V1_BASE_URL = "api/v1"

@ControllerAdvice
class DefaultExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(exception: Exception): ResponseEntity<ErrorResponse> {
        ExceptionUtils.getRootCause(exception)?.let {
            // handle request validation errors as 4XX
            when (it) {
                is SwaplyException -> return handleSwaplyException(it)
                is IllegalArgumentException -> return handleException(it)
                is ConstraintViolationException -> return handleException(it)
                else -> { /* do nothing */ }
            }
        }
        logger.error("Handling Exception: '${exception.message}'", exception)
        return createErrorResponse(ErrorCode.UNEXPECTED_ERROR, exception)
    }

    @ExceptionHandler(SwaplyException::class)
    fun handleSwaplyException(ex: SwaplyException): ResponseEntity<ErrorResponse> {
        logger.error("Handling ${ex.javaClass.simpleName}: '${ex.localizedMessage}' status: ${ex.httpStatus}, error: ${ex.errorCode}, devMessage: ${ex.message}", ex)
        return createErrorResponse(ex.errorCode, ex)
    }

    @ExceptionHandler(
        HttpRequestMethodNotSupportedException::class, HttpMediaTypeNotSupportedException::class, HttpMediaTypeNotAcceptableException::class,
        MissingPathVariableException::class, MissingServletRequestParameterException::class, MissingServletRequestPartException::class,
        ServletRequestBindingException::class, MethodArgumentNotValidException::class, NoHandlerFoundException::class,
    )
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Handling ${exception.javaClass.simpleName}: '${exception.message}'", exception)
        return createErrorResponse(ErrorCode.INVALID_REQUEST, exception)
    }

    private fun createErrorResponse(
        errorCode: ErrorCode,
        exception: Exception,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = errorCode.httpStatus.value(),
            code = errorCode,
            message = exception.localizedMessage,
        )
        return ResponseEntity.status(errorCode.httpStatus).body(errorResponse)
    }

}