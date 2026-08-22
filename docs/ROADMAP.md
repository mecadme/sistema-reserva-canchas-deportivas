# Estado y hoja de ruta

El sistema está implementado, integrado y ha sido levantado correctamente. La validación funcional manual está realizada; permanecen pendientes las pruebas automatizadas end-to-end y los entregables académicos finales.

## Fase 0 - Base técnica

**Estado: completada**

- Stack frontend definido: Angular 20, pnpm y Module Federation con Webpack 5.
- Stack backend definido: Java 17, Spring Boot y Maven.
- Cuatro microservicios y cuatro bases PostgreSQL independientes.
- Arquitectura, modelos de datos y comunicación REST documentados.
- Docker Compose y datos semilla configurados.

## Fase 1 - Primer flujo vertical

**Estado: completada**

- Registro e inicio de sesión con JWT y roles.
- Catálogo de canchas y horarios.
- Consulta de disponibilidad.
- Creación de reservas sin solapamiento.
- Integración del `shell` con `mf-clientes` mediante Module Federation.

## Fase 2 - Alcance funcional

**Estado: completada y validada manualmente**

- Historial y cancelación de reservas.
- Gestión administrativa de canchas, bloqueos, usuarios y reservas.
- Límite configurable de reservas activas.
- Estados `CONFIRMADA`, `CANCELADA` y `FINALIZADA`.
- Reportes básicos de ocupación, demanda y cancelaciones.
- Integración real entre frontend y los cuatro microservicios.

## Fase 3 - Calidad y documentación

**Estado: en progreso**

Completado:

- Pruebas unitarias iniciales por servicio y microfrontend.
- Swagger/OpenAPI disponible en ejecución.
- Colección Postman con endpoints y flujo funcional completo.
- Migraciones Flyway, DDL y datos semilla.
- Ejecución integrada con Docker Compose.
- Documentación de arquitectura, datos, frontend, backend e infraestructura.

Pendiente:

- Pruebas automatizadas end-to-end de los flujos principales.
- Aserciones y evidencias formales de ejecución de pruebas.
- Consolidación de contratos OpenAPI en una fase posterior.
- Documento formal de arquitectura en el formato de entrega.
- Manual formal de despliegue en el formato de entrega.
- Presentación final y guion de demostración.
