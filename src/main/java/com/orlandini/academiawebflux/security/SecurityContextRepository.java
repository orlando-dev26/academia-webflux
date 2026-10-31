package com.orlandini.academiawebflux.security;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.*;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component @RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private final AuthenticationManager manager;
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) { return Mono.empty(); }
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7)) return Mono.empty();
        String token = header.substring(7).trim();
        if (token.isEmpty()) return Mono.empty();
        return manager.authenticate(new UsernamePasswordAuthenticationToken(null, token))
                .map(authentication -> (SecurityContext) new SecurityContextImpl(authentication))
                .onErrorResume(AuthenticationException.class, e -> Mono.empty());
    }
}
