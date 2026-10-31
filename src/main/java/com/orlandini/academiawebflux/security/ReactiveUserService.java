package com.orlandini.academiawebflux.security;
import com.orlandini.academiawebflux.model.Role;
import com.orlandini.academiawebflux.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.*;
import java.util.List;

@Service @RequiredArgsConstructor
public class ReactiveUserService implements ReactiveUserDetailsService {
    private final IUserRepo userRepo;
    private final IRoleRepo roleRepo;
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Credenciales invalidas")))
                .flatMap(user -> Flux.fromIterable(user.getRoles() == null ? List.<Role>of() : user.getRoles())
                        .concatMap(ref -> roleRepo.findById(ref.getId())
                                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Rol no disponible"))))
                        .map(Role::getName).collectList().map(names -> (UserDetails)
                                org.springframework.security.core.userdetails.User
                                        .withUsername(user.getUsername()).password(user.getPassword())
                                        .disabled(!user.isStatus()).authorities(names.toArray(String[]::new)).build()));
    }
}
