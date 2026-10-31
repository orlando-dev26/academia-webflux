package com.orlandini.academiawebflux.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data @AllArgsConstructor @NoArgsConstructor
public class ReferenciaDTO {
    @NotBlank(message = "El ID de la referencia es obligatorio")
    private String id;
}
