package com.orlandini.academiawebflux.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "users")
public class User {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Field
    @org.springframework.data.mongodb.core.index.Indexed(unique = true)
    private String username;

    @Field
    @lombok.ToString.Exclude
    private String password;

    @Field
    private boolean status;

    @Field
    private List<Role> roles;
}
