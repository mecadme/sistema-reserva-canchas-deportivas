export type Deporte = 'PADEL' | 'TENIS' | 'BASQUET';

export const DEPORTE_LABELS: Record<Deporte, string> = {
  PADEL: 'Pádel',
  TENIS: 'Tenis',
  BASQUET: 'Básquet',
};

export const DEPORTE_EMOJIS: Record<Deporte, string> = {
  PADEL: '🏓',
  TENIS: '🎾',
  BASQUET: '🏀',
};

export const DEPORTES: Deporte[] = ['PADEL', 'TENIS', 'BASQUET'];

export interface Cancha {
  id: string;
  nombre: string;
  deporte: Deporte;
  horaApertura: string;
  horaCierre: string;
  activa: boolean;
}

export interface CanchaInput {
  nombre: string;
  deporte: Deporte;
  horaApertura: string;
  horaCierre: string;
}
