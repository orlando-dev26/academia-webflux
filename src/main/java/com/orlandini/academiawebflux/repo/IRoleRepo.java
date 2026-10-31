package com.orlandini.academiawebflux.repo;
import com.orlandini.academiawebflux.model.Role;
import reactor.core.publisher.Mono;
public interface IRoleRepo extends IGenericRepo<Role, String> {
    Mono<Role> findByName(String name);
}
