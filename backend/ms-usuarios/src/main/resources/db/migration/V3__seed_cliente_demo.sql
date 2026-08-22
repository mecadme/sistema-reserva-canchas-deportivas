-- Usuario cliente de demo (contraseña: secret), mismo dominio que el admin de bootstrap.
INSERT INTO usuarios (id, nombre, email, password_hash, rol, activo, creado_en, actualizado_en) VALUES
  ('77777777-7777-7777-7777-777777777777', 'Cliente Demo', 'cliente@canchas.local', '$2a$10$l1BpOPpYLo3Oa6W73NouZeND.VXAHfcu7Cz6gh1vg8UetcINGOwsG', 'USUARIO', TRUE, '2026-07-23 08:00:00+00', '2026-07-23 08:00:00+00');
