# Plan de sprints del backend

La planificación considera sprints de dos semanas. Cada incremento debe compilar, incluir pruebas y mantener actualizado Swagger/OpenAPI.

## Sprint 0 - Base técnica

- Acordar Java 17, Spring Boot 4.1, Maven y convenciones REST.
- Crear los cuatro proyectos y el reactor Maven.
- Configurar Docker Compose con una base PostgreSQL por microservicio.
- Configurar Flyway, Actuator, variables de entorno y manejo uniforme de errores.

**Criterio de salida:** los cuatro servicios compilan y las cuatro bases pueden levantarse de forma aislada.

## Sprint 1 - Usuarios y seguridad

- Registro de usuario final.
- Inicio de sesión y emisión de JWT.
- Roles `USUARIO` y `ADMINISTRADOR`.
- Consulta de perfil y activación/inactivación administrativa.
- Protección de API REST e integración mediante `X-Service-Key`.

**Criterio de salida:** un usuario puede registrarse e iniciar sesión; un administrador puede gestionar estados.

## Sprint 2 - Canchas y horarios

- CRUD de canchas para pádel, tenis y básquet.
- Definición de hora de apertura y cierre.
- Activación e inactivación.
- Registro y consulta de bloqueos por mantenimiento.
- Endpoint interno para validar una cancha y un período.

**Criterio de salida:** el administrador gestiona el catálogo y el sistema identifica períodos bloqueados.

## Sprint 3 - Reservas

- Consulta de disponibilidad por cancha y fecha.
- Creación de reservas en bloques de una hora.
- Prevención transaccional de solapamientos.
- Límite configurable de reservas activas.
- Historial propio, listado administrativo y cancelación según rol.
- Transición automática a estado `FINALIZADA`.

**Criterio de salida:** RN-01 a RN-08 están implementadas y una concurrencia no puede duplicar un bloque.

## Sprint 4 - Reportes y cierre

- Reservas por período, cancha y deporte.
- Cancelaciones por período.
- Porcentaje de ocupación por cancha.
- Auditoría de generación en `reportes_db`.
- Pruebas del reactor completo, revisión de Swagger y manual de ejecución.

**Criterio de salida:** el administrador genera reportes consistentes consumiendo APIs, sin acceder a bases ajenas.
