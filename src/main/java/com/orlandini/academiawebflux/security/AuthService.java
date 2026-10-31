package com.orlandini.academiawebflux.security;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service @RequiredArgsConstructor
public class AuthService {
    private final ReactiveUserService users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    public Mono<AuthResponse> login(AuthRequest request) {
        return users.findByUsername(request.getUsername()).filter(UserDetails::isEnabled)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Credenciales invalidas")))
                .flatMap(user -> Mono.fromCallable(() -> encoder.matches(request.getPassword(), user.getPassword()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(valid -> valid ? Mono.fromSupplier(() -> new AuthResponse(jwt.generate(user)))
                                : Mono.error(new BadCredentialsException("Credenciales invalidas"))))
                .onErrorMap(AuthenticationException.class,
                        e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
