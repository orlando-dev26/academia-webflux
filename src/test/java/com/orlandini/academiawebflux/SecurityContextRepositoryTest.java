package com.orlandini.academiawebflux;
import com.orlandini.academiawebflux.security.AuthenticationManager;
import com.orlandini.academiawebflux.security.SecurityContextRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.BadCredentialsException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;
class SecurityContextRepositoryTest {
    @Test void saveEsPublicadorYNoNull() {
        var contexts = new SecurityContextRepository(mock(AuthenticationManager.class));
        StepVerifier.create(contexts.save(MockServerWebExchange.from(MockServerHttpRequest.get("/")), null)).verifyComplete();
    }
    @Test void tokenInvalidoProduceContextoVacio() {
        var manager = mock(AuthenticationManager.class);
        when(manager.authenticate(any())).thenReturn(Mono.error(new BadCredentialsException("token")));
        var contexts = new SecurityContextRepository(manager);
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").header("Authorization", "Bearer token"));
        StepVerifier.create(contexts.load(exchange)).verifyComplete();
    }
    @Test void noOcultaFallosDeInfraestructura() {
        var manager = mock(AuthenticationManager.class);
        when(manager.authenticate(any())).thenReturn(Mono.error(new IllegalStateException("infraestructura")));
        var contexts = new SecurityContextRepository(manager);
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").header("Authorization", "Bearer token"));
        StepVerifier.create(contexts.load(exchange)).expectErrorMessage("infraestructura").verify();
    }
}
