# Preparación de la entrega

El backend está implementado, verificado y publicado en GitHub. El envío del correo de entrega no se ha realizado; el texto inferior es un borrador.

Publicación real comprobada el **31/10/2026** en [orlando-dev26/academia-webflux](https://github.com/orlando-dev26/academia-webflux). El README incluye las ocho capturas principales de Postman aportadas por el alumno.

## Archivos

- `dist/academia-webflux-entrega.zip`: fuentes, pruebas, documentación, scripts y artefactos IA. No contiene claves, contraseñas, .local, .idea ni target.
- `target/academia-webflux-0.0.1-SNAPSHOT.jar`: aplicación ejecutable. No contiene configuración privada; requiere variables o archivo local para JWT/MongoDB.
- `README.md`: ejecución, acceso y referencias de verificación.
- `docs/VERIFICACION.md`: resultado de las 41 pruebas.
- `requests.http` y `testdata`: demostración reproducible con datos ficticios.

El ZIP se genera mediante `scripts/empaquetar.ps1`, que usa una lista de fuentes permitidas y comprueba que no se incluyan secretos locales ni rutas privadas. El JAR es un artefacto separado y no se mete en el repositorio de fuentes.

## Repositorio publicado

El código se subió a la rama `main` mediante Git, autenticado por el alumno en su cuenta de GitHub. Se comprobaron los 107 archivos públicos, las ocho imágenes principales del README y las dos imágenes auxiliares, con exclusión de configuración privada, contraseñas y tokens.

Repositorio: [orlando-dev26/academia-webflux](https://github.com/orlando-dev26/academia-webflux).

Para obtener una copia:

```powershell
git clone https://github.com/orlando-dev26/academia-webflux.git
cd academia-webflux
```

Seguir después las instrucciones del README para generar la configuración privada de esa instalación y ejecutar las pruebas. No publicar los materiales originales del profesor ni exportaciones de Postman que contengan valores privados.

## Borrador de correo

Para: cursos@mitocodenetwork.com

Asunto: Evaluación final — Spring WebFlux AI — NOMBRE DEL ALUMNO

Hola,

Comparto mi evaluación final del curso Spring WebFlux AI.

Alumno: NOMBRE COMPLETO
Repositorio público: https://github.com/orlando-dev26/academia-webflux

El proyecto implementa CRUD de estudiantes y cursos, registro de matrícula y listados por edad en ambos estilos de endpoints, con MongoDB, seguridad JWT y manejo global de errores. Incluye agentes, workflows, especificaciones e instrucciones de ejecución en el README.

La verificación local terminó con 41 pruebas correctas. Las credenciales de cada instalación se generan localmente y no se publican en el repositorio.

Gracias.

## Plazo

El PDF indica 30/09/2026. La implementación se completó el 30/10/2026. No se dispone de una ampliación documentada; verificar con el curso la recepción aplicable.
