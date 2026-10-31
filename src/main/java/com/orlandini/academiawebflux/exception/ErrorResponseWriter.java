package com.orlandini.academiawebflux.exception;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;

@Component @RequiredArgsConstructor
public class ErrorResponseWriter {
    private final ServerCodecConfigurer codecs;
    public Mono<Void> write(ServerWebExchange exchange, HttpStatus status) {
        ServerResponse.Context context = new ServerResponse.Context() {
            public List<HttpMessageWriter<?>> messageWriters() { return codecs.getWriters(); }
            public List<ViewResolver> viewResolvers() { return List.of(); }
        };
        return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CustomErrorResponse(LocalDateTime.now(), ErrorMessages.forStatus(status.value())))
                .flatMap(response -> response.writeTo(exchange, context));
    }
}
