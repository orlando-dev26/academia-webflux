package com.orlandini.academiawebflux.handler;
import com.orlandini.academiawebflux.config.UriHelper;
import com.orlandini.academiawebflux.dto.CursoDTO;
import com.orlandini.academiawebflux.model.Curso;
import com.orlandini.academiawebflux.service.ICursoService;
import com.orlandini.academiawebflux.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component @RequiredArgsConstructor
public class CursoHandler {
    private final ICursoService service;
    @Qualifier("defaultMapper") private final ModelMapper mapper;
    private final RequestValidator validator;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::toDTO), CursoDTO.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        return service.findById(request.pathVariable("id")).switchIfEmpty(Mono.error(notFound()))
                .flatMap(value -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(toDTO(value)));
    }
    public Mono<ServerResponse> save(ServerRequest request) {
        return body(request).map(this::toDocument).flatMap(service::save)
                .flatMap(value -> ServerResponse.created(UriHelper.created(request.uri(), value.getId()))
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(toDTO(value)));
    }
    public Mono<ServerResponse> update(ServerRequest request) {
        return body(request).map(this::toDocument)
                .flatMap(value -> service.update(request.pathVariable("id"), value))
                .switchIfEmpty(Mono.error(notFound()))
                .flatMap(value -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(toDTO(value)));
    }
    public Mono<ServerResponse> delete(ServerRequest request) {
        return service.delete(request.pathVariable("id")).flatMap(deleted -> deleted
                ? ServerResponse.noContent().build() : Mono.error(notFound()));
    }
    private Mono<CursoDTO> body(ServerRequest request) {
        return request.bodyToMono(CursoDTO.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap(validator::validate);
    }
    private CursoDTO toDTO(Curso value) { return mapper.map(value, CursoDTO.class); }
    private Curso toDocument(CursoDTO value) { return mapper.map(value, Curso.class); }
    private ResponseStatusException notFound() { return new ResponseStatusException(HttpStatus.NOT_FOUND); }
}
