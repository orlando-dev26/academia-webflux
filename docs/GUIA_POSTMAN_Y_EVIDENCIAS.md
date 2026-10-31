# Pruebas guiadas y capturas de evidencia

Estado: **recorrido reducido completado**, con ocho capturas principales y dos auxiliares reales del alumno. El [registro de evidencias](evidencias/README.md) conserva cada petición y resultado; las ocho principales están incorporadas al README del proyecto. No se presenta una ejecución manual completa de la colección. Las 41 pruebas Java tienen su evidencia independiente en [VERIFICACION.md](VERIFICACION.md).

## Alcance acordado

Seguir la evaluación y el método del profesor: backend ejecutado desde IntelliJ, MongoDB y peticiones HTTP en Postman. No añadir interfaz gráfica. Las capturas son una propuesta de presentación del alumno, no una nueva obligación textual del PDF.

La clase 3 muestra Postman al probar /v1/dishes; la clase 19 lo utiliza para las rutas funcionales. Aquí se adaptan esas pruebas a estudiantes, cursos y matrículas, conservando los dos estilos exigidos.

## Primer paso: importar

1. Abrir Postman y usar Import. En algunas versiones aparece dentro de Use resources or import.
2. Seleccionar [Academia.postman_collection.json](postman/Academia.postman_collection.json).
3. Comprobar que aparece la colección Academia WebFlux - Evaluacion final. No ejecutar toda la colección todavía.

Documentación oficial: [importación de colecciones](https://learning.postman.com/latest-v-12/docs/getting-started/importing-and-exporting/importing-data).

## Segundo paso: configurar y hacer login

Esta parte se hará cuando la importación esté confirmada. La aplicación debe estar ejecutándose en IntelliJ y MongoDB activo.

Variables de la colección:

| Variable | Valor |
| --- | --- |
| baseUrl | http://localhost:8080 |
| username | admin, salvo cambio local |
| password | Copiar la contraseña del archivo privado .local/ACCESO.txt |
| token | Dejar vacío: se completa al hacer login |

No definir otro entorno con variables del mismo nombre que oculten las de esta colección. No publicar/exportar la copia usada con contraseñas o tokens. La colección incluida en el repositorio conserva esos valores vacíos.

En la carpeta 01, ejecutar 01 - Login anotado. Respuesta esperada: 200 y access_token. Su script guarda el token para las siguientes peticiones. Para la captura, mostrar Test Results con HTTP 200 y JWT recibido; no mostrar la contraseña resuelta ni el JWT completo.

Documentación oficial: [variables en scripts](https://learning.postman.com/docs/tests-and-scripts/write-scripts/postman-sandbox-reference/pm-variables).

## Plan reducido de evidencias principales

El alumno pidió priorizar lo que solicita la evaluación. El PDF exige funcionalidades y entregables; no una captura por cada petición. Se acordaron **ocho imágenes principales en total, todas recibidas y documentadas**. Las capturas adicionales de Bruno y del segundo curso se conservan como evidencia auxiliar.

| Imagen principal | Petición / resultado | Estado |
| --- | --- | --- |
| 02-login-anotado.png | POST /login, 200 y Test Results | Recibida |
| 03-login-funcional.png | POST /v2/login, 200 y Test Results | Recibida |
| 04-estudiante-creado-v1.png | POST /v1/estudiantes, 201 y campos completos | Recibida |
| 05-curso-creado-v1.png | POST /v1/cursos, 201 y los cuatro campos | Recibida |
| 06-matricula-v1.png | POST /v1/matriculas, 201 y objetos completos | Recibida |
| 07-edad-asc-v1.png | GET /v1/estudiantes/edad/asc, 200 y edades ordenadas | Recibida |
| 08-edad-desc-v2.png | GET /v2/estudiantes/edad/desc, 200 y edades ordenadas; enfoque funcional | Recibida |
| 09-sin-token-v2.png | GET /v2/estudiantes sin token, 401 y error JSON | Recibida |

## Recorrido reducido realizado

1. Carpeta 02 - Anotaciones v1, petición **06 - Edad asc**. Enviar y tomar la captura del listado ascendente.
2. Carpeta 03 - Funcionales v2, petición **07 - Edad desc**. Usa los mismos estudiantes de MongoDB; no requiere crear otros por v2. Tomar la captura descendente.
3. Volver a carpeta 02, **08 - Crear curso principal**. Enviar una vez y tomar su captura 201.
4. Carpeta 02, **09 - Crear segundo curso**. Enviar una vez para preparar el segundo ID, sin solicitar otra captura principal.
5. Carpeta 02, **13 - Registrar matricula**. Utiliza el estudiante y los dos cursos cuyos IDs guardó Postman. Tomar la captura 201 con los objetos completos.
6. Carpeta 03, **17 - Sin token**. Tomar la captura del rechazo 401.

La colección guarda IDs automáticamente. No hace falta escribirlos ni crear otras colecciones. Si expira el token, repetir el login. Las peticiones de creación insertan un registro en cada envío; la preparación no limpia la base ni borra datos anteriores.

## Qué acredita el resto de requisitos

- Las capturas son una selección de presentación, no la prueba exclusiva de todas las operaciones.
- Las **41 pruebas Java ejecutadas** comprueban CRUD completo de estudiantes y cursos, ambas direcciones de edad, matrícula, persistencia, validación y seguridad en ambos enfoques. Los archivos y resultados están en [VERIFICACION.md](VERIFICACION.md) y [CUMPLIMIENTO.md](CUMPLIMIENTO.md).
- La colección completa conserva todas las 26 combinaciones mínimas y las consultas complementarias. Puede ejecutarse por separado si el evaluador desea reproducirlas; no se declara que ya se hayan recorrido todas manualmente.
- Los casos de JWT vencido, otra firma, sin expiración y usuario deshabilitado constan en las pruebas del backend. La captura 401 sin token no sustituye esos casos.
- La estructura users/roles, errores globales y artefactos IA están en las fuentes y specs. No hace falta una captura de cada archivo.
- La carpeta 04 de Postman contiene borrados de los maestros creados por esa colección y consulta histórica. Queda fuera del recorrido reducido de capturas; el comportamiento DELETE ya tiene pruebas automáticas reales.

## Incorporación al README

Cuando el alumno comparta una captura, verificar su petición y resultado; después guardarla con nombre descriptivo en docs/evidencias y añadir al README su imagen relativa y una frase sobre qué demuestra. No agregar imágenes ficticias, enlaces rotos ni marcas de completado antes de recibir evidencia real.

Las ocho imágenes principales ya se insertaron en el README con rutas relativas a archivos existentes y textos que describen el resultado. Por ejemplo, la [captura de matrícula](evidencias/06-matricula-v1.png) muestra el documento completo devuelto tras el registro.

Antes de publicar se revisó que las imágenes seleccionadas no mostraran contraseña, token completo ni hashes de users. Código y evidencias publicados y comprobados el 31/10/2026 en [orlando-dev26/academia-webflux](https://github.com/orlando-dev26/academia-webflux). El correo de entrega no se ha enviado.
