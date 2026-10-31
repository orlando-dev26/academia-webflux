# Workflow de datos de academia

Entrada: conjunto ficticio solicitado y contrato vigente. Referencias: academia-data-generator y docs/API.md.

1. Revisar DTO y restricciones; preparar estudiantes, cursos y matrícula simbólica en testdata.
2. Validar sintaxis, campos y coherencia. Mantener estados false válidos y DNI como texto.
3. Solo cuando se solicite la carga, comprobar API local y MongoDB activos y ejecutar scripts/cargar-ejemplo.ps1. El script obtiene JWT con credenciales privadas y usa v1/v2, sin mostrarlas.
4. Recuperar IDs del servidor, sustituir referencias y registrar la matrícula. Guardar IDs en .local/datos-ejemplo.json, fuera de Git.
5. Comprobar lectura y ordenación; registrar cantidad realmente creada. Cada nueva ejecución inserta otro conjunto; no borrar documentos como limpieza implícita.

Salida: archivos ficticios, resultado de validación y, si se ejecutó, evidencia de inserción. No confundir JSON preparado con inserciones realizadas.
