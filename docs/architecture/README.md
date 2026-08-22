# Arquitectura del Sistema de Reserva de Canchas Deportivas

Este documento describe la arquitectura implementada: microfrontends con Module Federation en el frontend, microservicios Spring Boot en el backend, y una base de datos PostgreSQL independiente por servicio.

## 0. Modelo C4 (Contexto y Contenedores) en Structurizr DSL

El modelo formal C4 vive en [`c4/workspace.dsl`](c4/workspace.dsl) y es la fuente de verdad de los diagramas de **Contexto** (C1: Usuario Final / Administrador ↔ el sistema como caja negra) y **Contenedores** (C2: los 4 microfrontends, los 4 microservicios y sus 4 bases PostgreSQL, con las relaciones entre ellos). Los diagramas Mermaid de las secciones 1-3 de este documento son una vista renderizada equivalente, pensada para leerse directo en GitHub sin herramientas adicionales.

Para visualizar o editar el DSL con el editor visual de Structurizr:

```bash
docker run --rm -p 8080:8080 -v "$(pwd)/docs/architecture/c4:/usr/local/structurizr" structurizr/lite
# abrir http://localhost:8080
```

Para solo validar la sintaxis sin levantar el servidor:

```bash
docker run --rm -v "$(pwd)/docs/architecture/c4:/usr/local/structurizr" structurizr/cli validate -w workspace.dsl
```

## 1. Diagrama de contenedores

```mermaid
flowchart TB
    subgraph Browser["Navegador del usuario"]
        Shell["shell (host)<br/>puerto 4300"]
        MfClientes["mf-clientes (remote)<br/>puerto 4201"]
        MfAdmin["mf-administracion (remote)<br/>puerto 4202"]
        MfReportes["mf-reportes (remote)<br/>puerto 4203"]
    end

    subgraph Backend["Backend — microservicios Spring Boot"]
        MsUsuarios["ms-usuarios<br/>puerto 8081"]
        MsCanchas["ms-canchas<br/>puerto 8082"]
        MsReservas["ms-reservas<br/>puerto 8083"]
        MsReportes["ms-reportes<br/>puerto 8084"]
    end

    subgraph DB["PostgreSQL — una base por servicio"]
        UsuariosDB[("usuarios_db")]
        CanchasDB[("canchas_db")]
        ReservasDB[("reservas_db")]
        ReportesDB[("reportes_db")]
    end

    Shell -- "Module Federation<br/>(carga remoteEntry.js en runtime)" --> MfClientes
    Shell -- "Module Federation" --> MfAdmin
    Shell -- "Module Federation" --> MfReportes

    Shell -->|"REST (login/registro)"| MsUsuarios
    MfClientes --> MsReservas
    MfClientes --> MsCanchas
    MfAdmin --> MsCanchas
    MfAdmin --> MsUsuarios
    MfAdmin --> MsReservas
    MfReportes --> MsReportes

    MsUsuarios --> UsuariosDB
    MsCanchas --> CanchasDB
    MsReservas --> ReservasDB
    MsReportes --> ReportesDB

    MsReservas -- "REST interno<br/>X-Service-Key" --> MsCanchas
    MsReportes -- "REST interno<br/>X-Service-Key" --> MsReservas
    MsReportes -- "REST interno<br/>X-Service-Key" --> MsCanchas
```

**Estado actual:** el frontend está completo e integrado vía Module Federation, y hace llamadas HTTP reales a los 4 microservicios (login/registro, disponibilidad, reservas, canchas, usuarios y reportes) — ya no opera en modo mock.

## 2. Diagrama de microfrontends (Module Federation)

```mermaid
flowchart LR
    subgraph Shell["shell — host"]
        Routes["app.routes.ts"]
        Guards["role.guard / guest.guard"]
        Auth["AuthService (JWT real vía ms-usuarios)"]
        Nav["AdminNav<br/>(visible solo si role=admin)"]
    end

    Routes -->|"'/clientes' + roleGuard(['cliente'])"| RClientes["loadChildren:<br/>mfClientes/Routes"]
    Routes -->|"'/admin' + roleGuard(['admin'])"| RAdmin["loadChildren:<br/>mfAdministracion/Routes"]
    Routes -->|"'/reportes' + roleGuard(['admin'])"| RReportes["loadChildren:<br/>mfReportes/Routes"]

    RClientes -.->|remoteEntry.js :4201| MfClientes["mf-clientes<br/>disponibilidad, reservar, mis reservas"]
    RAdmin -.->|remoteEntry.js :4202| MfAdmin["mf-administracion<br/>canchas, bloqueos, usuarios, reservas globales"]
    RReportes -.->|remoteEntry.js :4203| MfReportes["mf-reportes<br/>ocupación, reservas por deporte, demanda"]
```

Cada remote expone únicamente sus rutas (`exposes: { './Routes': './src/app/app.routes.ts' }`); el `shell` es el único responsable de layout, autenticación y navegación entre módulos (`AdminNav` vive en el shell, no duplicado en cada remote).

## 3. Diagrama de microservicios e integraciones REST

