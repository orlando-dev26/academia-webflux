package com.orlandini.academiawebflux.security;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
@Configuration @EnableWebFluxSecurity
public class WebSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
            AuthenticationManager manager, SecurityContextRepository contexts,
            JwtAuthenticationEntryPoint entryPoint, JwtAccessDeniedHandler denied) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .requestCache(ServerHttpSecurity.RequestCacheSpec::disable)
                .authenticationManager(manager).securityContextRepository(contexts)
                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(denied))
                .authorizeExchange(e -> e.pathMatchers(HttpMethod.POST, "/login", "/v2/login").permitAll()
                        .anyExchange().authenticated()).build();
    }
}
