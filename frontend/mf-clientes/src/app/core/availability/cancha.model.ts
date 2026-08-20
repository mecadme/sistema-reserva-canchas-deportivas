export interface Cancha {
  id: string;
  nombre: string;
  emoji: string;
}

export const CANCHAS: Cancha[] = [
  { id: 'cancha-padel-1', nombre: 'Pádel', emoji: '🏓' },
  { id: 'cancha-tenis-1', nombre: 'Tenis', emoji: '🎾' },
  { id: 'cancha-basquet-1', nombre: 'Básquet', emoji: '🏀' },
];
