-- Catálogo de canchas
INSERT INTO canchas (id, nombre, deporte, hora_apertura, hora_cierre, activa, creado_en, actualizado_en) VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Pádel A',        'PADEL',   '07:00', '22:00', TRUE,  '2026-06-01 08:00:00+00', '2026-06-01 08:00:00+00'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Pádel B',        'PADEL',   '07:00', '22:00', TRUE,  '2026-06-01 08:00:00+00', '2026-06-01 08:00:00+00'),
  ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Tenis Central',  'TENIS',   '06:00', '21:00', TRUE,  '2026-06-01 08:00:00+00', '2026-06-01 08:00:00+00'),
  ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Básquet Norte',  'BASQUET', '08:00', '20:00', TRUE,  '2026-06-01 08:00:00+00', '2026-06-01 08:00:00+00'),
  ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Tenis Sur',      'TENIS',   '06:00', '21:00', FALSE, '2026-06-01 08:00:00+00', '2026-08-10 09:00:00+00');

-- Bloqueos por mantenimiento
INSERT INTO bloqueos_mantenimiento (id, cancha_id, inicio, fin, motivo, creado_en) VALUES
  ('f1000001-0000-0000-0000-000000000001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
   '2026-08-25 08:00:00+00', '2026-08-25 14:00:00+00',
   'Revisión de malla y suelo', '2026-08-18 10:00:00+00'),
  ('f1000001-0000-0000-0000-000000000002', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
   '2026-08-22 06:00:00+00', '2026-08-22 10:00:00+00',
   'Pintura de líneas de cancha', '2026-08-18 11:00:00+00'),
  ('f1000001-0000-0000-0000-000000000003', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
   '2026-08-15 08:00:00+00', '2026-08-15 12:00:00+00',
   'Cambio de tableros (ya realizado)', '2026-08-14 09:00:00+00');
