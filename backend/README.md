# Backend

El backend se construirá con microservicios Spring Boot independientes. Cada servicio expondrá una API REST documentada con OpenAPI y será dueño de sus datos.

| Servicio | Responsabilidad | Datos |
| --- | --- | --- |
| `ms-usuarios` | Registro, autenticación, usuarios y roles | `usuarios_db` |
| `ms-canchas` | Canchas, deportes y horarios | `canchas_db` |
| `ms-reservas` | Disponibilidad, reservas, cancelaciones y reglas de negocio | `reservas_db` |
| `ms-reportes` | Agregación de información para reportes | Sin acceso directo a tablas ajenas |

## Reglas de integración

- Comunicación síncrona mediante HTTP/REST cuando sea necesaria.
- Prohibido acceder directamente a tablas de otro microservicio.
- Contratos OpenAPI versionados junto al servicio.
- Configuración sensible mediante variables de entorno, nunca en Git.
- Migraciones y datos semilla reproducibles.
