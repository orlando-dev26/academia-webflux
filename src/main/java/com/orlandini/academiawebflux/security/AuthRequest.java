package com.orlandini.academiawebflux.security;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data @AllArgsConstructor @NoArgsConstructor
public class AuthRequest {
    @NotBlank private String username;
    @NotBlank @ToString.Exclude private String password;
}
