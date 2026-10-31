package com.orlandini.academiawebflux.config;
import com.orlandini.academiawebflux.dto.*;
import com.orlandini.academiawebflux.model.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper defaultMapper() {
        ModelMapper mapper = new ModelMapper();
        // Nombres iguales y asociaciones explícitas: evita confundir IDs anidados.
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.createTypeMap(EstudianteDTO.class, Estudiante.class)
                .addMappings(m -> m.skip(Estudiante::setId));
        mapper.createTypeMap(CursoDTO.class, Curso.class)
                .addMappings(m -> m.skip(Curso::setId));
        mapper.createTypeMap(Estudiante.class, EstudianteDTO.class);
        mapper.createTypeMap(Curso.class, CursoDTO.class);
        Converter<ReferenciaDTO, Estudiante> estudiante = ctx -> {
            if (ctx.getSource() == null) return null;
            Estudiante e = new Estudiante();
            e.setId(ctx.getSource().getId());
            return e;
        };
        Converter<List<ReferenciaDTO>, List<Curso>> cursos = ctx -> {
            if (ctx.getSource() == null) return null;
            return ctx.getSource().stream().map(ref -> {
                Curso c = new Curso();
                c.setId(ref.getId());
                return c;
            }).toList();
        };
        mapper.emptyTypeMap(MatriculaRequestDTO.class, Matricula.class)
                .addMappings(m -> {
                    m.skip(Matricula::setId);
                    m.map(MatriculaRequestDTO::getFechaMatricula, Matricula::setFechaMatricula);
                    m.map(MatriculaRequestDTO::getEstado, Matricula::setEstado);
                    m.using(estudiante).map(MatriculaRequestDTO::getEstudiante, Matricula::setEstudiante);
                    m.using(cursos).map(MatriculaRequestDTO::getCursos, Matricula::setCursos);
                });
        mapper.createTypeMap(Matricula.class, MatriculaDTO.class);
        return mapper;
    }
}
