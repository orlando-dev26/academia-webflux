# Backend de academia

Estado: implementado y verificado localmente, 30/10/2026.
Publicación: completada el 31/10/2026 en https://github.com/orlando-dev26/academia-webflux. Envío por correo: pendiente.

# Parte 1: Especificación

## Problema

Una academia necesita administrar estudiantes, cursos y matrículas.

## Objetivo

Construir un backend REST con Spring WebFlux y MongoDB,
siguiendo la estructura y los patrones enseñados en el curso.

## Entorno del proyecto

- Java 25.
- Spring Boot 4.1.2.
- Maven.
- Paquete base: com.orlandini.academiawebflux.
- MongoDB instalado localmente.
- Base de datos: academia_db.
- Puerto de la aplicación: 8080.

## Datos requeridos

### Estudiante

- id: String.
- nombres: String.
- apellidos: String.
- dni: String.
- edad: int.

### Curso

- id: String.
- nombre: String.
- siglas: String.
- estado: boolean.

### Matrícula

- fechaMatricula: LocalDateTime.
- estudiante: Estudiante.
- cursos: List<Curso>.
- estado: boolean.

La matrícula se guardará como un documento en MongoDB.

## Funcionalidades requeridas

1. CRUD de estudiantes:
   crear, listar, consultar por ID, actualizar y eliminar.
2. CRUD de cursos:
   crear, listar, consultar por ID, actualizar y eliminar.
3. Registrar matrícula.
4. Listar estudiantes por edad ascendente.
5. Listar estudiantes por edad descendente.

Todas estas operaciones estarán disponibles mediante:
- Controladores con anotaciones, bajo /v1.
- Rutas y handlers funcionales, bajo /v2.

Los dos estilos utilizarán los mismos servicios de negocio.

## Estructura del código

Conservar las capas del curso:
model, dto, repo, service, service.impl,
controller, handler, config, exception, validator y security.

Adaptar IGenericRepo, ICRUD y CRUDImpl para los CRUD.
Utilizar DTO y ModelMapper en los bordes HTTP.

## Seguridad requerida

- Autenticación mediante username y password.
- Contraseñas almacenadas con BCrypt.
- Login público para obtener access_token.
- Servicios de negocio protegidos mediante JWT.
- Verificar firma y expiración del token.
- Rechazar tokens ausentes, inválidos o vencidos.

Conservar la estructura del curso:
- users: id, username, password, status y roles.
- roles: id y name.
- roles de User: List<Role>.

La clave JWT se obtendrá de configuración externa.

## Decisiones propias de implementación

Estas decisiones complementan el enunciado:

- Añadir un id String a Matrícula.
- Recibir IDs de estudiante y cursos al registrar matrícula.
- Consultar sus datos y guardar una copia completa en la matrícula.
- Rechazar referencias inexistentes sin guardar la matrícula.
- Conservar los datos históricos al modificar después los maestros.
- Añadir consulta de matrícula por ID en ambos estilos.
- Validar campos obligatorios, edad no negativa,
  lista de cursos no vacía e IDs de cursos sin repetir.

## Contratos HTTP

Aplicar contratos equivalentes en ambos estilos:

- Creación: 201 con DTO y Location.
- Consulta y actualización: 200.
- Listado vacío: 200 con lista vacía.
- Eliminación correcta: 204.
- Recurso o referencia inexistente: 404.
- Entrada inválida: 400.
- Autenticación inválida: 401.
- Acceso denegado cuando corresponda: 403.

Utilizar errores JSON con datetime y message,
siguiendo el manejador global del curso.
Los errores de seguridad conservarán ese mismo formato.

## Artefactos de IA

Crear instrucciones adaptadas a academia para:
- Construcción.
- Revisión.
- Generación de pruebas.
- Generación de datos.

Crear workflows que coordinen esas tareas y documenten
las verificaciones realmente ejecutadas.

## Criterios de aceptación

- Persistir y recuperar todos los campos requeridos.
- Cubrir las 13 operaciones mínimas en ambos estilos.
- Comprobar orden ascendente y descendente por edad.
- Verificar matrícula válida y referencias inexistentes.
- Comprobar validación y errores equivalentes.
- Probar JWT válido, vencido, alterado y ausente.
- Verificar persistencia real en MongoDB.
- Documentar comandos y resultados de las pruebas.

## Entrega

Preparar un repositorio público con:
- Código fuente.
- Agentes, workflows y especificaciones.
- README con instrucciones de ejecución.
- .gitignore que excluya compilados, archivos del IDE y secretos.

# Parte 2: Implementación y verificación

## Resultado

Backend completo con los tres documentos requeridos, CRUD genérico para maestros, repositorios reactivos y servicios compartidos por ambos estilos. EstudianteController/Handler y CursoController/Handler ofrecen las cinco operaciones; EstudianteServiceImpl añade orden por edad mediante MongoDB. MatriculaServiceImpl registra una copia completa de referencias resueltas y rechaza entradas incompletas antes de guardar.

DTO/MapperConfig separan contrato y persistencia; RequestValidator valida cuerpo y asociaciones anidadas. WebExceptionHandler y ErrorResponseWriter unifican errores JSON. UriHelper conserva esquema/host/puerto y elimina query al construir Location.

RouterConfig cubre las mismas operaciones bajo /v2 que los controladores bajo /v1. Son 26 combinaciones mínimas y dos consultas adicionales de matrícula. Login anotado /login y funcional /v2/login; protección JWT global según la spec de security. La semilla privada es optativa.

## Artefactos

Cuatro definiciones: academia-builder, academia-reviewer, academia-test-writer y academia-data-generator. Tres workflows: new-and-review, generate-unit-tests y generate-test-data. Specs academia/seguridad y contexto CLAUDE.md. Datos ficticios y script de carga que resuelve IDs reales. Se entregan los archivos; no se afirma que se hayan instanciado subagentes independientes.

## Verificación real

30/10/2026 21:09:25 America/Lima: scripts/verificar.ps1 -Mode verify, Temurin 25.0.5 y MongoDB nativo. Resultado 41 ejecuciones, 0 fallos, 0 errores, 0 omitidas; BUILD SUCCESS. JAR ejecutable generado.

27 ejecuciones de integración con HTTP y MongoDB reales más 14 de componentes. Se comprobaron todos los campos y operaciones en v1/v2, edad/empates, matrícula e histórico, inexistencias sin escritura, validación, JWT vigente y variantes inválidas, cuentas deshabilitadas y errores equivalentes. El caso 403 verifica el escritor con codecs reales sin inventar permisos por rol.

El detalle reproducible está en docs/VERIFICACION.md; el mapa de requisitos en docs/CUMPLIMIENTO.md y los contratos en docs/API.md. Configuración privada excluida de Git/ZIP. Publicación pública comprobada el 31/10/2026 en https://github.com/orlando-dev26/academia-webflux, con código, colección y capturas reales. El envío por correo sigue pendiente; docs/ENTREGA.md contiene el borrador.
