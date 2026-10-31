package com.orlandini.academiawebflux.service.impl;

import com.orlandini.academiawebflux.repo.IGenericRepo;
import com.orlandini.academiawebflux.service.ICRUD;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public Mono<T> save(T t) {
        return getRepo().save(t);
    }

    @Override
    public Mono<T> update(ID id, T t) {
        return getRepo().findById(id)
                .flatMap(existente -> {
                    try {
                        Method method = t.getClass()
                                .getMethod("setId", id.getClass());

                        method.invoke(t, id);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }

                    return getRepo().save(t);
                });
    }

    @Override
    public Flux<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public Mono<Boolean> delete(ID id) {
        return getRepo().findById(id)
                .flatMap(existente ->
                        getRepo().deleteById(id).thenReturn(true))
                .defaultIfEmpty(false);
    }
}