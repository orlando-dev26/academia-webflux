package com.orlandini.academiawebflux.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "matriculas")
public class Matricula {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Field
    private LocalDateTime fechaMatricula;

    @Field
    private Estudiante estudiante;

    @Field
    private List<Curso> cursos;

    @Field
    private boolean estado;
}