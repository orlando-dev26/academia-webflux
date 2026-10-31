# Contrato HTTP

Base local: `http://localhost:8080`. Cuerpos y respuestas JSON. Salvo login, enviar `Authorization: Bearer <token>`. `/v1` usa anotaciones y `/v2` rutas funcionales; comparten servicios, colecciones y contrato.

## Rutas

Sustituir `{v}` por `v1` o `v2` y `{id}` por un ID real.

| Método | Ruta | Resultado correcto |
| --- | --- | --- |
| POST | `/{v}/estudiantes` | 201, DTO y Location |
| GET | `/{v}/estudiantes` | 200, lista |
| GET | `/{v}/estudiantes/{id}` | 200, DTO |
| PUT | `/{v}/estudiantes/{id}` | 200, DTO actualizado |
| DELETE | `/{v}/estudiantes/{id}` | 204, sin cuerpo |
| GET | `/{v}/estudiantes/edad/asc` | 200, menor a mayor edad |
| GET | `/{v}/estudiantes/edad/desc` | 200, mayor a menor edad |
| POST | `/{v}/cursos` | 201, DTO y Location |
| GET | `/{v}/cursos` | 200, lista |
| GET | `/{v}/cursos/{id}` | 200, DTO |
| PUT | `/{v}/cursos/{id}` | 200, DTO actualizado |
| DELETE | `/{v}/cursos/{id}` | 204, sin cuerpo |
| POST | `/{v}/matriculas` | 201, documento completo y Location |
| GET | `/{v}/matriculas/{id}` | 200, documento histórico (operación adicional) |
| POST | `/login` | 200, access_token; anotaciones, público |
| POST | `/v2/login` | 200, access_token; funcional, público |

Son 26 combinaciones requeridas de negocio, más dos consultas de matrícula y dos logins: 30 combinaciones método/ruta. Los empates de edad se ordenan por ID ascendente para estabilizar ambos listados.

## Cuerpos de entrada

Estudiante (POST o PUT):

```json
{"nombres":"Ana","apellidos":"Perez","dni":"00123456","edad":24}
```

Curso (POST o PUT):

```json
{"nombre":"Programacion reactiva","siglas":"WEBFLUX","estado":true}
```

Los IDs de estudiantes/cursos se generan en el servidor. En PUT manda el ID de la ruta: se reemplazan los campos del recurso existente; no crea uno nuevo si falta. Enviar todos los campos. El DNI es texto y conserva ceros iniciales. Nombres, apellidos, DNI, nombre y siglas no pueden estar vacíos; edad es obligatoria y no negativa. El estado del curso es obligatorio y `false` es válido.

Matrícula (sustituir IDs por los devueltos al crear maestros):

```json
{
  "fechaMatricula":"2026-10-30T10:30:00",
  "estudiante":{"id":"ID_REAL_ESTUDIANTE"},
  "cursos":[{"id":"ID_REAL_CURSO_1"},{"id":"ID_REAL_CURSO_2"}],
  "estado":true
}
```

La API recibe referencias mínimas, consulta al estudiante y todos los cursos y guarda sus campos completos en un único documento de `matriculas`. No necesita una transacción de varias escrituras. Si falta una referencia, responde 404 y no guarda la matrícula. Fecha, estado, estudiante y lista de cursos son obligatorios; no admite lista vacía, elementos nulos, IDs vacíos ni cursos repetidos.

La respuesta incluye `id`, `fechaMatricula`, estudiante completo, cursos completos y `estado`. Las modificaciones o eliminaciones posteriores de los maestros conservan esta copia histórica. La matrícula no impone una prohibición adicional a cursos con estado false: el PDF no fija esa regla.

## Autenticación

POST `/login` o `/v2/login`:

```json
{"username":"admin","password":"PASSWORD_PRIVADO_LOCAL"}
```

Respuesta 200:

```json
{"access_token":"JWT_FIRMADO"}
```

JWT HS256 con `sub`, `roles`, `iat` y `exp`. El servidor verifica firma y expiración; rechaza también tokens sin expiración. Consulta usuario habilitado y roles actuales en MongoDB en cada petición autenticada. Deshabilitar el usuario invalida su acceso aunque el token no haya vencido. ADMIN y USER pueden consumir estos servicios: no se inventó una restricción de CRUD por rol que el PDF no exige.

`users`: id, username, password BCrypt, status, roles List<Role> con IDs. `roles`: id y name. El password nunca se devuelve por la API. El login no crea cuentas. La semilla inicial es optativa y se configura fuera del código.

## Errores comunes

```json
{"datetime":"2026-10-30T21:00:00","message":"Recurso no encontrado"}
```

| Estado | Situación | message |
| --- | --- | --- |
| 400 | Cuerpo ausente, mal formado, campos o referencias de entrada inválidos | Solicitud invalida o datos incompletos |
| 401 | Falta token, firma/expiración inválida, login incorrecto o usuario deshabilitado | Autenticacion requerida o credenciales invalidas |
| 403 | Acceso denegado por la cadena de seguridad cuando corresponda | Acceso denegado |
| 404 | Recurso o referencia inexistente | Recurso no encontrado |
| 405 | Método HTTP no permitido | Metodo no permitido |
| 415 | Tipo de contenido no admitido | Tipo de contenido no admitido |
| 500 | Fallo interno | Error interno del servidor |

Los detalles internos no se exponen. Un listado sin datos devuelve `[]` con 200. La capa de seguridad se ejecuta antes de la validación: una solicitud sin token recibe 401 aunque su cuerpo sea inválido.
