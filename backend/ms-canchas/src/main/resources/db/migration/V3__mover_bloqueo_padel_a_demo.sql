-- Reubica el bloqueo de mantenimiento de Pádel A al 29-ago-2026 (día de la presentación):
-- cubre 07:00-10:00 hora local (America/Guayaquil, -05:00 => 12:00-15:00 UTC), así la
-- pantalla de disponibilidad muestra visualmente el estado BLOQUEADO (RN-07) el día de la demo.
--
-- Va como migración nueva (no se edita V2__seed.sql) porque Flyway valida el checksum de
-- las migraciones ya aplicadas al arrancar: modificar V2 directamente rompe esa validación
-- y tumba el microservicio en cualquier entorno que ya haya corrido la migración anterior.
UPDATE bloqueos_mantenimiento
SET inicio = '2026-08-29 12:00:00+00',
    fin = '2026-08-29 15:00:00+00',
    creado_en = '2026-08-27 10:00:00+00'
WHERE id = 'f1000001-0000-0000-0000-000000000001';