```mermaid
flowchart TB
    ms_usuarios["ms-usuarios<br/>Registro, login JWT, roles, activar/inactivar"]
    ms_canchas["ms-canchas<br/>ABM canchas, horarios, bloqueos de mantenimiento"]
    ms_reservas["ms-reservas<br/>Disponibilidad, crear/cancelar, RN-01 a RN-08"]
    ms_reportes["ms-reportes<br/>Ocupación, reservas por período/deporte"]

    ms_reservas -- "GET /internal/v1/canchas/{id}<br/>valida cancha activa + horario + bloqueos" --> ms_canchas
    ms_reportes -- "GET /internal/v1/reservas/resumen" --> ms_reservas
    ms_reportes -- "GET /internal/v1/canchas" --> ms_canchas

    style ms_usuarios fill:#e4efe9,stroke:#0f4c3a
    style ms_canchas fill:#e4efe9,stroke:#0f4c3a
    style ms_reservas fill:#e4efe9,stroke:#0f4c3a
    style ms_reportes fill:#e4efe9,stroke:#0f4c3a
```

`ms-usuarios` no depende de ningún otro servicio ni es consultado internamente por ellos: la identidad del usuario viaja en el JWT (claim `sub` + `roles`), no por llamada REST. Toda comunicación entre microservicios usa la cabecera `X-Service-Key` (`ServiceKeyInterceptor`) para autenticar tráfico interno, separado de la autenticación JWT de usuarios finales.

## 4. Modelo de datos por servicio

Cada microservicio administra su propio esquema PostgreSQL, sin joins ni foreign keys entre bases distintas (las relaciones externas se guardan como UUID sueltos, ej. `usuario_id` en `reservas_db` no referencia físicamente a `usuarios_db`).

### `usuarios_db`
```mermaid
erDiagram
    usuarios {
        UUID id PK
        varchar nombre
        varchar email UK
        varchar password_hash
        varchar rol "USUARIO | ADMINISTRADOR"
        boolean activo
        timestamptz creado_en
        timestamptz actualizado_en
    }
```

### `canchas_db`
```mermaid
erDiagram
    canchas ||--o{ bloqueos_mantenimiento : "tiene"
    canchas {
        UUID id PK
        varchar nombre UK
        varchar deporte "PADEL | TENIS | BASQUET"
        time hora_apertura
        time hora_cierre
        boolean activa
    }
    bloqueos_mantenimiento {
        UUID id PK
        UUID cancha_id FK
        timestamptz inicio
        timestamptz fin
        varchar motivo
    }
```

### `reservas_db`
```mermaid
erDiagram
    reservas {
        UUID id PK
        UUID usuario_id "sin FK física, viene de usuarios_db"
        UUID cancha_id "sin FK física, viene de canchas_db"
        varchar cancha_nombre "instantánea para trazabilidad"
        varchar deporte "PADEL | TENIS | BASQUET"
        timestamptz inicio
        timestamptz fin
        varchar estado "CONFIRMADA | CANCELADA | FINALIZADA"
        UUID cancelada_por
        varchar motivo_cancelacion
    }
```

La tabla `reservas` tiene un constraint `EXCLUDE USING gist (cancha_id WITH =, tstzrange(inicio, fin, '[)') WITH &&) WHERE (estado = 'CONFIRMADA')` — esto aplica **RN-02 (no solapamiento)** a nivel de base de datos, no solo en código Java, garantizando la regla incluso bajo concurrencia.

### `reportes_db`
```mermaid
erDiagram
    reporte_consultas {
        UUID id PK
        UUID usuario_id
        date fecha_desde
        date fecha_hasta
        bigint total_reservas
        timestamptz generado_en
    }
```

`reporte_consultas` es una tabla de auditoría (qué admin generó qué reporte y cuándo) — los datos del reporte en sí (ocupación, cancelaciones) se calculan al vuelo consultando `ms-reservas` y `ms-canchas` por REST, sin duplicar su información.

## 5. Decisiones de arquitectura

| Fecha | Decisión | Motivo |
|---|---|---|
| 2026-08 | Angular 20 (standalone components) + Webpack 5 vía `@angular-architects/module-federation` | Angular CLI usa esbuild/Vite por defecto, pero Module Federation requiere el runtime de Webpack |
| 2026-08 | Java 17, Spring Boot, Maven multi-módulo, arquitectura hexagonal (`domain/port/in`, `domain/port/out`) en cada microservicio | Separar reglas de negocio de infraestructura (JPA, REST) facilita testear RN-01 a RN-08 de forma aislada |
| 2026-08 | Sin API Gateway/BFF | El PDF lo marca como opcional; con 4 servicios el frontend puede llamarlos directo sin sobre-ingeniería |
| 2026-08 | RN-02 aplicada con constraint `EXCLUDE` en PostgreSQL, no solo validación en Java | Garantiza la regla bajo condiciones de carrera concurrentes, que una validación solo en código no puede prevenir |
| 2026-08 | Frontend construido primero en modo mock (`localStorage`), luego conectado a los 4 microservicios reales vía `HttpClient` | Permitió avanzar y validar UX/flujos de Module Federation en paralelo al desarrollo del backend, sin bloquear a ningún frente |
| 2026-08 | CORS habilitado explícitamente en los 4 microservicios, restringido al origen del `shell` (`http://localhost:4300`) | Necesario porque el navegador bloquea peticiones cross-origin por defecto; se limita al único origen que hace llamadas reales |
| 2026-08 | `spring-boot-starter-flyway` agregado a los 4 microservicios (además de `flyway-core`) | Spring Boot 4.1.0 movió el auto-configure de Flyway a un módulo separado; sin este starter, Flyway nunca se ejecutaba y Hibernate generaba el schema sin los constraints de negocio (ej. `EXCLUDE` de RN-02) |
