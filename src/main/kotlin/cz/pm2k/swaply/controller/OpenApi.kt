package cz.pm2k.swaply.controller

import cz.pm2k.swaply.dto.ErrorDto
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE)
@ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "OK"),
    ApiResponse(responseCode = "400", description = "Bad request", content = [Content(schema = Schema(implementation = ErrorDto::class))]),
    ApiResponse(responseCode = "500", description = "Application error", content = [Content(schema = Schema(implementation = ErrorDto::class))]),
])
@MustBeDocumented
annotation class ApiResponsesOk