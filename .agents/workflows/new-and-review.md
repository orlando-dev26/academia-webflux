# Construcción y revisión de academia

## Objetivo

Implementar un bloque del backend y revisar su cumplimiento
contra la especificación de academia.

Trabajar por partes para comprender y verificar cada avance.

## Agentes asignados

- academia-builder: implementar y corregir.
- academia-reviewer: revisar e informar hallazgos.

Las tareas se realizan secuencialmente:
primero construcción y después revisión.

## Entrada

Identificar el bloque solicitado y su alcance.

Ejemplos:
- Crear el modelo Estudiante.
- Implementar el CRUD de estudiantes en ambos estilos.
- Registrar matrícula.
- Incorporar seguridad JWT.

Distinguir un bloque parcial de una funcionalidad completa.

## Documentos de referencia

Leer antes de trabajar:

- .agents/features/academia/spec.md
- .agents/subagents/academia-builder.md
- .agents/subagents/academia-reviewer.md
- El código existente relacionado con el bloque.

Consultar los materiales del profesor únicamente en modo lectura.
Realizar los cambios dentro del proyecto academia-webflux.

## Paso 1: Preparar el bloque

1. Identificar los requisitos aplicables.
2. Revisar qué archivos existen.
3. Determinar qué archivos se crearán o modificarán.
4. Definir cómo se comprobará el resultado.

## Paso 2: Construir

Aplicar las instrucciones de academia-builder.

- Seguir los patrones del curso.
- Respetar campos y tipos de la especificación.
- Conservar las abstracciones compartidas.
- Mantener la lógica de negocio en los servicios.
- Implementar únicamente el bloque solicitado.

Cuando el bloque incluya operaciones HTTP,
cubrir anotaciones y endpoints funcionales.

## Paso 3: Verificar

Ejecutar las comprobaciones pertinentes desde la raíz del proyecto.

En Windows:
- powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\verificar.ps1 -Mode compile
- powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\verificar.ps1 -Mode verify para el cierre.

Confirmar que Maven utiliza Java 25.
El script configura también la ruta corta de sockets de Java en Windows.
Fuera de Windows, usar el wrapper con JAVA_HOME apuntando a JDK 25.

Compilar cuando cambie código Java o dependencias.
Ejecutar las pruebas relacionadas con el bloque implementado.

Separar pruebas unitarias de integración.
Las pruebas de integración usarán una base de prueba separada.

Registrar los comandos realmente ejecutados y sus resultados.
Si una comprobación no se ejecuta, explicar la causa.

## Paso 4: Revisar

Aplicar las instrucciones de academia-reviewer.

Comprobar, según el alcance del bloque:

- Campos, tipos y persistencia.
- Uso correcto de Mono y Flux.
- Reutilización de servicios y CRUD genérico.
- Mapeo DTO/documento.
- Validación y errores.
- Rutas, respuestas y equivalencia entre ambos estilos.
- Referencias y datos históricos de matrícula.
- Orden por edad.
- Seguridad y configuración.
- Cobertura real de las pruebas.

Separar defectos del bloque de funcionalidades futuras.

## Paso 5: Corregir

Si hay hallazgos:

1. Entregarlos a academia-builder.
2. Corregir los problemas identificados.
3. Repetir las comprobaciones afectadas.
4. Solicitar una nueva revisión.

Se establece un máximo de tres rondas de corrección
como decisión de este workflow.

Si persisten errores, entregar un diagnóstico con:
- Problemas pendientes.
- Archivos afectados.
- Comprobaciones fallidas.
- Próximas acciones.

No marcar el bloque como terminado si quedan defectos
que impidan cumplir sus criterios de aceptación.

## Paso 6: Registrar el avance

Actualizar la Parte 2 del spec con evidencia real:

- Bloque implementado.
- Archivos creados o modificados.
- Decisiones adicionales.
- Comandos ejecutados.
- Resultados observados.
- Pendientes.

Una revisión parcial no acredita el proyecto completo.

## Criterios de cierre

- El bloque cumple su alcance.
- Los campos y tipos coinciden con la especificación.
- Las comprobaciones pertinentes se ejecutaron correctamente,
  o sus limitaciones quedaron documentadas.
- Los hallazgos funcionales del bloque fueron resueltos.
- El informe distingue lo implementado de lo pendiente.

## Informe final

Presentar:

1. Resultado del bloque.
2. Archivos y propósito de cada cambio.
3. Contratos HTTP, cuando corresponda.
4. Validaciones aplicadas.
5. Hallazgos y correcciones.
6. Verificaciones realizadas y resultados.
7. Pendientes y siguiente bloque.
