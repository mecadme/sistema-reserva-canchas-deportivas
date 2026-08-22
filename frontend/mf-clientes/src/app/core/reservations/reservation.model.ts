export type ReservationStatus = 'Confirmada' | 'Cancelada' | 'Finalizada';

export interface Reservation {
  id: string;
  canchaId: string;
  canchaNombre: string;
  canchaEmoji: string;
  fecha: string;
  hora: string;
  estado: ReservationStatus;
}

export interface NewReservationInput {
  canchaId: string;
  fecha: string;
  hora: string;
}
