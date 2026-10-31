package com.orlandini.academiawebflux.service.impl;

import com.orlandini.academiawebflux.model.Curso;
import com.orlandini.academiawebflux.model.Matricula;
import com.orlandini.academiawebflux.repo.IMatriculaRepo;
import com.orlandini.academiawebflux.service.ICursoService;
import com.orlandini.academiawebflux.service.IEstudianteService;
import com.orlandini.academiawebflux.service.IMatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatriculaServiceImpl implements IMatriculaService {

    private final IMatriculaRepo repo;
    private final IEstudianteService estudianteService;
    private final ICursoService cursoService;

    @Override
    public Mono<Matricula> save(Matricula matricula) {
        return Mono.defer(() -> {
            validarMatricula(matricula);

            Matricula nueva = new Matricula();
            nueva.setFechaMatricula(matricula.getFechaMatricula());
            nueva.setEstado(matricula.isEstado());

            return completarEstudiante(nueva, matricula.getEstudiante().getId())
                    .flatMap(m -> completarCursos(m, matricula.getCursos()))
                    .flatMap(repo::save);
        });
    }

    @Override
    public Mono<Matricula> findById(String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Matricula no encontrada")));
    }

    private Mono<Matricula> completarEstudiante(Matricula matricula, String id) {
        return estudianteService.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estudiante no encontrado: " + id)))
                .map(estudiante -> {
                    matricula.setEstudiante(estudiante);
                    return matricula;
                });
    }

    private Mono<Matricula> completarCursos(Matricula matricula, List<Curso> referencias) {
        return Flux.fromIterable(referencias)
                .concatMap(curso -> cursoService.findById(curso.getId())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Curso no encontrado: " + curso.getId()))))
                .collectList()
                .map(cursos -> {
                    matricula.setCursos(cursos);
                    return matricula;
                });
    }

    private void validarMatricula(Matricula matricula) {
        if (matricula == null || matricula.getFechaMatricula() == null) {
            throw entradaInvalida("La matricula y su fecha son obligatorias");
        }

        if (matricula.getId() != null) {
            throw entradaInvalida("El ID de la matricula se genera al registrar");
        }

        if (matricula.getEstudiante() == null
                || matricula.getEstudiante().getId() == null
                || matricula.getEstudiante().getId().isBlank()) {
            throw entradaInvalida("El ID del estudiante es obligatorio");
        }

        if (matricula.getCursos() == null || matricula.getCursos().isEmpty()) {
            throw entradaInvalida("Debe indicar al menos un curso");
        }

        Set<String> ids = new HashSet<>();
        for (Curso curso : matricula.getCursos()) {
            if (curso == null || curso.getId() == null || curso.getId().isBlank()) {
                throw entradaInvalida("Cada curso debe tener un ID");
            }
            if (!ids.add(curso.getId())) {
                throw entradaInvalida("No se permite repetir cursos");
            }
        }
    }

    private ResponseStatusException entradaInvalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }
}