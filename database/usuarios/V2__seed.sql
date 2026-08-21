-- Usuarios de prueba (contraseña: secret)
-- El administrador admin@canchas.local lo inserta DataInitializer al arrancar.
INSERT INTO usuarios (id, nombre, email, password_hash, rol, activo, creado_en, actualizado_en) VALUES
  ('11111111-1111-1111-1111-111111111111', 'Carlos Mendoza',  'carlos@example.com',  '$2a$10$l1BpOPpYLo3Oa6W73NouZeND.VXAHfcu7Cz6gh1vg8UetcINGOwsG', 'USUARIO',        TRUE,  '2026-07-01 09:00:00+00', '2026-07-01 09:00:00+00'),
  ('22222222-2222-2222-2222-222222222222', 'María Torres',    'maria@example.com',   '$2a$10$l1BpOPpYLo3Oa6W73NouZeND.VXAHfcu7Cz6gh1vg8UetcINGOwsG', 'USUARIO',        TRUE,  '2026-07-05 10:30:00+00', '2026-07-05 10:30:00+00'),
  ('33333333-3333-3333-3333-333333333333', 'Juan Pérez',      'juan@example.com',    '$2a$10$l1BpOPpYLo3Oa6W73NouZeND.VXAHfcu7Cz6gh1vg8UetcINGOwsG', 'USUARIO',        TRUE,  '2026-07-10 08:00:00+00', '2026-07-10 08:00:00+00'),
  ('44444444-4444-4444-4444-444444444444', 'Ana García',      'ana@example.com',     '$2a$10$l1BpOPpYLo3Oa6W73NouZeND.VXAHfcu7Cz6gh1vg8UetcINGOwsG', 'USUARIO',        TRUE,  '2026-07-15 11:00:00+00', '2026-07-15 11:00:00+00'),
  ('55555555-5555-5555-5555-555555555555', 'Luis Ramos',      'luis@example.com',    '$2a$10$l1BpOPpYLo3Oa6W73NouZeND.VXAHfcu7Cz6gh1vg8UetcINGOwsG', 'USUARIO',        FALSE, '2026-07-20 14:00:00+00', '2026-08-01 09:00:00+00'),
  ('66666666-6666-6666-6666-666666666666', 'Sofía Vega',      'sofia@example.com',   '$2a$10$l1BpOPpYLo3Oa6W73NouZeND.VXAHfcu7Cz6gh1vg8UetcINGOwsG', 'ADMINISTRADOR',  TRUE,  '2026-07-22 08:00:00+00', '2026-07-22 08:00:00+00');
