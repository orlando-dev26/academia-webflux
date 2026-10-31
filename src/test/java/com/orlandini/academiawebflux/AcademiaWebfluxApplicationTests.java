package com.orlandini.academiawebflux;
import com.orlandini.academiawebflux.security.JwtUtil;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AcademiaWebfluxApplicationTests {
    private static String secret(int b) {
        byte[] bytes = new byte[48]; Arrays.fill(bytes, (byte)b);
        return Base64.getEncoder().encodeToString(bytes);
    }
    @Test void exigeClaveFuerteYVigenciaPositiva() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil("", Duration.ofHours(1)));
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil("no-es-base64", Duration.ofHours(1)));
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil(Base64.getEncoder().encodeToString(new byte[8]), Duration.ofHours(1)));
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil(secret(1), Duration.ZERO));
    }
    @Test void firmaSubjectRolesYClaveIncorrecta() {
        JwtUtil jwt = new JwtUtil(secret(1), Duration.ofHours(1));
        String token = jwt.generate(User.withUsername("alumno").password("hash").authorities("ADMIN").build());
        Claims claims = jwt.validate(token);
        assertEquals("alumno", claims.getSubject());
        assertEquals(List.of("ADMIN"), claims.get("roles"));
        assertTrue(claims.getExpiration().after(new Date()));
        assertThrows(JwtException.class, () -> new JwtUtil(secret(2), Duration.ofHours(1)).validate(token));
        assertThrows(JwtException.class, () -> jwt.validate("malformado"));
    }
}
