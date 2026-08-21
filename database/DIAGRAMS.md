# Diagramas ER interactivos (opcional, con ChartDB)

Los diagramas ER "oficiales" del proyecto (entregable E1) ya están versionados como texto en [`docs/architecture/README.md`](../docs/architecture/README.md), en formato Mermaid — se ven directo en GitHub sin instalar nada.

Esta guía es para cuando alguien del equipo quiera **explorar visualmente e interactuar** con el schema (hacer zoom, reordenar tablas, anotar) — no reemplaza el entregable, es una herramienta de apoyo.

## Levantar ChartDB

```bash
docker run --rm -p 8080:80 ghcr.io/chartdb/chartdb:latest
```

Abre `http://localhost:8080`. No hace falta `OPENAI_API_KEY` — esa variable solo habilita funciones de IA (por ejemplo, exportar el DDL a otro dialecto SQL), que no usamos aquí.

No corras esto junto al resto del stack en `infra/docker-compose.yml`: es una herramienta puntual, no una pieza del sistema. Ciérrala cuando termines (`docker stop` o `Ctrl+C`, gracias al `--rm` no deja nada corriendo).

## Un diagrama por base (no uno combinado)

Cada microservicio es dueño exclusivo de su base — sin `JOIN`, sin foreign keys cruzadas entre `usuarios_db`, `canchas_db`, `reservas_db` y `reportes_db` (ver [`docs/architecture/README.md`](../docs/architecture/README.md#4-modelo-de-datos-por-servicio)). Generar un solo diagrama combinado sugeriría visualmente una relación entre bases que no existe. Por eso: **4 diagramas separados**, uno por carpeta de este directorio.

## Flujo paso a paso

Para cada servicio (`usuarios`, `canchas`, `reservas`, `reportes`):

1. Abre `http://localhost:8080` y crea un diagrama nuevo.
2. Elige **PostgreSQL** como motor.
3. Selecciona la opción de importar por **DDL script** (pegar SQL), no por conexión en vivo — no hace falta tener ninguna base levantada.
4. Copia el contenido de `database/<servicio>/V1__*.sql` de este repo y pégalo.

   Ejemplo para reservas:
   ```bash
   cat database/reservas/V1__reservas.sql
   ```
5. ChartDB dibuja el diagrama automáticamente. Guárdalo con un nombre claro, ej. `reservas_db`.
6. Repite para los otros 3 servicios.

`V2__seed.sql` no hace falta pegarlo — son `INSERT`, no definen estructura; ChartDB solo necesita el DDL (`V1__*.sql`).

## Nota sobre `reservas`

El diagrama de `reservas_db` es el más interesante de los 4: la tabla `reservas` tiene un constraint `EXCLUDE USING gist` (no un `CHECK` ni una FK) que aplica RN-02 (no solapamiento de horarios) directamente en PostgreSQL. ChartDB debería mostrarlo entre los índices/constraints de la tabla al importar el DDL — vale la pena señalarlo si se usa este diagrama en una presentación.
