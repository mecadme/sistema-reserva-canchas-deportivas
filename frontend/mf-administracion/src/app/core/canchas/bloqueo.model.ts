export interface Bloqueo {
  id: string;
  canchaId: string;
  desde: string;
  hasta: string;
  motivo: string;
}

export interface BloqueoInput {
  desde: string;
  hasta: string;
  motivo: string;
}
