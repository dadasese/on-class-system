package com.onclass.report.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;

@Component
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        // Swagger / OpenAPI — public
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**",
                                "/api-docs/**", "/webjars/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/reports/events/**").permitAll()
                        .pathMatchers("/api/v1/reports/**").authenticated()
                        .anyExchange().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .build();
    }
}
