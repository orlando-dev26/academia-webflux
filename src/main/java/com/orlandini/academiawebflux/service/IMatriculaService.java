package com.orlandini.academiawebflux.service;

import com.orlandini.academiawebflux.model.Matricula;
import reactor.core.publisher.Mono;

public interface IMatriculaService {

    Mono<Matricula> save(Matricula matricula);

    Mono<Matricula> findById(String id);
}















