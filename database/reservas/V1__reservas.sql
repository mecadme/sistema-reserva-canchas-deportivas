CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE reservas (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    cancha_id UUID NOT NULL,
    cancha_nombre VARCHAR(120) NOT NULL,
    deporte VARCHAR(20) NOT NULL CHECK (deporte IN ('PADEL', 'TENIS', 'BASQUET')),
    inicio TIMESTAMPTZ NOT NULL,
    fin TIMESTAMPTZ NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('CONFIRMADA', 'CANCELADA', 'FINALIZADA')),
    cancelada_por UUID,
    motivo_cancelacion VARCHAR(250),
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_reserva_periodo CHECK (inicio < fin)
);

ALTER TABLE reservas ADD CONSTRAINT ex_reserva_cancha_horario
    EXCLUDE USING gist (
        cancha_id WITH =,
        tstzrange(inicio, fin, '[)') WITH &&
    ) WHERE (estado = 'CONFIRMADA');

CREATE INDEX idx_reservas_usuario_estado ON reservas (usuario_id, estado, inicio);
CREATE INDEX idx_reservas_periodo ON reservas (inicio, fin);
