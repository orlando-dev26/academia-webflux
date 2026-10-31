# Cumplimiento de la evaluación final

La página del enunciado y la clase 25 fijan el alcance. Las decisiones de contrato y validación se describen como adaptaciones propias en la spec y en API.md.

| ID | Requisito | Evidencia entregada | Estado |
| --- | --- | --- | --- |
| RF-01 | CRUD estudiantes | EstudianteController/Handler, IEstudianteService y repositorio; integración v1/v2 | Cumplido |
| RF-02 | CRUD cursos | CursoController/Handler, ICursoService y repositorio; integración v1/v2 | Cumplido |
| RF-03 | Registrar matrícula como documento Mongo | MatriculaServiceImpl, DTO de entrada/salida, referencias resueltas y copia completa; integración y consulta de documento | Cumplido |
| RF-04 | Edad ascendente | findAllByOrderByEdadAscIdAsc; rutas edad/asc en ambos estilos; prueba con empates | Cumplido |
| RF-05 | Edad descendente | findAllByOrderByEdadDescIdAsc; rutas edad/desc en ambos estilos; prueba con empates | Cumplido |
| IA-01 | Agentes, workflows y spec necesarios | Cuatro definiciones de agentes, tres workflows, specs academia/seguridad y CLAUDE.md | Cumplido como archivos; sin ejecución independiente de subagentes |
| RT-01 | Spring Boot 4.x+ | POM 4.1.2; verify y JAR ejecutable | Cumplido |
| RT-02 | MongoDB | Repositorios reactivos y pruebas sobre MongoDB Server real | Cumplido |
| RT-03 | Todos los servicios en ambos estilos | 26 combinaciones mínimas, más GET de matrícula por estilo; mismo servicio y base | Cumplido |
| RT-04 | Errores globales | WebExceptionHandler, ErrorResponseWriter, JSON datetime/message, integración y 403 de componente | Cumplido |
| RT-05 | Spring Security y JWT | SecurityWebFilterChain, login, manager y contexto; todos los endpoints de negocio protegidos | Cumplido |
| RT-06 | Estructura users/roles del curso | User y Role conservan campos; BCrypt, referencias de roles y semilla | Cumplido |
| RT-07 | Solo tokens vigentes | Firma HS256, exp obligatoria, tokens negativos en todas las rutas, positivos y usuario habilitado | Cumplido |
| EN-01 | Repositorio público del alumno | [orlando-dev26/academia-webflux](https://github.com/orlando-dev26/academia-webflux), fuentes y evidencias publicadas y verificadas el 31/10/2026 | Cumplido |
| EN-02 | .gitignore | Excluye target, IDE, .local, .env, logs y dist; ZIP de fuentes con lista permitida | Cumplido |
| EN-03 | Presentación por correo | Borrador con asunto y contenido en ENTREGA.md | Pendiente de envío por el alumno |

Resultado de verificación: **41 pruebas correctas**, 0 fallos/errores/omitidas. [Evidencia detallada](VERIFICACION.md).

## Correspondencia con las clases

- Clases 3, 4 y 6: documentos, repositorios reactivos y CRUD genérico.
- Clases 7 a 9: DTO, ModelMapper, validación y errores globales.
- Clases 11, 14 y 20: constructor/revisor, datos y pruebas con workflows.
- Clases 17 a 19: handlers y rutas funcionales para todos los recursos.
- Clases 21 y 22: users/roles, BCrypt, JWT y manejo de 401.
- Clase 25: alcance completo y archivos IA para revisión.

Las capas y abstracciones se conservan. Se corrigieron respuestas de ausencia, Location, cuerpo vacío, contexto de seguridad null y comprobación del usuario habilitado. Matrícula usa referencias de entrada y copia histórica propia; el PDF no especifica esa política. No se incorporaron servicios ajenos al escenario como restaurante, reportes, chatbot o migración relacional.
