package com.orlandini.academiawebflux.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @AllArgsConstructor @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatriculaDTO {
    private String id;
    private LocalDateTime fechaMatricula;
    private EstudianteDTO estudiante;
    private List<CursoDTO> cursos;
    private Boolean estado;
}
