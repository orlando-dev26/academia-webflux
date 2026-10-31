package com.orlandini.academiawebflux.service.impl;

import com.orlandini.academiawebflux.model.Estudiante;
import com.orlandini.academiawebflux.repo.IEstudianteRepo;
import com.orlandini.academiawebflux.repo.IGenericRepo;
import com.orlandini.academiawebflux.service.IEstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl
        extends CRUDImpl<Estudiante, String>
        implements IEstudianteService {

    private final IEstudianteRepo repo;

    @Override
    protected IGenericRepo<Estudiante, String> getRepo() {
        return repo;
    }

    @Override
    public Flux<Estudiante> findAllOrderByEdadAsc() {
        return repo.findAllByOrderByEdadAscIdAsc();
    }

    @Override
    public Flux<Estudiante> findAllOrderByEdadDesc() {
        return repo.findAllByOrderByEdadDescIdAsc();
    }
}