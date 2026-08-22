CREATE TABLE canchas (
    id UUID PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    deporte VARCHAR(20) NOT NULL CHECK (deporte IN ('PADEL', 'TENIS', 'BASQUET')),
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_horario_cancha CHECK (hora_apertura < hora_cierre)
);

CREATE TABLE bloqueos_mantenimiento (
    id UUID PRIMARY KEY,
    cancha_id UUID NOT NULL REFERENCES canchas(id),
    inicio TIMESTAMPTZ NOT NULL,
    fin TIMESTAMPTZ NOT NULL,
    motivo VARCHAR(250) NOT NULL,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_bloqueo_periodo CHECK (inicio < fin)
);

CREATE INDEX idx_bloqueos_cancha_periodo ON bloqueos_mantenimiento (cancha_id, inicio, fin);
