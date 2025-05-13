package cz.pm2k.swaply.controller

import cz.pm2k.swaply.enum.ErrorCode
import cz.pm2k.swaply.exception.SwaplyException
import io.mockk.mockk
import io.mockk.spyk
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpStatus
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.ServletRequestBindingException
import kotlin.test.Test

class DefaultExceptionHandlerTest {

    private val defaultExceptionHandler = spyk<DefaultExceptionHandler>()

    companion object {
        private const val MSG = "message"

        @JvmStatic
        fun unknownExceptions() = listOf(
            SwaplyException(errorCode = ErrorCode.INVALID_REQUEST, message = MSG),
            IllegalArgumentException(MSG),
            ConstraintViolationException(MSG, setOf(mockk(), mockk())),
        )

        @JvmStatic
        fun allHttpErrrorStatuses() = HttpStatus.entries.filter { it.isError }

    }

    @ParameterizedTest
    @MethodSource("unknownExceptions")
    fun `handleUnknownException - happy path`(rootCause: Exception) {
        // Test
        val response = defaultExceptionHandler.handleUnknownException(Exception(MSG, rootCause))

        // Verify
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorCode.INVALID_REQUEST, response.body?.code)
        assertEquals(MSG, response.body?.message)
    }

    @Test
    fun `handleUnknownException - unknown cause`() {
        // Test
        val response = defaultExceptionHandler.handleUnknownException(Exception(MSG, Exception()))

        // Verify
        assertNotNull(response)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals(ErrorCode.UNEXPECTED_ERROR, response.body?.code)
        assertEquals(MSG, response.body?.message)
    }

    @ParameterizedTest
    @MethodSource("allHttpErrrorStatuses")
    fun `handleSwaplyException - all http statuses`(status: HttpStatus) {
        // Data
        val exception = SwaplyException(errorCode = ErrorCode.INVALID_REQUEST, httpStatus = status, message = MSG)

        // Test
        val response = defaultExceptionHandler.handleSwaplyException(exception)

        // Verify
        assertNotNull(response)
        assertEquals(ErrorCode.INVALID_REQUEST.httpStatus, response.statusCode)
        assertEquals(ErrorCode.INVALID_REQUEST, response.body?.code)
        assertEquals(MSG, response.body?.message)
    }

    @ParameterizedTest
    @EnumSource(ErrorCode::class)
    fun `handleInvestoryException - all error codes`(errorCode: ErrorCode) {
        // Data
        val exception = SwaplyException(errorCode = errorCode, httpStatus = HttpStatus.I_AM_A_TEAPOT, message = MSG)

        // Test
        val response = defaultExceptionHandler.handleSwaplyException(exception)

        // Verify
        assertNotNull(response)
        assertEquals(errorCode.httpStatus, response.statusCode)
        assertEquals(errorCode, response.body?.code)
        assertEquals(MSG, response.body?.message)
    }

    @ParameterizedTest
    @ValueSource(classes = [
        HttpMediaTypeNotSupportedException::class,
        HttpMediaTypeNotAcceptableException::class,
        ServletRequestBindingException::class,
    ])
    fun `handleException - happy path`(clazz: Class<Exception>) {
        // Data
        val exception = clazz.getDeclaredConstructor(String::class.java).newInstance(MSG)

        // Test
        val response = defaultExceptionHandler.handleException(exception)

        // Verify
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorCode.INVALID_REQUEST, response.body?.code)
        assertEquals(MSG, response.body?.message)
    }

}