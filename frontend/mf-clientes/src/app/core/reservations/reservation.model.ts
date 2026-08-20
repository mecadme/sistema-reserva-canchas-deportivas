export type ReservationStatus = 'Confirmada' | 'Cancelada' | 'Finalizada';

export interface Reservation {
  id: string;
  canchaId: string;
  canchaNombre: string;
  canchaEmoji: string;
  fecha: string;
  hora: string;
  estado: ReservationStatus;
  creadaEn: string;
}

export interface NewReservationInput {
  canchaId: string;
  canchaNombre: string;
  canchaEmoji: string;
  fecha: string;
  hora: string;
}
