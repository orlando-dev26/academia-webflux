# Capturas reales de las pruebas guiadas

Esta carpeta conserva capturas originales aportadas por el alumno y revisadas antes de incorporarlas al README. El resto de evidencias se añadirá a medida que se ejecuten las pruebas guiadas.

| Evidencia | Petición | Resultado visible | Estado |
| --- | --- | --- | --- |
| [Login anotado](02-login-anotado.png) | POST /login | 200 OK; HTTP 200 y JWT recibido en PASSED; 2/2 | Validada |
| [Login funcional](03-login-funcional.png) | POST /v2/login | 200 OK; HTTP 200 y JWT recibido en PASSED; 2/2 | Validada |
| [Estudiante creado por anotaciones](04-estudiante-creado-v1.png) | POST /v1/estudiantes | 201 Created; cinco campos completos; edad 18; DNI textual con ceros iniciales; Test Results 2/2 | Validada |
| [Segundo estudiante para comparar edades](aux-estudiante-comparacion-v1.png) | POST /v1/estudiantes | 201 Created; Bruno Evidencia v1; edad 30; Test Results 2/2 | Auxiliar validada |
| [Edad ascendente por anotaciones](07-edad-asc-v1.png) | GET /v1/estudiantes/edad/asc | 200 OK; edades visibles 18, 18, 18, 24, 30; Test Results 2/2 | Validada |
| [Edad descendente funcional](08-edad-desc-v2.png) | GET /v2/estudiantes/edad/desc | 200 OK; edades visibles 30, 30, 24, 18, 18; Test Results 2/2 | Validada |
| [Curso creado por anotaciones](05-curso-creado-v1.png) | POST /v1/cursos | 201 Created; cuatro campos completos; SWF; estado true; Test Results 2/2 | Validada |
| [Segundo curso para la matrícula](aux-segundo-curso-v1.png) | POST /v1/cursos | 201 Created; MongoDB Evidencia v1; MDB; estado false; Test Results 2/2 | Auxiliar validada |
| [Registro de matrícula](06-matricula-v1.png) | POST /v1/matriculas | 201 Created; fecha, estudiante completo, dos cursos completos y estado; Test Results 2/2 | Validada |
| [Rechazo funcional sin token](09-sin-token-v2.png) | GET /v2/estudiantes sin autenticación | 401 Unauthorized; datetime/message; Test Results 2/2 | Validada |

Plan acordado con el alumno: **ocho capturas principales, todas recibidas e incorporadas**. Las capturas de Bruno y del segundo curso se conservan como apoyo a la preparación de datos, sin añadir otras imágenes completas al README principal. El recorrido reducido está completo; no se solicita una captura de cada petición de la colección.

Las capturas de login no muestran contraseña ni token completo y acreditan el estado de respuesta y la recepción de un token con formato JWT. La creación de estudiante muestra los datos ficticios preparados en la colección. Los casos de firma, expiración y acceso protegido se documentan por separado en las pruebas del backend. Estas imágenes no representan una ejecución completa de la colección.

Ver [guía de pruebas y evidencias](../GUIA_POSTMAN_Y_EVIDENCIAS.md). Cada imagen publicada se acompañará de su operación, estado y conclusión. No guardar aquí contraseñas, JWT completos ni imágenes de resultados no ejecutados.
