package com.orlandini.academiawebflux.handler;
import com.orlandini.academiawebflux.config.UriHelper;
import com.orlandini.academiawebflux.dto.*;
import com.orlandini.academiawebflux.model.Matricula;
import com.orlandini.academiawebflux.service.IMatriculaService;
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
public class MatriculaHandler {
    private final IMatriculaService service;
    @Qualifier("defaultMapper") private final ModelMapper mapper;
    private final RequestValidator validator;
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(MatriculaRequestDTO.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)))
                .flatMap(validator::validate).map(dto -> mapper.map(dto, Matricula.class))
                .flatMap(service::save).flatMap(value -> ServerResponse
                        .created(UriHelper.created(request.uri(), value.getId()))
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(mapper.map(value, MatriculaDTO.class)));
    }
    public Mono<ServerResponse> findById(ServerRequest request) {
        return service.findById(request.pathVariable("id")).flatMap(value -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON).bodyValue(mapper.map(value, MatriculaDTO.class)));
    }
}
