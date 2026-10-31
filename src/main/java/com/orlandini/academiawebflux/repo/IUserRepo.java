package com.orlandini.academiawebflux.repo;
import com.orlandini.academiawebflux.model.User;
import reactor.core.publisher.Mono;
public interface IUserRepo extends IGenericRepo<User, String> {
    Mono<User> findByUsername(String username);
}
