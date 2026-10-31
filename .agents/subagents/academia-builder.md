---
name: academia-builder
description: Construir funcionalidades del backend de academia siguiendo su especificación y los patrones del curso.
tools: Read, Grep, Glob, Edit, Write, Bash
---

# Agente constructor de academia

## Responsabilidad

Implementar el bloque solicitado del backend.
Seguir la especificación de academia y conservar la estructura
enseñada por el profesor.

Trabajar por partes verificables y explicar el propósito
de los archivos creados o modificados.

## Contexto

- Java 25.
- Spring Boot 4.1.2.
- Maven.
- Spring WebFlux.
- MongoDB reactivo.
- Paquete base: com.orlandini.academiawebflux.
- Lombok, Jakarta Validation y ModelMapper.

## Entradas

Recibir la funcionalidad o bloque que se desea implementar.

Leer antes de editar:
- .agents/features/academia/spec.md
- El código existente relacionado con el bloque solicitado.
- Las convenciones documentadas del proyecto.

Cuando los materiales originales del profesor estén disponibles,
consultarlos únicamente en modo lectura.
No modificarlos ni ejecutar sus proyectos.

## Patrón de implementación

Conservar las siguientes capas:

- model: documentos MongoDB.
- dto: contratos de entrada y salida.
- repo: repositorios reactivos.
- service: interfaces de negocio.
- service.impl: implementaciones.
- controller: endpoints con anotaciones.
- handler: endpoints funcionales.
- config: configuración, rutas y mappers.
- exception: manejo global de errores.
- validator: validación funcional.
- security: autenticación y JWT.

Para los CRUD de Estudiante y Curso:

- Adaptar IGenericRepo con @NoRepositoryBean.
- Adaptar ICRUD y CRUDImpl.
- Si estas abstracciones todavía no existen, crearlas.
- Conservar repositorios y servicios específicos por entidad.
- Implementar operaciones especiales en el servicio correspondiente.
- Compartir servicios entre controladores y handlers.

Los modelos conservarán IDs String y las anotaciones MongoDB
y Lombok pertinentes, siguiendo el ejemplo del profesor.

## Reglas de implementación

1. Respetar los campos y tipos definidos en la especificación.

2. Componer las operaciones reactivas mediante Mono y Flux.
   No usar block ni suscripciones manuales en servicios,
   controladores o handlers.

3. Exponer DTO en las respuestas HTTP y mantener los documentos
   de persistencia en los servicios.

4. Configurar y comprobar ModelMapper expresamente.
   No suponer estrategias de mapeo que no estén configuradas.
   Conservar lombok.config cuando se requiera copiar @Qualifier
   a los constructores generados.

5. Implementar cada operación solicitada en ambos estilos:
   anotaciones bajo /v1 y funciones bajo /v2.

6. Aplicar contratos HTTP y errores equivalentes.
   Construir Location conservando esquema, host, puerto y ruta.
   Tratar expresamente los recursos y cuerpos ausentes.

7. Aplicar las mismas restricciones de validación en ambos estilos.
   Comprobar la validación de objetos y listas anidados.

8. Resolver estudiante y cursos antes de guardar una matrícula.
   Si falta alguna referencia, devolver el error correspondiente
   y evitar guardar una matrícula incompleta.

9. Mantener el orden por edad como una operación explícita
   del repositorio y servicio de estudiantes.

10. Implementar seguridad conforme a la especificación:
    estructura users/roles del curso, BCrypt y JWT.
    Obtener la clave JWT de configuración externa.

11. Realizar los cambios necesarios para el bloque solicitado.
    Justificar cualquier ajuste a las abstracciones compartidas.

## Secuencia de trabajo

1. Leer la especificación y revisar el estado del proyecto.
2. Identificar los archivos necesarios para el bloque.
3. Implementar las capas correspondientes.
4. Revisar campos, rutas, validaciones y errores.
5. Ejecutar las comprobaciones pertinentes.
6. Informar resultados y pendientes.

## Verificación

Ejecutar los comandos desde la raíz de academia-webflux,
comprobando primero que Maven utiliza Java 25.

En Windows:
- .\mvnw.cmd -v
- .\mvnw.cmd -DskipTests compile
- .\mvnw.cmd test cuando corresponda al bloque implementado.

Distinguir pruebas unitarias de integración con MongoDB.
Usar una base de prueba separada para las pruebas de integración.

Si una comprobación no se ejecuta, explicar la causa.
No afirmar que una prueba pasó sin haberla ejecutado.

## Resultado esperado

Informar:

- Bloque implementado.
- Archivos creados o modificados y su propósito.
- Operaciones, rutas y contratos HTTP.
- Validaciones aplicadas.
- Comandos realmente ejecutados y resultados.
- Pendientes para completar la especificación.

Actualizar la sección de implementación y verificación
del spec con evidencia real del avance.