package com.orlandini.academiawebflux;
import com.orlandini.academiawebflux.model.Estudiante;
import com.orlandini.academiawebflux.repo.IGenericRepo;
import com.orlandini.academiawebflux.service.impl.CRUDImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CRUDImplTest {
    @Mock IGenericRepo<Estudiante, String> repo;
    CRUDImpl<Estudiante, String> service;
    @BeforeEach void init() {
        service = new CRUDImpl<>() {
            protected IGenericRepo<Estudiante, String> getRepo() { return repo; }
        };
    }
    @Test void updateConservaIdDeRutaYReemplazaTodosLosCampos() {
        when(repo.findById("ruta")).thenReturn(Mono.just(new Estudiante("ruta", "Antes", "Antes", "001", 30)));
        when(repo.save(any())).thenAnswer(call -> Mono.just(call.getArgument(0)));
        Estudiante enviado = new Estudiante("otro", "Ana", "Perez", "0009", 0);
        StepVerifier.create(service.update("ruta", enviado)).assertNext(value -> {
            assertEquals("ruta", value.getId()); assertEquals("Ana", value.getNombres());
            assertEquals("Perez", value.getApellidos()); assertEquals("0009", value.getDni()); assertEquals(0, value.getEdad());
        }).verifyComplete();
    }
    @Test void updateAusenteNoGuarda() {
        when(repo.findById("ausente")).thenReturn(Mono.empty());
        StepVerifier.create(service.update("ausente", new Estudiante())).verifyComplete();
        verify(repo, never()).save(any());
    }
    @Test void deleteAusenteDevuelveFalse() {
        when(repo.findById("ausente")).thenReturn(Mono.empty());
        StepVerifier.create(service.delete("ausente")).expectNext(false).verifyComplete();
        verify(repo, never()).deleteById(anyString());
    }
    @Test void deleteExistenteDevuelveTrue() {
        when(repo.findById("id")).thenReturn(Mono.just(new Estudiante()));
        when(repo.deleteById("id")).thenReturn(Mono.empty());
        StepVerifier.create(service.delete("id")).expectNext(true).verifyComplete();
    }
    @Test void errorDePersistenciaNoSeOculta() {
        when(repo.findById("id")).thenReturn(Mono.error(new IllegalStateException("fallo de persistencia")));
        StepVerifier.create(service.delete("id")).expectErrorMessage("fallo de persistencia").verify();
    }
}
