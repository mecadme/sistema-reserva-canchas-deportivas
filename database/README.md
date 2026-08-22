# Base de datos

DDL y datos de prueba (seed) de PostgreSQL, uno por microservicio — entregable E4 del documento de alcance.

```text
database/
├── usuarios/
│   ├── V1__usuarios.sql   # DDL: tabla usuarios
│   └── V2__seed.sql       # datos de prueba
├── canchas/
│   ├── V1__canchas.sql    # DDL: canchas + bloqueos_mantenimiento
│   └── V2__seed.sql
├── reservas/
│   ├── V1__reservas.sql   # DDL: reservas (incluye el constraint EXCLUDE que aplica RN-02)
│   └── V2__seed.sql
└── reportes/
    ├── V1__reportes.sql   # DDL: reporte_consultas
    └── V2__seed.sql
```

## Fuente de verdad

Estos archivos son una **copia de solo lectura** de las migraciones reales, que viven en cada microservicio:

```text
backend/ms-usuarios/src/main/resources/db/migration/
backend/ms-canchas/src/main/resources/db/migration/
backend/ms-reservas/src/main/resources/db/migration/
backend/ms-reportes/src/main/resources/db/migration/
```

**No edites los archivos de esta carpeta directamente** — no tienen efecto sobre la base de datos real. Cada microservicio ejecuta sus propias migraciones automáticamente al arrancar, vía [Flyway](https://flywaydb.org/) (requiere la dependencia `spring-boot-starter-flyway` además de `flyway-core`; sin ella, Spring Boot 4.x no activa el auto-configure de Flyway y Hibernate genera el schema sin los constraints de negocio).

Si modificas la migración real de un microservicio, actualiza también la copia aquí para que ambas no queden desincronizadas.

## Cómo se ejecutan

No hace falta correr estos scripts a mano. Al levantar el sistema (`docker compose -f infra/docker-compose.yml up`, o `backend/docker-compose.yml` para solo el backend), cada microservicio se conecta a su base PostgreSQL y Flyway aplica `V1` (DDL) y `V2` (seed) automáticamente en el primer arranque.

Para inspeccionar una base directamente:
```bash
docker exec -it infra-usuarios-db-1 psql -U reservas -d usuarios_db
```

## Independencia entre servicios

Cada base (`usuarios_db`, `canchas_db`, `reservas_db`, `reportes_db`) es propia de su microservicio — no hay `JOIN`, claves foráneas ni consultas cruzadas entre bases distintas. Las relaciones externas (`usuario_id`, `cancha_id`) se guardan como UUID sueltos; cuando un servicio necesita datos de otro, los pide por REST (ver `docs/architecture/README.md`).

## Diagramas ER interactivos (opcional)

Los diagramas ER "oficiales" (entregable E1) ya están versionados como texto en `docs/architecture/README.md`. Si quieres explorar el schema visualmente (zoom, reordenar, anotar), ver [`DIAGRAMS.md`](./DIAGRAMS.md) — usa [ChartDB](https://chartdb.io) self-hosted vía Docker, pegando estos mismos archivos `V1__*.sql`.
