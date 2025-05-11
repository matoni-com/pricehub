package com.example.fulfilment.security;

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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter filter) throws Exception {
        // TODO: add exception handling
        return http
                .securityMatcher("/web/**", "/authenticate", "/hello")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.POST, "/authenticate").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}
