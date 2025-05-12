package com.example.fulfilment.security;

import com.example.fulfilment.security.exceptionhandling.RestAccessDeniedHandler;
import com.example.fulfilment.security.exceptionhandling.RestAuthenticationEntryPoint;
import com.example.fulfilment.security.filters.JwtAuthenticationFilter;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import javax.crypto.SecretKey;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "maggieUsernamePassword")
    public AuthenticationManager maggieUsernamePasswordAuthManager(
            BCryptPasswordEncoder passwordEncoder,
            MaggieUserDetailsService maggieUserDetailsService,
            HttpSecurity http
    ) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(maggieUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        AuthenticationManagerBuilder authManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.authenticationProvider(provider);

        return authManagerBuilder.build();
    }

    @Bean(name = "maggieJwt")
    public AuthenticationManager maggieJwtAuthManager(HttpSecurity http, JwtAuthenticationProvider provider) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.authenticationProvider(provider);

        return authManagerBuilder.build();
    }

    @Bean
    public SecretKey jwtSignatureKey() {
        return Jwts.SIG.HS256.key().build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        return http
                .securityMatcher("/web/**", "/authenticate", "/hello", "/products/**")
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.POST, "/authenticate").permitAll()
                                .requestMatchers(HttpMethod.GET, "/products/**").hasAuthority("READ_PRODUCT")
                                .anyRequest().authenticated()
                )
                // This will put the ExceptionTranslationFilter before the JwtAuthenticationFilter in the filter chain.
                // This way ExceptionTranslationFilter can handle exceptions that are thrown in JwtAuthenticationFilter
                // since they will bubble up through the call stack. ExceptionTranslationFilter handles the exceptions
                // using the AuthenticationEntryPoint and AccessDeniedHandler that we configured below.
                .addFilterAfter(jwtAuthFilter, ExceptionTranslationFilter.class)
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint("Invalid or missing token"))
                        .accessDeniedHandler(new RestAccessDeniedHandler("Access denied"))
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .build();
    }

}
