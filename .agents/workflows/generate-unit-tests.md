# Workflow de pruebas de academia

Entrada: cambio concreto y criterios de aceptación. Referencias: academia-test-writer, academia-reviewer y specs de academia/seguridad.

1. Revisar las pruebas existentes y seleccionar casos afectados por el cambio.
2. Aplicar las instrucciones de academia-test-writer para añadir casos que detecten comportamiento incorrecto observable. Cubrir v1/v2 cuando cambie HTTP y usar mapper/validación reales cuando se modifiquen contratos.
3. Ejecutar scripts/verificar.ps1 -Mode verify desde el proyecto con MongoDB activo y JDK 25.
4. Analizar fallos. Corregir producción o expectativas erróneas contra la spec; no suprimir casos correctos ni ocultar errores. Repetir solo las comprobaciones afectadas y el cierre requerido.
5. Aplicar los criterios de academia-reviewer y registrar comando/resultado/limitaciones en Parte 2 de la spec y docs/VERIFICACION.md.

Máximo tres rondas de corrección para este workflow. Si quedan fallos, informar su diagnóstico y dejar el estado incompleto. No atribuir ejecución a agentes independientes si se aplicaron sus instrucciones sin instanciarlos.
