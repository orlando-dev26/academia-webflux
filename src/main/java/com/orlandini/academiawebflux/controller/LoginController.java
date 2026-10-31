package com.orlandini.academiawebflux.controller;
import com.orlandini.academiawebflux.security.*;
import com.orlandini.academiawebflux.validator.RequestValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
@RestController @RequiredArgsConstructor
public class LoginController {
    private final AuthService auth;
    private final RequestValidator validator;
    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody Mono<AuthRequest> body) {
        return body.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap(validator::validate).flatMap(auth::login);
    }
}
