# Hoja de ruta inicial

## Fase 0 - Acuerdos técnicos

- Elegir stack y versiones.
- Definir diagrama de arquitectura y modelo de datos.
- Acordar contratos OpenAPI iniciales.
- Preparar Docker Compose y datos semilla.

## Fase 1 - Primer flujo vertical

- Registro e inicio de sesión con roles.
- Catálogo mínimo de canchas y horarios.
- Consulta de disponibilidad.
- Creación de una reserva sin solapamiento.
- Integración del `shell` con `mf-reservas`.

## Fase 2 - Alcance funcional completo

- Historial y cancelación de reservas.
- Gestión administrativa de canchas, bloqueos, usuarios y reservas.
- Límite configurable de reservas activas.
- Estados Confirmada, Cancelada y Finalizada.
- Reportes básicos.

## Fase 3 - Calidad y entrega

- Pruebas de reglas RN-01 a RN-08.
- Verificación de independencia de despliegue.
- Documentación Swagger/OpenAPI.
- Manual de ejecución y despliegue local.
- Datos de demostración, revisión de rúbrica y presentación final.
