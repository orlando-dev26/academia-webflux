package com.orlandini.academiawebflux;
import com.orlandini.academiawebflux.config.MapperConfig;
import com.orlandini.academiawebflux.dto.*;
import com.orlandini.academiawebflux.model.*;
import jakarta.validation.*;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class MapperValidationTest {
    ModelMapper mapper = new MapperConfig().defaultMapper();
    static ValidatorFactory factory;
    @BeforeAll static void start() { factory = Validation.buildDefaultValidatorFactory(); }
    @AfterAll static void stop() { factory.close(); }
    @Test void estudianteCompletoYEntradaNoControlaId() {
        Estudiante entity = new Estudiante("id", "Ana", "Perez", "001", 0);
        EstudianteDTO dto = mapper.map(entity, EstudianteDTO.class);
        assertEquals("id", dto.getId()); assertEquals("001", dto.getDni()); assertEquals(0, dto.getEdad());
        Estudiante copy = mapper.map(dto, Estudiante.class);
        assertNull(copy.getId()); assertEquals("Ana", copy.getNombres()); assertEquals("Perez", copy.getApellidos());
        assertEquals("001", copy.getDni()); assertEquals(0, copy.getEdad());
    }
    @Test void booleanFalseEsDatoValidoYSeMapea() {
        CursoDTO dto = new CursoDTO("enviado", "Java", "JV", false);
        assertTrue(factory.getValidator().validate(dto).isEmpty());
        Curso doc = mapper.map(dto, Curso.class);
        assertNull(doc.getId()); assertFalse(doc.isEstado());
        CursoDTO out = mapper.map(new Curso("id", "Java", "JV", false), CursoDTO.class);
        assertEquals("id", out.getId()); assertEquals("JV", out.getSiglas()); assertEquals(false, out.getEstado());
        dto.setEstado(null); assertFalse(factory.getValidator().validate(dto).isEmpty());
    }
    @Test void referenciasSeMapeanSinConfundirIdRaiz() {
        var fecha = LocalDateTime.of(2026, 10, 30, 12, 0);
        var dto = new MatriculaRequestDTO(fecha, new ReferenciaDTO("e1"),
                List.of(new ReferenciaDTO("c2"), new ReferenciaDTO("c1")), false);
        Matricula doc = mapper.map(dto, Matricula.class);
        assertNull(doc.getId()); assertEquals(fecha, doc.getFechaMatricula()); assertFalse(doc.isEstado());
        assertEquals("e1", doc.getEstudiante().getId());
        assertEquals(List.of("c2", "c1"), doc.getCursos().stream().map(Curso::getId).toList());
    }
    @Test void validaReferenciasYElementosEnCascada() {
        var dto = new MatriculaRequestDTO(LocalDateTime.now(), new ReferenciaDTO("e1"), List.of(new ReferenciaDTO("c1")), false);
        Validator validator = factory.getValidator();
        assertTrue(validator.validate(dto).isEmpty());
        dto.setEstudiante(new ReferenciaDTO(" ")); assertFalse(validator.validate(dto).isEmpty());
        dto.setEstudiante(new ReferenciaDTO("e1")); dto.setCursos(List.of(new ReferenciaDTO("")));
        assertFalse(validator.validate(dto).isEmpty());
        dto.setCursos(Arrays.asList((ReferenciaDTO)null)); assertFalse(validator.validate(dto).isEmpty());
        dto.setCursos(List.of()); assertFalse(validator.validate(dto).isEmpty());
        dto.setCursos(List.of(new ReferenciaDTO("c1"))); dto.setEstado(null);
        assertFalse(validator.validate(dto).isEmpty());
    }
}
