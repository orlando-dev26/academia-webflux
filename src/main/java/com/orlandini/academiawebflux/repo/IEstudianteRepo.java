package com.orlandini.academiawebflux.repo;

import com.orlandini.academiawebflux.model.Estudiante;
import reactor.core.publisher.Flux;

public interface IEstudianteRepo
        extends IGenericRepo<Estudiante, String> {

    Flux<Estudiante> findAllByOrderByEdadAscIdAsc();

    Flux<Estudiante> findAllByOrderByEdadDescIdAsc();
}