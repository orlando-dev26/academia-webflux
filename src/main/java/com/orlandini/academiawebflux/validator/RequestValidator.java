package com.orlandini.academiawebflux.validator;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component @RequiredArgsConstructor
public class RequestValidator {
    private final Validator validator;
    public <T> Mono<T> validate(T body) {
        return Mono.defer(() -> {
            if (body == null || !validator.validate(body).isEmpty())
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
            return Mono.just(body);
        });
    }
}
