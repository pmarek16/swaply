package cz.pm2k.swaply.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.UUID

const val CORRELATION_ID = "correlation-id"

@Component
class FilterConfig: Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val correlationId = httpRequest.getHeader(CORRELATION_ID) ?: UUID.randomUUID().toString()
        MDC.put(CORRELATION_ID, correlationId)
        chain?.doFilter(request, response)
    }

}