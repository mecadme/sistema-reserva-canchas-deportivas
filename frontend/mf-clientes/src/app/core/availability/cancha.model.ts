export type Deporte = 'PADEL' | 'TENIS' | 'BASQUET';

export const DEPORTE_EMOJIS: Record<Deporte, string> = {
  PADEL: '🏓',
  TENIS: '🎾',
  BASQUET: '🏀',
};

export interface Cancha {
  id: string;
  nombre: string;
  emoji: string;
}
