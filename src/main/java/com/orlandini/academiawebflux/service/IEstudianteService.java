package com.orlandini.academiawebflux.service;

import com.orlandini.academiawebflux.model.Estudiante;
import reactor.core.publisher.Flux;

public interface IEstudianteService
        extends ICRUD<Estudiante, String> {

    Flux<Estudiante> findAllOrderByEdadAsc();

    Flux<Estudiante> findAllOrderByEdadDesc();
}