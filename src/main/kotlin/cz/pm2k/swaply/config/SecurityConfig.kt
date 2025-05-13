package cz.pm2k.swaply.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.DefaultSecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${spring.security.user.name}")
    private val username: String,

    @Value("\${spring.security.user.password}")
    private val password: String,
) {

    @Bean
    fun userDetailsService() = InMemoryUserDetailsManager(
        User.builder()
            .passwordEncoder { PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(it) }
            .username(username)
            .password(password)
            .build()
    )

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
    ): DefaultSecurityFilterChain = http
        // disable sessions (stateless HTTP is used)
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .authorizeHttpRequests { it
            // enable free access to all API endpoints with method OPTIONS
            .requestMatchers(HttpMethod.OPTIONS, "/**")
            .permitAll()
            //enable access to basic actuator endpoints
            .requestMatchers("/actuator/**")
            .permitAll()
            // all other requests are authenticated
            .anyRequest().authenticated()
        }
        .httpBasic {}
        .build()

}