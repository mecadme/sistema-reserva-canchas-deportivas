-- Agrega un segundo bloqueo de mantenimiento para Pádel A, un día después del sembrado en
-- V3 (2026-08-29), para que la prueba de "cancha bloqueada" en Postman siga siendo válida
-- aunque la fecha del 29-ago ya haya pasado. Cubre 07:00-10:00 hora local
-- (America/Guayaquil, -05:00 => 12:00-15:00 UTC) del 2026-08-30, igual que el bloqueo
-- original, solo que un día después.
--
-- Se agrega como fila nueva (no se toca la de V3) porque Flyway valida el checksum de las
-- migraciones ya aplicadas al arrancar: modificar V3 directamente rompe esa validación.
INSERT INTO bloqueos_mantenimiento (id, cancha_id, inicio, fin, motivo, creado_en)
VALUES (
  'f1000001-0000-0000-0000-000000000004', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
  '2026-08-30 12:00:00+00', '2026-08-30 15:00:00+00',
  'Revisión de malla y suelo', '2026-08-28 10:00:00+00'
)
ON CONFLICT (id) DO NOTHING;
