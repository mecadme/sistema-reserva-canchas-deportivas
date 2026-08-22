-- Recorre las fechas del seed de reservas para que la demo del 29-ago-2026 tenga datos
-- reales alrededor de esa fecha: histórico FINALIZADA/CANCELADA antes del 29-ago, reservas
-- CONFIRMADA el propio 29-ago (algunas listas para cancelar/crear en vivo) y reservas
-- CONFIRMADA los días siguientes para que "Mis reservas" no quede vacío tras la demo.
--
-- Va como migración nueva (no se edita V2__seed.sql) porque Flyway valida el checksum de
-- las migraciones ya aplicadas al arrancar: modificar V2 directamente rompe esa validación
-- y tumba el microservicio en cualquier entorno que ya haya corrido la migración anterior.

-- ── Histórico (22-26 ago) / FINALIZADA — mismo usuario/cancha que en V2, solo se corren
-- las fechas una semana hacia adelante ────────────────────────────────────────────────
UPDATE reservas SET
  inicio = '2026-08-22 09:00:00+00', fin = '2026-08-22 10:00:00+00',
  creado_en = '2026-08-21 20:00:00+00', actualizado_en = '2026-08-22 10:00:00+00'
WHERE id = 'e0000001-0000-0000-0000-000000000001';

UPDATE reservas SET
  inicio = '2026-08-23 10:00:00+00', fin = '2026-08-23 11:00:00+00',
  creado_en = '2026-08-22 18:00:00+00', actualizado_en = '2026-08-23 11:00:00+00'
WHERE id = 'e0000002-0000-0000-0000-000000000002';

UPDATE reservas SET
  inicio = '2026-08-24 08:00:00+00', fin = '2026-08-24 09:00:00+00',
  creado_en = '2026-08-23 12:00:00+00', actualizado_en = '2026-08-24 09:00:00+00'
WHERE id = 'e0000003-0000-0000-0000-000000000003';

UPDATE reservas SET
  inicio = '2026-08-25 14:00:00+00', fin = '2026-08-25 15:00:00+00',
  creado_en = '2026-08-24 09:00:00+00', actualizado_en = '2026-08-25 15:00:00+00'
WHERE id = 'e0000004-0000-0000-0000-000000000004';

UPDATE reservas SET
  inicio = '2026-08-26 16:00:00+00', fin = '2026-08-26 17:00:00+00',
  creado_en = '2026-08-25 10:00:00+00', actualizado_en = '2026-08-26 17:00:00+00'
WHERE id = 'e0000005-0000-0000-0000-000000000005';

-- ── Canceladas (24 y 27 ago) ────────────────────────────────────────────────────────
UPDATE reservas SET
  inicio = '2026-08-27 11:00:00+00', fin = '2026-08-27 12:00:00+00',
  creado_en = '2026-08-26 08:00:00+00', actualizado_en = '2026-08-26 08:45:00+00'
WHERE id = 'e0000006-0000-0000-0000-000000000006';

UPDATE reservas SET
  inicio = '2026-08-24 15:00:00+00', fin = '2026-08-24 16:00:00+00',
  creado_en = '2026-08-23 14:00:00+00', actualizado_en = '2026-08-23 16:00:00+00'
WHERE id = 'e0000007-0000-0000-0000-000000000007';

-- ── 29-ago (día de la demo) / CONFIRMADA ────────────────────────────────────────────
-- Cliente demo: pasa de Carlos/Pádel A a cliente@canchas.local/Pádel B — reserva lista
-- para CANCELAR en vivo durante la presentación.
UPDATE reservas SET
  usuario_id = '77777777-7777-7777-7777-777777777777',
  cancha_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
  cancha_nombre = 'Pádel B', deporte = 'PADEL',
  inicio = '2026-08-29 15:00:00+00', fin = '2026-08-29 16:00:00+00',
  creado_en = '2026-08-27 09:00:00+00', actualizado_en = '2026-08-27 09:00:00+00'
WHERE id = 'e0000008-0000-0000-0000-000000000008';

-- Carlos: pasa de Básquet Norte a Pádel A, en la tarde (después del bloqueo de
-- mantenimiento de la mañana); deja el resto de la tarde/noche de Pádel A libre para
-- CREAR una reserva en vivo.
UPDATE reservas SET
  cancha_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
  cancha_nombre = 'Pádel A', deporte = 'PADEL',
  inicio = '2026-08-29 19:00:00+00', fin = '2026-08-29 20:00:00+00',
  creado_en = '2026-08-27 10:00:00+00', actualizado_en = '2026-08-27 10:00:00+00'
WHERE id = 'e0000009-0000-0000-0000-000000000009';

-- María Torres: se queda en Tenis Central, movida al 29-ago.
UPDATE reservas SET
  inicio = '2026-08-29 14:00:00+00', fin = '2026-08-29 15:00:00+00',
  creado_en = '2026-08-27 11:00:00+00', actualizado_en = '2026-08-27 11:00:00+00'
WHERE id = 'e0000010-0000-0000-0000-000000000010';

-- ── 30-31 ago / CONFIRMADA (para que "Mis reservas" no quede vacío tras la demo) ────
-- Juan Pérez: pasa de Pádel B a Básquet Norte, 30-ago.
UPDATE reservas SET
  cancha_id = 'dddddddd-dddd-dddd-dddd-dddddddddddd',
  cancha_nombre = 'Básquet Norte', deporte = 'BASQUET',
  inicio = '2026-08-30 13:00:00+00', fin = '2026-08-30 14:00:00+00',
  creado_en = '2026-08-27 09:30:00+00', actualizado_en = '2026-08-27 09:30:00+00'
WHERE id = 'e0000011-0000-0000-0000-000000000011';

-- Ana García: pasa de Pádel A a Pádel B, 31-ago.
UPDATE reservas SET
  cancha_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
  cancha_nombre = 'Pádel B', deporte = 'PADEL',
  inicio = '2026-08-31 16:00:00+00', fin = '2026-08-31 17:00:00+00',
  creado_en = '2026-08-27 09:45:00+00', actualizado_en = '2026-08-27 09:45:00+00'
WHERE id = 'e0000012-0000-0000-0000-000000000012';

-- Carlos: segunda reserva futura (30-ago, Pádel A) — no existía en V2, se agrega.
INSERT INTO reservas (id, usuario_id, cancha_id, cancha_nombre, deporte, inicio, fin, estado, cancelada_por, motivo_cancelacion, creado_en, actualizado_en)
VALUES (
  'e0000013-0000-0000-0000-000000000013',
  '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
  'Pádel A', 'PADEL',
  '2026-08-30 20:00:00+00', '2026-08-30 21:00:00+00',
  'CONFIRMADA', NULL, NULL, '2026-08-27 10:15:00+00', '2026-08-27 10:15:00+00'
)
ON CONFLICT (id) DO NOTHING;
