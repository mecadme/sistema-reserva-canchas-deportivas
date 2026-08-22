# Backend - Sistema de Reserva de Canchas

Backend compuesto por cuatro microservicios Spring Boot independientes. Cada servicio es dueño de su base PostgreSQL y se integra exclusivamente mediante API REST.

| Servicio | Puerto | Base | Responsabilidad |
| --- | ---: | --- | --- |
| `ms-usuarios` | 8081 | `usuarios_db` | Registro, autenticación JWT, usuarios y roles |
| `ms-canchas` | 8082 | `canchas_db` | Canchas, deportes, horarios y bloqueos |
| `ms-reservas` | 8083 | `reservas_db` | Disponibilidad, reservas, cancelaciones y RN-01 a RN-08 |
| `ms-reportes` | 8084 | `reportes_db` | Reportes y auditoría de consultas |

## Principios de datos

- No existen `JOIN`, claves foráneas ni consultas entre bases de microservicios.
- Las relaciones externas se conservan como UUID (`usuario_id`, `cancha_id`).
- `ms-reservas` guarda datos mínimos de cancha como instantánea para trazabilidad.
- `ms-reportes` obtiene datos por REST y registra sus ejecuciones en `reportes_db`.
- Flyway administra las migraciones de cada servicio.

## Ejecución

```bash
cp .env.example .env
docker compose up --build
```

Swagger queda disponible en:

- `http://localhost:8081/swagger-ui.html`
- `http://localhost:8082/swagger-ui.html`
- `http://localhost:8083/swagger-ui.html`
- `http://localhost:8084/swagger-ui.html`

Credenciales de demostración:

- Administrador: `admin@canchas.local` / `Admin123*`

## Compilación y pruebas

```bash
mvn clean verify
```

La planificación de desarrollo está definida en [SPRINTS.md](SPRINTS.md).

## Comunicación interna

Los endpoints `/internal/**` requieren la cabecera `X-Service-Key`. El valor se entrega mediante `SERVICE_API_KEY` y nunca debe confirmarse en el repositorio para un ambiente real.
