# Seguridad JWT de academia

Estado: implementada y verificada localmente, 30/10/2026.

## Parte 1: especificación

Proteger todos los servicios de academia con Spring Security y JWT siguiendo la estructura del curso. Login público por username/password; contraseñas BCrypt. users contiene id, username, password, status y roles List<Role>; roles contiene id y name. Los usuarios guardan referencias a roles, cuyos nombres se resuelven en MongoDB.

POST /login con anotaciones y POST /v2/login funcional devuelven access_token. Las demás rutas requieren JWT firmado y vigente. Firma HS256, secreto externo Base64 de al menos 32 bytes, expiración positiva y obligatoria. El usuario debe seguir habilitado; resolver roles actuales al autenticar la petición.

No inventar una restricción por rol a los CRUD: el PDF exige autenticación, no una distribución ADMIN/USER de operaciones. Mantener el formato datetime/message para errores 401/403 y los del handler global.

No usar password o claves reales en código/Git. Semilla optativa configurada externamente; crear solo si no existe, sin reemplazar contraseña de cuenta existente. No guardar sesión ni devolver null desde save del contexto.

## Parte 2: implementación y verificación

- security: AuthRequest/Response, AuthService, ReactiveUserService, JwtUtil, AuthenticationManager, SecurityContextRepository, entry point, denied handler y WebSecurityConfig.
- model User/Role y repositorios IUserRepo/IRoleRepo conservan el esquema del profesor. Índice único en username; Role se embebe también en users, por lo que name no declara un índice único propagable.
- InitialDataConfig permite una semilla optativa; scripts/preparar-local.ps1 genera valores privados distintos por instalación.
- Controllers/handlers de login comparten AuthService. ErrorResponseWriter conserva el contrato de errores.
- Se verificaron ambos logins, BCrypt, roles, credenciales incorrectas y usuario deshabilitado. Cada ruta de negocio se probó sin token, con token mal formado, vencido, con otra firma y sin exp. Los casos positivos usan login y JWT reales. Deshabilitar usuario después del login revoca acceso.
- save del contexto devuelve Mono.empty; un fallo de MongoDB no se camufla como credenciales inválidas.
- Comando real: scripts/verificar.ps1 -Mode verify. Batería total 41 ejecuciones correctas, sin fallos/errores/omitidas. Detalle en docs/VERIFICACION.md.

Decisiones propias: exigir exp explícita y volver a consultar usuario/roles en cada petición. No incluye refresh tokens, administración de cuentas ni endpoints de menús; no son exigencias de esta evaluación.
