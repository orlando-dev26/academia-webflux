package com.orlandini.academiawebflux.handler;
import com.orlandini.academiawebflux.security.*;
import com.orlandini.academiawebflux.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
@Component @RequiredArgsConstructor
public class LoginHandler {
    private final AuthService auth;
    private final RequestValidator validator;
    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(AuthRequest.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap(validator::validate).flatMap(auth::login)
                .flatMap(value -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(value));
    }
}
