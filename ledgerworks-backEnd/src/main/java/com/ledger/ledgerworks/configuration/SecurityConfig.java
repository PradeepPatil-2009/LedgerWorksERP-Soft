package com.ledger.ledgerworks.configuration;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ledger.ledgerworks.security.JwtAuthenticationFilter;

/**
 * Stateless JWT security.
 *
 * <p>Public: login, API docs, and the document downloads that the browser opens
 * via raw navigation (a bearer header cannot be attached to those). Everything
 * else requires a valid token; user management and destructive backup operations
 * require the ADMIN role.</p>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // CORS preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // public authentication + API docs
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                    // role-restricted areas
                    .requestMatchers("/api/users/**").hasRole("ADMIN")
                    // all backup operations (download, restore, delete) are admin-only
                    .requestMatchers("/api/backup/**").hasRole("ADMIN")
                    // GST exports are limited to roles allowed to handle filings
                    .requestMatchers(HttpMethod.GET, "/api/gst/export/**").hasAnyRole("ADMIN", "ACCOUNTANT")
                    // admin-only configuration + audit modules
                    .requestMatchers(HttpMethod.GET, "/api/company-settings/**").authenticated()
                    .requestMatchers("/api/company-settings/**").hasRole("ADMIN")
                    .requestMatchers("/api/number-series/**").hasRole("ADMIN")
                    .requestMatchers("/api/financial-years/**").hasRole("ADMIN")
                    .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")
                    .requestMatchers("/api/import/**").hasRole("ADMIN")
                    // writes are denied to read-only VIEWER accounts
                    .requestMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "ACCOUNTANT", "USER")
                    .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "ACCOUNTANT", "USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/**").hasAnyRole("ADMIN", "ACCOUNTANT", "USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("ADMIN", "ACCOUNTANT", "USER")
                    // everything else requires a valid token
                    .anyRequest().authenticated()
            )
            .headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin())
                    .contentTypeOptions(contentType -> {})
                    .referrerPolicy(referrer -> referrer
                            .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                    .contentSecurityPolicy(csp -> csp
                            .policyDirectives("default-src 'self'; frame-ancestors 'none'; object-src 'none'")))
            .exceptionHandling(ex -> ex
                    // 401 when no/invalid authentication; 403 when authenticated but
                    // lacking the required role (e.g. a VIEWER attempting a write).
                    .authenticationEntryPoint((request, response, authException) ->
                            response.sendError(401, "Unauthorized"))
                    .accessDeniedHandler((request, response, deniedException) ->
                            response.sendError(403, "Forbidden")))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Prevent Spring Boot from ALSO registering the JWT filter as a plain servlet
     * filter (it is a @Component). It must run only inside the security chain.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
