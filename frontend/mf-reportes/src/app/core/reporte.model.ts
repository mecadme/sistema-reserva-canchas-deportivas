export interface OcupacionCancha {
  canchaNombre: string;
  reservasOcupadas: number;
  bloquesDisponibles: number;
  porcentajeOcupacion: number;
}

export interface Reporte {
  desde: string;
  hasta: string;
  total: number;
  confirmadas: number;
  canceladas: number;
  finalizadas: number;
  ocupacionPorCancha: OcupacionCancha[];
  reservasPorDeporte: Record<string, number>;
}
