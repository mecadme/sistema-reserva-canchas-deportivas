import { Reservation } from './reservation.model';

export function reservationStartDate(fecha: string, hora: string): Date {
  const [year, month, day] = fecha.split('-').map(Number);
  const [hour] = hora.split(':').map(Number);
  return new Date(year, month - 1, day, hour, 0, 0, 0);
}

export function isCancelable(reservation: Reservation, now: Date = new Date()): boolean {
  if (reservation.estado !== 'Confirmada') {
    return false;
  }
  return reservationStartDate(reservation.fecha, reservation.hora) > now;
}
