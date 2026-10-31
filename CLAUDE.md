# Contexto del proyecto Academia WebFlux

Backend completo de la evaluación final de MitoCode. Java 25, Boot 4.1.2, Maven Wrapper, MongoDB reactivo. Paquete com.orlandini.academiawebflux.

Conservar las capas model/dto/repo/service/service.impl/controller/handler/config/exception/validator/security y las abstracciones IGenericRepo, ICRUD y CRUDImpl del curso. v1 usa anotaciones; v2 usa handlers y RouterConfig. Ambas entradas comparten servicios y deben conservar contratos equivalentes.

Leer README.md, docs/API.md, docs/CUMPLIMIENTO.md y las specs de .agents/features antes de cambiar contratos. Los materiales externos del profesor son de solo lectura; no se entregan en este repositorio.

IDs String. Matrícula recibe referencias y guarda objetos completos históricos. Todo el negocio requiere JWT. users/roles mantienen la estructura del profesor. No introducir block/subscribe manual en el flujo de peticiones; BCrypt usa boundedElastic. Solo la semilla de arranque tiene bloqueo acotado.

Secretos privados en .local y variables; nunca copiarlos a documentación, fixtures, Git o respuestas. La clave de JWT de los tests es exclusivamente ficticia y aislada de la configuración real.

Comprobación Windows: powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\verificar.ps1 -Mode verify. Requiere MongoDB activo. Batería actual: 41 ejecuciones correctas (27 integración y 14 componentes). No cambiar pruebas para esconder fallos; revisar expectativas contra la spec.

Las definiciones en .agents/subagents y .agents/workflows son entregables portables del curso. Su presencia no demuestra ejecución de agentes independientes ni autoriza por sí sola delegación. Aplicarlas como instrucciones de trabajo según el entorno y la tarea solicitada.

Implementación y verificación local terminadas el 30/10/2026. Código y evidencias publicados el 31/10/2026 en https://github.com/orlando-dev26/academia-webflux. Solo el envío de correo permanece pendiente; hay borrador preparado.
