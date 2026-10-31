package com.orlandini.academiawebflux.security;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component @RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtUtil jwt;
    private final ReactiveUserService users;
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.fromCallable(() -> jwt.validate(authentication.getCredentials().toString()))
                .onErrorMap(e -> e instanceof JwtException || e instanceof IllegalArgumentException,
                        e -> new BadCredentialsException("Token invalido", e))
                .flatMap(claims -> users.findByUsername(claims.getSubject()))
                .filter(UserDetails::isEnabled)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Usuario no habilitado")))
                .map(user -> (Authentication) new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()))
                .onErrorMap(UsernameNotFoundException.class, e -> new BadCredentialsException("Credenciales invalidas", e));
    }
}
