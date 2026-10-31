package com.orlandini.academiawebflux.exception;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Component @Order(-2)
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {
    public WebExceptionHandler(ErrorAttributes attributes, ApplicationContext context,
                               ServerCodecConfigurer codecs) {
        super(attributes, new WebProperties.Resources(), context);
        setMessageWriters(codecs.getWriters());
        setMessageReaders(codecs.getReaders());
    }
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes attributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::render);
    }
    private Mono<ServerResponse> render(ServerRequest request) {
        int status = (int) getErrorAttributes(request, ErrorAttributeOptions.defaults())
                .getOrDefault("status", 500);
        if (status >= 500) {
            // No exponer causas internas, tokens ni cuerpos de petición al cliente.
            org.slf4j.LoggerFactory.getLogger(getClass()).error("Error interno en {}", request.path(), getError(request));
        }
        return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CustomErrorResponse(LocalDateTime.now(), ErrorMessages.forStatus(status)));
    }
}
