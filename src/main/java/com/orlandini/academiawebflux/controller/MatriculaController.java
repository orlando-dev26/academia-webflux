package com.orlandini.academiawebflux.controller;
import com.orlandini.academiawebflux.config.UriHelper;
import com.orlandini.academiawebflux.dto.*;
import com.orlandini.academiawebflux.model.Matricula;
import com.orlandini.academiawebflux.service.IMatriculaService;
import com.orlandini.academiawebflux.validator.RequestValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController @RequiredArgsConstructor
@RequestMapping(value = "/v1/matriculas", produces = MediaType.APPLICATION_JSON_VALUE)
public class MatriculaController {
    private final IMatriculaService service;
    @Qualifier("defaultMapper") private final ModelMapper mapper;
    private final RequestValidator validator;
    @PostMapping
    public Mono<ResponseEntity<MatriculaDTO>> save(@Valid @RequestBody Mono<MatriculaRequestDTO> body,
                                                  ServerHttpRequest request) {
        return body.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap(validator::validate).map(dto -> mapper.map(dto, Matricula.class))
                .flatMap(service::save).map(value -> ResponseEntity
                        .created(UriHelper.created(request.getURI(), value.getId()))
                        .body(mapper.map(value, MatriculaDTO.class)));
    }
    @GetMapping("/{id}")
    public Mono<MatriculaDTO> findById(@PathVariable String id) {
        return service.findById(id).map(value -> mapper.map(value, MatriculaDTO.class));
    }
}
