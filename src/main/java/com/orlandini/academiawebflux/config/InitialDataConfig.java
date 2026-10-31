package com.orlandini.academiawebflux.config;
import com.orlandini.academiawebflux.model.*;
import com.orlandini.academiawebflux.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;
import java.time.Duration;
import java.util.List;

@Component @RequiredArgsConstructor
public class InitialDataConfig implements ApplicationRunner {
    private final IUserRepo users;
    private final IRoleRepo roles;
    private final PasswordEncoder encoder;
    @Value("${app.bootstrap.enabled:false}") private boolean enabled;
    @Value("${app.bootstrap.username:admin}") private String username;
    @Value("${app.bootstrap.password:}") private String password;
    @Override public void run(ApplicationArguments args) {
        if (!enabled) return;
        if (username.isBlank() || password.isBlank())
            throw new IllegalStateException("Configura usuario y password de bootstrap");
        // Bloqueo acotado solo durante inicialización; nunca en el flujo de peticiones.
        Flux.just("ADMIN", "USER").concatMap(name -> roles.findByName(name)
                        .switchIfEmpty(Mono.defer(() -> roles.save(new Role(null, name)))))
                .collectMap(Role::getName)
                .flatMap(found -> users.findByUsername(username).switchIfEmpty(Mono.defer(() ->
                        Mono.fromCallable(() -> encoder.encode(password)).subscribeOn(Schedulers.boundedElastic())
                                .flatMap(hash -> users.save(new User(null, username, hash, true,
                                        List.of(new Role(found.get("ADMIN").getId(), null))))))))
                .then().block(Duration.ofSeconds(30));
    }
}
