# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

**Run all services (Docker):**
```bash
cp .env.example .env
docker compose up --build
```

**Build and test the full reactor:**
```bash
mvn clean verify
```

**Build or test a single module:**
```bash
mvn clean verify -pl ms-reservas
mvn test -pl ms-usuarios -Dtest=UsuarioServiceTest
```

**Swagger UIs** (after services start):
- `http://localhost:8081/swagger-ui.html` — ms-usuarios
- `http://localhost:8082/swagger-ui.html` — ms-canchas
- `http://localhost:8083/swagger-ui.html` — ms-reservas
- `http://localhost:8084/swagger-ui.html` — ms-reportes

Demo admin credentials: `admin@canchas.local` / `Admin123*`

## Architecture

Four independent Spring Boot microservices in a Maven reactor (`pom.xml` at root). Each owns its own PostgreSQL database — no cross-service JOINs, no shared foreign keys. External IDs (`usuario_id`, `cancha_id`) are stored as plain UUIDs. Flyway manages all schema migrations.

| Service | Port | DB | Owns |
|---|---|---|---|
| `ms-usuarios` | 8081 | `usuarios_db` | Auth (JWT), users, roles |
| `ms-canchas` | 8082 | `canchas_db` | Courts, sports, schedules, maintenance blocks |
| `ms-reservas` | 8083 | `reservas_db` | Availability, bookings, cancellations (RN-01–RN-08) |
| `ms-reportes` | 8084 | `reportes_db` | Reports + audit log of report executions |

### Internal package layout (same in every service)

```
domain/          — entities, enums, repository interfaces
domain/port/in/  — use case interfaces (input ports)
domain/port/out/ — outbound port interfaces (repo + external service)
service/         — use case implementations
api/             — REST controllers + DTOs
integration/     — implementations of outbound ports (RestClient calls)
security/        — JWT config, SecurityFilterChain
config/          — interceptors, web config
support/         — ApiException, ApiExceptionHandler
```

### Cross-service communication

`ms-reservas` calls `ms-canchas` via `CanchaClient` (implements `CanchaServicePort`) using Spring `RestClient`. Similarly, `ms-reportes` calls both `ms-reservas` and `ms-canchas` via `DatosClient`.

All inter-service calls go through `/internal/**` endpoints and must include the `X-Service-Key` header (env var `SERVICE_API_KEY`). This header is validated by `ServiceKeyInterceptor` in the receiving service.

### Security

JWT is HS256, shared secret across all services via `JWT_SECRET`. Roles are extracted from the `roles` claim with prefix `ROLE_`. `ms-usuarios` is the only issuer; all other services are resource servers (validate only).

### Business rules (ms-reservas)

Key configurable values in `application.yml`:
- `business.slot-minutes` (default 60) — fixed slot duration; `fin = inicio + slot`
- `business.max-active-reservations` (default 3) — per-user active limit
- `business.zone-id` (default `America/Guayaquil`) — used for availability calendar

A unique DB constraint on `(cancha_id, inicio)` prevents double-booking at the DB level; the service catches `DataIntegrityViolationException` and returns HTTP 409. A scheduled job (`FinalizadorJob`) transitions past `CONFIRMADA` reservations to `FINALIZADA`.

### Data snapshot pattern

When a reservation is created, `ms-reservas` copies `cancha_nombre` and `deporte` from the response of `CanchaClient.obtener()` into the `reservas` table. This keeps historical records consistent even if the court is later renamed or deleted.
