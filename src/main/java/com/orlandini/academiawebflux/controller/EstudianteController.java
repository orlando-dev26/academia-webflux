package com.orlandini.academiawebflux.controller;
import com.orlandini.academiawebflux.config.UriHelper;
import com.orlandini.academiawebflux.dto.EstudianteDTO;
import com.orlandini.academiawebflux.model.Estudiante;
import com.orlandini.academiawebflux.service.IEstudianteService;
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
@RequestMapping(value = "/v1/estudiantes", produces = MediaType.APPLICATION_JSON_VALUE)
public class EstudianteController {
    private final IEstudianteService service;
    @Qualifier("defaultMapper") private final ModelMapper mapper;
    private final RequestValidator validator;

    @GetMapping
    public Flux<EstudianteDTO> findAll() { return service.findAll().map(this::toDTO); }

    @GetMapping("/edad/asc")
    public Flux<EstudianteDTO> edadAsc() { return service.findAllOrderByEdadAsc().map(this::toDTO); }
    @GetMapping("/edad/desc")
    public Flux<EstudianteDTO> edadDesc() { return service.findAllOrderByEdadDesc().map(this::toDTO); }

    @GetMapping("/{id}")
    public Mono<EstudianteDTO> findById(@PathVariable String id) {
        return service.findById(id).switchIfEmpty(Mono.error(notFound())).map(this::toDTO);
    }
    @PostMapping
    public Mono<ResponseEntity<EstudianteDTO>> save(@Valid @RequestBody Mono<EstudianteDTO> body,
                                                 ServerHttpRequest request) {
        return body.switchIfEmpty(Mono.error(badRequest())).flatMap(validator::validate)
                .map(this::toDocument).flatMap(service::save)
                .map(saved -> ResponseEntity.created(UriHelper.created(request.getURI(), saved.getId()))
                        .body(toDTO(saved)));
    }
    @PutMapping("/{id}")
    public Mono<EstudianteDTO> update(@PathVariable String id, @Valid @RequestBody Mono<EstudianteDTO> body) {
        return body.switchIfEmpty(Mono.error(badRequest())).flatMap(validator::validate)
                .map(this::toDocument).flatMap(value -> service.update(id, value))
                .switchIfEmpty(Mono.error(notFound())).map(this::toDTO);
    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id).flatMap(deleted -> deleted
                ? Mono.just(ResponseEntity.noContent().<Void>build()) : Mono.error(notFound()));
    }
    private EstudianteDTO toDTO(Estudiante value) { return mapper.map(value, EstudianteDTO.class); }
    private Estudiante toDocument(EstudianteDTO value) { return mapper.map(value, Estudiante.class); }
    private ResponseStatusException notFound() { return new ResponseStatusException(HttpStatus.NOT_FOUND); }
    private ResponseStatusException badRequest() { return new ResponseStatusException(HttpStatus.BAD_REQUEST); }
}
