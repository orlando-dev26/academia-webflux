# Evidencia de verificación

Ejecución real: 30/10/2026, finalizada a las 21:09:25 (America/Lima).

Última repetición del backend sin interfaz gráfica: **30/10/2026 a las 21:39:34 (America/Lima)**, con las mismas 41 ejecuciones correctas y BUILD SUCCESS. La colección y las capturas de Postman se preparan por separado; todavía no se han ejecutado ni obtenido en Postman.

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\verificar.ps1 -Mode verify
```

Entorno: Windows 11, Temurin 25.0.5, Spring Boot 4.1.2, Maven Wrapper 3.10.0, MongoDB Server 9.0 nativo en localhost:27017. Se generó el JAR ejecutable mediante el plugin de Spring Boot.

| Clase | Ejecuciones | Fallos | Errores | Omitidas |
| --- | ---: | ---: | ---: | ---: |
| AcademiaApiIntegrationTest | 27 | 0 | 0 | 0 |
| AcademiaWebfluxApplicationTests | 2 | 0 | 0 | 0 |
| CRUDImplTest | 5 | 0 | 0 | 0 |
| MapperValidationTest | 4 | 0 | 0 | 0 |
| SecurityContextRepositoryTest | 3 | 0 | 0 | 0 |
| **Total** | **41** | **0** | **0** | **0** |

Resultado Maven: **BUILD SUCCESS**. Los reportes originales de esta ejecución están en `target/surefire-reports` y la salida de Maven en `.local/maven-verify.log`; son archivos locales, no fuentes para Git.

## Cobertura funcional comprobada

- CRUD completo de estudiantes y cursos por v1/v2: todos los campos, persistencia real, ID del servidor, PUT sobre el ID de la ruta, actualización completa, DELETE 204 y ausencias 404.
- Listados vacíos y orden de edad ascendente/descendente con empates estabilizados por ID.
- Registro de matrícula con estudiante/cursos completos, consulta entre estilos y comprobación directa del documento Mongo. El histórico sobrevive a actualización y eliminación de maestros.
- Referencias inexistentes y cursos duplicados: no se guarda una matrícula incompleta.
- ModelMapper real, ID de raíz separado de IDs anidados, validación Jakarta en cascada, omisiones, false válido, cuerpo vacío, JSON mal formado y tipo de contenido inválido.
- Login en ambos estilos, credenciales incorrectas, usuario inexistente/deshabilitado, roles del token y acceso de usuario regular.
- Todas las rutas de negocio rechazan token ausente, mal formado, vencido, firmado con otra clave o sin expiración. Tokens válidos recorren las operaciones positivas. Deshabilitar el usuario después del login produce 401.
- Formato JSON de errores y fechas en ambos estilos. La escritura 403 se comprueba con un intercambio simulado y los codecs reales: no se añadió una operación de negocio reservada a ADMIN para fabricar ese caso.
- Contrato genérico de actualización/borrado, propagación de fallos del repositorio y `save` del contexto devuelve Mono vacío, nunca null.

## Aislamiento y alcance

La integración levanta un servidor HTTP real en puerto aleatorio y usa WebTestClient contra ese puerto. Crea una base `academia_it_<UUID>` en el MongoDB local y solo elimina esa base generada al terminar, con comprobación del nombre. No limpia `academia_db` ni los materiales del curso. No requiere Docker/Testcontainers.

Los 14 casos restantes comprueban componentes; el nombre heredado `AcademiaWebfluxApplicationTests` aloja dos casos de JWT/configuración, no una segunda batería de integración completa. Las variantes parametrizadas se cuentan como ejecuciones independientes.

No se midió cobertura porcentual, carga concurrente, penetración ni despliegue cloud. No se ejecutaron subagentes independientes: las definiciones y workflows se entregan como artefactos adaptados del curso. Las pruebas sí se ejecutaron realmente.

## Correcciones que resolvieron fallos reales

- Ruta corta de sockets locales de JDK 25 en Windows: evita `Invalid argument: connect` con TEMP virtualizado. El script configura `jdk.net.unixdomain.tmpdir` al ejecutar pruebas.
- Índice único solo en username; no anotar Role.name con índice único, porque Role también se embebe en users y Mongo podría crear un índice no deseado sobre roles.name.
- Los DTO tienen ID de solo lectura para entradas HTTP. Las pruebas leen el JSON de respuesta como Map al verificar IDs: deserializar la respuesta en el mismo DTO ignoraría esos IDs por diseño.
- Validación de elementos de lista con `List<@NotNull @Valid ReferenciaDTO>`, de acuerdo con el validador efectivo del proyecto.

Los avisos de APIs internas de Lombok/ModelMapper y de instrumentación de Mockito no impidieron compilar ni ejecutar la batería. No se ocultaron errores para obtener el resultado.

## Comprobación de la instancia final

Después de generar el JAR se inició la aplicación local con scripts/iniciar.ps1 -Restart. Ambos logins respondieron correctamente, v1/v2 devolvieron 401 sin token y los listados autenticados respondieron 200. Se cargaron cuatro estudiantes, tres cursos y una matrícula ficticios, recuperando el documento completo desde v1/v2. La revisión posterior confirmó asc/desc sobre los cuatro estudiantes sin reinsertar datos. Esta demostración adicional no se contabiliza como nuevas pruebas JUnit.

Resumen de Maven extraído de los XML reales: [resultado-pruebas.json](resultado-pruebas.json).
