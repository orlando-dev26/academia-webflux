package com.orlandini.academiawebflux.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @AllArgsConstructor @NoArgsConstructor
public class MatriculaRequestDTO {
    @NotNull(message = "La fecha de matricula es obligatoria")
    private LocalDateTime fechaMatricula;
    @NotNull(message = "El estudiante es obligatorio") @Valid
    private ReferenciaDTO estudiante;
    @NotEmpty(message = "Debe indicar al menos un curso")
    private List<@NotNull(message = "La referencia del curso es obligatoria") @Valid ReferenciaDTO> cursos;
    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
