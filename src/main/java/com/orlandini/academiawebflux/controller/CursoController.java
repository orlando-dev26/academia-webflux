package com.orlandini.academiawebflux.controller;
import com.orlandini.academiawebflux.config.UriHelper;
import com.orlandini.academiawebflux.dto.CursoDTO;
import com.orlandini.academiawebflux.model.Curso;
import com.orlandini.academiawebflux.service.ICursoService;
import com.orlandini.academiawebflux.validator.RequestValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.*;

@RestController @RequiredArgsConstructor
@RequestMapping(value = "/v1/cursos", produces = MediaType.APPLICATION_JSON_VALUE)
public class CursoController {
    private final ICursoService service;
    @Qualifier("defaultMapper") private final ModelMapper mapper;
    private final RequestValidator validator;

    @GetMapping
    public Flux<CursoDTO> findAll() { return service.findAll().map(this::toDTO); }

    @GetMapping("/{id}")
    public Mono<CursoDTO> findById(@PathVariable String id) {
        return service.findById(id).switchIfEmpty(Mono.error(notFound())).map(this::toDTO);
    }
    @PostMapping
    public Mono<ResponseEntity<CursoDTO>> save(@Valid @RequestBody Mono<CursoDTO> body,
                                                 ServerHttpRequest request) {
        return body.switchIfEmpty(Mono.error(badRequest())).flatMap(validator::validate)
                .map(this::toDocument).flatMap(service::save)
                .map(saved -> ResponseEntity.created(UriHelper.created(request.getURI(), saved.getId()))
                        .body(toDTO(saved)));
    }
    @PutMapping("/{id}")
    public Mono<CursoDTO> update(@PathVariable String id, @Valid @RequestBody Mono<CursoDTO> body) {
        return body.switchIfEmpty(Mono.error(badRequest())).flatMap(validator::validate)
                .map(this::toDocument).flatMap(value -> service.update(id, value))
                .switchIfEmpty(Mono.error(notFound())).map(this::toDTO);
    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id).flatMap(deleted -> deleted
                ? Mono.just(ResponseEntity.noContent().<Void>build()) : Mono.error(notFound()));
    }
    private CursoDTO toDTO(Curso value) { return mapper.map(value, CursoDTO.class); }
    private Curso toDocument(CursoDTO value) { return mapper.map(value, Curso.class); }
    private ResponseStatusException notFound() { return new ResponseStatusException(HttpStatus.NOT_FOUND); }
    private ResponseStatusException badRequest() { return new ResponseStatusException(HttpStatus.BAD_REQUEST); }
}
