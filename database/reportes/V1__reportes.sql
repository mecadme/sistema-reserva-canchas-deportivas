CREATE TABLE reporte_consultas (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    fecha_desde DATE NOT NULL,
    fecha_hasta DATE NOT NULL,
    total_reservas BIGINT NOT NULL,
    generado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_reporte_fechas CHECK (fecha_desde <= fecha_hasta)
);

CREATE INDEX idx_reporte_consultas_usuario ON reporte_consultas (usuario_id, generado_en DESC);
