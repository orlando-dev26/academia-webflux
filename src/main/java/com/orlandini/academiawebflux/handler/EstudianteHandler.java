package com.orlandini.academiawebflux.handler;
import com.orlandini.academiawebflux.config.UriHelper;
import com.orlandini.academiawebflux.dto.EstudianteDTO;
import com.orlandini.academiawebflux.model.Estudiante;
import com.orlandini.academiawebflux.service.IEstudianteService;
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
public class EstudianteHandler {
    private final IEstudianteService service;
    @Qualifier("defaultMapper") private final ModelMapper mapper;
    private final RequestValidator validator;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::toDTO), EstudianteDTO.class);
    }

    public Mono<ServerResponse> edadAsc(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.findAllOrderByEdadAsc().map(this::toDTO), EstudianteDTO.class);
    }
    public Mono<ServerResponse> edadDesc(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(service.findAllOrderByEdadDesc().map(this::toDTO), EstudianteDTO.class);
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
    private Mono<EstudianteDTO> body(ServerRequest request) {
        return request.bodyToMono(EstudianteDTO.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap(validator::validate);
    }
    private EstudianteDTO toDTO(Estudiante value) { return mapper.map(value, EstudianteDTO.class); }
    private Estudiante toDocument(EstudianteDTO value) { return mapper.map(value, Estudiante.class); }
    private ResponseStatusException notFound() { return new ResponseStatusException(HttpStatus.NOT_FOUND); }
}
