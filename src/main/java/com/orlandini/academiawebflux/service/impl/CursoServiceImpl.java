package com.orlandini.academiawebflux.service.impl;

import com.orlandini.academiawebflux.model.Curso;
import com.orlandini.academiawebflux.repo.ICursoRepo;
import com.orlandini.academiawebflux.repo.IGenericRepo;
import com.orlandini.academiawebflux.service.ICursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl
        extends CRUDImpl<Curso, String>
        implements ICursoService {

    private final ICursoRepo repo;

    @Override
    protected IGenericRepo<Curso, String> getRepo() {
        return repo;
    }
}