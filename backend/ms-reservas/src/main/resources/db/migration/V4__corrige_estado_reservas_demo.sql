-- V3__reservas_demo_29_ago.sql corrió inicio/fin de las reservas sembradas hacia el 29-31 ago,
-- pero nunca reasignó `estado`: e0000008-e0000012 venían de V2 como CONFIRMADA con fechas ya
-- pasadas (20-22 ago), el FinalizadorJob las pasó a FINALIZADA en runtime, y V3 las movió al
-- futuro sin revertir eso — quedaron FINALIZADA con fecha futura. Como disponibilidad() y
-- "mis reservas" solo cuentan CONFIRMADA, esas reservas dejaron de verse en la demo.
--
-- Va como migración nueva (no se edita V3) porque Flyway valida el checksum de las
-- migraciones ya aplicadas al arrancar: modificar V3 directamente rompe esa validación
-- y tumba el microservicio en cualquier entorno que ya haya corrido la migración anterior.
--
-- El filtro `fin > now()` (en vez de listar los ids a ciegas) hace la migración idempotente
-- respecto del FinalizadorJob: si algún día se vuelve a aplicar sobre una base donde estas
-- reservas ya pasaron de verdad, no las "resucita" a CONFIRMADA.
UPDATE reservas
SET estado = 'CONFIRMADA', actualizado_en = now()
WHERE id IN (
  'e0000008-0000-0000-0000-000000000008',
  'e0000009-0000-0000-0000-000000000009',
  'e0000010-0000-0000-0000-000000000010',
  'e0000011-0000-0000-0000-000000000011',
  'e0000012-0000-0000-0000-000000000012'
)
AND estado = 'FINALIZADA'
AND fin > now();
