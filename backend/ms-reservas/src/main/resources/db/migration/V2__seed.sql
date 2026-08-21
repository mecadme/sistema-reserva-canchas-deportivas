-- Reservas de prueba
-- Los IDs de cancha y usuario coinciden con los seeds de ms-canchas y ms-usuarios.
-- Las reservas pasadas están en estado FINALIZADA; las futuras en CONFIRMADA.

INSERT INTO reservas (id, usuario_id, cancha_id, cancha_nombre, deporte, inicio, fin, estado, cancelada_por, motivo_cancelacion, creado_en, actualizado_en) VALUES

  -- ── Pasadas / FINALIZADA ──────────────────────────────────────────────────────
  ('e0000001-0000-0000-0000-000000000001',
   '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
   'Pádel A', 'PADEL',
   '2026-08-15 09:00:00+00', '2026-08-15 10:00:00+00',
   'FINALIZADA', NULL, NULL, '2026-08-14 20:00:00+00', '2026-08-15 10:00:00+00'),

  ('e0000002-0000-0000-0000-000000000002',
   '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
   'Tenis Central', 'TENIS',
   '2026-08-16 10:00:00+00', '2026-08-16 11:00:00+00',
   'FINALIZADA', NULL, NULL, '2026-08-15 18:00:00+00', '2026-08-16 11:00:00+00'),

  ('e0000003-0000-0000-0000-000000000003',
   '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
   'Pádel B', 'PADEL',
   '2026-08-17 08:00:00+00', '2026-08-17 09:00:00+00',
   'FINALIZADA', NULL, NULL, '2026-08-16 12:00:00+00', '2026-08-17 09:00:00+00'),

  ('e0000004-0000-0000-0000-000000000004',
   '44444444-4444-4444-4444-444444444444', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
   'Básquet Norte', 'BASQUET',
   '2026-08-18 14:00:00+00', '2026-08-18 15:00:00+00',
   'FINALIZADA', NULL, NULL, '2026-08-17 09:00:00+00', '2026-08-18 15:00:00+00'),

  ('e0000005-0000-0000-0000-000000000005',
   '11111111-1111-1111-1111-111111111111', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
   'Tenis Central', 'TENIS',
   '2026-08-18 16:00:00+00', '2026-08-18 17:00:00+00',
   'FINALIZADA', NULL, NULL, '2026-08-17 10:00:00+00', '2026-08-18 17:00:00+00'),

  -- ── Canceladas ───────────────────────────────────────────────────────────────
  ('e0000006-0000-0000-0000-000000000006',
   '22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
   'Pádel A', 'PADEL',
   '2026-08-19 11:00:00+00', '2026-08-19 12:00:00+00',
   'CANCELADA',
   '22222222-2222-2222-2222-222222222222', 'Surgió un imprevisto',
   '2026-08-18 08:00:00+00', '2026-08-18 08:45:00+00'),

  ('e0000007-0000-0000-0000-000000000007',
   '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
   'Pádel B', 'PADEL',
   '2026-08-16 15:00:00+00', '2026-08-16 16:00:00+00',
   'CANCELADA',
   '00000000-0000-0000-0000-000000000000', 'Cancelación administrativa',
   '2026-08-15 14:00:00+00', '2026-08-15 16:00:00+00'),

  -- ── Futuras / CONFIRMADA ─────────────────────────────────────────────────────
  -- Carlos Mendoza: 2 reservas futuras (Pádel A y Básquet Norte)
  ('e0000008-0000-0000-0000-000000000008',
   '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
   'Pádel A', 'PADEL',
   '2026-08-20 09:00:00+00', '2026-08-20 10:00:00+00',
   'CONFIRMADA', NULL, NULL, '2026-08-19 08:00:00+00', '2026-08-19 08:00:00+00'),

  ('e0000009-0000-0000-0000-000000000009',
   '11111111-1111-1111-1111-111111111111', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
   'Básquet Norte', 'BASQUET',
   '2026-08-21 08:00:00+00', '2026-08-21 09:00:00+00',
   'CONFIRMADA', NULL, NULL, '2026-08-19 08:10:00+00', '2026-08-19 08:10:00+00'),

  -- María Torres: 1 reserva futura
  ('e0000010-0000-0000-0000-000000000010',
   '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
   'Tenis Central', 'TENIS',
   '2026-08-20 10:00:00+00', '2026-08-20 11:00:00+00',
   'CONFIRMADA', NULL, NULL, '2026-08-19 08:30:00+00', '2026-08-19 08:30:00+00'),

  -- Juan Pérez: 1 reserva futura
  ('e0000011-0000-0000-0000-000000000011',
   '33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
   'Pádel B', 'PADEL',
   '2026-08-22 11:00:00+00', '2026-08-22 12:00:00+00',
   'CONFIRMADA', NULL, NULL, '2026-08-19 09:00:00+00', '2026-08-19 09:00:00+00'),

  -- Ana García: 1 reserva futura
  ('e0000012-0000-0000-0000-000000000012',
   '44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
   'Pádel A', 'PADEL',
   '2026-08-21 14:00:00+00', '2026-08-21 15:00:00+00',
   'CONFIRMADA', NULL, NULL, '2026-08-19 09:15:00+00', '2026-08-19 09:15:00+00');
